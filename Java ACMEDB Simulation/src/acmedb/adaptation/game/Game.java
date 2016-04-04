package acmedb.adaptation.game;

import java.io.*;

import acmedb.adaptation.Algorithm;
import acmedb.adaptation.Job;
import acmedb.adaptation.policies.*;
import acmedb.adaptation.policies.general.LRU;
import acmedb.adaptation.policies.general.MRU;
import acmedb.adaptation.policies.general.Random;
/*
 * Game represents different adaptive caching design.
 * So for each game, it defines the rules whereby the algorithms 
 * would interact and collaborate with each other.
 * All adaptive designs will inherit from the abstract class
 * Game, and builds their own sets of rules by extending and
 * modifying the get_request() method.
 * 
 * General rules: we search for an object in all policy queues.
 * If we get a hit, we update the statistics for the object. We 
 * thus make space for missed and retrieved object by replacing
 * other objects in the cache based on the replacement policy
 */
public abstract class Game {

	public Game(int l, int cacheSize){
		this.level = l;
		this.csize = cacheSize;
		this.policies = new Algorithm[level][];
		this.histlen = 1000;
		this.lastByteCount = new double[level];
		for (int i =0; i<level; i++){
			lastByteCount[i] = 0; 
		}
		
	}
	public void allocPolicies(int lev, int numPolicies){
		policies[lev] = new Algorithm[numPolicies];
	}
	
	public Algorithm[][] get_policies(){
		return policies;
	}
	public void initFreeSpace(int i,int j){
		
	}
	public void registerAll(int lev){
		  	Random rand = new Random();
			LRU lru = new LRU();
			MRU mru = new MRU();
			
			policies[lev][0] = rand;
			policies[lev][1] = lru;
			policies[lev][2] = mru; 
	}
	public void registerx(int lev, int pos, Algorithm algo){
		policies[lev][pos] = algo;	
	}
	public void print(int count, double bytecount, int numpolicies){
		if ( count%1000 == 0){
			for(int i=0;i< numpolicies;i++)
				System.err.println("p["+i+"]="+((double) (policies[0][i].getHit() / count)) * 100 );  
			System.err.println("i="+count);
			System.err.flush();
		}
	}
	public void log(int count, double bytecount, int numpolicies){
		//print out the hit rate and byte hit rate at the end of the simulation
		for (int l=0;l<level;l++){
			int totalhits = 0;
			double totalhr = 0;
			for (int i=0;i<numpolicies;i++){
				policies[l][i].setHitRates(policies[l][i].getHit()/count*100,
						policies[l][i].getBytehit()/bytecount*100);
				System.out.println("---------------------------");
				System.out.println("Hits = "+ policies[l][i].getHit());
				System.out.println("HR = "+ policies[l][i].getHitRate());
				totalhits += policies[l][i].getHit();
				totalhr += policies[l][i].getHitRate();
			}
			System.out.println("TotalHR = "+totalhr+"  Total Hits = "+ totalhits);
		}
	}
	public void stats(int count, double bytecount, int l, int numpolicies, int log_interval)throws IOException{
		assert (count >= 0);
		assert(bytecount >= 0);

		f[0][l].write( count/log_interval + "\t");
		for(int i=0;i<numpolicies;i++) {
			double val = (double)policies[l][i].getHit()/count*100;
			f[0][l].write(val+"\t");
		}
	}
	
	public abstract Job get_request(int id, int size, int l, int count, double bytecount, int numPolicies) throws IOException;
	public void history(int count, double bytecount, int l, int numpolicies) throws IOException{
		assert (count >= 0);
		assert(bytecount >= 0);

		f[0][l].write(count/1000+" ");
		for(int i=0;i<numpolicies;i++) {
			double hist = (double)policies[l][i].getHitHistory()/histlen*100;
			f[0][l].write(hist +" ");
		}
		f[0][l].write("\n");

		f[1][l].write(count/1000 + " ");
		for(int j=0;j<numpolicies;j++) {
			 double delta = bytecount-getLastByteCount(l);
			assert( delta > 0);
			 double bytehist = (( double)policies[l][j].getByteHistory()/delta)*100;
			assert(bytehist <=100);
			f[1][l].write(bytehist + " ");
			policies[l][j].clearHitHistories();
		}
		f[1][l].write("\n");
		setLastByteCount(bytecount, l);
	}
	
	private void setLastByteCount(double val, int level) {
		lastByteCount[level]=val;
		
	}
	private double getLastByteCount(int level) {
		 return lastByteCount[level]; 
	}
	
	public void setLogFiles(int numpolicies, String filename1, String filename2, String outrcvictimname) throws IOException{
		outRCVictim = new BufferedWriter(new FileWriter(outrcvictimname));

		  //outRCVictim.write( "CLOCKS PER SECOND: " + CLOCKS_PER_SEC +"\n");
		  outRCVictim.write("Request\tTime Diff (ms)\n") ;
		  f = new BufferedWriter[2][2];
		  for( int i =0; i<level; i++) {
		    if (i==0) {
		      f[0][i] = new BufferedWriter(new FileWriter(filename1));
		    }
		    else {
		      f[0][i] = new BufferedWriter(new FileWriter(filename2));
		    }

		    f[0][i].write( "Request");
			  for(int j=0;j<numpolicies;j++) {
				  f[0][i].write("\t" + policies[i][j].Name());
			  }
			  f[0][i].write("\tRealCache\n");
			}
	}

	protected Algorithm[][] policies;
	protected int level;
	protected int csize;
	protected double[] lastByteCount;
	protected int histlen;
	public BufferedWriter[][] f;
	public BufferedWriter outRCVictim;//Real Cache Victim
	/*private int[] freespace;*/
	
	public void closeFile() throws IOException{
		outRCVictim.close();
		f[0][0].close();
		
	}
	
}
