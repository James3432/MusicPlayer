package jk509.player.clustering;

import java.io.Serializable;

import jk509.player.Constants;

public abstract class AbstractCluster implements Constants, Serializable {

	private static final long serialVersionUID = 1L;
	
	// private int id; // needed?
	protected SongCluster parent;
	protected int level; // level within cluster hierarchy: 0 = root, n = leaf
	protected boolean leaf; // if True, this cluster is just 1 song
	// private Song track; // only non-null if leaf==True
	// Other audio feature data here
	protected boolean playing; // whether a track in this cluster is currently playing. If true in a leaf, then obviously its 'track' is playing
	protected double randomness; // from 0 (not random - exploitation) to 1.0 (fully random - exploration)

	protected AbstractCluster(int level, SongCluster parent) {
		this.level = level;
		this.parent = parent;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int l) {
		level = l;
	}

	public double getRandomness() {
		return randomness;
	}

	public void setRandomness(double r) {
		randomness = r;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean b) {
		playing = b;
	}

	public SongCluster getParent() {
		return parent;
	}

	public void setParent(SongCluster c) {
		parent = c;
	}

}
