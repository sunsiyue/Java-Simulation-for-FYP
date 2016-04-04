package acmedb.adaptation.policies.general;
import acmedb.adaptation.*;

public class MRU extends Algorithm {

	public void Cache(Job job) {
		count++;
		pq.EnqueueJob(count, job);
		incrUsed(job.JobSize());
	}

	public Job Uncache() {
		return UncacheMax();
	}

	@Override
	public void incrHit(int id, double size) {
		count++;
		hit++;
		byteHit += size;
		pq.adjustPriority(id, this.count);
		
	}

	public String Name() {
		return "MRU";
	}

}
