package jk509.player.clustering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import jk509.player.Constants;
import jk509.player.core.Song;

public class SongCluster extends AbstractCluster {

	private static final long serialVersionUID = 1L;
	
	private List<AbstractCluster> clusters; // child clusters. only non-null if leaf==False
	private ArrayList<ArrayList<Double>> p; // cluster transition probabilities between all child clusters. only non-null if leaf==False
											// p.get(from).get(to) is the structure. ie. List<Row> where Row = List<Val>
											/*
											 *  From | To: A  B  C  ...
											 *  -----
 											 *  A          p  p  p ...
											 *  B          p  p  p ...
											 *  C          p  p  p ...
											 *  ...
											 */
	private int[] assignments;   // song->cluster indices
	private double[] preferred;  // similar to one row in 'p', but gives overall chance of being in one cluster.
	                             // like a STATIONARY DISTRIBUTION :)  except measured directly
	private Deque<Song> history;
	private SimpleKMeans clusterController;
	
	private int clusterPlaying = -1; // only > -1 if playing==True. Equivalent to the current "state" in a machine-learning sense

	public SongCluster(List<Song> songs, JFrame form){
		this(songs, 0, null, form);
	}
	public SongCluster(List<Song> songs, int level, SongCluster parent, JFrame form) {
		super(songs, level, parent);
		leaf = false;
		ResetClusters(form);
	}

	public SongCluster(LeafCluster c, JFrame form) {
		super(Arrays.asList(c.getTrack()), c.getLevel(), c.getParent());
		leaf = false;
		clusters = new ArrayList<AbstractCluster>();
		clusters.add(c);
		InitPMatrix();
	}
	
	public void ResetAll(JFrame form){
		for(Song s : tracks)
			s.setAudioFeatures(null);
		ResetClusters(form);
	}
	
	public void ResetClusters(JFrame form){
		clusters = new ArrayList<AbstractCluster>();
		
		if(tracks.size() <= Constants.MAX_CLUSTERS){
			assignments = new int[tracks.size()];
			for(int i=0; i<tracks.size(); ++i){
				LeafCluster leaf = new LeafCluster(level + 1, this, tracks.get(i));
				leaf.setCentroid(tracks.get(i).getAudioFeatures());
				clusters.add(leaf);
				assignments[i] = i;
			}
		}else{
		
			AbstractClusterer clusterer = new KMeansClusterer(tracks);
			clusterer.run(form);
			// PrintClusters(clusterer.getResult());
			assignments = clusterer.getAssignments();
			clusterController = ((KMeansClusterer) clusterer).getClusterer();
			System.out.println(clusterController.getOptions());
			System.out.println("=========*******************=============");
			System.out.println(clusterController.listOptions());
			
			List<ArrayList<Song>> cs = clusterer.getResult();
			int i=0;
			for (ArrayList<Song> cluster : cs) {
				Instance in = clusterController.getClusterCentroids().instance(i);
				double[] centroid = in.toDoubleArray();
				AbstractCluster newCluster = null;
				if (cluster.size() > 1){
					newCluster = /*clusters.add(*/new SongCluster(cluster, level + 1, this, form);
				}else if (cluster.size() == 1){
					newCluster = /*clusters.add(*/new LeafCluster(level + 1, this, cluster.get(0));
				}
				clusters.add(newCluster);
				newCluster.setCentroid(centroid);
				//if(level==0)
					//System.out.println("Root cluster added");
				++i;
			}
		}
		
		InitPMatrix();
	}
	
	public void ResetLearning(){
		p = null;
		InitPMatrix();
	}

	private void InitPMatrix() {
		p = new ArrayList<ArrayList<Double>>();
		int size = clusters.size();
		double prob = 1. / size;
		for (int i = 0; i < size; ++i) {
			p.add(new ArrayList<Double>());
			for (int j = 0; j < size; ++j) {
				if(Constants.PROBABILITIES_INITIALLY_SPREAD)
					p.get(i).add(prob);
				else
					p.get(i).add((i==j ? 1.0 : 0.0));
			}
		}
	}
	
	public List<ArrayList<Double>> getP(){
		return p;
	}
	
	public void clearHistory(){
		history = new LinkedList<Song>();
	}
	
	public Deque<Song> getHistory(){
		return history;
	}
	
