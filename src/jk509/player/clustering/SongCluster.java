package jk509.player.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jk509.player.core.Song;

public class SongCluster extends AbstractCluster {

	private List<AbstractCluster> clusters; // child clusters. only non-null if leaf==False
	private ArrayList<ArrayList<Double>> p; // cluster transition probabilities between all child clusters. only non-null if leaf==False

	private int clusterPlaying = -1; // only > -1 if playing==True. Equivalent to the current "state" in a machine-learning sense

	public SongCluster(int level, SongCluster parent) {
		super(level, parent);
		leaf = false;
		p = new ArrayList<ArrayList<Double>>();
		clusters = new ArrayList<AbstractCluster>();
	}
	
	public SongCluster(int level, SongCluster parent, List<Song> tracks){
		this(level, parent);
		ClusterFirstTime(tracks);
	}
	public SongCluster(LeafCluster c){
		this(c.getLevel(), c.getParent(), new ArrayList<Song>(Arrays.asList(c.getTrack())));
	}

	public int getClusterPlaying() {
		return clusterPlaying;
	}

	public void setClusterPlaying(int c) {
		clusterPlaying = c;
	}

	public void AddTrack(Song s) {
		// TODO
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

	public void Update(UserAction action) {
		// TODO
		switch(action.type){
		case UserAction.TRACK_FINISHED: 
			
			break;
		case UserAction.TRACK_SKIPPED:
			
			break;
		}
	}
	
	// TODO, for testing with artificial history data
	public void LearnHistory(){
		
	} 
	
	private void ClusterFirstTime(List<Song> tracks){
		// initialise p
		/*set all to 0
		p.add
		
		// initialise clusters
		clusters.add
		
		needs to be recursive. use AddTrack?  use algorithm formula from wiki for Q-learning*/
	}

	public Song next() {
		// TODO
		/*
		 * choose most probable next cluster, taking into account the `randomness'. 
		 * Use heuristic to choose track within that cluster (what if staying within cluster?). 
		 * If all these tracks played recently, may choose next cluster.
		 * 
		 */
		return new Song();
	}
	
	public void Reset(){
		playing = false;
		clusterPlaying = -1;
	}
	
	// The "actions" in the Markov Decision Process
	public class UserAction {
		final static int TRACK_FINISHED = 0;
		final static int TRACK_SKIPPED = 1;
		public int type; // one of the above
	}
}
