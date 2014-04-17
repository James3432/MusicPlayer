package jk509.player.clustering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import jk509.player.Constants;
import jk509.player.core.Song;

public class SongCluster extends AbstractCluster {

	private static final long serialVersionUID = 1L;
	
	private List<AbstractCluster> clusters; // child clusters. only non-null if leaf==False
	private ArrayList<ArrayList<Double>> p; // cluster transition probabilities between all child clusters. only non-null if leaf==False

	private int clusterPlaying = -1; // only > -1 if playing==True. Equivalent to the current "state" in a machine-learning sense

	public SongCluster(List<Song> songs, JFrame form){
		this(songs, 0, null, form);
	}
	public SongCluster(List<Song> songs, int level, SongCluster parent, JFrame form) {
		super(level, parent);
		leaf = false;

		clusters = new ArrayList<AbstractCluster>();
		
		//System.out.println(level);
		//System.out.println(songs.size());
		
		if(songs.size() <= Constants.MAX_CLUSTERS){
			for(int i=0; i<songs.size(); ++i){
				clusters.add(new LeafCluster(level + 1, this, songs.get(i)));
			}
		}else{
		
			AbstractClusterer clusterer = new KMeansClusterer(songs);
			clusterer.run(form);
			// PrintClusters(clusterer.getResult());
			
			List<ArrayList<Song>> cs = clusterer.getResult();
			for (ArrayList<Song> cluster : cs) {
				if (cluster.size() > 1)
					clusters.add(new SongCluster(cluster, level + 1, this, form));
				else if (cluster.size() == 1)
					clusters.add(new LeafCluster(level + 1, this, cluster.get(0)));
				//if(level==0)
					//System.out.println("Root cluster added");
			}
		}
		
		InitPMatrix();
	}

	public SongCluster(LeafCluster c, JFrame form) {
		super(c.getLevel(), c.getParent());
		leaf = false;
		clusters = new ArrayList<AbstractCluster>();
		clusters.add(c);
		InitPMatrix();
	}

	private void InitPMatrix() {
		p = new ArrayList<ArrayList<Double>>();
		int size = clusters.size();
		double prob = 1. / size;
		for (int i = 0; i < size; ++i) {
			p.add(new ArrayList<Double>());
			for (int j = 0; j < size; ++j) {
				p.get(i).add(prob);
			}
		}
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

		// sample
		int from = 5;
		int to = 7;
		double pr = p.get(from).get(to);

		pr += 0.1;
		
		switch (action.type) {
		case UserAction.TRACK_FINISHED:

			break;
		case UserAction.TRACK_SKIPPED:

			break;
		}
		
		p.get(from).set(to, pr);
	}

	// TODO, for testing with artificial history data
	public void LearnHistory() {

	}

	// unused (see constructor)
	//private void ClusterFirstTime(List<Song> tracks) {
		// initialise p
		/*
		 * set all to 0 p.add
		 * 
		 * // initialise clusters clusters.add
		 * 
		 * needs to be recursive. use AddTrack? use algorithm formula from wiki for Q-learning
		 */
	//}

	public Song next() {
		// TODO
		/*
		 * choose most probable next cluster, taking into account the `randomness'. Use heuristic to choose track within that cluster (what if staying within cluster?). If all these tracks played recently, may choose next cluster.
		 */
		return new Song();
	}

	public void Reset() {
		playing = false;
		clusterPlaying = -1;
	}
	
	public List<AbstractCluster> getChildren(){
		return clusters;
	}
	
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
	


	// The "actions" in the Markov Decision Process
	public class UserAction implements Serializable {
		private static final long serialVersionUID = 1L;
		final static int TRACK_FINISHED = 0;
		final static int TRACK_SKIPPED = 1;
		public int type; // one of the above
	}
}