	public void addHistory(Song s){
		history.add(s);
		while(history.size() > Constants.HISTORY_SIZE)
			history.pop();
	}
	
	/*
	 *  Normalises the matrix P. Also corrects any negative values to 0, and re-initialises a row if it's sum = 0
	 */
	private void NormaliseP() {
		for (int i = 0; i < p.size(); ++i) {
			double tot = 0.0;
			for (int j = 0; j < p.get(i).size(); ++j) {
				double val = p.get(i).get(j);
				// deal with negative values
				if(val < 0){
					val = 0;
					p.get(i).set(j, val);
				}
				tot += p.get(i).get(j);
			}
			// If total was zero, re-initialise
			if(tot == 0.0){
				double prob = 1. / p.get(i).size();
				for (int j = 0; j < p.get(i).size(); ++j) {
					if(Constants.PROBABILITIES_INITIALLY_SPREAD)
						p.get(i).set(j, prob);
					else
						p.get(i).set(j, (i==j ? 1.0 : 0.0));
				}
			}else{
			// Otherwise, scale up to normalise
				double scale = 1. / tot;
				for (int j = 0; j < p.get(i).size(); ++j) {
					p.get(i).set(j, p.get(i).get(j) * scale);
				}
			}
		}
	}

	public int getClusterPlaying() {
		return clusterPlaying;
	}
	public List<Integer> getClusterPlayingPath(){
		List<Integer> ls = new ArrayList<Integer>();
		if(clusterPlaying > -1){
			ls.add(clusterPlaying);
			if(!(clusters.get(clusterPlaying) instanceof LeafCluster))
				ls.addAll(((SongCluster) clusters.get(clusterPlaying)).getClusterPlayingPath());
			return ls;
		}else
			return null;
	}
	public Song getClusterPlayingSong(){
		Song res = null;
		List<Integer> path = getClusterPlayingPath();
		if(path == null)
			return null;
		SongCluster next = this;
		for(int i=0; i<path.size()-1; ++i){
			next = (SongCluster) next.getChildren().get(path.get(i));
		}
		LeafCluster last = (LeafCluster) next.getChildren().get(path.get(path.size()-1));
		res = last.getTrack();
		return res;
	}
	
	@SuppressWarnings("unused")
	private void setClusterPlaying(int c) {
		clusterPlaying = c;
	}

	public void AddTrack(Song s) {
		// TODO
		/*
		clusterController.clusterInstance(instance);
		clusterController.distributionForInstance(instance);
		*/
	}

	public void AddTracks(Song[] s) {
		for (int i = 0; i < s.length; ++i)
			AddTrack(s[i]);
	}

	public void RemoveTrack(Song s) {
		// TODO
	}

	public void RemoveTracks(Song[] s) {
		for (int i = 0; i < s.length; ++i)
			RemoveTrack(s[i]);
	}

	public boolean contains(Song s){
		return tracks.contains(s);
	}
	
	public int getClusterIndex(Song s){
		try{
			int index = assignments[tracks.indexOf(s)];
			return index;
		}catch(ArrayIndexOutOfBoundsException e){
			return -1;
		}
	}
	
	public AbstractCluster getCluster(Song s){
		return clusters.get(getClusterIndex(s));
	}
	
	/*
	 * Get the next recommended track
	 * 
	 * If playingCluster has already been set, that will be the 'source' track. Otherwise, heuristics and 'preferred' will be used to pick a starting track.
	 * 
	 * This method should only be called on the root tree. Recursion down to lower levels is performed internally
	 */
	public Song next() {
		// TODO
		/*
		 * choose most probable next cluster, taking into account the `randomness'. Use heuristic to choose track within that cluster (what if staying within cluster?). If all these tracks played recently, may choose next cluster.
		 * update clusterPlaying, or let calling method do that? (we don't know if it plays it or not...although we'll hardly be changing it upon every pause/play)
		 */
		//start at root, pick clusters traversing down. Heuristic if different branch from currplaying.
		
		// Don't allow call unless we are at root
		if(this.level != 0)
			return null;
		
		Song choice = null;
		
		if(clusterPlaying < 0 || clusterPlaying >= clusters.size()){
			choice = heuristicChoice(null, this);
		}else{
			// Location of what was previously playing
			List<Integer> sourcePath = getClusterPlayingPath();
			AbstractCluster next = this;
			int nextCluster = 0;
			for(int level = 0; level < sourcePath.size(); ++level){
				if(!(next instanceof SongCluster)){
					// base case: need to step back up a level probably
					// TODO set choice here
					// several cases: source a leaf but next isnt, next a leaf but source isn't, both leaves. stepping up a level
					// account for all factors in heuristic method
				}else{
					SongCluster current = (SongCluster) next;
					//choose next cluster
					ArrayList<Double> row = current.getP().get(sourcePath.get(level));
					nextCluster = chooseNextCluster(row);
					if(nextCluster == sourcePath.get(level))
						next = current.getChildren().get(nextCluster);
					else{
						choice = heuristicChoice(current.getClusterPlayingSong(), current.getChildren().get(nextCluster));
						break;
					}
				}
			}
		}
		
		setPlayingCluster(choice);
		return choice;
	}
	
