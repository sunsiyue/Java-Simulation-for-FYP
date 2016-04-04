package acmedb.adaptation.policies.general;
import acmedb.adaptation.Algorithm;
import acmedb.adaptation.Job;

public class Random extends Algorithm{

	public void Cache(Job job) {
		count++;
		pq.EnqueueJob(count, job);
		incrUsed(job.JobSize());
	}

	@Override
	public Job Uncache() {
		Job job = null;
		if (getUsed()<=0) return null;
		int draw = (int) (Math.random()*pq.getLength());
		job = pq.getJobAtPos(draw);
		pq.DequeueJob(job.JobID());
		decrUsed(job.JobSize());
		return job;
	}

	public String Name() {
		return "RANDOM";
	}

	public void incrHit(int id, double size) {
		this.hit++;
		this.byteHit+= size;
		this.count++;
	}

}
