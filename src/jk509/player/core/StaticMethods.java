package jk509.player.core;

public class StaticMethods {

	/*
	 * Euclidean distance function
	 */
	public static double computeDistance(double[] a, double[] b){
		int els = Math.min(a.length, b.length);
		double tot = 0.0;
		for(int i=0; i<els; ++i)
			tot += (b[i] - a[i]) * (b[i] - a[i]);
		double res =  Math.sqrt(tot);
		// don't return non-double values
		if(Double.isInfinite(res) || Double.isNaN(res))
			res = Double.MAX_VALUE;
		return res;
	}
	
}
