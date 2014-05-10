package jk509.player.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import jk509.player.Constants;
import jk509.player.clustering.AbstractCluster;
import jk509.player.gui.GenericExtFilter;
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
	 * Nearest cluster to a song
	 */
	public static int nearestCluster(List<AbstractCluster> ls, Song s){
		int result = -1;
		double[] target = s.getAudioFeatures();
		double closest = Double.MAX_VALUE;
		for(int i=0; i<ls.size(); ++i){
			double dist = computeDistance(ls.get(i).getCentroid(), target);
			if(dist < closest){
				closest = dist;
				result = i;
			}
		}
		return result;
	}
	
	public static int maxArrayIndex(double[] arr){
		double max = 0.;
		int ind = 0;
		for(int i=0; i<arr.length; ++i)
			if(arr[i] > max){
				max = arr[i];
				ind = i;
			}
		return ind;
	}
	
	/*
	 * Sum an array
	 */
	public static double arraySum(double[] arr){
		double tot = 0.;
		for(int i=0; i<arr.length; ++i)
			tot += arr[i];
		return tot;
	}
	
	/*
	 * Generate random string of length n
	 */
	public static String generateString(int n) {
		String s = "";
		for (int i = 0; i < n; ++i) {
			Random r = new Random();
			char c = (char) (r.nextInt(26) + 'a');
			s = s + c;
		}
		return s;
	}
	
	/*
	 * Interpolate based on randomness
	 * return double 0.0 <= x <= 1.0
	 * 
	 */
	public static double Interpolate(double randomness, double low, double mid, double high){
		// up the mid value a bit if it's too low
		if(mid < 0.05)
			mid = 0.05;
		
		// when randomness = 1, return high. when randomness = 0, return low. when randomness = 0.5, return mid.
		if(randomness > 0.5){
			return high + (mid-high)*2*(1. - randomness);
		}else{
			return mid + (low-mid)*(1 - 2*randomness);
		}
	}
	
	/*
	 * Remove duplicates from a song list
	 */
	public static List<Song> deduplicate(List<Song> songs){
		HashSet<String> songset = new HashSet<String>();
		List<Song> res = new ArrayList<Song>();
		for(Song s : songs){
			String str = s.getLocation().toLowerCase().replace("/", "\\");
			if(! songset.contains(str)){
				songset.add(str);
				res.add(s);
			}
		}
		return res;
	}
	
	/*
	 * Get randomness value to use
	 */
	public static double getRandomness(double system, double user){
		double r_user = user - 0.5; // centre about 0.
		r_user = Constants.RANDOMNESS_USER_CONTROL * r_user; // scale down user control
		double r_tot = system + r_user; // now combine
		r_tot = Math.max(r_tot, Constants.RANDOMNESS_MIN);
		r_tot = Math.min(r_tot, Constants.RANDOMNESS_MAX);
		return r_tot;
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
					if(endsWith(s.getLocation(), loc2))
						res.add(s);
				}
			}
			for(Song s : tracks){
				if(locEquals(s.getLocation() ,loc))
					res.add(s);
			}
		}
		return res;
	}
	
	public static boolean endsWith(String a, String b){
		String a2 = a.toLowerCase();
		String b2 = b.toLowerCase();
		String a3;
		if(a2.contains("/"))
			a3 = a2.replace("/", "\\");
		else
			a3 = a2.replace("\\", "/");
		return (a2.endsWith(b2) || a3.endsWith(b2));
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
			if(locEquals(tracks.get(i).getLocation(), loc))
					return tracks.get(i);
		return null;
	}
	
	/*
	 * Compare two song locations
	 */
	public static boolean locEquals(String a, String b){
		String a2;
		if(a.contains("/"))
			a2 = a.replace("/", "\\");
		else
			a2 = a.replace("\\", "/");
		return(a.toLowerCase().equals(b.toLowerCase()) || a2.toLowerCase().equals(b.toLowerCase()));
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
	 * Delete all temp files in the settings directory
	 */
	public static void deleteTempFiles(){
		String folder = getSettingsDir();
		String ext = ".mp3";
	    GenericExtFilter filter = new GenericExtFilter(ext);
	    File dir = new File(folder);
	 
	    String[] list = dir.list(filter);
	 
	    if (list.length == 0) return;
	 
	    File fileDelete;
	 
	    for (String file : list){
	    	String temp = new StringBuffer(folder).append(File.separator).append(file).toString();
	    	fileDelete = new File(temp);
	    	boolean isdeleted = fileDelete.delete();
	    	System.out.println("file : " + temp + " is deleted : " + isdeleted);
	    }
	}
	 
	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
	public static String getExtension(String str) {
		File f = new File(str);
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
	
	public static int getWavDuration(File file) throws Exception {
        AudioInputStream stream;
        stream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = stream.getFormat();
        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format
                    .getSampleRate(), format.getSampleSizeInBits() * 2, format
                    .getChannels(), format.getFrameSize() * 2, format
                    .getFrameRate(), true); // big endian
            stream = AudioSystem.getAudioInputStream(format, stream);
        }
        DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat(),
                ((int) stream.getFrameLength() * format.getFrameSize()));
        Clip clip = (Clip) AudioSystem.getLine(info);
        clip.close();
        double output = clip.getBufferSize()
                / (clip.getFormat().getFrameSize() * clip.getFormat()
                        .getFrameRate());
        return (int) Math.ceil(output);
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
