package acmedb.adaptation.policies.db;

import acmedb.adaptation.Algorithm;
import acmedb.adaptation.Job;

public class LRFU extends Algorithm{
	public LRFU(){
		count =0;
	}
	public void incrHit(int id, double size){
		count++;
		hit++;
		byteHit += size;

		// update stats for cache-resident job
		double crf = F(0) + F(count - pq.getJob(id).last) * pq.getJob(id).crf;

		pq.getJob(id).crf = crf;
		pq.getJob(id).last = count;

		pq.adjustPriority(id, crf);
	}
	public Job Uncache(){
		return UncacheMin();

	}
	public void Cache(Job job){
		count++;

		incrUsed(job.JobSize());

		// update stats for newly cached job
		double crf = F(0);
		job.crf = crf;
		job.last = count;

		pq.EnqueueJob(crf, job);
	}
	public String Name(){
		return "LRFU";
	}

	double F(int x){
		return Math.pow(0.5,(0.5 * x));

	}

}
