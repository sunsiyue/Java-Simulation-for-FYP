package acmedb.adaptation;

public abstract class Algorithm {
	public double hit;
	public double byteHit;
	public int used;
	public double ratio;
	public double hitrate;
	public double byteHitRate;
	public double hithist;
	public double bytehist;
	public PriorityQueue pq;
	public double totalprio;
	public int count;
	
	public Algorithm (){
		this.hit = 0;
		this.byteHit = 0;
		this.used = 0;
		this.ratio = 0;
		this.pq = new PriorityQueue();
		this.totalprio = 0;
		this.count = 0;
	}
	public Algorithm (int i){
	}
	public Job getJob(int id){
		return this.pq.getJob(id);
	}
	public double getHit(){
		return this.hit;
	}
	public double getBytehit(){
		return this.byteHit;
	}
	public int getUsed(){
		return this.used;
	}
	public double getRatio(){
		return this.ratio;
	}
	
	public double getHitRate(){
		return this.hitrate;
	}
	
	public double getByteHitRate(){
		return this.byteHitRate;
	}
	public double getHitHistory(){
		return this.hithist;
	}
	public double getByteHistory(){
		return this.bytehist;
	}
	public void incrUsed(int amount){
		this.used+= amount;
	}
	public void decrUsed(int amount){
		this.used-= amount;
	}
	
	public abstract void Cache(Job job);
	public abstract Job Uncache();
	
	public Job UncacheMin(){
		if (getUsed()<=0) return null;
		Job job = pq.getMin();
		pq.DequeueJob(job.JobID());
		decrUsed(job.JobSize());
		return job;
	}
	public Job UncacheMax(){
		if(getUsed() <= 0) return null;
		Job job = pq.getMax();
		pq.DequeueJob(job.JobID());
		decrUsed(job.JobSize());
		return job;	
	}
	
	public Job release(int id){
		assert (this.getUsed()>0);
		Job j = pq.DequeueJob(id);
		decrUsed(j.JobSize());
		return j;
	}
	
	public PriorityQueue getPQ(){
		return this.pq;
	}
	public abstract void incrHit(int id, double size);
	public abstract String Name();
	public void setHitRates(double hr, double bhr) {
		hitrate = hr;
		byteHitRate = bhr;
	}
	public void incrHitHistories(int count2, int histlen, int size) {
		hithist++;
		bytehist += size;
		
	}
	public void clearHitHistories() {
		hithist = 0;
		bytehist = 0;		
	}
	
}