	/*
	 * TODO maybe incorporate 'recently played' here too, so we never explore a cluster we've played through?
	 */
	private int chooseNextCluster(ArrayList<Double> row){
		/*
		 * Randomness is the key variable here: from 0 (not random - exploitation) to 1.0 (fully random - exploration)
		 */
		
		// Find the most probable next cluster
		int maxrow = -1;
		double max = 0.;
		for(int i=0; i<row.size(); ++i){
			if(row.get(i) > max){
				max = row.get(i);
				maxrow = i;
			}
		}
	
		// TODO: the inbetween choice, using randomness to skew probabilities from {0, 0, 1, 0, 0} (ie. the above) to fully random (ie. more extreme than below)
		
		// Pick cluster based on probabilities
		double rand = Math.random();
		double tot = 0.;
		int choice = -1;
		for(int i=0; i<row.size(); ++i){
			tot += row.get(i);
			if(rand < tot){
				choice = i;
				break;
			}
		}
		return choice;
	}
	
	//TODO Heuristic method using preferred
	/*
	 * Factors to account for:
	 *  - 'preferred'
	 *  - randomnesss
	 *  - recent play list (how to do this? don't want to change weights)
	 *  - audio features (just distance between feature vectors. future option of grabbing start/end audio features)
	 */
	private Song heuristicChoice(Song source, SongCluster cluster){
		Song choice = null;
		
		if(source == null){
			// we are choosing from all songs
			
		}else{
			// heuristic called because we've made a long jump between clusters so no P matrix is available
			
		}
		
		return choice;
	}
	
	/*
	 * Update p matrix as a result of an action
	 * 
	 * This method should only be called on the root tree. Recursion down to lower levels is performed internally
	 * 
	 */
	public void Update(UserAction action) {
		// TODO
		
		// Don't allow call unless we are at root
		if(this.level != 0)
			return;
		
		Song source = action.source;
		Song target = action.target;
		Song chosen = action.chosen;
		
		//find lowest common ancestor, update nodes from here up. 
		List<Integer> sourceBranch = getIndexList(source);
		List<Integer> targetBranch = getIndexList(target);
		
		int lca = getLowestCommonAncestor(sourceBranch, targetBranch);

		AbstractCluster next = this;
		for(int level = 0; level <= lca; level++){
			SongCluster current = (SongCluster) next;
			current.localUpdate(action, TrackLink.SOURCE_TO_TARGET);
			next = current.getChildren().get(sourceBranch.get(level));
		}
		
		if(action.requiresChosen()){
			List<Integer> chosenBranch = getIndexList(chosen);
			int lca1 = lca;
			int lca2 = getLowestCommonAncestor(sourceBranch, chosenBranch);
			int lca3 = getLowestCommonAncestor(targetBranch, chosenBranch);
			next = this;
			for(int level = 0; level <= lca1; level++){
				SongCluster current = (SongCluster) next;
				current.localUpdate(action, TrackLink.SOURCE_TO_TARGET);
				next = current.getChildren().get(sourceBranch.get(level));
			}
			next = this;
			for(int level = 0; level <= lca2; level++){
				SongCluster current = (SongCluster) next;
				current.localUpdate(action, TrackLink.SOURCE_TO_CHOSEN);
				next = current.getChildren().get(sourceBranch.get(level));
			}
			next = this;
			for(int level = 0; level <= lca3; level++){
				SongCluster current = (SongCluster) next;
				current.localUpdate(action, TrackLink.TARGET_TO_CHOSEN);
				next = current.getChildren().get(sourceBranch.get(level));
			}
		}
		
	}
	
