package jk509.player.logging;

import java.io.Serializable;

// One statistics item, to be stored in a cumulative manner with 1 object per day in a json file
public class Statistics implements Serializable {

	private static final long serialVersionUID = 1L;

	public String[] descriptors = { 
			// These are all cumulative totals, unless otherwise indicated
			// All times in seconds
			"Tot time",                // Total listening time, including subcategories below
			"Tot tracks",              // Tracks listened to == number of listens
			"Stored tracks", 		   // Tracks in library
			"Stored time", 			   // Total time of tracks in library
			"Smart time",              // Listening time in smart mode (see note below)
			"Smart tracks",            // Tracks listened to in smart mode, including playing through playlists (so technically should subtract these times)
			"Shuffle time",            // Time spent in shuffle mode
			"Shuffle tracks",          // Track listens in shuffle mode
			"Auto playlists",		   // Number of auto playlists
			"Manual playlists",        // Number of manual playlists
			"Manual playlist time",    // Time spent in playlists
			"Auto playlist time",      // Same for auto playlists
			"Manual playlist tracks",  // Number of listens in playlists
			"Auto playlist tracks",    // Same for auto playlists
			"Coverage",			       // Number of distinct songs listened to. So coverage % = (this / tot tracks) % 
			"Coverage diff",           // OPPOSITE: it's actually size of the intersection. [Number of distinct songs listened to which are not in previous set]
			"Skips",                   // Number of skips, leading to skips/listens and skips/time
			"Early skips",             // Number of those in first 'IGNORE_SKIP_TIME' seconds (15ish) 
			"Jumps",			       // Number of track changes
			"Queues",			       // Number of "add to queue" tracks
			"Searches" 				   // Number of searches 
		};          
	
	public static final int Tot_time = 0;
	public static final int Tot_tracks = 1;
	public static final int Stored_tracks = 2;
	public static final int Stored_time = 3;
	public static final int Smart_time = 4;
	public static final int Smart_tracks = 5;
	public static final int Shuffle_time = 6;
	public static final int Shuffle_tracks = 7;
	public static final int Auto_playlists	= 8;
	public static final int Manual_playlists = 9;
	public static final int Manual_playlist_time = 10;
	public static final int Auto_playlist_time = 11;
	public static final int Manual_playlist_tracks = 12;
	public static final int Auto_playlist_tracks = 13;
	public static final int Coverage = 14;
	public static final int Coverage_diff = 15;
	public static final int Skips = 16;
	public static final int Early_skips = 17;
	public static final int Jumps = 18;
	public static final int Queues = 19;
	public static final int Searches = 20;
	private static final int size = 21;
	
	public double[] values = new double[descriptors.length];
	
	public int size(){
		return size;
	}
	
	@Override public String toString(){ 
		String res = "";
		for(int i=0; i<descriptors.length; ++i)
			res += descriptors[i] + ": " + values[i] + "\n";
		return res;
	}
	
}