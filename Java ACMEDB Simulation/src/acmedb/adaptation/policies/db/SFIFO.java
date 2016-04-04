package acmedb.adaptation.policies.db;

import acmedb.adaptation.Algorithm;
import acmedb.adaptation.Job;
import acmedb.adaptation.PriorityQueue;

public class SFIFO extends Algorithm{
	public SFIFO(){
		primary = new PriorityQueue();
		secondary = new PriorityQueue();
	}
	public SFIFO(int cachesize){
		primary = new PriorityQueue();
		secondary = new PriorityQueue();
		p_size = (int) (0.7 * cachesize);
		s_size = cachesize - p_size; //(int) (0.3 * cachesize);
	}
	public void incrHit(int id, double size){
		count++;
		hit++;
		byteHit += size;

		if (primary.getJob(id)!= null) {
			return;
		}
		AddToPrimary(RemoveFromSecondary(id));
		if (primary.getLength() >= p_size) {
			AddToSecondary(RemoveFromPrimary());
		}
	}
	public Job Uncache(){
		return secondary.DequeueFront();

	}
	public void Cache(Job job){
		count++;
		AddToPrimary(job);
		if (primary.getLength() > p_size) {
			AddToSecondary(RemoveFromPrimary());
		}

		incrUsed(job.JobSize());

	}
	public String Name(){
		return "SFIFO";
	}

	//virtual void PrintBuffer(ofstream outfile, const char * policyname, int id);
	public PriorityQueue primary;
	public PriorityQueue secondary;
	int p_size, s_size;

	/* methods for the primary buffer */
	public void AddToPrimary(Job job){
		primary.EnqueueJob(0, job);

	}
	public Job RemoveFromPrimary(){
		return primary.DequeueFront();

	}

	/* methods for the secondary buffer */
	public Job FindInSecondary(int id){
		return secondary.getJob(id);

	}
	public void AddToSecondary(Job job){
		secondary.EnqueueJob(0, job);

	}
	public Job RemoveFromSecondary(int id){
		return secondary.DequeueJob(id);

	}

	public Job getJob(int id){
		Job retJob = null;

		retJob = primary.getJob(id);
		if (retJob == null) {
			retJob = secondary.getJob(id);
		}
		return retJob;

	}
	public Job release(int id){
		Job retjob;
		retjob = primary.getJob(id);
		if (retjob!= null) {
			return primary.DequeueJob(id);
		}
		else {
			return secondary.DequeueJob(id);
		}
	}
	public void Request(int id){
		if (secondary.getJob(id) != null) {
			AddToPrimary(RemoveFromSecondary(id));
			if (primary.getLength() >= p_size) {
				AddToSecondary(RemoveFromPrimary());
			}
		}
		else {
			Job newjob = new Job(id,0);
			AddToPrimary(newjob);
			if (primary.getLength() > p_size) {
				AddToSecondary(RemoveFromPrimary());
				if (secondary.getLength() > s_size) {
					secondary.DequeueFront();
				}
			}
		}

	}

}
