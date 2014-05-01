package jk509.player.core;

import java.util.ArrayList;
import java.util.List;

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
	
	/*
	 * Get user's home directory path
	 */
	public static String getHomeDir(){
		String homedir = System.getenv("user.home");
		if (homedir == null)
			homedir = System.getenv("USERPROFILE");
		return homedir;
	}
	
	public static String getSettingsDir(){
		String homedir = System.getenv("user.home");
		if (homedir == null)
			homedir = System.getenv("USERPROFILE");
		return homedir + "\\Music Factory\\";
	}

	
	/*
	 * Set audioFeatures in track list from a double array list
	 */
	public static void SetFeaturesFromFile(List<Song> songs, List<double[]> fs){
		for(int i=0; i<Math.min(songs.size(), fs.size()); ++i)
			songs.get(i).setAudioFeatures(fs.get(i));
	}
	
	/*
	 * Extract audio feature data from song list into double array list
	 */
	public static List<double[]> GetFeaturesFromSongs(List<Song> songs){
		List<double[]> res = new ArrayList<double[]>();
		for(Song s : songs)
			res.add(s.getAudioFeatures());
		return res;
	}
}
