package acmedb.adaptation.policies.db;

import acmedb.adaptation.Algorithm;
import acmedb.adaptation.Job;
import acmedb.adaptation.PriorityQueue;

public class TWOQ extends Algorithm{
	public TWOQ(){
		// cached jobs go into these two buffers
		  Am = new PriorityQueue();
		  A1in = new PriorityQueue();

		  // "headers" go in this buffer, so they are 
		  // separate jobs that represent headers of
		  // previously cached jobs.
		  A1out = new PriorityQueue();
	}
	
	public TWOQ(int cachesize){
		// cached jobs go into these two buffers
		  Am = new PriorityQueue();
		  A1in = new PriorityQueue();

		  // "headers" go in this buffer, so they are 
		  // separate jobs that represent headers of
		  // previously cached jobs.
		  A1out = new PriorityQueue();

		  FreeSlots = cachesize;
		  Kin = (int) (0.2 * cachesize);   // 25%
		  Kout = (int) (0.5 * cachesize);   // 50%
	}
	
	public void incrHit(int id, double size){
		count++;
		hit++;
		byteHit += size;

	  if (Am.getJob(id) != null) {
	    Am.EnqueueJob(0, Am.DequeueJob(id));
	  }

	}
	public Job Uncache(){
		int id_out;

		  FreeSlots++;

		  if (A1in.getLength() > Kin) {
		    Job  A1inTail = A1in.DequeueFront();
		    id_out = A1inTail.JobID();

		    Job  headerJob = new Job(id_out, 1);
		    A1out.EnqueueJob(0, headerJob);

		    if (A1out.getLength() > Kout) {
		      Job  removedHeader = A1out.DequeueFront();
		      removedHeader = null;
		    }
		    // put X into the reclaimed page slot
		    return A1inTail;
		  }
		  else {
		    Job  AmTail = Am.DequeueFront();
		    // put X into the reclaimed page slot
		    return AmTail;
		  }

	}
	public void Cache(Job  job){
		FreeSlots--;
		count++;

	  if (A1out.getJob(job.JobID()) != null) {
	    //reclaimed = Reclaim(id);
	    //Am.EnqueueJob(0, reclaimed);
	    Am.EnqueueJob(0, job);
	  }
	  else {
	    if (A1in.getJob(job.JobID()) != null) {
	      // do nothing
	    }
	    else {
	      //reclaimed = Reclaim(id);
	      //A1in.EnqueueJob(0, reclaimed);
	      A1in.EnqueueJob(0, job);
	    }
	  }

	  //pq.EnqueueJob((double)count, job);
		incrUsed(job.JobSize());
	}
	public String Name(){
		return "2Q";
	}

	//virtual void PrintBuffer(ofstream outfile, const char  policyname, int id);
	/* buffers to be used with 2Q */
	public PriorityQueue Am;
	public PriorityQueue A1in;
	public PriorityQueue A1out;

	int Kin, Kout, FreeSlots;

	public Job getJob(int id) {
		Job  retJob = null;
		  
		  retJob = Am.getJob(id);
		  if (retJob == null) {
		    retJob = A1in.getJob(id);
		  }
		  return retJob;
	}
	public Job release(int id) {
		if (Am.getJob(id) != null) {
		    return Am.DequeueJob(id);
		  }
		  else {
		    if (A1in.getJob(id) != null) {
		      return A1in.DequeueJob(id);
		    }
		    else {
		      // ERROR!
		      // job is neither in Am nor A1in, so why are we here???!
		      assert(false);
		      return null;
		    }
		  }
	}

	/* methods for the Am buffer */
	public Job FindInAm(int id) {
		 return Am.getJob(id);
	}
	public void AddtoAm(Job job) {
		 Am.EnqueueJob(0, job);
	}
	public Job RemoveFromAm(int id) {
		return Am.DequeueJob(id);
	}

	/* methods for the A1in buffer */
	public Job FindInA1in(int id) {
		return A1in.getJob(id);

	}
	public void AddtoA1in(Job job) {
		A1in.EnqueueJob(0, job);
	}
	public Job RemoveFromA1in(int id) {
		  return A1in.DequeueJob(id);
	}

	/* methods for the A1out buffer */
	public Job FindInA1out(int id) {
		  return A1out.getJob(id);
	}
	public void AddtoA1out(Job job) {
		  A1out.EnqueueJob(0, job);
	}
	public Job RemoveFromA1out(int id) {
		  return A1out.DequeueJob(id);
	}

	public Job Reclaim(int id) {
		Job  newjob = new Job(id, 1);
		  int id_out;

		  if (FreeSlots > 0) {
		    // put X into a free page slot
		    return newjob;
		  }
		  else {
		    if (A1in.getLength() > Kin) {
		      Job  A1inTail = A1in.DequeueFront();
		      id_out = A1inTail.JobID();
		      //delete A1inTail; ??? paged out of A1in buffer (but may still need to keep the job)

		      Job  headerJob = new Job(id_out, 1);
		      A1out.EnqueueJob(0, headerJob);

		      if (A1out.getLength() > Kout) {
		        Job  removedHeader = A1out.DequeueFront();
		        removedHeader = null;
		      }
		      // put X into the reclaimed page slot
		      return newjob;
		    }
		    else {
		      Job  AmTail = Am.DequeueFront();
		      AmTail = null;
		      // put X into the reclaimed page slot
		      return newjob;
		    }
		  }
	}

	public void Request(int id) {
		Job  reclaimed = null;

		  if (Am.getJob(id) != null) {
		    Am.EnqueueJob(0, Am.DequeueJob(id));
		  }
		  else {
		    if (A1out.getJob(id) != null) {
		      reclaimed = Reclaim(id);
		      Am.EnqueueJob(0, reclaimed);
		    }
		    else {
		      if (A1in.getJob(id) != null) {
		        // do nothing
		      }
		      else {
		        reclaimed = Reclaim(id);
		        A1in.EnqueueJob(0, reclaimed);
		      }
		    }
		  }

	}
}
