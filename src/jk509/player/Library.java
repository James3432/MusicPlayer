package jk509.player;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jk509.player.Playlist.Shuffle;
import jk509.player.TableSorter.Directive;

/*
 *  Library format for save files
 */
public class Library implements Serializable, Cloneable {

	private static final long serialVersionUID = 516185787632474552L;
	
	//private List<Song> tracks; // todo: won't use
	private List<Playlist> playlists;
	private Map<String, BufferedImage> artwork;
	private int currentPlaylist = 2; // currently viewing, not currently playing, playlist.
	private int volume = 100; // 0-100
	private int[] colWidths;
	public final static int HIDDEN_PLAYLISTS = 2; // 0 is search, 1 is shuffle list
	public final static int MAIN_PLAYLIST = 2; // index of main "songs" playlist
	public boolean searching = false;
	public boolean playingInSearch = false;
	private Shuffle shuffle;
	
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
	
	public Playlist getPlaylist(int i){
		if(searching && playingInSearch)
			return getPlaylists().get(0);
		return getPlaylists().get(i + HIDDEN_PLAYLISTS);
	}
	
	public int getPlaylistCount(){
		return getPlaylists().size() - HIDDEN_PLAYLISTS;
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
		playlists.add(new Playlist("Search", Playlist.DEFAULT));
		playlists.add(new Playlist("Shuffle", Playlist.DEFAULT));
		playlists.add(new Playlist("Songs", Playlist.DEFAULT));
	}
	
	public Playlist[] getPlaylistsAsArray(){
		Playlist[] res = new Playlist[playlists.size() - HIDDEN_PLAYLISTS];
		for(int i=0; i<playlists.size() - HIDDEN_PLAYLISTS; i++){
			res[i] = playlists.get(i + HIDDEN_PLAYLISTS);
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
	
	public void clearViews(){
		setSelection(new int[]{});
		setViewPos(new Point(0, 0));
		setSort(new ArrayList<Directive>());
	}

	public int search(String q, int playlistToSearch, int trackPlaying){
		searching = true;
		getPlaylists().get(0).setList(getPlaylists().get(playlistToSearch+HIDDEN_PLAYLISTS).search(q, trackPlaying));
		setCurrentPlaylist(0);
		if(getPlaylists().get(0).size() > 0)
			getPlaylists().get(0).setSelection(new int[]{0});
		getPlaylists().get(0).setSort(null);
		getPlaylists().get(0).setViewPos(new Point(0, 0));
		if(getPlaylists().get(playlistToSearch+HIDDEN_PLAYLISTS).trackPlaying > -1)
			playingInSearch = true;
		return getPlaylists().get(playlistToSearch+HIDDEN_PLAYLISTS).trackPlaying;
	}
	
	//public int searchTrackPlaying(int track){
		// find new track index in model after search filter has been applied
		// now done by method above
	//}
	
	public List<Song> searchResults(){
		return getPlaylists().get(0).getList();
	}
	
	public void cancelSearch(int i){
		setCurrentPlaylist(i + HIDDEN_PLAYLISTS);
		searching = false;
		playingInSearch = false;
	}
	
	public void shuffle(int playlist){
		if(searching)
			shuffle = getPlaylists().get(0).shuffle();
		else
			shuffle = getPlaylist(playlist).shuffle();
		getPlaylists().get(1).setList(shuffle.tracks);
		// don't set current playlist, because we play but don't view the shuffle...
	}
	
	public int shuffleIndexToModel(int i){
		if(i < 0 )
			return shuffle.tracks.size()-1;
		if(i >= shuffle.tracks.size())
			return 0;
		return shuffle.indices.get(i);
	}
	
	public int modelIndexToShuffle(int index){
		if(index < 0 )
			return shuffle.tracks.size()-1;
		if(index >= shuffle.tracks.size())
			return 0;
		for(int i=0; i<shuffle.indices.size(); ++i)
			if(shuffle.indices.get(i) == index)
				return i;
		//else
		shuffle(getCurrentPlaylist());
		return 0;
	}
	
	public List<Song> getShuffle(){
		return getPlaylists().get(1).getList();
	}
	
	public int getTrackIndex(String loc/*ID*/, int playlist){
		return getPlaylist(playlist).getIndexOf(loc);
	}
	
	@Override
	public Object clone(){
		Library lib;
		try {
			lib = (Library) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		lib.artwork = this.artwork;
		lib.playlists = this.playlists; // TODO: deep clone?
		return lib;
	}
	
}
