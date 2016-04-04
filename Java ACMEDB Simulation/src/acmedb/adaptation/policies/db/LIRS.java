package acmedb.adaptation.policies.db;

import acmedb.adaptation.Algorithm;
import acmedb.adaptation.Job;
import acmedb.adaptation.PriorityQueue;

/* Low Inter-Reference Recency Set */

public class LIRS extends Algorithm{
	public LIRS(){
		lir_block_set = new PriorityQueue();
		stack_s = new PriorityQueue();
		list_q = new PriorityQueue();
	}
	public void incrHit(int id, double size){
		count++;
		hit++;
		byteHit += size;

		Job theJob = lir_block_set.getJob(id);

		if (theJob != null) {
			AccessLIRBlock(theJob);
		}
		else
		{
			theJob = list_q.getJob(id);

			if (theJob != null) {
				AccessResidentHIRBlock(theJob);
			}
			else {
				// error !!
				// theJob is in neither buffer, so why are we here?
				assert(false);
			}
		}
	}
	public Job Uncache(){
		//
		// uncache will uncache the front block from list_q
		//

		Job uncached = null;

		uncached = list_q.DequeueJob(list_q.getFront().JobID());

		decrUsed(uncached.JobSize());

		assert(uncached!= null);

		return uncached;
	}
	public void Cache(Job job){
		count++;
		if (lir_block_set.getLength() < L_LIRS) {

			// keep adding to lir block set until it is filled up
			Push(lir_block_set, job);

			// add to stack s at the same time
			// need to create new job for stack s
			Job stack_s_job = new Job(job.JobID(), job.JobSize());
			stack_s_job.isLIR = true;
			stack_s_job.isResident = true;
			Push(stack_s, stack_s_job);

		}
		else {

			CacheNonResidentHIRBlock(job);

		}

		incrUsed(job.JobSize());
	}
	public String Name(){
		return "LIRS";
	}

	//public void PrintBuffer(ofstream outfile, const char policyname, int id);
	public PriorityQueue lir_block_set;
	public PriorityQueue stack_s;  /*LIRS Stack S - holds LIR blocks as well 
	                    as resident and non-resident HIR blocks */
	public PriorityQueue list_q;   // list Q - holds all resident HIR blocks
	public int L_LIRS;
	public int L_HIRS;

