package acmedb.adaptation;
/*
 * Distribution: class for a probability distribution, used
 * for the selecting of policy stochastically.
 */
public class Distribution {
	private int num_entries;
	private double[] begin;
	private double[] end;
	private double[] prob;
	public Distribution(int num_entries){
		this.num_entries= num_entries;
		this.begin = new double[num_entries];
		this.end = new double[num_entries];
		this.prob = new double[num_entries];
		/* initialize set of probabilities */
		prob[0] = 0.0;
		begin[0] = 0.0;
		end[0] = 0.0;
		/* set the beginning weight equal to 1/n */
		for (int i=0;i<num_entries;i++){
			prob[i] = (double) 1.0/num_entries;
			if (i==0){
				end[i] = prob[i];
			}
			else {
				end[i] = prob[i]+begin[i];
			}
			if (i<num_entries-1)
				begin[i+1] = begin[i] + prob[i];
		}
	}
	/* pick out the result based on probability */
	public int sample(){
		double draw = Math.random();

		return sample_bSearch(draw,0,num_entries-1);
	}
	/* binary search for the chosen policy */
	public int sample_bSearch(double draw, int left, int right){
		if(left > right) {
			return -1;
		}
		int half = (left + right) / 2;
		if(draw >= begin[half] && draw <= end[half]) {
			if(half == 0) return 0;
	                return half;
	        } else if (draw >= end[half]) return sample_bSearch(draw, half+1, right);
	          else if (draw <= begin[half]) return sample_bSearch(draw, left, half-1);
		return -1;	
	}
	public double getProb(int i){
		assert ((i>=0) && (i<num_entries));
		return prob[i];
	}
	public void setProb(int i, double what){
		assert (i>=0) && (i<=num_entries);
		prob[i] = what;
	}
	public void normalizeProb(){
		double sum = 0.0;
		for (int i=0;i<num_entries; i++) 
			sum+=prob[i];
		for (int i=0;i<num_entries;i++)
			prob[i] = prob[i]/sum;
				
	}
	public void distribute(){
		for(int i=0; i < num_entries ; i++) {
            begin[i] = end[i-1];
            end[i] = begin[i] + prob[i];
        }
		end[num_entries] = 1.0;
	}
}
