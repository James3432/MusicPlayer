package jk509.player.clustering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JFrame;

import jk509.player.Constants;
import jk509.player.core.Song;
import jk509.player.core.StaticMethods;
import jk509.player.gui.GUIupdater;
import jk509.player.gui.Updater;
import jk509.player.learning.UserAction;
import jk509.player.logging.Logger;
import jk509.player.logging.Logger.LogType;
import weka.core.Instance;

public class SongCluster extends AbstractCluster {

	private static final long serialVersionUID = 1L;
	
	private List<AbstractCluster> clusters; // child clusters. only non-null if leaf==False
	public ArrayList<ArrayList<Double>> p; // cluster transition probabilities between all child clusters. only non-null if leaf==False
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
	//private Deque<Song> history;
	//private SimpleKMeans clusterController;
	protected double[] centroid; // These aren't normalised
	
	private int clusterPlaying = -1; // only > -1 if playing==True. Equivalent to the current "state" in a machine-learning sense

	public SongCluster(List<Song> songs, Updater updater){
		this(songs, 0, null, updater);
	}
	public SongCluster(List<Song> songs, int level, SongCluster parent, Updater updater) {
		super(songs, level, parent);
		leaf = false;
		ResetClusters(updater);
	}

	public SongCluster(LeafCluster c/*, GUIupdater updater*/) {
		super(new ArrayList<Song>(c.getTracks()), c.getLevel(), c.getParent());
		leaf = false;
		clusters = new ArrayList<AbstractCluster>();
		clusters.add(c);
		c.setLevel(c.getLevel()+1);
		assignments = new int[1];
		assignments[0] = 0;
		preferred = new double[1]; // TODO init values?
		preferred[0] = 0.5;
		centroid = c.getTrack().getAudioFeatures();
		InitPMatrix();
	}
	
	/*
	 * Reset features and rebuild clustering
	 */
	public void ResetAll(Updater updater){
		for(Song s : tracks)
			s.setAudioFeatures(null);
		ResetClusters(updater);
	}
	
	/*
	 * Rebuild entire clustering from here downwards, creating new child clusters
	 */
	public void ResetClusters(Updater updater){

		if(level == 0)
			updater.suspend();
		
		clusters = new ArrayList<AbstractCluster>();
		
		// TODO could be a bug here, as tracks features won't be analysed!
		if(tracks.size() <= Constants.MAX_CLUSTERS){
			assignments = new int[tracks.size()];
			preferred = new double[tracks.size()];
			double pr = 1. / tracks.size();
			for(int i=0; i<tracks.size(); ++i)
				preferred[i] = pr;
			
			for(int i=0; i<tracks.size(); ++i){
				LeafCluster leaf = new LeafCluster(level + 1, this, tracks.get(i));
				//leaf.setCentroid(tracks.get(i).getAudioFeatures());
				clusters.add(leaf);
				assignments[i] = i;
				preferred[i] += (tracks.get(i).getPlayCount() * 1. / tracks.size());
			}
		}else{
		
			AbstractClusterer clusterer = new KMeansClusterer(tracks);
			clusterer.run(updater);
			// PrintClusters(clusterer.getResult());
			tracks = clusterer.getTracksUsed();
			assignments = clusterer.getAssignments();
			if(assignments == null)
				assignments = new int[0];
			//clusterController = ((KMeansClusterer) clusterer).getClusterer();
			//System.out.println(clusterController.getOptions());
			//System.out.println("=========*******************=============");
			//System.out.println(clusterController.listOptions());
			
			List<ArrayList<Song>> cs = clusterer.getResult();
			preferred = new double[cs.size()];
			double pr = 1. / cs.size();
			for(int i=0; i<cs.size(); ++i)
				preferred[i] = pr;
			int i=0;
			for (ArrayList<Song> cluster : cs) {
				Instance in = ((KMeansClusterer) clusterer).getClusterer().getClusterCentroids().instance(i);
				double[] centroid = in.toDoubleArray();
				AbstractCluster newCluster = null;
				if (cluster.size() > 1){
					newCluster = /*clusters.add(*/new SongCluster(cluster, level + 1, this, updater);
				}else if (cluster.size() == 1){
					newCluster = /*clusters.add(*/new LeafCluster(level + 1, this, cluster.get(0));
				}
				clusters.add(newCluster);
				preferred[i] += getTotPreferred(newCluster);
				newCluster.setCentroid(centroid);
				//if(level==0)
					//System.out.println("Root cluster added");
				++i;
			}
		}
		
		// Don't need to call ResetLearning because each level's constructor calls this function, so initP gets called at every level
		InitPMatrix();
		
		Logger.backupClusters(this);
		
		// Clear out temp file
		StaticMethods.deleteTempFiles();
		
		if(level == 0)
			updater.resume();
	}
	