	/*
	 * Update just this level's p matrix for the given action
	 */
	private void localUpdate(UserAction action, int link){
		int type = action.type;
		double val = action.value;
		Song source = action.source;
		Song target = action.target;
		Song chosen = action.chosen;
		
		Song from = null, to = null;
		double reward = 0.0;
		
		// Set 'from', 'to', and 'reward' based on type of action
		switch(type){
		case UserAction.TRACK_FINISHED:
			{
				// User finished track. Ignore val
				from = source;
				to = target;
				reward = Constants.REWARD_TRACK_FINISHED;
			} break;
		case UserAction.TRACK_SKIPPED:
			{
				// User skipped track. Use val
				from = source;
				to = target;
				double max = Constants.REWARD_TRACK_SKIPPED_MAX;
				double min = Constants.REWARD_TRACK_SKIPPED_MIN;
				double time_played = val;
				reward =  min + (time_played * (max - min));
			} break;
		case UserAction.TRACK_CHANGED:
			{
				// User changed track manually. Use val and source,target,chosen
				switch(link){
				case TrackLink.SOURCE_TO_TARGET:
					{
						// User chose new track so 'target' got skipped. Use val.
						from = source;
						to = target;
						double max = Constants.REWARD_TRACK_SKIPPED_MAX;
						double min = Constants.REWARD_TRACK_SKIPPED_MIN;
						double time_played = val;
						reward =  min + (time_played * (max - min));
					} break;
				// The influence of the next two depends on how far through the track the user was. They behave in the same way.
				// ie. skipped straight away -> no inference target-to-chosen, but skipped later -> no inference source-to-chosen
				// No negative rewards here. Total reward given is currently max+min, since same ranges used for both links
				case TrackLink.SOURCE_TO_CHOSEN:
					{
						// NB: (val = 1 - val) below
						from = source;
						to = chosen;
						double max = Constants.REWARD_TRACK_CHOSEN_MAX;
						double min = Constants.REWARD_TRACK_CHOSEN_MIN;
						double time_played = val;
						reward =  min + ((1 - time_played) * (max - min));
					} break;
				case TrackLink.TARGET_TO_CHOSEN:
					{
						from = target;
						to = chosen;
						double max = Constants.REWARD_TRACK_CHOSEN_MAX;
						double min = Constants.REWARD_TRACK_CHOSEN_MIN;
						double time_played = val;
						reward =  min + (time_played * (max - min));
					} break;
				}
			} break;
		case UserAction.PLAYLIST_SHARED:
			{
				// Both tracks are in the same playlist
				from = source;
				to = target;
				reward = Constants.REWARD_TRACK_PLAYLIST;
			} break;
		case UserAction.PLAYLIST_ADJACENT: 
			{
				// The tracks are adjacent in the same playlist
				from = source;
				to = target;
				reward = Constants.REWARD_TRACK_PLAYLIST_ADJ;
			} break;
		}
		
		UpdateP(from, to, reward);
		
		NormaliseP();
	}

	// For testing with artificial history data
	public void LearnHistory(List<UserAction> history) {
		for(UserAction ua : history)
			Update(ua);
	}
	
	/*
	 * Update p matrix
	 */
	private void UpdateP(Song from, Song to, double reward){
		if(from == null || to == null || reward == 0.0)
			return;
		
		int fromI = getClusterIndex(from);
		int toI = getClusterIndex(to);
		double pr = p.get(fromI).get(toI);
		
		// The highest probability in "to's row" 
		double optimal_future = 0.0;
		for(int i = 0; i<p.get(toI).size(); ++i)
			if(p.get(toI).get(i) > optimal_future)
				optimal_future = p.get(toI).get(i);
		
		pr = pr + Constants.LEARNING_RATE * (reward + Constants.DISCOUNT_FACTOR * optimal_future - pr);
		
		p.get(fromI).set(toI, pr);
	}
	
	/*
	 * Get path from root to a song in the clustering. e.g [5, 0, 2] means it's in level0 cluster 5, then level1 cluster 0, then in level2 cluster 2 is the LeafCluster
	 */
	private List<Integer> getIndexList(Song s){
		if(s == null)
			return null;
		int c = getClusterIndex(s);
		List<Integer> ls = new ArrayList<Integer>();
		if(c > -1){
			ls.add(c);
			if(!(clusters.get(c) instanceof LeafCluster))
				ls.addAll(((SongCluster) clusters.get(c)).getIndexList(s));
			return ls;
		}else
			return null;
	}
	
