package jk509.player.core;

import java.util.ArrayList;
import java.util.List;

import jk509.player.Constants;
import christophedelory.playlist.AbstractPlaylistComponent;
import christophedelory.playlist.Media;
import christophedelory.playlist.Parallel;
import christophedelory.playlist.Sequence;

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
	
	/*
	 * Get the location of the user "Music Factory" folder
	 */
	public static String getSettingsDir(){
		String homedir = System.getenv("user.home");
		if (homedir == null)
			homedir = System.getenv("USERPROFILE");
		return homedir + "\\Music Factory\\";
	}
	
	/*
	 * Get just a file's name, without path or extension
	 */
	public static String getFileName(String file){
		if(file == null || file.equals(""))
			return "";
		int from = file.lastIndexOf("/");
		if(from < 0)
			from = file.lastIndexOf("\\");
		int to = file.lastIndexOf(".");
		if(to < 0 || to < file.length() - 5) // can't have long extensions anyway
			to = file.length();
		
		return file.substring(from+1, to);
		
	}
	
	/*
	 * Extract a playlist from a list of song locations on disk, which may have relative paths
	 */
	public static List<Song> getSongsByLocation(List<String> locs, List<Song> tracks){
		List<Song> res = new ArrayList<Song>();
		for(String loc : locs){
			if(loc.startsWith("..")){
				String loc2 = loc.substring(3);
				for(Song s : tracks){
					if(s.getLocation().endsWith(loc2))
						res.add(s);
				}
			}
			for(Song s : tracks){
				if(s.getLocation().equals(loc))
					res.add(s);
			}
		}
		return res;
	}
	
	/*
	 * Get number of threads to use (usually == #cores)
	 */
	public static int getThreadCount(){
		int threadCount = Constants.PARALELLISM;
		
		int procs = Runtime.getRuntime().availableProcessors();
		if(Constants.PARALLELISM_USE_PROC_COUNT && procs > 0 && procs < 128)
			threadCount = procs;
		
		return threadCount;
	}

	/*
	 * Get a Song by its location on disk
	 */
	public static Song GetSongByLoc(String loc, List<Song> tracks){
		if(loc == null || loc.equals("") || tracks == null || tracks.size() < 1)
			return null;
		for(int i=0; i<tracks.size(); ++i)
			if(tracks.get(i).getLocation().equals(loc))
					return tracks.get(i);
		return null;
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
	
	/*
	 * Extract a real Playlist from a Lizzy-imported abstract playlist
	 */
	public static void playlistConverter(AbstractPlaylistComponent component, List<String> results) throws Exception {
		if (component instanceof Sequence) {
			final Sequence sequence = (Sequence) component;

			if (sequence.getRepeatCount() < 0) {
				throw new IllegalArgumentException("A PLIST playlist cannot handle a sequence repeated indefinitely");
			}

			final AbstractPlaylistComponent[] components = sequence.getComponents();

			for (int iter = 0; iter < sequence.getRepeatCount(); iter++) {
				for (AbstractPlaylistComponent c : components) {
					playlistConverter(c, results); // May throw Exception.
				}
			}
		} else if (component instanceof Parallel) {
			throw new IllegalArgumentException("A PLIST playlist cannot play different media at the same time");
		} else if (component instanceof Media) {
			final Media media = (Media) component;

			if (media.getDuration() != null) {
				throw new IllegalArgumentException("A PLIST playlist cannot handle a timed media");
			}

			if (media.getRepeatCount() < 0) {
				throw new IllegalArgumentException("A PLIST playlist cannot handle a media repeated indefinitely");
			}

			if (media.getSource() != null) {
				for (int iter = 0; iter < media.getRepeatCount(); iter++) {
					results.add(media.getSource().toString());
				}
			}
		}
	}
}
