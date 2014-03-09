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
	
	//private List<Song> tracks; // todo: won't use
	private List<Playlist> playlists;
	private Map<String, BufferedImage> artwork;
	private int currentPlaylist = 0;

	public Library() {
		//tracks = new ArrayList<Song>();
		playlists = new ArrayList<Playlist>();
		artwork = new HashMap<String, BufferedImage>();
		Initialise();
	}

	public Library(List<Song> ts, List<Playlist> ps, Map<String, BufferedImage> art) {
		//tracks = ts;
		playlists = ps;
		artwork = art;
		Initialise();
	}

	public List<Song> getTracks() {
		//return tracks;
		return playlists.get(currentPlaylist).getList();
	}

	public List<Playlist> getPlaylists() {
		return playlists;
	}
	
	public void setPlaylist(int index, List<Song> songs){
		if(playlists.size() < MusicPlayer.FIXED_PLAYLIST_ELEMENTS){
			Initialise();
		}
		playlists.get(index).setList(songs);
	}
	
	public void setCurrentPlaylist(int index){
		currentPlaylist = index;
	}
	
	private void Initialise(){
		playlists = new ArrayList<Playlist>();
		playlists.add(new Playlist("Songs", Playlist.DEFAULT));
		playlists.add(new Playlist("Artists", Playlist.DEFAULT));
		playlists.add(new Playlist("Albums", Playlist.DEFAULT));
	}
	
	public Playlist[] getPlaylistsAsArray(){
		Playlist[] res = new Playlist[playlists.size()];
		for(int i=0; i<playlists.size(); i++){
			res[i] = playlists.get(i);
		}
		return res;
	}
	
	public Map<String, BufferedImage> getArtwork() {
		return artwork;
	}

	public int size() {
		//return tracks.size();
		return playlists.get(currentPlaylist).size();
	}

	public Song get(int i) {
		//return tracks.get(i);
		return playlists.get(currentPlaylist).get(i);
	}
	
	public void remove(int i){
		//tracks.remove(i);
		playlists.get(currentPlaylist).remove(i);
	}

	public void addTrack(Song s) {
		//tracks.add(s);
		playlists.get(currentPlaylist).add(s);
	}

	public void addTracks(List<Song> list) {
		// TODO: append
		//tracks = list;
		playlists.get(currentPlaylist).setList(list);
	}

	public void addPlaylist(Playlist p) {
		if(playlists.size() < MusicPlayer.FIXED_PLAYLIST_ELEMENTS){
			Initialise();
		}
		playlists.add(p);
	}

	public void addArtwork(String s, BufferedImage im) {
		artwork.put(s, im);
	}

}
