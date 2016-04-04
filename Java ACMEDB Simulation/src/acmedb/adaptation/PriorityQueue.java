package acmedb.adaptation;
import java.io.BufferedWriter;
import java.io.IOException;

import acmedb.adaptation.Job;
import acmedb.adaptation.Node;

/*
 * Priority queue with a linked list implementation.
 */
public class PriorityQueue {	
	private int length;
	public Node jobList;
	public Node joblist_start;
	
	public PriorityQueue(){
		this.jobList = null;
		this.length = 0;
		this.joblist_start = jobList;
	}
	
	public void EnqueueJob(double priority,Job job){
		assert (job!= null);
		this.length++;
		Node node = new Node(priority, job);
		node.next = jobList;
		if (jobList != null){
			jobList.prev = node;
		}
		jobList = node;
		if (jobList.next == null){
			joblist_start = jobList; 
		}
		assert (jobList != null);
		assert (joblist_start != null);
		
	}
	public Job DequeueFront(){
		Node curr = jobList;
		Job returnedJob = null;
		assert (jobList!=null);
		if (curr == joblist_start){
			jobList = null;
			joblist_start = jobList;
		}
		else
		{
			curr = joblist_start;
			joblist_start = joblist_start.prev;
			joblist_start.next.prev = null;
			joblist_start.next = null;
		}
		returnedJob = curr.getJob();
		length --;
		/* remove data for garbage collection */
		curr = null;
		assert (returnedJob!=null);
		return returnedJob;
	}
	
	public Job DequeueJob(int jobId){
		Node curr = jobList;
		assert (jobList!=null);
		Job retJob = null;
		/* add check if jobID exists*/
		while (curr!=null){
			if (jobId == curr.getJob().JobID()){
				
				if (curr.prev == null){
					jobList = curr.next;
					if (jobList!=null){
						jobList.prev = null;
					}
				}
				else
				{
					curr.prev.next = curr.next;
					if (curr.next == null){
						joblist_start = curr.prev;
					}
					else
					{
						curr.next.prev = curr.prev;
					}
				}
				retJob = curr.getJob();
				curr = null;
				length--;
				break;
			}
			curr = curr.next;
		}
		assert (retJob != null);
		return retJob;
	}
	public void adjustPriority(int jobId, double priority){
		Node curr = jobList;
		while (curr!=null){
			if (jobId == curr.getJob().JobID()){
				curr.priority = priority;
				break;
			}
			curr = curr.next;
		}
		assert (curr!=null);
	}
	public double getPriority(int jobId){
		Node curr = jobList;
		while (curr!=null){
			if (jobId == curr.getJob().JobID()){
				return curr.priority;
			}
			curr = curr.next;
		}
		return 0;
	}
	public void incPriority(int jobId){
		Node curr = jobList;
		while (curr!=null){
			if (jobId ==curr.getJob().JobID()){
				curr.priority++;
			}
			curr = curr.next;
		}
		assert (curr!=null);
	}
	public void decPriority(int jobId){
		Node curr = jobList;
		while (curr!=null){
			if (jobId ==curr.getJob().JobID()){
				curr.priority--;
			}
			curr = curr.next;
		}
		assert (curr!=null);
	}
	public Job getMin(){
		Node curr = jobList;
		assert (curr!=null);
		double min = curr.priority;
		Job minJob = curr.getJob();
		curr = curr.next;
		while (curr!=null){
			if (curr.priority < min){
				minJob = curr.getJob();
				min = curr.priority;
			}
			curr = curr.next;
		}
		assert (minJob!= null);
		return minJob;
	}
	public Job getMax(){
		Node curr = jobList;
		assert (curr!=null);
		double max = curr.priority;
		Job maxJob = curr.getJob();
		curr = curr.next;
		while (curr!=null){
			if (curr.priority > max){
				maxJob = curr.getJob();
				max = curr.priority;
			}
			curr = curr.next;
		}
		assert (maxJob!= null);
		return maxJob;
	}
	public double maxPriority(){
		Node curr = jobList;
		assert (curr!=null);
		double max = curr.priority;
		curr = curr.next;
		while (curr!=null){
			if (curr.priority > max){
				max = curr.priority;
			}
			curr = curr.next;
		}
		return max;
	}
	public double minPriority(){
		Node curr = jobList;
		assert (curr!=null);
		double min = curr.priority;
		curr = curr.next;
		while (curr!=null){
			if (curr.priority < min){
				min = curr.priority;
			}
			curr = curr.next;
		}
		return min;
	}
	public Job getJob(int jobId){
		Node curr = jobList;
		while(curr!=null) {
			if(curr.getJob().JobID() == jobId) {
				return curr.getJob();
			}
			curr = curr.next;
		} 
		return null;
	}
	public Job getJobAtPos(int pos){
		assert(pos<=length);
		Node curr = jobList;
		if ( pos == 0 ) {
			return curr.getJob();
		}
		for (int i = 1; i<=pos; i++) {
			curr = curr.next;
		}
		return curr.getJob();
	}
	public int getLength(){
		return this.length;
	}
	public Job getFront(){
		return joblist_start.getJob();
	}
	public Job getEnd(){
		return jobList.getJob();
	}
	public Job getTop(){
		return jobList.getJob();
	}
	public Job getBottom(){
		return joblist_start.getJob();
	}
	
	//print methods for debugging
	public void printQueue()
	{
		Node curr = jobList;
		while(curr!= null) {
			System.out.print("[" + curr.getJob().JobID() + " - " +curr.priority + "] ");
			curr = curr.next;
		}
		System.out.println();
	}

	public void printQueue(BufferedWriter outfile) throws IOException
	{
		Node curr = jobList;
		while(curr!= null) {
			outfile.write(curr.getJob().JobID() + "," + curr.priority + "," + curr.getJob() + "," + curr.getJob().isLIR +"\n");
			curr = curr.next;
		}
	}

}