	/*
	 * Given two index paths, returns the lowest level they have in common (ie. the split point of 2 songs' paths)
	 */
	private int getLowestCommonAncestor(List<Integer> l1, List<Integer> l2){
		if(l1 == null || l2 == null || l1.size() < 1 || l2.size() < 1)
			return -1;
		int level = 0;
		for(int i=0; i<Math.min(l1.size(), l2.size()); ++i){
			level = i;
			if(l1.get(i) != l2.get(i)){
				break;
			}
		}
		return level;
	}
	
	/*
	 * Set which track is playing by passing the track
	 */
	public void setPlayingCluster(Song s){
		// TODO: clear previous clusterplaying from any lower branches
		int index = getClusterIndex(s);
		if(clusterPlaying > -1 && clusterPlaying != index && clusters.get(clusterPlaying) instanceof SongCluster)
			((SongCluster) clusters.get(clusterPlaying)).clearPlaying();
		clusterPlaying = index;
		if(clusters.get(clusterPlaying) instanceof SongCluster)
			((SongCluster) clusters.get(clusterPlaying)).setPlayingCluster(s);
		
	}
	
	/*
	 * Clear trackplaying
	 */
	public void clearPlaying(){
		if(clusters.get(clusterPlaying) instanceof SongCluster)
			((SongCluster) clusters.get(clusterPlaying)).clearPlaying();
		clusterPlaying = -1;
	}

	public void Reset() {
		playing = false;
		clusterPlaying = -1;
	}
	
	public List<AbstractCluster> getChildren(){
		return clusters;
	}
	
	// See MusicPlayer.PrintClusters for full hierarchical output
	@SuppressWarnings("unused")
	private void PrintClusters(List<ArrayList<Song>> cs) {
		for (int i = 0; i < cs.size(); ++i) {
			System.out.println("Cluster " + i + " ---------------- ");
			for (int j = 0; j < cs.get(i).size(); ++j) {
				System.out.println(cs.get(i).get(j).getArtist() + " -- " + cs.get(i).get(j).getName());
			}
			System.out.println();
		}
	}
	
	public void PrintP(){
		System.out.println("Probability matrix at level "+level+":");
		System.out.println();
		for(int i=0; i<p.size(); ++i){
			for(int j=0; j<p.get(i).size(); ++j){
				System.out.print(p.get(i).get(j) + " | ");
			}
			System.out.println();
		}
		System.out.println();
	}
	

	// The "actions" in the Markov Decision Process
	public class UserAction implements Serializable {
		private static final long serialVersionUID = 1L;
		
		/*
		 * The types of user action which may affect the learning in different ways
		 */
		final static int TRACK_FINISHED = 0;    // user listened to 'target' all the way through: positive link with 'source' 
		final static int TRACK_SKIPPED = 1;     // user skipped 'target' when chosen after 'source' after (value * target.length) seconds through song
		final static int TRACK_CHANGED = 2;     // user picked song 'chosen' after (value * target.length) seconds through 'target' which was chosen after 'source'
		final static int PLAYLIST_SHARED = 3;   // 'source' and 'target' are in the same user-playlist (imported or otherwise)
		final static int PLAYLIST_ADJACENT = 4; // 'target' directly follows 'source' in a user-playlist (imported or otherwise)
		
		public int type; // one of the above
		public double value; // 0.0 - 1.0  eg. fraction of way through track at which user skipped
		public Song source;
		public Song target;
		public Song chosen;
		
		public UserAction(int type, double val, Song s, Song t, Song c){
			this.type = type;
			this.value = val;
			this.source = s;
			this.target = t;
			this.chosen = c;
		}
		
		public boolean requiresChosen(){
			return (type == TRACK_CHANGED);
		}
	}
	
	private static class TrackLink implements Serializable {
		private static final long serialVersionUID = 1L;
		final static int SOURCE_TO_TARGET = 0;   
		final static int SOURCE_TO_CHOSEN = 1;  
		final static int TARGET_TO_CHOSEN = 2; 
	}
}
