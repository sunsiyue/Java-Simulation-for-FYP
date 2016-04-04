package acmedb.adaptation.game;
import java.io.*;

import acmedb.adaptation.*;

/*
 * Final game with Machine Learning capability
 */

public class ShareGame extends NLevelFixedGame{
	public static final double ETA = 0.5;
	public Job get_request(int id, int size, int l, int count,  double bytecount, int numpolicies) throws IOException{
		int rfound =0;
		int vfound =0;
		Job vjj = null;
		@SuppressWarnings("unused")
		int log_interval = 1000;

		Job rjj = null;

		rjj = realcache[l][0].getJob(id);
		if (rjj!= null) {
			rfound = 1;
			for(int i=0; i<numpolicies; i++) {
				realcache[l][i].incrHit(id, size);
			}
			realhits[l]++; realbytes[l]+= size;
		}
		// Ask the next level if the REAL CACHE MISSES
		if(rfound == 0) {
			// cout << "miss level " << l <<endl;

			if ( l+1 < level ) {
				rjj = get_request(id, size, l+1, count, bytecount, numpolicies);
			}
			// otherwise, find some space!
			if (realfree[l] < size) {
				/*clock_t start, finish;
				start = clock();
*/
				int which = w[l].sample();
				assert(which >= 0);
				// cout << "Policy " << which << " Used " << realcache[l][which]->getUsed() <<endl;
				//outRCVictim.write("Policy " + which + " used " + realcache[l][which].getUsed()+"\n");
				Job thatJob = realcache[l][which].Uncache();
				outRCVictim.write("Uncached " + thatJob.JobID() + " with policy "+ realcache[l][which].Name()+"\n");
				assert(thatJob != null);
				int id2 = thatJob.JobID();
				realfree[l] += thatJob.JobSize();

				// clean up the rest of the policies
				for(int i=0; i<numpolicies; i++) {
					if(i == which) continue;
					realcache[l][i].release(id2);
				}
				// delete thatJob;

				/*finish = clock();*/
				//outRCVictim.write( count + "\t\n") ;

			}
			if ( l+1 == level) {
				assert(rjj == null);
				rjj = new Job(id, size);
			}
			for(int j=0; j<numpolicies; j++) {
				realcache[l][j].Cache(rjj);
			}
			realfree[l] -= size;
			assert(realfree[l] >= 0);
		}
		// Update Virtuals
		for(int i=0; i<numpolicies; i++) {
/*			policies[l][i].pq.printQueue();*/
			vjj = policies[l][i].getJob(id);
			if(vjj!=null) {

				policies[l][i].incrHit(id, size); // log(size));
				vfound=1;
				loss[l][i] = 0;
			}
			// added the following else, as weights weren't being correctly updated ???
			else {
				vfound = 0;
			}
			if(vfound == 0) {

				loss[l][i] = 1;
				if (virtfree[l][i] < size) {
					Job thisJob = policies[l][i].Uncache();
					virtfree[l][i] += thisJob.JobSize();
					thisJob = null;
				}

				vjj = new Job(id, size);
				policies[l][i].Cache(vjj);
				virtfree[l][i] -= size;
				assert(virtfree[l][i] >= 0 && virtfree[l][i] <= csize); 

			}

		}

		// loss update (STATIC-EXPERT)

/*		for (int j=0; j<numpolicies;j++) 
			System.out.print(loss[l][j]+" ");
		System.out.println();*/
		for(int j=0; j<numpolicies; j++) {
			//System.out.println(w[l].getProb(j)+" "+w[l].getProb(j)*Math.exp(-ETA*loss[l][j])+" "+loss[l][j]);
			w[l].setProb(j, w[l].getProb(j)*Math.exp(-ETA*loss[l][j]));
			// WEIGHTED MAJORITY ALGORITHM
			// if(loss[l][i] != 0)  w[l]->setProb(i+1, w[l]->getProb(i+1)*BETA);
		}
		w[l].normalizeProb();
		// w[l]->normalizeProb();
		// not needed nere, just wastes time

		// share update (FIXED-SHARE)
		// double pool = 0.0;		
/*		for (int i = 0;i<numpolicies;i++){
			System.out.print("["+policies[l][i].Name()+" - " + w[l].getProb(i)+ " ] ");
		}
		System.out.println();*/
		return rjj;

	}
	public void PrintStats(BufferedWriter[] outfile, int buffer_size, int count, int numpolicies) throws IOException{
		String temp;

		  for (int i=0; i<level; i++) {
		    outfile[i].write( buffer_size);
		    for (int j=0; j<numpolicies; j++) {
		      
		      temp= String.format( "%-3.2f", ((double) (policies[i][j].getHit() / count)) * 100);
		      outfile[i].write("\t" + temp);

		    }
		    temp = String.format("%-3.2f", ((double) (realcache[i][0].getHit() / count)) * 100);
		    outfile[i].write("\t" + temp +"\n");
		  }
		
	}
	public void DebugCall(){
		System.out.println(policies+"\n");
	}
	
	public void stats(int count, double bytecount, int l, int numpolicies, int log_interval)throws IOException{
		String temp;

		assert (count >= 0);
		assert(bytecount >= 0);

		f[0][l].write(count/log_interval + "\t");
		for(int i=0;i<numpolicies;i++) {
			double val = (double)policies[l][i].getHit()/count*100;
	    temp = String.format("%-3.2f", val);
			f[0][l].write( temp + "\t");
		}
	  temp = String.format("%-3.2f", (double)realcache[l][0].getHit()/count*100);
		f[0][l].write( temp + "\n");
	}
	public double[] realhits;

	int[][] virtfree;
	int[] realfree;
	Distribution[] w;
	Algorithm[][] realcache;
	double[][] loss;
	double[] realbytes;
	
	public ShareGame(int level, int numPolicies, int cacheSize) {
		super(level, numPolicies, cacheSize);
		realcache = new Algorithm[level][];
		virtfree= new int[level][];
		realfree= new int[level];
		w = new Distribution[level];
		loss = new double[level][];

		for(int i =0; i< level;i++){
			realcache[i] = new Algorithm[numPolicies];
			virtfree[i] = new int[numPolicies];
			realfree[i] = csize;
			w[i] = new Distribution(numPolicies);
			loss[i] = new double[numPolicies];
			for(int j=0; j<numPolicies; j++) {
				virtfree[i][j] = csize;
			}
		}
		realhits = new double[level];
		realbytes = new double[level];
		for ( int lev=0; lev<level; lev++) {
			realhits[lev] = 0; realbytes[lev] = 0;
		}
	}
	
	public void registerReal(int lev, int pos, Algorithm algo){
		realcache[lev][pos] = algo;
	}
	
	public void printCurr(int numPolicies){
		for (int i=0;i<numPolicies;i++){
			System.out.println(realcache[0][i].Name());
			//realcache[0][i].pq.printQueue();
			policies[0][i].pq.printQueue();
		}
	}

}
