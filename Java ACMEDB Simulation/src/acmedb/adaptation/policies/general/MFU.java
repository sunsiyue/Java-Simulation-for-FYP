package acmedb.adaptation.policies.general;

import acmedb.adaptation.Algorithm;
import acmedb.adaptation.Job;

public class MFU extends Algorithm{

	public void Cache(Job job) {
		count++;
		pq.EnqueueJob(1, job);
		incrUsed(job.JobSize());
	}
	public Job Uncache() {
		return UncacheMax();
	}

	public void incrHit(int id, double size) {
		count++;
		hit++;
		byteHit += size;
		pq.incPriority(id);
	}

	public String Name() {
		return "MFU";
	}

}
