package jk509.player.clustering;

import java.io.Serializable;
import java.util.List;

import jk509.player.Constants;
import jk509.player.core.Song;

public abstract class AbstractCluster implements Constants, Serializable {

	private static final long serialVersionUID = 1L;
	
	protected SongCluster parent;
	protected int level; // level within cluster hierarchy: 0 = root, n = leaf
	protected boolean leaf; // if True, this cluster is just 1 song
	//private double randomness = Constants.RANDOMNESS_MAX; // from RAND_MIN ~ 0 (not random - exploitation) to RAND_MAX ~ 1 (fully random - exploration)
	//private double user_randomness = 0.;                  // from 0 (not random) to 1.0 (very random)
	
	protected List<Song> tracks;

	protected AbstractCluster(List<Song> tracks, int level, SongCluster parent) {
		this.tracks = tracks;
		this.level = level;
		this.parent = parent;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int l) {
		level = l;
	}
	
	public abstract double[] getCentroid();
	
	public abstract void setCentroid(double[] d);

	public boolean isLeaf() {
		return leaf;
	}

	/*public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean b) {
		playing = b;
	}*/

	public List<Song> getTracks(){
		return tracks;
	}
	
	public void setTracks(List<Song> ts){
		tracks = ts;
	}
	
	public SongCluster getParent() {
		return parent;
	}

	public void setParent(SongCluster c) {
		parent = c;
	}

	@Override public String toString(){
		return (leaf ? "Leaf" : "Song") + " cluster: "+tracks.toString();
	}
	
}
