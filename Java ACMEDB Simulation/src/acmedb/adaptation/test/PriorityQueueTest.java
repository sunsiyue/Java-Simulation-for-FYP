package acmedb.adaptation.test;
import acmedb.adaptation.*;

public class PriorityQueueTest {
	public static void main(String args[]){
		PriorityQueue pq = new PriorityQueue();
		Job job = new Job(1, 1);
		pq.EnqueueJob(1.3, job);
		job = new Job(2,3);
		pq.EnqueueJob(1.6, job);
		pq.DequeueFront();
	}
}
