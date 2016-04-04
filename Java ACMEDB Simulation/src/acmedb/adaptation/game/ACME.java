package acmedb.adaptation.game;
import acmedb.adaptation.*;
/*
 * Voting game based on the ACME paper.
 */
public abstract class ACME extends NLevelFixedGame{

	int[][] virtfree;
	int[] realfree;
	Distribution[][] w;
	Algorithm[][] realcache;
	double[][] loss;
	double[] realhits;
	double[] realbytes;
	
	public ACME(int level,int numPolicies, int cacheSize) {
		super(level, numPolicies, cacheSize);
		realcache = new Algorithm[level][numPolicies];
		virtfree = new int[level][numPolicies];
		realfree = new int[level];
		w = new Distribution[level][numPolicies];
		loss = new double[level][numPolicies];
		for (int i = 0;i<level;i++){
			realfree[i] = csize;
			for (int j =0;j<numPolicies;j++){
				virtfree[i][j] = csize;
			}
		realhits = new double[level];
		realbytes = new double[level];
		realhits[0] = 0;
		realbytes[0] = 0;
		//write to file. to be implemented
		}
	}
	
	public void registerReal(int lev, Algorithm[] algo) {
		realcache[lev] = algo;
	}
	

}
