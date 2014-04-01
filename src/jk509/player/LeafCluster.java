package jk509.player;

public class LeafCluster extends AbstractCluster {

	private Song track;

	public LeafCluster(int level, SongCluster parent) {
		super(level, parent);
		leaf = true;
	}

	public LeafCluster(int level, SongCluster parent, Song s) {
		this(level, parent);
		setTrack(s);
	}

	public Song getTrack() {
		return track;
	}

	public void setTrack(Song s) {
		track = s;
	}
}
