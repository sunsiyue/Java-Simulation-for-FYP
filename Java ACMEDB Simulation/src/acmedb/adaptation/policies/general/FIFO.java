package acmedb.adaptation.policies.general;

import acmedb.adaptation.Algorithm;
import acmedb.adaptation.Job;

public class FIFO extends Algorithm{

	public void Cache(Job job) {
		count++;
		pq.EnqueueJob((double)count, job);
		incrUsed(job.JobSize());
	}

	public Job Uncache() {
		return UncacheMin();
	}
	
	public void incrHit(int id, double size) {
		count++;
		hit++;
		byteHit += size;
	}

	public String Name() {
		return "FIFO";
	}

}
