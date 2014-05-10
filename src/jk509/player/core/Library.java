package jk509.player.core;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jk509.player.Constants;
import jk509.player.MusicPlayer;
import jk509.player.clustering.SongCluster;
import jk509.player.core.Playlist.Shuffle;
import jk509.player.core.TableSorter.Directive;
import jk509.player.logging.Statistics;

/*
 *  Library format for save files
 */
public class Library implements Serializable, Cloneable {

	private static final long serialVersionUID = 516185787632474552L;

	// private List<Song> tracks; // todo: won't use
	private List<Playlist> playlists;
	//private Map<String, BufferedImage> artwork;
	private HashSet<String> songset;
	private HashSet<String> coverage;
	private int currentPlaylist = 2; // currently viewing, not currently playing, playlist.
	private int volume = 100; // 0-100
	private int[] colWidths;
	public final static int HIDDEN_PLAYLISTS = 2; // 0 is search, 1 is shuffle list
	public final static int MAIN_PLAYLIST = 2; // index of main "songs" playlist
	public boolean searching = false;
	public boolean playingInSearch = false;
	private Shuffle shuffle;
	private int[] normalToSearch;
	private int[] searchToNormal;
	private SongQueue queue;
	private SongCluster clusters;
	private String user_id;
	
	private Statistics stats;
	
	public int lastUpdateDay = 0;
	public int lastStatsDay = 0;
	public boolean smartPlay = Constants.SMART_PLAY_DEFAULT;
	public boolean autoupload = true;
	private boolean ignoreFeatureless = false;
	//private Deque<Song> history; // from oldest--->recent

	public Library() {
		// tracks = new ArrayList<Song>();
		playlists = new ArrayList<Playlist>();
		//artwork = new HashMap<String, BufferedImage>();
		songset = new HashSet<String>();
		colWidths = new int[] { 25, 25, 300, 200, 200, 100, 80, 80, 100 };
		Initialise();
		generateUserID();
	}

	public Library(List<Song> ts, List<Playlist> ps, Map<String, BufferedImage> art) {
		// tracks = ts;
		this();
		playlists = ps;
		//artwork = art;
		Initialise();
	}

	public boolean ignoreFeatureless(){
		return ignoreFeatureless;
	}
	public void ignoreFeatureless(boolean t){
		ignoreFeatureless = t;
	}
	
	public List<Song> getTracks() {
		// return tracks;
		return playlists.get(currentPlaylist).getList();
	}
	
	public Statistics getStats(){
		if(stats == null)
			stats = new Statistics();
		return stats;
	}
	// Plan is to never use this, as all stats are cumulative
	public void resetStats(){
		stats = new Statistics();
	}
	public void setStat(int pos, double val){
		if(stats == null)
			stats = new Statistics();
		try{
			stats.values[pos] = val;
		}catch(ArrayIndexOutOfBoundsException e){
			stats = new Statistics();
			stats.values[pos] = val;
		}
	}
	public double getStat(int pos){
		if(stats == null)
			stats = new Statistics();
		return stats.values[pos];
	}
	public void updateStat(int pos, double val){
		if(stats == null)
			stats = new Statistics();
		try{
			stats.values[pos] += val;
		}catch(ArrayIndexOutOfBoundsException e){
			stats = new Statistics();
			stats.values[pos] += val;
		}
	}

	public List<Playlist> getPlaylists() {
		return playlists;
	}

	public void setPlaylist(int index, List<Song> songs) {
		if (playlists.size() < MusicPlayer.FIXED_PLAYLIST_ELEMENTS) {
			Initialise();
		}
		playlists.get(index).setList(songs);
	}

	public Playlist getPlaylist(int i) {
		if (searching && playingInSearch)
			return getPlaylists().get(0);
		return getPlaylists().get(i + HIDDEN_PLAYLISTS);
	}

	public int getPlaylistCount() {
		return getPlaylists().size() - HIDDEN_PLAYLISTS;
	}

	public void addToPlaylist(int index, List<Song> songs) {
		if (playlists.size() < MusicPlayer.FIXED_PLAYLIST_ELEMENTS) {
			Initialise();
		}
		playlists.get(index).append(songs);
	}

	public void setCurrentPlaylist(int index) {
		currentPlaylist = index;
	}

	public int getCurrentPlaylist() {
		return currentPlaylist;
	}

