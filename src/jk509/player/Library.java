package jk509.player;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/*
 *  Library format for save files
 */
public class Library {

	private List<Song> tracks;
	private List<Playlist> playlists;
	private Map<String, BufferedImage> artwork;

	public Library() {
		tracks = null;
		playlists = null;
		artwork = null;
	}

	public Library(List<Song> ts, List<Playlist> ps,
			Map<String, BufferedImage> art) {
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

	public void addTrack(Song s) {
		tracks.add(s);
	}

	public void addPlaylist(Playlist p) {
		playlists.add(p);
	}

	public void addArtwork(String s, BufferedImage im) {
		artwork.put(s, im);
	}

}
