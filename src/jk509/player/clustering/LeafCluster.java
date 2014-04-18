package jk509.player.clustering;

import java.util.ArrayList;

import jk509.player.core.Song;

public class LeafCluster extends AbstractCluster {

	private static final long serialVersionUID = 1L;
	
	private Song track;

	private LeafCluster(int level, SongCluster parent) {
		super(null, level, parent);
		leaf = true;
	}

	public LeafCluster(int level, SongCluster parent, Song s) {
		this(level, parent);
		setTrack(s);
	}

	public Song getTrack() {
		if(tracks.size() == 1)
			return tracks.get(0);
		return track;
	}

	public void setTrack(Song s) {
		track = s;
		tracks = new ArrayList<Song>();
		tracks.add(s);
	}
}
