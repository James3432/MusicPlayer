package jk509.player;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  Library format for save files
 */
public class Library implements Serializable {

	private static final long serialVersionUID = 516185787632474552L;
	
	private List<Song> tracks;
	private List<Playlist> playlists;
	private Map<String, BufferedImage> artwork;

	public Library() {
		tracks = new ArrayList<Song>();
		playlists = new ArrayList<Playlist>();
		artwork = new HashMap<String, BufferedImage>();
	}

	public Library(List<Song> ts, List<Playlist> ps, Map<String, BufferedImage> art) {
		tracks = ts;
		playlists = ps;
		artwork = art;
	}

	public List<Song> getTracks() {
		return tracks;
	}

	public List<Playlist> getPlaylists() {
		return playlists;
	}

	public Map<String, BufferedImage> getArtwork() {
		return artwork;
	}

	public int size() {
		return tracks.size();
	}

	public Song get(int i) {
		return tracks.get(i);
	}
	
	public void remove(int i){
		tracks.remove(i);
	}

	public void addTrack(Song s) {
		tracks.add(s);
	}

	public void addTracks(List<Song> list) {
		// TODO: append
		tracks = list;
	}

	public void addPlaylist(Playlist p) {
		playlists.add(p);
	}

	public void addArtwork(String s, BufferedImage im) {
		artwork.put(s, im);
	}

}
