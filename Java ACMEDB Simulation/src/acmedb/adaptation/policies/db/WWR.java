package acmedb.adaptation.policies.db;

import acmedb.adaptation.Algorithm;
import acmedb.adaptation.Job;
import acmedb.adaptation.PriorityQueue;

public class WWR extends Algorithm{
	public WWR(){
		WaitRoom = new PriorityQueue();
		WeighRoom = new PriorityQueue();
	}
	public WWR(int cachesize){
		WaitRoom = new PriorityQueue();
		WeighRoom = new PriorityQueue();
		WeighRoomSize = (int) (0.9 * cachesize);
		WaitRoomSize = cachesize - WeighRoomSize;

	}
	public void incrHit(int id, double size){
		count++;
		hit++;
		byteHit += size;

		if (FindInWeighRoom(id) != null) {
			AddToWeighRoom(RemoveFromWeighRoom(id));
			Prefetch(id + 1);
		}
		else {
			if (FindInWaitRoom(id) != null) {
				AddToWeighRoom(RemoveFromWaitRoom(id));
				Prefetch(id + 1);
			}
		}

	}
	public Job Uncache(){
		return WeighRoom.DequeueFront();
	}
	public void Cache(Job job){
		count++;

		// uncache?? 
				// need to check size of buffer??
		AddToWeighRoom(job);
		Prefetch(job.JobID() + 1);

		incrUsed(job.JobSize());


	}
	public String Name(){
		return "WWR";
	}

	//public void PrintBuffer(ofstream outfile, const char * policyname, int id);
	PriorityQueue WeighRoom;
	PriorityQueue WaitRoom;
	int WeighRoomSize, WaitRoomSize;

	public Job getJob(int id){
		Job  retJob = null;

		retJob = WeighRoom.getJob(id);
		if (retJob == null) {
			retJob = WaitRoom.getJob(id);
		}
		return retJob;

	}
	public Job release(int id){
		// release a job that is either in Am or A1in

		if (WeighRoom.getJob(id) != null) {
			return WeighRoom.DequeueJob(id);
		}
		else {
			if (WaitRoom.getJob(id) != null) {
				return WaitRoom.DequeueJob(id);
			}
			else {
				// ERROR!
				// job is neither in weighing room nor waiting room, so why are we here???!
				assert(false);
				return null;
			}
		}

	}

	/* Weighing Room methods */
	public void AddToWeighRoom(Job job){
		WeighRoom.EnqueueJob(0,job);

	}
	public Job RemoveFromWeighRoom(int id){
		return WeighRoom.DequeueJob(id);


	}
	public Job FindInWeighRoom(int id){
		return WeighRoom.getJob(id);

	}

	/* Waiting Room methods */
	public void AddToWaitRoom(Job job){
		WaitRoom.EnqueueJob(0, job);
	}
	public Job RemoveFromWaitRoom(int id) {
		return WaitRoom.DequeueJob(id);
	}
	public Job FindInWaitRoom(int id) {
		return WaitRoom.getJob(id);
	}
	public Job Prefetch(int id) {
		if ((FindInWeighRoom(id) != null) && (FindInWaitRoom(id) != null)) {
			Job PrefetchedJob = new Job(id, 0);

			// need to check size of waiting room, and remove the front if exceeded first
			if (WaitRoom.getLength() >= WaitRoomSize) {
				WaitRoom.DequeueFront();
			}
			AddToWaitRoom(PrefetchedJob);
			return PrefetchedJob;
		}
		else {
			return null;
		}

	}

	public void Request(int id) {
		if (FindInWeighRoom(id) != null) {
			AddToWeighRoom(RemoveFromWeighRoom(id));
			Prefetch(id + 1);
		}
		else {
			if (FindInWaitRoom(id) != null) {
				AddToWeighRoom(RemoveFromWaitRoom(id));
				Prefetch(id + 1);
			}
			else {
				Job FromDisk = new Job(id, 0);

				AddToWeighRoom(FromDisk);
				Prefetch(id + 1);
			}
		}

	}
}