	public LIRS(int cachesize){
		lir_block_set = new PriorityQueue();
		stack_s = new PriorityQueue();
		list_q = new PriorityQueue();
		L_LIRS = (int) (0.99 * cachesize);   // 99%
		L_HIRS = cachesize - L_LIRS;
	}
	public Job getJob(int id) {
		Job retJob = null;

		retJob = lir_block_set.getJob(id);
		if (retJob == null) {
			retJob = list_q.getJob(id);
		}
		return retJob;

	}
	public Job release(int id) {
		// release will release an arbitrary block from cache, which may not be
		// according to the policy's uncaching rules
		// so ...

		// will release something from either lir_block_set or list_q
		//
		// if lir_block_set, then there is one less in lir_block_set, so shift
		// the latest one from list_q to the lir_block_set, and put the
		// incoming one into the end of list_q
		//
		// if list_q, then put the incoming one into the end of list_q
		Job rel = list_q.getJob(id);
		Job uncached = null;

		if (rel != null) {
			// the job is in list Q, so remove it from there
			uncached = list_q.DequeueJob(id);
		}
		else {
			// try to get the job to be released from the LIR block set
			rel = lir_block_set.getJob(id);

			if (rel != null) {
				// the job is in the LIR block set, so remove it from there
				uncached = lir_block_set.DequeueJob(id);

				// we now need to get the latest job from list Q and put it
				// into the LIR block set, since the LIR block set is now
				// missing one block

				// get the latest job that was added to list Q, remove it from
				// list Q, and add to the LIR block set
				Job newlir = list_q.DequeueJob(list_q.getEnd().JobID());
				lir_block_set.EnqueueJob(0, newlir);

				// now, see if that job that we just inserted into the LIR
				// block set exists in stack S
				Job newlir_in_stack = stack_s.getJob(newlir.JobID());
				if (newlir_in_stack != null) {
					// it is in stack S already, so we just need to flag
					// it as an LIR block, and as a resident block
					newlir_in_stack.isLIR = true;
					newlir_in_stack.isResident = true;
				}
				else {
					// it is not in stack S, so we add it to stack S first,
					// and then flag it as an LIR block, and as a resident block
					newlir_in_stack = new Job(newlir.JobID(), newlir.JobSize());
					stack_s.EnqueueJob(0, newlir_in_stack);
					newlir_in_stack.isLIR = true;
					newlir_in_stack.isResident = true;
				}
			}
		}

		// now, we try to retrieve the released job from stack S 
		rel = stack_s.getJob(id);

		if (rel != null) {
			// if it is in stack S, flag it as an non-resident HIR block
			rel.isLIR = false;
			rel.isResident = false;
		}

		// finally, prune the stack, so the bottom block is an LIR block,
		// just in case the block that we have release was the bottom 
		// LIR block ...
		PruneStack();

		decrUsed(uncached.JobSize());

		return uncached;

	}
	public void PruneStack() {
		// performs prune stack
		while (stack_s.getBottom()!= null) {
			if (!stack_s.getBottom().isLIR) {
				Job delJob = stack_s.DequeueJob(stack_s.getBottom().JobID());
				delJob = null;
			}
			else {
				break;
			}
		}
	}
	public void Promote(PriorityQueue list, Job job) {
		list.EnqueueJob(0,list.DequeueJob(job.JobID()));
	}
	public void Push(PriorityQueue list, Job job) {
		list.EnqueueJob(0,job);

	}
	public void Pop(PriorityQueue list, Job job) {
		list.DequeueJob(job.JobID());

	}
	public void AccessLIRBlock(Job job) {
		boolean doPrune = stack_s.getBottom().JobID() == job.JobID();

		Promote(stack_s, job);
		if (doPrune) {
			PruneStack();
		}

	}
	public void AccessResidentHIRBlock(Job job) {
		int jobid = job.JobID(), jobsize = job.JobSize();
		boolean wasJobInStack = stack_s.getJob(job.JobID()) != null;

		if (wasJobInStack) {

			//  PROMOTE IT TO THE TOP OF STACK S
			stack_s.getJob(jobid).isLIR = true;
			Promote(stack_s, job);

			//  REMOVE THE BLOCK FROM LIST Q
			// remove and delete the corresponding job from list Q
			Job del_list_q_job = list_q.DequeueJob(jobid);

			//  CHANGE ITS STATUS TO LIR (ADD TO LIR BLOCK SET)

			// determine whether a job with the same id exists in the LIR block set

			Push(lir_block_set, del_list_q_job);
			//}

			//
			//  LIR BLOCK AT BOTTOM OF STACK S MOVED TO LIST Q WITH STATUS CHANGED TO HIR
			//  (REMOVED FROM LIR BLOCK SET)
			//

			// get the bottom block from stack S
			Job bottomBlock = stack_s.getBottom();
			int bbid = bottomBlock.JobID(); //, bbsize = bottomBlock->JobSize();

			// remove and delete the bottom block from stack S
			Pop(stack_s, bottomBlock);
			bottomBlock = null;

			// remove and delete the job with the bottom block id (of stack S) from the LIR block set

			Job removed_block = lir_block_set.DequeueJob(bbid);
			//}


			assert(removed_block!= null);
			Push(list_q, removed_block);

			PruneStack();

		}
		else {

			Job stack_s_job = new Job(jobid, jobsize);

			// set the resident HIR block status of the job
			stack_s_job.isLIR = false;
			stack_s_job.isResident = true;

			// add it to stack S
			Push(stack_s, stack_s_job);

			Promote(list_q, job);

		}

	}
	public void CacheNonResidentHIRBlock(Job job) {
		Job jobInStack = stack_s.getJob(job.JobID());

		if (jobInStack != null) {

			//
			//  CHANGE THE STATUS TO LIR (ADD TO LIR BLOCK SET), 
			//  AND MOVE IT TO THE TOP OF STACK S
			//

			// set the block status of the existing block in stack S
			jobInStack.isLIR = true;
			jobInStack.isResident = true;

			// move the block to the top of stack S
			Promote(stack_s, jobInStack);

			// determine whether a job with the same id exists in the LIR block set
			// *** don't need to check this since its a non-resident HIR block
			//Job * jobInLIRBlockSet = lir_block_set->getJob(job->JobId());

			//if (jobInLIRBlockSet == NULL) {
			// add the job to be cached to the LIR block set, if it wasn't there
			Push(lir_block_set, job);
			//}

			//
			//  CHANGE THE STATUS OF THE BOTTOM BLOCK IN STACK S TO HIR (REMOVE FROM LIR BLOCK SET)
			//  AND MOVE IT TO LIST Q
			//

			// get the bottom block from stack S
			Job bottomBlock = stack_s.getBottom();

			// create a new job to be added to list Q, from the bottom block id of stack S
			//Job * list_q_job = new Job(bottomBlock->JobId(), bottomBlock->JobSize());

			// remove and delete the job with the bottom block id (of stack S) from the LIR block set
			//if (lir_block_set->getJob(bottomBlock->JobId())) {
			Job removed_block = lir_block_set.DequeueJob(bottomBlock.JobID());
			//delete removed_block;
			//}

			// add the new list Q job to list Q
			//Push(list_q, list_q_job);
			assert(removed_block!= null);
			Push(list_q, removed_block);

			// remove and delete the bottom block from stack S
			Pop(stack_s, bottomBlock);
			bottomBlock = null;

			//
			//  PRUNE STACK S
			//

			PruneStack();

		}
		else {

			// add it to stack S
			Job stack_s_job = new Job(job.JobID(), job.JobSize());
			// set the resident HIR block status of the job
			stack_s_job.isLIR = false;
			stack_s_job.isResident = true;

			//Push(stack_s, job);
			Push(stack_s, stack_s_job);

			// create a new job for list q based on the newly added stack S job
			//Job * list_q_job = new Job(job->JobId(), job->JobSize());

			// add it to list Q
			//Push(list_q, list_q_job);
			Push(list_q, job);

		}

	}
}