	private void Initialise() {
		playlists = new ArrayList<Playlist>();
		playlists.add(new Playlist("Search", Playlist.DEFAULT));
		playlists.add(new Playlist("Shuffle", Playlist.DEFAULT));
		playlists.add(new Playlist("Songs", Playlist.DEFAULT));
	}

	public Playlist[] getPlaylistsAsArray() {
		Playlist[] res = new Playlist[playlists.size() - HIDDEN_PLAYLISTS];
		for (int i = 0; i < playlists.size() - HIDDEN_PLAYLISTS; i++) {
			res[i] = playlists.get(i + HIDDEN_PLAYLISTS);
		}
		return res;
	}

	/*public Map<String, BufferedImage> getArtwork() {
		return artwork;
	}*/

	public int size() {
		// return tracks.size();
		return playlists.get(currentPlaylist).size();
	}

	public Song get(int i) {
		// return tracks.get(i);
		return playlists.get(currentPlaylist).get(i);
	}

	public void remove(int i) {
		// tracks.remove(i);
		getClusters().RemoveTrack(playlists.get(currentPlaylist).get(i));
		playlists.get(currentPlaylist).remove(i);
	}

	public void addTrack(Song s) {
		// tracks.add(s);
		playlists.get(currentPlaylist).add(s);
	}

	// append is used instead now
	/*public void addTracks(List<Song> list) {
		// tracks = list;
		playlists.get(currentPlaylist).setList(list);
	}*/

	public int[] getSelection() {
		return playlists.get(currentPlaylist).getSelection();
	}

	public void setSelection(int[] ns) {
		playlists.get(currentPlaylist).setSelection(ns);
	}

	public Point getViewPos() {
		return playlists.get(currentPlaylist).getViewPos();
	}

	public void setViewPos(Point pos) {
		playlists.get(currentPlaylist).setViewPos(pos);
	}

	public List<Directive> getSort() {
		return playlists.get(currentPlaylist).getSort();
	}

	public void setSort(List<Directive> s) {
		playlists.get(currentPlaylist).setSort(s);
	}

	public void addPlaylist(Playlist p) {
		if (playlists.size() < MusicPlayer.FIXED_PLAYLIST_ELEMENTS) {
			Initialise();
		}
		playlists.add(p);
	}

	public void addPlaylists(List<Playlist> ps) {
		playlists.addAll(ps);
	}

	/*public void addArtwork(String s, BufferedImage im) {
		artwork.put(s, im);
	}

	public void addArtwork(Map<String, BufferedImage> map) {
		artwork.putAll(map);
	}*/

	public int getVolume() {
		return volume;
	}

	public void setVolume(int v) {
		volume = v;
	}

	public int[] getColWidths() {
		return colWidths;
	}

	public void setColWidths(int[] c) {
		colWidths = c;
	}

	public void clearViews() {
		setSelection(new int[] {});
		setViewPos(new Point(0, 0));
		setSort(new ArrayList<Directive>());
	}

	public int search(String q, int playlistToSearch, int trackPlaying) {
		searching = true;
		searchToNormal = getPlaylists().get(playlistToSearch + HIDDEN_PLAYLISTS).search(q);
		normalToSearch = new int[getPlaylists().get(playlistToSearch + HIDDEN_PLAYLISTS).size()];
		for (int i = 0; i < normalToSearch.length; ++i)
			normalToSearch[i] = -1;
		for (int i = 0; i < searchToNormal.length; ++i)
			normalToSearch[searchToNormal[i]] = i;
		List<Song> searchList = new ArrayList<Song>();
		for (int i = 0; i < searchToNormal.length; ++i)
			searchList.add(getPlaylists().get(playlistToSearch + HIDDEN_PLAYLISTS).get(searchToNormalModel(i)));

		getPlaylists().get(0).clearViews();
		getPlaylists().get(0).setList(searchList);
		setCurrentPlaylist(0);
		if (getPlaylists().get(0).size() > 0)
			getPlaylists().get(0).setSelection(new int[] { 0 });
		getPlaylists().get(0).setSort(null);
		getPlaylists().get(0).setViewPos(new Point(0, 0));
		if (normalToSearchModel(trackPlaying) > -1)
			playingInSearch = true;
		return normalToSearchModel(trackPlaying);
	}

	public int normalToSearchModel(int r) {
		if (r < 0)
			return -1;
		try {
			return normalToSearch[r];
		} catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}

