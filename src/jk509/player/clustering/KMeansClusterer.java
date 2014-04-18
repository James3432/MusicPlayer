package jk509.player.clustering;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import jk509.player.Constants;
import jk509.player.core.Song;
import jk509.player.features.FeatureGrabber;
import jk509.player.gui.GUIupdater;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class KMeansClusterer extends AbstractClusterer {

	public KMeansClusterer(List<Song> s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(JFrame frame) {
		if (tracks.size() < 1)
			return;
		if (!(new File(Constants.featureXMLLocation)).exists())
			return;

		List<Song> featureless = GetSongsWithoutFeatures(tracks);
		
		if(featureless.size() > 0){
			GUIupdater updater = new GUIupdater(frame);
			List<double[]> results = null;
			if (Constants.DEBUG_LOAD_FEATURES_FILE) {
				FileInputStream fin;
				try {
					fin = new FileInputStream(new File(features_path));
					ObjectInputStream oos = new ObjectInputStream(fin);
					results = (List<double[]>) oos.readObject();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Starting feature extraction for "+featureless.size()+" tracks.");
				featureGrabber = new FeatureGrabber();
				featureGrabber.run(featureless, updater);
				/* List<double[]> */
				results = featureGrabber.getWeightedResults();
				featureGrabber = null;
				// TODO: normalise (probably inside featureGrabber)
	
				if (Constants.DEBUG_SAVE_FEATURES) {
					System.out.println("Features extracted, saving to disk");
					saveFeatures(results);
					System.out.println("Saved.");
				}
			}
		}

		// double[] weights = (new AudioFeatures()).getWeights();
		// The weighting below is between tracks, not between features

		// keep track of which tracks didn't succeed in feature extraction
		// TODO: how to deal with this later??
		/*boolean[] featurelessTracks = new boolean[results.size()];

		for (int i = 0; i < results.size(); ++i) {
			if (results.get(i) == null)
				featurelessTracks[i] = true;
			else
				dataset.add(new Instance(1.0, results.get(i)));
		}*/

		Instances dataset = getSongFeatures(tracks);
		// TODO: need to deal with any songs still without features here
		//System.out.println("Dataset ready");

		try {
			SimpleKMeans kmeans = new SimpleKMeans();

			kmeans.setSeed(Constants.KMEANS_SEED); // TODO: change?

			// This is the important parameter to set
			kmeans.setPreserveInstancesOrder(true);
			kmeans.setNumClusters(Constants.MAX_CLUSTERS);

			//System.out.println("Start clustering...");
			kmeans.buildClusterer(dataset);
			//System.out.println("Done clustering");

			// This array returns the cluster number (starting with 0) for each instance
			// The array has as many elements as the number of instances
			assignments = kmeans.getAssignments();

			clusters = new ArrayList<ArrayList<Song>>();

			for (int i = 0; i < tracks.size(); ++i) {
				//if (!featurelessTracks[i]) {
					int clusterNum = assignments[i];
					// add more clusters to result if needed
					while (clusters.size() <= clusterNum)
						clusters.add(new ArrayList<Song>());
					// add track instance to cluster
					clusters.get(clusterNum).add(tracks.get(i));
					// System.out.printf("Instance %d -> Cluster %d", i, clusterNum);
					// System.out.println();
					//j++;
				//}
			}
			if (Constants.DEBUG_SAVE_CLUSTERS) {
				System.out.println("Saving to disk...");
				saveClusters();
				System.out.println("Saved");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			clusters = new ArrayList<ArrayList<Song>>();
			clusters.clear();
		}
	}

	/*
	 * test weka
	 */
	/*
	 * public static void main(String[] args){ Attribute num1 = new Attribute("num1"); Attribute num2 = new Attribute("num2"); FastVector attributes = new FastVector(); attributes.addElement(num1); attributes.addElement(num2); Instances dataset = new Instances("Test-dataset", attributes, 5); dataset.add(new Instance(1.0, new double[]{ 0.3, 0.5 })); dataset.add(new Instance(1.0, new double[]{ 0.4, 0.6 })); dataset.add(new Instance(1.0, new double[]{ 0.3, 0.4 })); dataset.add(new Instance(1.0, new double[]{ 0.9, 0.2 })); dataset.add(new Instance(1.0, new double[]{ 0.8, 0.3 }));
	 * 
	 * 
	 * try { SimpleKMeans kmeans = new SimpleKMeans();
	 * 
	 * kmeans.setSeed(10);
	 * 
	 * // This is the important parameter to set kmeans.setPreserveInstancesOrder(true); kmeans.setNumClusters(2);
	 * 
	 * kmeans.buildClusterer(dataset);
	 * 
	 * // This array returns the cluster number (starting with 0) for each instance // The array has as many elements as the number of instances int[] assignments = kmeans.getAssignments();
	 * 
	 * int i=0; for(int clusterNum : assignments) { System.out.printf("Instance %d -> Cluster %d", i, clusterNum); System.out.println(); i++; } } catch (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * 
	 * }
	 */

}
