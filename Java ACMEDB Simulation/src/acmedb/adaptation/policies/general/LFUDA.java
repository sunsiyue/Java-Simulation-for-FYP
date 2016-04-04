package acmedb.adaptation.policies.general;

import acmedb.adaptation.Algorithm;
import acmedb.adaptation.Job;

public class LFUDA extends Algorithm{
	private double L = 0;
	public void Cache(Job job) {
		count++;
		double prio = 1.0 + L;
		totalprio += prio;
		pq.EnqueueJob(prio, job);
		incrUsed(job.JobSize());
	}

	public Job Uncache() {
		L = pq.minPriority();
		return UncacheMin();
	}

	public void incrHit(int id, double size) {
		double prio;

		count++;
		hit++;
		byteHit += size;
		Job j = pq.getJob(id);
		prio = pq.getPriority(id);
		totalprio -= prio;
		j.incFreq();
		prio = j.JobFreq() + L;
		totalprio += prio;
		pq.adjustPriority(id, prio);
	}

	public String Name() {
		return "LFUDA";
	}

}