	public int searchToNormalModel(int r) {
		if (r < 0)
			return -1;
		try {
			return searchToNormal[r];
		} catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}

	// public int searchTrackPlaying(int track){
	// find new track index in model after search filter has been applied
	// now done by method above
	// }

	public List<Song> searchResults() {
		return getPlaylists().get(0).getList();
	}

	public void cancelSearch(int i) {
		setCurrentPlaylist(i + HIDDEN_PLAYLISTS);
		searching = false;
		playingInSearch = false;
	}

	public void shuffle(int playlist) {
		if (searching)
			shuffle = getPlaylists().get(0).shuffle();
		else
			shuffle = getPlaylist(playlist).shuffle();
		getPlaylists().get(1).setList(shuffle.tracks);
		// don't set current playlist, because we play but don't view the shuffle...
	}

	public int shuffleIndexToModel(int i) {
		if (i < 0)
			return shuffle.tracks.size() - 1;
		if (i >= shuffle.tracks.size())
			return shuffle.indices.get(0);
		return shuffle.indices.get(i);
	}

	public int modelIndexToShuffle(int index) {
		if (index < 0)
			return shuffle.tracks.size() - 1;
		if (index >= shuffle.tracks.size())
			return 0;
		for (int i = 0; i < shuffle.indices.size(); ++i)
			if (shuffle.indices.get(i) == index)
				return i;
		// else
		shuffle(getCurrentPlaylist());
		return 0;
	}

	public List<Song> getShuffle() {
		return getPlaylists().get(1).getList();
	}

	public int getTrackIndex(String loc/* ID */, int playlist) {
		return getPlaylist(playlist).getIndexOf(loc);
	}

	public int getTrackIndex(Song s, int playlist) {
		return getPlaylist(playlist).getIndexOf(s);
	}

	@Override
	public Object clone() {
		Library lib;
		try {
			lib = (Library) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		//lib.artwork = this.artwork;
		lib.playlists = this.playlists; // deep clone not required because we don't edit playlists after cloning (for lib file save)
		lib.user_id = this.user_id;
		return lib;
	}

	public String getUserID(){
		if(user_id == null || user_id.equals("") || user_id.length() < 1)
			generateUserID();
		return user_id;
	}
	private void generateUserID() {
		user_id = StaticMethods.generateString(Constants.USER_ID_LENGTH);
	}
	public SongQueue getQueue() {
		return queue;
	}

	public void setQueue(SongQueue sq) {
		queue = sq;
	}

	public void createQueue(SongQueueElement start) {
		queue = new SongQueue(start);
	}

	public void deleteQueue() {
		queue = null;
	}

	public boolean hasQueue() {
		return queue != null;
	}

	public SongCluster getClusters(){
		if(clusters == null)
			clusters = new SongCluster(new ArrayList<Song>(), null);
		return clusters;
	}
	
	public void setClusters(SongCluster sc){
		clusters = sc;
	}
	
	public List<Song> getMainList(){
		return getPlaylists().get(Library.MAIN_PLAYLIST).getList();
	}
	
	public boolean contains(Song s){
		return contains(s.getLocation());
	}
	public boolean contains(String s){
		if(songset == null)
			rebuildSet();
		return songset.contains(s.toLowerCase().replace("/", "\\"));
	}
	
	public void addToSet(Song s){
		if(songset == null)
			rebuildSet();
		songset.add(s.getLocation().toLowerCase().replace("/", "\\"));
	}
	
	public void rebuildSet(){
		List<Song> ls = getMainList();
		if(songset == null)
			songset = new HashSet<String>();
		else
			songset.clear();
		for(Song s : ls)
			addToSet(s);
	}
	
	public void addCoverage(Song s){
		if(coverage == null)
			coverage = new HashSet<String>();
		coverage.add(s.getLocation());
	}
	public int coverage(){
		if(coverage == null)
			coverage = new HashSet<String>();
		return coverage.size();
	}
	public HashSet<String> getCoverageSet(){
		if(coverage == null)
			coverage = new HashSet<String>();
		return coverage;
	}
	
	public List<Song> getAllNotContained(List<Song> songs){
		List<Song> res = new ArrayList<Song>();
		for(Song s : songs)
			if(! contains(s))
				res.add(s);
		return res;
	}
	
}
