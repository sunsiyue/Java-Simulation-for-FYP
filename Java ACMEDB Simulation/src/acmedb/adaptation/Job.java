package acmedb.adaptation;

/* simulate a workload for the database*/
public class Job {
	private int jobId;
	private int size;
	private int freq = 1;
	/*constructor*/
	public Job(int jobId,int size){
		this.jobId = jobId;
		this.size = size;
	}
	/*getter methods*/
	public int JobID(){
		return this.jobId;
	}
	public int JobSize(){
		return this.size;
	}
	public int JobFreq(){
		return this.freq;
	}
	/* timestamp, stored as a long*/
	public long time;
	public boolean isLIR;
	public boolean isResident;
	public double crf;
	public int last;
	public int[] k = new int[2];
	public int[] k_time = new int[2];
	public void incFreq() {
		freq ++;
		
	}
}
