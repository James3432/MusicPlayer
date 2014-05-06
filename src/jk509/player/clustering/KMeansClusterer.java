package jk509.player.clustering;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import jk509.player.Constants;
import jk509.player.core.Song;
import jk509.player.core.StaticMethods;
import jk509.player.features.FeatureGrabber;
import jk509.player.gui.Updater;
import jk509.player.logging.Logger;
import jk509.player.logging.Logger.LogType;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class KMeansClusterer extends AbstractClusterer {

	private SimpleKMeans kmeans;
	
	public KMeansClusterer(List<Song> s) {
		super(s);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(Updater updater) {
		if (tracks.size() < 1){
			Logger.log("No tracks found for analysis", LogType.ERROR_LOG);
			return;
		}
		if (!(new File(Constants.featureXMLLocation)).exists()){
			Logger.log("Can't find features.xml in: "+Constants.featureXMLLocation, LogType.ERROR_LOG);
			return;
		}

		try{
		
			List<Song> featureless = GetSongsWithoutFeatures(tracks);
			
			if(featureless.size() > 0){
				//GUIupdater updater = new GUIupdater(frame);
				if (Constants.DEBUG_LOAD_FEATURES_FILE) {
					FileInputStream fin;
					try {
						List<double[]> results = null;
						fin = new FileInputStream(new File(features_path));
						ObjectInputStream oos = new ObjectInputStream(fin);
						results = (List<double[]>) oos.readObject();
						StaticMethods.SetFeaturesFromFile(featureless, results);
					} catch (Exception e) {
						Logger.log(e, LogType.ERROR_LOG);
					}
				} else {
					System.out.println("Starting feature extraction for "+featureless.size()+" tracks.");
					featureGrabber = new FeatureGrabber();
					featureGrabber.run(featureless, updater);
					/* List<double[]> */
					//results = featureGrabber.getWeightedResults();
					featureGrabber = null;
					// TODO: normalise (probably inside featureGrabber)
		
					if (Constants.DEBUG_SAVE_FEATURES) {
						List<double[]> results = StaticMethods.GetFeaturesFromSongs(tracks);
						System.out.println("Features extracted, saving to disk");
						saveFeatures(results);
						System.out.println("Saved.");
					}
				}
			}

		}catch(Exception e){
			Logger.log(e, LogType.ERROR_LOG);
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
		
		boolean[] featurelessTracks = new boolean[tracks.size()];
		for(int i=0; i<tracks.size(); ++i) {
			Song s = tracks.get(i);
			if (s.getAudioFeatures() == null || s.getAudioFeatures().length != Constants.FEATURES)
				featurelessTracks[i] = true;
			else
				featurelessTracks[i] = false;
		}
		
		// This method only picks out valid-feature tracks
		Instances dataset = getSongFeatures(tracks);
		// TODO: need to deal with any songs still without features here
		//System.out.println("Dataset ready");

		try {
			kmeans = new SimpleKMeans();

			kmeans.setSeed(Constants.KMEANS_SEED);

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

			for (int i = 0, j=0; i < tracks.size(); ++i) {
				if (!featurelessTracks[i]) {
					int clusterNum = assignments[j];
					// add more clusters to result if needed
					while (clusters.size() <= clusterNum)
						clusters.add(new ArrayList<Song>());
					// add track instance to cluster
					clusters.get(clusterNum).add(tracks.get(i));
					// System.out.printf("Instance %d -> Cluster %d", i, clusterNum);
					// System.out.println();
					j++;
				}
			}
			if (Constants.DEBUG_SAVE_CLUSTERS) {
				System.out.println("Saving to disk...");
				saveClusters();
				System.out.println("Saved");
			}
		} catch (Exception e) {
			Logger.log(e, LogType.ERROR_LOG);
			clusters = new ArrayList<ArrayList<Song>>();
			clusters.clear();
		}
	}
	
	public SimpleKMeans getClusterer(){
		return kmeans;
	}

}
