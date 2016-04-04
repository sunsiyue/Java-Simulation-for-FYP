package acmedb.adaptation.game;
import java.io.*;

import acmedb.adaptation.*;
/*
 * FixedGame represents static policies. There is only 
 * one cache and one policy in this game.
 * We test all policies at once, but the policies are 
 * not interacting yet
 */
public abstract class FixedGame extends Game{
	private int[] freespace;
	public FixedGame(int level, int numPolicies, int cacheSize) {
		super(level, cacheSize);
		freespace = new int[numPolicies];
		for (int i=0;i<numPolicies;i++){
			freespace[i]=this.csize;
		}
	}
	
	public Job get_request(int id, int size, int l, int count, double bytecount, int numpolicies) throws IOException{
		int found = 0;
		Job jj = null;
		//if object size > cache size;
		if (csize<size) return null;
		// loop through all policies
		for (int i = 0;i< numpolicies;i++){
			//try to retrieve from the cache of current policy
			jj = policies[l][i].getJob(id);
			if (jj != null) { //found object
				assert (jj!= null);
				policies[l][i].incrHit(id,size);
				found = 1;
				policies[l][i].incrHitHistories(count,histlen,size);
			}
			if (found == 1) { //not found
				//make space for the object (not needed for db version)
				while (freespace[i]<size){
					Job thisJob = policies[0][i].Uncache();
					freespace[i] += thisJob.JobSize();
					thisJob = null;
				}
			}
			
			//create the new job
			jj = new Job(id, size);
			assert (jj!= null);
			policies[l][i].Cache(jj);
			freespace[i]-= size;
		}
		if (count % 1000 == 0){
			history(count,bytecount,l, numpolicies);
		}
		return jj;
	}

}
