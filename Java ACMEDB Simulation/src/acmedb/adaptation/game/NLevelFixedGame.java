package acmedb.adaptation.game;

import java.io.IOException;

import acmedb.adaptation.Job;

/*
 * NLevelFixedGame: we have N linearly connected caches
 * Each level has a fixed policy. This game is used to test
 * heterogeneous caches  
 */
public abstract class NLevelFixedGame extends Game{
	private int[] freespace;
	private int demote;
	private double demotedBytes;
	public NLevelFixedGame(int level, int numPolicies, int cacheSize) {
		super(level, cacheSize);
		freespace = new int[numPolicies];
		for (int i=0;i<level;i++){
			freespace[i] = csize;
			lastByteCount[i] = 0;
		}
		demotedBytes = 0;
	}

	public Job get_request(int id, int size, int l, int count, double bytecount, int numPolicies) throws IOException{
		int found = 0;
		Job jj = null;
		
		if (size > csize) return null;
		for (int i = 0; i< numPolicies; i++){
			jj = policies[l][i].getJob(id);
			if (jj!= null){
				assert (jj!=null);
				policies[l][i].incrHit(id, size);
				found = 1;
				policies[l][i].incrHitHistories(count, histlen, size);
			}
			if (found == 0){
				if (l+1 < level){
					jj = get_request(id, size, l+1, count, bytecount, numPolicies);
				}
				while (freespace[l] < size){
					Job thisJob = policies[l][i].Uncache();
					freespace[l]+= thisJob.JobSize();
					if ((demote!=0) && (l+1<level)){
						while (freespace[l+1] < thisJob.JobSize()){
							Job thatJob = policies[l+1][i].Uncache();
							freespace[l+1] += thatJob.JobSize();
							thatJob = null;
						}
						policies[l+1][i].Cache(thisJob);
						demotedBytes +=thisJob.JobSize();
						freespace[l+1]-=thisJob.JobSize();
					}
				}
				jj = new Job(id,size);
				policies[l][i].Cache(jj);
				freespace[l]-=size;
			}
		}
		if (count%1000 == 0){
			history(count,bytecount,l, numPolicies);
		}
		return jj;
	}
}
