package acmedb.adaptation;
import acmedb.adaptation.Job;

public class Node {
	private Job job;
	public double priority;
	public Node next,prev;
	public Node(double priority, Job job){
		this.priority = priority;
		this.job = job;
	}
	public Job getJob(){
		return this.job;		
	}

}
