package acmedb.adaptation;

import java.io.*;

import acmedb.adaptation.game.ShareGame;
import acmedb.adaptation.policies.db.*;
import acmedb.adaptation.policies.general.*;

public class acmedbRun {
	public static final int numPolicies = 1;
	public static final String parfile = "parfile";
	public static final String infile = "input";
	public static final String outfile = "output";
	public static final int CACHESIZE = 1000;
	public static final int LEVEL = 1; 			//single level cache
	public static final int NUMPOLICIES = 1;   // numbers of policies used
	public static final int NUMEXPERTS = NUMPOLICIES * LEVEL; 	
	public static final int TOTAL_POLICY_COUNT = 14; //numbers of policies implemented;
	/* 8 general caching policies */
	public static final int use_rand = 0;
	public static final int use_lru = 1;
	public static final int use_mru = 0;
	public static final int use_lfu = 0;
	public static final int use_mfu = 0;
	public static final int use_fifo = 0;
	public static final int use_lifo = 0;
	public static final int use_lfuda = 0;
	/* unimplemented
	 * database-specific caching methods
	 */
	public static final int use_lruk = 0;
	public static final int use_sfifo = 0;
	public static final int use_twoq = 0;
	public static final int use_wwr = 0;
	public static final int use_lirs = 0;
	public static final int use_lrfu = 0;

	private static final String TRACE_FILE = "input";
	private static final String TEST_SET = "input-set";



	public static void registerSet(int l,ShareGame game, Algorithm[] policies, Algorithm[] realcache){
		for(int i=0; i<numPolicies; i++) {
			game.registerx(l, i, policies[l*numPolicies+i]);
			game.registerReal(l, i, realcache[l*numPolicies+i]);
		}
	}

