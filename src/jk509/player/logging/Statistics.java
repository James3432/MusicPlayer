package jk509.player.logging;

// One statistics item, to be stored in a cumulative manner with 1 object per day in a json file
class Statistics {
	public String[] descriptors = { "Tot time", "Tot tracks", "Smart time", "Smart tracks", "Shuffle time", "Shuffle tracks", "Playlists" /* tot TODO split into manual/auto */, "Playlist time", "Playlist tracks", 
			"% in playlists", "Tracks", "Total time", "Coverage" /*% songs listened to*/, "Coverage diff" /*consistency of listening clusters over days*/, 
			"Skips per track", "Skips per time", "Jumps per time", "Jumps per track", "Manual playlists" /* created */, "Auto playlists" /* created */, 
			"Searches" };          // all these as a function of days into trial
	public double[] values = new double[descriptors.length];
	@Override public String toString(){ 
		String res = "";
		for(int i=0; i<descriptors.length; ++i)
			res += descriptors[i] + ": " + values[i] + "\n";
		return res;
	}
}