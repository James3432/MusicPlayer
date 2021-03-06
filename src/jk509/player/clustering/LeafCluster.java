package jk509.player.clustering;

import java.util.ArrayList;

import jk509.player.core.Song;

public class LeafCluster extends AbstractCluster {

	private static final long serialVersionUID = 1L;
	
	//private Song track;

	private LeafCluster(int level, SongCluster parent) {
		super(null, level, parent);
		leaf = true;
	}

	public LeafCluster(int level, SongCluster parent, Song s) {
		this(level, parent);
		setTrack(s);
	}

	public Song getTrack() {
		if(tracks.size() > 0)
			return tracks.get(0);
		else return null;
		//return track;
	}

	public void setTrack(Song s) {
		//track = s;
		tracks = new ArrayList<Song>();
		tracks.add(s);
	}
	
	public double[] getCentroid(){
		return getTrack().getAudioFeatures();
	}
	
	public void setCentroid(double[] d){
		//getTrack().setAudioFeatures(d);
		return; // never want to set a leaf's centroid like this: it should always just be the track's audio features
	}
}
