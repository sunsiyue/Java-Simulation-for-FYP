package acmedb.adaptation.policies.general;
import acmedb.adaptation.*;

public class LRU extends Algorithm{

	public void Cache(Job job) {
		count++;
		pq.EnqueueJob(count, job);
		incrUsed(job.JobSize());
	}

	public Job Uncache() {
		return UncacheMin();
	}

	public void incrHit(int id, double size) {
		this.hit++;
		this.byteHit+= size;
		this.count++;
		this.pq.adjustPriority(id, this.count);
	}

	public String Name() {
		return "LRU";
	}
	
}