	public static void main (String[] args) {
		long startTime = System.nanoTime();
		//myCall();


		try{
			BufferedReader parFile = new BufferedReader(new FileReader(parfile));
			BufferedWriter[] outFile = new BufferedWriter[2];
			outFile[0] = new BufferedWriter(new FileWriter(outfile+".1"));
			if (LEVEL == 2)
				outFile[1] = new BufferedWriter(new FileWriter(outfile+".2"));

			int cachesize = CACHESIZE;
			boolean started = false;
			String line;
			while(((line=parFile.readLine())!=null) && ((cachesize=Integer.parseInt(line))!=0)){
				int count =0;
				double bytecount =0;
				BufferedReader reader = new BufferedReader(new FileReader(infile));

				ShareGame game = new ShareGame(LEVEL,NUMPOLICIES, cachesize);
				Algorithm[] policies = new Algorithm[NUMEXPERTS];
				Algorithm[] realcache = new Algorithm[NUMEXPERTS];
				/* Round 1 */
				Random rand = new Random(); Random r_rand = new Random();
				LRU lru = new LRU();  LRU r_lru = new LRU();
				MRU mru = new MRU();  MRU r_mru = new MRU();
				LFU lfu = new LFU();  LFU r_lfu = new LFU();
				MFU mfu = new MFU();  MFU r_mfu = new MFU();
				FIFO fifo = new FIFO();  FIFO r_fifo = new FIFO();
				LIFO lifo = new LIFO();  LIFO r_lifo = new LIFO();
				LFUDA lfuda = new LFUDA();  LFUDA r_lfuda = new LFUDA();
				//database specific policies;
				LRFU lrfu = new LRFU(); LRFU r_lrfu = new LRFU();
				LIRS lirs = new LIRS(cachesize); LIRS r_lirs = new LIRS(cachesize);
				LRUK lruk = new LRUK(); LRUK r_lruk = new LRUK();
				SFIFO sfifo = new SFIFO(cachesize); SFIFO r_sfifo = new SFIFO(cachesize);
				TWOQ twoq = new TWOQ(cachesize); TWOQ r_twoq = new TWOQ(cachesize);
				WWR wwr = new WWR(cachesize); WWR r_wwr = new WWR(cachesize);

				/* Round 2 */
				Random rand2 = new Random(); Random r_rand2 = new Random();
				LRU lru2 = new LRU();  LRU r_lru2 = new LRU();
				MRU mru2 = new MRU();  MRU r_mru2 = new MRU();
				LFU lfu2 = new LFU();  LFU r_lfu2 = new LFU();
				MFU mfu2 = new MFU();  MFU r_mfu2 = new MFU();
				FIFO fifo2 = new FIFO();  FIFO r_fifo2 = new FIFO();
				LIFO lifo2 = new LIFO();  LIFO r_lifo2 = new LIFO();
				LFUDA lfuda2 = new LFUDA();  LFUDA r_lfuda2 = new LFUDA();
				//database specific policies;
				LRFU lrfu2 = new LRFU(); LRFU r_lrfu2 = new LRFU();
				LIRS lirs2 = new LIRS(cachesize); LIRS r_lirs2 = new LIRS(cachesize);
				LRUK lruk2 = new LRUK(); LRUK r_lruk2 = new LRUK();
				SFIFO sfifo2 = new SFIFO(cachesize); SFIFO r_sfifo2 = new SFIFO(cachesize);
				TWOQ twoq2 = new TWOQ(cachesize); TWOQ r_twoq2 = new TWOQ(cachesize);
				WWR wwr2 = new WWR(cachesize); WWR r_wwr2 = new WWR(cachesize);

				int exp_count = 0; // experts count
				for (int exp = 0; exp <TOTAL_POLICY_COUNT; exp++){
					switch (exp){
					case 0:
						if (use_rand == 1){
							policies[exp_count] = rand;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = rand2;
							realcache[exp_count] = r_rand;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_rand2;
							exp_count++;
						}
						break;
					case 1:
						if (use_lru == 1){
							policies[exp_count] = lru;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = lru2;
							realcache[exp_count] = r_lru;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_lru2;
							exp_count++;
						}
						break;
					case 2:
						if (use_mru == 1){
							policies[exp_count] = mru;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = mru2;
							realcache[exp_count] = r_mru;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_mru2;
							exp_count++;
						}
						break;
					case 3:
						if (use_lfu == 1){
							policies[exp_count] = lfu;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = lfu2;
							realcache[exp_count] = r_lfu;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_lfu2;
							exp_count++;
						}
						break;

					case 4:
						if (use_mfu == 1){
							policies[exp_count] = mfu;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = mfu2;
							realcache[exp_count] = r_mfu;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_mfu2;
							exp_count++;
						}
						break;
					case 5:
						if (use_fifo == 1){
							policies[exp_count] = fifo;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = fifo2;
							realcache[exp_count] = r_fifo;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_fifo2;
							exp_count++;
						}
						break;
					case 6:
						if (use_lifo == 1){
							policies[exp_count] = lifo;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = lifo2;
							realcache[exp_count] = r_lifo;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_lifo2;
							exp_count++;
						}
						break;
					case 7:
						if (use_lfuda == 1){
							policies[exp_count] = lfuda;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = lfuda2;
							realcache[exp_count] = r_lfuda;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_lfuda2;
							exp_count++;
						}
						break;
					case 8:
						if (use_lirs == 1){
							policies[exp_count] = lirs;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = lirs2;
							realcache[exp_count] = r_lirs;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_lirs2;
							exp_count++;
						}
						break;
					case 9:
						if (use_lruk == 1){
							policies[exp_count] = lruk;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = lruk2;
							realcache[exp_count] = r_lruk;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_lruk2;
							exp_count++;
						}
						break;
					case 10:
						if (use_lrfu == 1){
							policies[exp_count] = lrfu;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = lrfu2;
							realcache[exp_count] = r_lrfu;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_lrfu2;
							exp_count++;
						}
						break;
					case 11:
						if (use_sfifo == 1){
							policies[exp_count] = sfifo;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = sfifo2;
							realcache[exp_count] = r_sfifo;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_sfifo2;
							exp_count++;
						}
						break;

					case 12:
						if (use_twoq == 1){
							policies[exp_count] = twoq;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = twoq2;
							realcache[exp_count] = r_twoq;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_twoq2;
							exp_count++;
						}
						break;
					case 13:
						if (use_wwr == 1){
							policies[exp_count] = wwr;
							if (LEVEL==2) policies[NUMPOLICIES+exp_count] = wwr2;
							realcache[exp_count] = r_wwr;
							if (LEVEL==2) realcache[NUMPOLICIES+exp_count] = r_wwr2;
							exp_count++;
						}
						break;
					}
				}


				for (int j = 0;j<LEVEL;j++){
					game.allocPolicies(j, NUMPOLICIES);
					registerSet(j, game, policies, realcache);
				}

				/*String fname;
				int retvalue;
				 */
				/*retvalue=sprintf(fname, OUTPATH TRACE_FILE "_victim_" TEST_SET "_cs_%d"OUTFILE_EXT,cachesize);*/
				game.setLogFiles(NUMPOLICIES, "request1", "request2", "victim");
				if (!started) {
					started = true;

					for (int lev=0;lev<LEVEL;lev++) {
						outFile[lev].write("C_Size");
						for (int k=0; k<NUMPOLICIES; k++) {							
							outFile[lev].write("\t" + policies[NUMPOLICIES*lev+k].Name());
						}
						outFile[lev].write("\tRealCache\n");
					}

				}

				int id;
				int size=1;
				while(((line=reader.readLine())!=null) && ((id=Integer.parseInt(line))!=0)){
					count++;
					if (count == 1) {
						for (int m=0; m<NUMPOLICIES; m++)
							System.out.println("p[" + m + "] --> " + realcache[m].Name() +"\n");
						System.out.println("\n" + "Trace: " + TRACE_FILE +"\n");
						System.out.println(" Test: " + TEST_SET + "\n");
						System.out.println("Cachesize: " + cachesize +"\n\n");
					}
					game.get_request(id, size, 0, count, bytecount, NUMPOLICIES);
					/*System.out.println("Job ID: " +id);
					game.printCurr(numPolicies);*/
					game.print(count, bytecount, NUMPOLICIES);

				}

				//outLoopTimes << cachesize << "\t" << count << "\t" << starttime << "\t" << finishtime << "\t" << game->realhits[0] << endl;
				game.log(count, bytecount, NUMPOLICIES);
				game.PrintStats(outFile, cachesize, count, NUMPOLICIES);
				reader.close();
				game.closeFile();
			}
			parFile.close(); 
			for (int levs=0;levs<LEVEL;levs++) {
				outFile[levs].close();
				outFile[levs]=null;
			}

		} catch (IOException e){
			e.printStackTrace();
		}

		long stopTime = System.nanoTime();
		System.out.println("execution time: " + (stopTime - startTime)/1000000000.0 + "ms");
	}



}
