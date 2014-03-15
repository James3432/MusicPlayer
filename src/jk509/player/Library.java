package jk509.player;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jk509.player.TableSorter.Directive;

/*
 *  Library format for save files
 */
public class Library implements Serializable {

	private static final long serialVersionUID = 516185787632474552L;
	
	//private List<Song> tracks; // todo: won't use
	private List<Playlist> playlists;
	private Map<String, BufferedImage> artwork;
	private int currentPlaylist = 0; // currently viewing, not currently playing, playlist.
	private int volume = 100; // 0-100
	private int[] colWidths;

	public Library() {
		//tracks = new ArrayList<Song>();
		playlists = new ArrayList<Playlist>();
		artwork = new HashMap<String, BufferedImage>();
		colWidths = new int[] { 25, 25, 300, 200, 200, 100, 80, 80, 100 };
		Initialise();
	}

	public Library(List<Song> ts, List<Playlist> ps, Map<String, BufferedImage> art) {
		//tracks = ts;
		this();
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
	
	public void addToPlaylist(int index, List<Song> songs){
		if(playlists.size() < MusicPlayer.FIXED_PLAYLIST_ELEMENTS){
			Initialise();
		}
		playlists.get(index).append(songs);
	}
	
	public void setCurrentPlaylist(int index){
		currentPlaylist = index;
	}
	
	public int getCurrentPlaylist(){
		return currentPlaylist;
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
	
	public int[] getSelection(){
		return playlists.get(currentPlaylist).getSelection();
	}
	
	public void setSelection(int[] ns){
		playlists.get(currentPlaylist).setSelection(ns);
	}
	
	public Point getViewPos(){
		return playlists.get(currentPlaylist).getViewPos();
	}
	
	public void setViewPos(Point pos){
		playlists.get(currentPlaylist).setViewPos(pos);
	}
	
	public List<Directive> getSort(){
		return playlists.get(currentPlaylist).getSort();
	}
	
	public void setSort(List<Directive> s){
		playlists.get(currentPlaylist).setSort(s);
	}

	public void addPlaylist(Playlist p) {
		if(playlists.size() < MusicPlayer.FIXED_PLAYLIST_ELEMENTS){
			Initialise();
		}
		playlists.add(p);
	}
	
	public void addPlaylists(List<Playlist> ps) {
		playlists.addAll(ps);
	}

	public void addArtwork(String s, BufferedImage im) {
		artwork.put(s, im);
	}
	
	public void addArtwork(Map<String, BufferedImage> map) {
		artwork.putAll(map);
	}
	
	public int getVolume(){
		return volume;
	}
	
	public void setVolume(int v){
		volume = v;
	}
	
	public int[] getColWidths(){
		return colWidths;
	}
	
	public void setColWidths(int[] c){
		colWidths = c;
	}

}