	/*
	 * Reset all P matrices in this and child clusters
	 */
	public void ResetLearning(){
		p = null;
		InitPMatrix();
		int size = getChildren().size();
		preferred = new double[size];
		double pr = 1. / size;
		for(int i=0; i<size; ++i)
			preferred[i] = pr;
		for(AbstractCluster c : clusters)
			if(c instanceof SongCluster)
				((SongCluster) c).ResetLearning();
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
	
	/*public void clearHistory(){
		history = new ArrayDeque<Song>();
	}
	
	public Deque<Song> getHistory(){
		return history;
	}
	
	public void addHistory(Song s){
		history.add(s);
		while(history.size() > Constants.HISTORY_SIZE)
			history.pop();
	}*/
	
	/*
	 *  Normalises the matrix P. Also corrects any negative values to 0, and re-initialises a row if it's sum = 0
	 */
	private void NormaliseP() {
		// For each row...
		for (int i = 0; i < p.size(); ++i) {
			
			// Deal with negatives by adding least value to all
			double smallest = 0.0;
			for (int j = 0; j < p.get(i).size(); ++j) {
				double val = p.get(i).get(j);
				if(val < smallest){
					smallest = val;
				}
			}
			// add least value to all only if needed
			if(smallest < 0.0){
				for (int j = 0; j < p.get(i).size(); ++j) {
					p.get(i).set(j, p.get(i).get(j) + (- smallest));
				}
			}
			// Now find row sum (for scaling later)
			double tot = 0.0;
			for (int j = 0; j < p.get(i).size(); ++j) {
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
			// Otherwise, scale to normalise
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
				try{
					ls.addAll(((SongCluster) clusters.get(clusterPlaying)).getClusterPlayingPath());
				}catch(Exception e){
					return null;
				}
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
	
	protected void recomputeCentroid(){
		int size = getChildren().size();
		for(int x=0; x<centroid.length; ++x){
			double tot = 0.0;
			for(AbstractCluster c : getChildren())
				tot += c.getCentroid()[x];
			centroid[x] = tot / size;
		}
	}

	/*
	 * Add track, assuming it's not a duplicate
	 */
	private boolean AddTrack(Song s, Updater updater) {
	
		List<Song> ls = new ArrayList<Song>();
		ls.add(s);
		AbstractClusterer clusterer = new KMeansClusterer(ls);
		clusterer.run(updater);
		// PrintClusters(clusterer.getResult());
		List<Song> used = clusterer.getTracksUsed();
		if(used.contains(s) && s.getAudioFeatures() != null){
		
			AbstractCluster current = this;
			boolean addClusterHere = false;
			while( ! (current instanceof LeafCluster || addClusterHere) ){
				current.tracks.add(s);
				addClusterHere = (current instanceof SongCluster && ((SongCluster) current).getChildren().size() < Constants.MAX_CLUSTERS);  // NOTE this requires max_clusters to match library!!
				int closest = StaticMethods.nearestCluster(((SongCluster) current).getChildren(), s);
				if(StaticMethods.computeDistance(((SongCluster) current).getChildren().get(closest).getCentroid(), s.getAudioFeatures()) < Constants.SAME_CLUSTER_DIST_THRESHOLD)
					addClusterHere = false;
				if(!addClusterHere){
					((SongCluster) current).assignments = arrayAppend(((SongCluster) current).assignments, closest);
					current = ((SongCluster) current).getChildren().get(closest);
				}
			}
			if(current instanceof LeafCluster){
				SongCluster newCluster = new SongCluster((LeafCluster) current);
				newCluster.getChildren().add(new LeafCluster(newCluster.getLevel()+1, newCluster, s));
				SongCluster root = current.getParent();
				//root.clusters.remove(current);
				//root.clusters.add(newCluster);
				root.getChildren().set(root.getChildren().indexOf(current), newCluster);
				newCluster.assignments = new int[]{0, 1}; 
				newCluster.preferred = new double[2];
				newCluster.preferred[0] = 0.5; newCluster.preferred[1] = 0.5;
				newCluster.tracks.add(s);
				newCluster.InitPMatrix();
				current = newCluster;
			}else{
				((SongCluster) current).getChildren().add(new LeafCluster(current.getLevel()+1, (SongCluster) current, s));
				((SongCluster) current).assignments = arrayAppend(((SongCluster) current).assignments, ((SongCluster) current).getChildren().size()-1);
				((SongCluster) current).preferred = arrayAppend(((SongCluster) current).preferred, 1. / ((SongCluster) current).getChildren().size());
				((SongCluster) current).AddClusterP();
			}
			
			while(current.level > 0){
				((SongCluster) current).recomputeCentroid();
				current = current.getParent();
			}
		
			return true;
			
		}else{
			// error processing
			return false;
		}
				
		/*
		 * at lowest level, add to songcluster if size < cluster_size, else group with child into new songcluster
		 * 
		 * add to: clusters, tracks, preferred, p, assignments at each relevant level
		 * 
		 * just use centroid distances. alternative was:
		 * 
		clusterController.clusterInstance(instance);
		clusterController.distributionForInstance(instance);
		*/
	}

	public boolean AddTracks(List<Song> ss, JFrame form) {
		boolean result = true;
		GUIupdater updater = new GUIupdater(form, false);
		updater.suspend();
		for(Song s : ss)
			if(!AddTrack(s, updater))
				result = false;
		updater.resume();
		return result;
	}

	public void RemoveTrack(Song s) {
		int cluster = getClusterIndex(s);
		if(cluster < 0)
			return; // track not here
		
		if(clusterPlaying > -1 && getClusterPlayingSong() == s)
			clearPlaying();
		
		// This is a songcluster, so can safely do:
		assignments = arrayRemove(assignments, tracks.indexOf(s));
		tracks.remove(s);
		
		int deletedIndex = -1;
		
		if(getChildren().get(cluster) instanceof LeafCluster){
			// delete whole leafCluster
			deletedIndex = cluster;
		}else{
			// recurse
			((SongCluster) getChildren().get(cluster)).RemoveTrack(s);
			if(((SongCluster) getChildren().get(cluster)).getChildren().size() < 1){
				// delete songcluster, as it is now empty
				deletedIndex = cluster;
			}else if(((SongCluster) getChildren().get(cluster)).getChildren().size() == 1){
				// possibly convert to leafcluster
				SongCluster old = (SongCluster) getChildren().get(cluster);
				if(old.getChildren().get(0) instanceof LeafCluster){
					LeafCluster oldLeaf = (LeafCluster) old.getChildren().get(0);
					LeafCluster newLeaf = new LeafCluster(old.level, old.getParent(), oldLeaf.getTrack());
					//replace old with newLeaf
					getChildren().set(cluster, newLeaf);
				}
			}
		}
			
		if(deletedIndex > -1){
		
			getChildren().remove(deletedIndex);
			
			if(deletedIndex < clusterPlaying)
				clusterPlaying--;
			
			for(int i=0; i<assignments.length; ++i)
				if(assignments[i] > deletedIndex)
					assignments[i] --;
			
			preferred = arrayRemove(preferred, deletedIndex);
			RemoveClusterP(deletedIndex);
			
			NormaliseP();
			
		}
	}
	
	/*
	 * remove element from an array
	 */
	protected double[] arrayRemove(double[] arr, int index){
		if(index >= arr.length)
			return arr;
		double[] out = new double[arr.length-1];
		System.arraycopy(arr, 0, out, 0, index);
		System.arraycopy(arr, index+1, out, index, arr.length - 1 - index);
		return out;
	}
	protected int[] arrayRemove(int[] arr, int index){
		if(index >= arr.length)
			return arr;
		int[] out = new int[arr.length-1];
		System.arraycopy(arr, 0, out, 0, index);
		System.arraycopy(arr, index+1, out, index, arr.length - 1 - index);
		return out;
	}
	protected double[] arrayAppend(double[] arr, double a){
		double[] out = new double[arr.length+1];
		System.arraycopy(arr, 0, out, 0, arr.length);
		out[arr.length] = a;
		return out;
	}
	protected int[] arrayAppend(int[] arr, int a){
		int[] out = new int[arr.length+1];
		System.arraycopy(arr, 0, out, 0, arr.length);
		out[arr.length] = a;
		return out;
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
			int index = tracks.indexOf(s);
			int cluster = assignments[index];
			return cluster;
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
	public Song next(List<Song> history, List<Double> history_weights, double randomness) {
		/*
		 * choose most probable next cluster, taking into account the `randomness'. Use heuristic to choose track within that cluster (what if staying within cluster?). If all these tracks played recently, may choose next cluster.
		 * update clusterPlaying, or let calling method do that? (we don't know if it plays it or not...although we'll hardly be changing it upon every pause/play)
		 */
		//start at root, pick clusters traversing down. Heuristic if different branch from currplaying.
		
		// Don't allow call unless we are at root
		if(this.level != 0)
			return null;
		
		if(history == null){
			history = new ArrayList<Song>();
			history_weights = new ArrayList<Double>();
		}
		else if(history_weights == null)
			history_weights = generateHistoryWeights(history.size());
		
		Song choice = null;
		
		//double randomness = getNetRandomness();
		
		if(clusterPlaying < 0 || clusterPlaying >= clusters.size()){
			choice = heuristicChoice(null, this, history, history_weights);
		}else{
			// Location of what was previously playing
			List<Integer> sourcePath = getClusterPlayingPath();
			Song source = getClusterPlayingSong();
			if(sourcePath == null || source == null){
				choice = heuristicChoice(null, this, history, history_weights);
			}else{
				AbstractCluster next = this;
				int nextCluster = 0;
				for(int level = 0; level <= sourcePath.size(); ++level){
					if(!(next instanceof SongCluster)){
						// base case: need to step back up a level probably
						// several cases: source a leaf but next isnt, next a leaf but source isn't, both leaves. stepping up a level
						// account for all factors in heuristic method
						SongCluster nextup = next.getParent();
						choice = heuristicChoice(source, nextup, history, history_weights);
						break;
					}else{
						SongCluster current = (SongCluster) next;
						//choose next cluster
						ArrayList<Double> row = current.getP().get(sourcePath.get(level));
						nextCluster = chooseNextCluster(row, randomness);
						if(nextCluster < 0){
							choice = heuristicChoice(source, current, history, history_weights);
						}else if(nextCluster == sourcePath.get(level))
							next = current.getChildren().get(nextCluster); // the only recursive case
						else{
							if(current.getChildren().get(nextCluster) instanceof LeafCluster)
								choice = heuristicChoice(source, current, history, history_weights);
							else
								choice = heuristicChoice(source, (SongCluster) current.getChildren().get(nextCluster), history, history_weights);
							break;
						}
					}
				}
			}
		}
		
		setPlayingCluster(choice);
		
		return choice;
	}
	
	private void DeNan(ArrayList<Double> row){
		boolean reset = false;
		for(int i=0; i<row.size(); ++i){
			if (Double.isNaN(row.get(i)))
				reset = true;
		}
		if(reset){
			for(int i=0; i<row.size(); ++i){
				row.set(i, 1. / row.size());
			}
			Logger.log("Removed NaN values from P matrix", LogType.ERROR_LOG);
		}
	}
	
	/*
	 * Pick the next cluster to explore. If we've played all these tracks too recently, the heuristic will look one or more levels upwards
	 */
	private int chooseNextCluster(ArrayList<Double> row, double randomness){
		/*
		 * Randomness is the key variable here: from 0 (not random - exploitation) to 1.0 (fully random - exploration)
		 */
		DeNan(row);
		
		// Find the most probable next cluster
		int maxrow = -1;
		double max = 0.;
		for(int i=0; i<row.size(); ++i){
			if(row.get(i) > max){
				max = row.get(i);
				maxrow = i;
			}
		}
	
		// The inbetween choice, using randomness to skew probabilities from {0, 0, 1, 0, 0} (ie. the above) to fully random (ie. more extreme than below)
		double evens = 1. / row.size();
		
		ArrayList<Double> newrow = new ArrayList<Double>();
		for(int i=0; i<row.size(); ++i){
			newrow.add(StaticMethods.Interpolate(randomness, (i==maxrow ? 1 : 0), row.get(i), evens));
		}
		
		// Normalise
		double tot = 0.;
		for(int i=0; i<newrow.size(); ++i){
			tot += newrow.get(i);
		}
		for(int i=0; i<newrow.size(); ++i){
			newrow.set(i, (tot==0.0 ? 1. / newrow.size() : newrow.get(i) / tot));
		}
		
		// Pick cluster based on probabilities
		double rand = Math.random();
		tot = 0.;
		int choice = -1;
		for(int i=0; i<newrow.size(); ++i){
			tot += newrow.get(i);
			if(rand < tot){
				choice = i;
				break;
			}
		}
		
		if(choice < 0){
			Logger.log("Error choosing next cluster. Tot="+tot+" Rand="+rand+" RowInSize="+row.size()+" RowOutSize="+newrow.size()+"  Row:"+row.toString()+"  NewRow: "+newrow.toString(), LogType.ERROR_LOG);
		}
		
		return choice;
	}
	
	/*
	 * Factors to account for:
	 *  - 'preferred'
	 *  - randomnesss
	 *  - recent play list (how to do this? don't want to change weights)
	 *  - audio features (just distance between feature vectors. future option of grabbing start/end audio features)
	 *  
	 *  This method searches for tracks within cluster. Will step up a level to parent cluster if none found
	 */
	private Song heuristicChoice(Song source, SongCluster startcluster, List<Song> history, List<Double> history_weights){
		// double randomness = getNetRandomness();
		// won't use randomness here at all
		Song choice = null;
		SongCluster cluster = startcluster; // TODO: set Constants.MIN_HEURISTIC_SIZE then if not enough options here, jump up a level (if parent != null). ideally want this after counting #tracks not in history though.
		while (choice == null && cluster != null){
		
			List<Song> songs = cluster.getTracks();
			double[] votes = new double[songs.size()];
			
			if(source == null){
				// we are choosing from all songs
				/*while(choice == null || history.contains(choice)){
					randomness, preferred. don't go up at all, only down
					if(exhausted)
						break; // return null;
				}*/
				
				// Basic implementation which uses 'preferred'
				
				// Removed, inefficient:
				/*for(int i=0; i<votes.length; ++i)
					votes[i] = 1.0;
				Stack<SongCluster> next = new Stack<SongCluster>();
				next.push(s_cluster);
				while(!next.empty()){
					SongCluster current = next.pop();
					int i=0;
					for(AbstractCluster c : current.getChildren()){
						double prefs = current.preferred[i++];
						if(c instanceof SongCluster){
							List<Song> local = c.getTracks();
							for(int j=0; j<songs.size(); ++j)
								if(local.contains(songs.get(j)))
									votes[j] *= prefs;
							next.push((SongCluster) c);
						}
					}
				}*/
				SongCluster next = cluster;
				while(true){
					double[] prefs = next.preferred;
					int h = StaticMethods.maxArrayIndex(prefs);
					AbstractCluster child = next.getChildren().get(h);
					if(child instanceof LeafCluster){
						// use 
						Song s = ((LeafCluster) child).getTrack();
						int v = songs.indexOf(s);
						if(v > -1)
							votes[v] = 100;
						else
							votes[0] = 100; // error
						break;
					}else{
						next = (SongCluster) child;
					}
				}
				
			}else{
				// heuristic called because we've made a long jump between clusters so no P matrix is available
				/*while(choice == null || history.contains(choice)){
					distances, randomness, preferred. don't go up at all, only down
					recurse into child clusters and recompute distances
					if(exhausted)
						break; // return null;
				}*/
				
				// distance-to-cluster-centroid traversing code (needs to be made recursive)
				/*int childClusters = cluster.getChildren().size();
				double[][] centroids = new double[childClusters][];
				for(int i=0; i<childClusters; ++i)
					centroids[i] = cluster.getChildren().get(i).getCentroid();
				
				double[] distances = new double[childClusters];
				for(int i=0; i<childClusters; ++i)
					distances[i] = StaticMethods.computeDistance(features, centroids[i]);*/
				
				// Basic implementation which uses feature distances
				double[] features = Normalise(source.getAudioFeatures());
				double[] dists = new double[songs.size()];
				for(int i=0; i<songs.size(); ++i){
					dists[i] = StaticMethods.computeDistance(features, Normalise(songs.get(i).getAudioFeatures()));
					votes[i] = 1. / dists[i]; // a higher vote is better, which corresponds to a shorter distance
				}
				
				//double[] prefs = normTo(cluster.preferred, Constants.MAX_PREFERRED_EFFECT);
				for(int i=0; i<songs.size(); ++i){
					int clust = cluster.getClusterIndex(songs.get(i));
					votes[i] *= cluster.preferred[clust];
				}
			}
			
			/*
			 * Weight any tracks in history
			 */
			for(int i=0; i<songs.size(); ++i){
				if(Double.isInfinite(votes[i]))
					votes[i] = Double.MAX_VALUE;
				if(songs.get(i) == null || (source != null && songs.get(i).equals(source)))
					votes[i] = 0;
				int pos = history.indexOf(songs.get(i));
				if(pos > -1)
					votes[i] *= history_weights.get(pos);
				if(Double.isNaN(votes[i]))
					votes[i] = 0.0;
			}
			
			/*
			 * Now pick the top ranked song
			 */
			final Integer[] indices = new Integer[songs.size()];
			for(int i=0;i<indices.length;++i)
				indices[i] = i;
			final double[] weights = votes;
	
			Arrays.sort(indices, new Comparator<Integer>() {
			    @Override public int compare(final Integer o1, final Integer o2) {
			        return Double.compare(weights[o1], weights[o2]);
			    }
			});
			int top_song = indices[indices.length-1];
			choice = songs.get(top_song);
			
			// Fine because, infinte loop impossible
			if(weights[top_song] <= 0.0)
				choice = null;
			
			// Hop back up the cluster tree until heuristicChoice is successful
			cluster = cluster.getParent();
		}
		
		if(choice == null){
			System.out.println("Couldn't choose next track");
			Logger.log("Couldn't choose next track in SongCluster line 767", LogType.ERROR_LOG);
			
			choice = chooseRandom(startcluster, history);
		}
		
		return choice;
	}
	
	private Song chooseRandom(SongCluster cluster, List<Song> history){
		if(cluster.tracks.size() > history.size()){
			// just pick from cluster 
			List<Song> selection = new ArrayList<Song>(cluster.tracks);
			selection.removeAll(history);
			// now pick at random
			int index = 0 + (int)(Math.random() * selection.size());
			return selection.get(index);
		}else{
			SongCluster root = cluster;
			while(root.getParent() != null)
				root = root.getParent();
			if(root.tracks.size() > history.size()){
				// just pick from root
				List<Song> selection = new ArrayList<Song>(root.tracks);
				selection.removeAll(history);
				// now pick at random
				int index = 0 + (int)(Math.random() * selection.size());
				return selection.get(index);
			}else{
				if(root.tracks.size() > Constants.HISTORY_NONREPEAT){
					// pick something played a whle ago
					if(history != null && history.size() > 0)
						return history.get(0);
					else
						return null;
				}else{
					// something's wrong
					return null;
				}
			}
		}
	}
	
	private double[] Normalise(double[] in){
		double[] out = new double[in.length];
		out[0] = in[0] / 22.;
		out[1] = in[1] / 0.15;
		out[2] = in[2] / 0.002;
		out[3] = in[3] / 1600;
		out[4] = in[4] / 0.003;
		out[5] = in[5] / 0.15;
		out[6] = in[6] / 0.55;
		out[7] = in[7] / 60;
		out[8] = in[8] / 200;
		out[9] = in[9] / 1000;
		out[10] = in[10] / 0.009;
		out[11] = (in[11] + 500) / 400;
		out[12] = in[12] / 3;
		out[13] = in[13] / 3;
		out[14] = in[14] / 2;
		out[15] = in[15] / 1;
		out[16] = in[16] + 1;
		out[17] = in[17] + 1;
		out[18] = in[18] + 1;
		out[19] = in[19] + 1;
		out[20] = in[20] + 1;
		out[21] = in[21] + 1;
		out[22] = in[22] + 1;
		out[23] = in[23] + 1;
		out[24] = in[24] + 2;
		out[25] = in[25] + 1;
		out[26] = in[26] + 1;
		out[27] = in[27] + 1;
		out[28] = in[28] + 1;
		out[29] = in[29] + 1;
		out[30] = in[30] + 1;
		out[31] = in[31] + 1;
		out[32] = in[32] + 1;
		out[33] = in[33] + 1;
		out[34] = in[34] / 0.5;
		out[35] = in[35] / 50;
		out[36] = in[36] / 3000;
		out[37] = in[37] / 200000;
		out[38] = in[38] / 30000000;
		return out;
	}
	
	@SuppressWarnings("unused")
	private double[] normTo(double[] arr, double max){
		double highest = 0;
		for(int i=0; i<arr.length; ++i)
			if(arr[i] > highest)
				highest = arr[i];
		double scale = max / highest;
		double[] out = new double[arr.length];
		for(int i=0; i<arr.length; ++i)
			out[i] = arr[i] * scale;
		return out;
	}
	
	/*
	 * Update p matrix as a result of an action
	 * 
	 * This method should only be called on the root tree. Recursion down to lower levels is performed internally
	 * 
	 */
	public void Update(UserAction action) {
		
		// Don't allow call unless we are at root
		if(this.level != 0)
			return;
		
		Logger.log(action.toString(), LogType.LEARNING_LOG);
		if(Constants.DEBUG_DISPLAY_UPDATES){
			System.out.println(action.toString());
		}
		
		Song source = action.source;
		Song target = action.target;
		Song chosen = action.chosen;
		
		//find lowest common ancestor, update nodes from here up. 
		List<Integer> sourceBranch = getIndexList(source);
		List<Integer> targetBranch = getIndexList(target);
		
		int lca = getLowestCommonAncestor(sourceBranch, targetBranch);

		AbstractCluster next = this;
		
		if(!action.requiresChosen()){
			for(int level = 0; level <= lca; level++){
				SongCluster current = (SongCluster) next;
				current.localUpdate(action, TrackLink.SOURCE_TO_TARGET, levelMultiplier(lca - level));
				next = current.getChildren().get(sourceBranch.get(level));
			}
		}else{
		//if(action.requiresChosen()){
			List<Integer> chosenBranch = getIndexList(chosen);
			int lca1 = lca;
			int lca2 = getLowestCommonAncestor(sourceBranch, chosenBranch);
			int lca3 = getLowestCommonAncestor(targetBranch, chosenBranch);
			next = this;
			// Turns out this was getting duplicated since any track change calls TRACK_SKIPPED
			// Update: not any more!
			for(int level = 0; level <= lca1; level++){
				SongCluster current = (SongCluster) next;
				current.localUpdate(action, TrackLink.SOURCE_TO_TARGET, levelMultiplier(lca1 - level));
				next = current.getChildren().get(sourceBranch.get(level));
			}
			next = this;
			for(int level = 0; level <= lca2; level++){
				SongCluster current = (SongCluster) next;
				current.localUpdate(action, TrackLink.SOURCE_TO_CHOSEN, levelMultiplier(lca2 - level));
				next = current.getChildren().get(sourceBranch.get(level));
			}
			next = this;
			for(int level = 0; level <= lca3; level++){
				SongCluster current = (SongCluster) next;
				current.localUpdate(action, TrackLink.TARGET_TO_CHOSEN, levelMultiplier(lca3 - level));
				next = current.getChildren().get(targetBranch.get(level));
			}
		}
		
	}
	
	// Exponential function which weights rewards more heavily for changes close the the lca (since all others are just i=j points in P)
	private double levelMultiplier(int distFromLca){
		return 1. / Math.pow(2, distFromLca);
	}
	
	/*
	 * Update just this level's p matrix for the given action
	 */
	private void localUpdate(UserAction action, TrackLink link, double reward_multiplier){
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
		case UserAction.TRACK_QUEUED:
			{
				// User queued track. Ignore val
				from = source;
				to = target;
				reward =  Constants.REWARD_TRACK_QUEUED;
			} break;
		case UserAction.TRACK_CHANGED:
			{
				// User changed track manually. Use val and source,target,chosen
				switch(link){
				case SOURCE_TO_TARGET:
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
				case SOURCE_TO_CHOSEN:
					{
						// NB: (val = 1 - val) below
						from = source;
						to = chosen;
						double max = Constants.REWARD_TRACK_CHOSEN_MAX;
						double min = Constants.REWARD_TRACK_CHOSEN_MIN;
						double time_played = val;
						reward =  min + ((1 - time_played) * (max - min));
					} break;
				case TARGET_TO_CHOSEN:
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
		
		reward = reward * reward_multiplier;
		
		UpdateP(from, to, reward);
		
		if(Constants.BACK_UPDATES)
			UpdateP(to, from, reward * Constants.BACK_UPDATE_SCALAR);
		
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
		
		// This is the only place 'preferred' gets updated
		preferred[toI] = Math.max(0., preferred[toI] + reward);
		// And add it to every parent
		SongCluster c = this;
		int ind = 0;
		try{
			while(c.getParent() != null){
				ind = c.getParent().getChildren().indexOf((AbstractCluster) c);
				c = c.getParent();
				c.preferred[ind] = Math.max(0., preferred[ind] + reward);
			}
		}catch(ArrayIndexOutOfBoundsException e){
			Logger.log("Error whilst updating matrix of preferred tracks:", LogType.ERROR_LOG);
			Logger.log(e, LogType.ERROR_LOG);
			try{
				while(c.preferred.length <= ind)
					c.preferred = arrayAppend(c.preferred, 0.);
				c.preferred[ind] = Math.max(0., preferred[ind] + reward);
			}catch(Exception e2){
				Logger.log("Error whilst updating matrix of preferred tracks:", LogType.ERROR_LOG);
				Logger.log(e2, LogType.ERROR_LOG);
			}
		}catch(Exception e){
			Logger.log("Error whilst updating matrix of preferred tracks:", LogType.ERROR_LOG);
			Logger.log(e, LogType.ERROR_LOG);
		}
	}
	
	/*
	 * Delete 1 cluster from P at this level
	 */
	private void RemoveClusterP(int r){
		// remove row
		p.remove(r);
		
		// remove column
		for(int i=0; i<p.size(); ++i)
			p.get(i).remove(r);
	}
	
	public double[] getPrefs(){
		return preferred;
	}
	
	/*
	 * Add a cluster and redistribute weights accordingly
	 */
	protected void AddClusterP(){
		for(int y=0; y<p.size(); ++y){
			double avg = 0.;
			for(int x=0; x<p.get(y).size(); ++x){
				avg += p.get(y).get(x);
			}
			avg = avg / p.get(y).size();
			p.get(y).add(avg);
		}
		
		ArrayList<Double> newrow = new ArrayList<Double>();
		for(int i=0; i<getChildren().size();++i)
			if(Constants.PROBABILITIES_INITIALLY_SPREAD)
				newrow.add(1. / getChildren().size());
			else
				newrow.add((i==getChildren().size()-1 ? 1.0 : 0.0));
		p.add(newrow);
		
		NormaliseP();
	}
	
	/*
	 * Get path from root to a song in the clustering. e.g [5, 0, 2] means it's in level0 cluster 5, then level1 cluster 0, then in level2 cluster 2 is the LeafCluster
	 */
	public List<Integer> getIndexList(Song s){
		if(s == null)
			return null;
		int c = getClusterIndex(s);
		List<Integer> ls = new ArrayList<Integer>();
		if(c > -1){
			ls.add(c);
			if(!(clusters.get(c) instanceof LeafCluster))
				try{
					ls.addAll(((SongCluster) clusters.get(c)).getIndexList(s));
				}catch(NullPointerException e){
					return null;
				}
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
	
	private List<Double> generateHistoryWeights(int l){
		// first element is oldest, last is most recent
		ArrayList<Double> res = new ArrayList<Double>();
		for(int i=0; i<l; ++i){
			res.add(Math.min(1.0, Math.max(0.0, Constants.HISTORY_WEIGHT_STEP * ((l-1) - i - Constants.HISTORY_NONREPEAT))));
		}
		return res;
	}
	
	/*
	 * Set which track is playing by passing the track
	 */
	public void setPlayingCluster(Song s){
		if(s == null){
			clearPlaying();
			return;
		}
		
		int index = getClusterIndex(s);
		if(clusterPlaying > -1 && clusterPlaying != index && clusters.get(clusterPlaying) instanceof SongCluster)
			((SongCluster) clusters.get(clusterPlaying)).clearPlaying();
		clusterPlaying = index;
		if(clusterPlaying > -1 && clusters.get(clusterPlaying) instanceof SongCluster)
			((SongCluster) clusters.get(clusterPlaying)).setPlayingCluster(s);
		
	}
	
	private double getTotPreferred(AbstractCluster c){
		if(c instanceof LeafCluster)
			return 0.;
		else{
			double tot = StaticMethods.arraySum(((SongCluster) c).preferred);
			for(AbstractCluster cs : ((SongCluster) c).getChildren())
				tot += getTotPreferred(cs);
			return tot;
		}
	}
	
	/*
	 * Clear trackplaying
	 */
	public void clearPlaying(){
		if(clusterPlaying > -1){
			if(clusters.get(clusterPlaying) instanceof SongCluster)
				((SongCluster) clusters.get(clusterPlaying)).clearPlaying();
			clusterPlaying = -1;
		}
	}

	public void Reset() {
		//playing = false;
		clusterPlaying = -1;
	}
	
	public List<AbstractCluster> getChildren(){
		return clusters;
	}
	
	public double[] getCentroid(){
		return centroid;
	}
	
	public void setCentroid(double[] d){
		centroid = d;
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
	public void PrintAllP(int maxlevel){
		System.out.println();
		int level = 0;
		int cluster = 0;
		Queue<AbstractCluster> clusts = new LinkedList<AbstractCluster>();
		clusts.add(this);
		int clustersPerLevel = 1;
		int clustersPerNextLevel = 0;
		while(!clusts.isEmpty()){
			AbstractCluster current = clusts.poll();
			if(current instanceof SongCluster){
				SongCluster c = ((SongCluster) current);
				System.out.println("Level "+level+" Cluster "+cluster);
				System.out.println();
				for(int i=0; i<c.p.size(); ++i){
					for(int j=0; j<c.p.get(i).size(); ++j){
						System.out.print(c.p.get(i).get(j) + " | ");
					}
					System.out.println();
				}
				System.out.println();
				clusts.addAll(c.getChildren());
				clustersPerNextLevel += c.getChildren().size();
			}else{
				System.out.println("Level "+level+" Cluster "+cluster);
				System.out.println();
				System.out.println("Leaf: "+((LeafCluster)current).getTrack().toString());
				System.out.println();
			}
			cluster++;
			if(cluster >= clustersPerLevel){
				clustersPerLevel = clustersPerNextLevel;
				clustersPerNextLevel = 0;
				level++;
				cluster = 0;
				if(level > maxlevel)
					break;
			}
		}
	}
	
	private enum TrackLink implements Serializable {
		SOURCE_TO_TARGET, 
		SOURCE_TO_CHOSEN, 
		TARGET_TO_CHOSEN
	}
}
