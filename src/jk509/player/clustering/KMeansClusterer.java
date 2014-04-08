package jk509.player.clustering;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jk509.player.Constants;
import jk509.player.core.Song;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class KMeansClusterer extends AbstractClusterer {

	public KMeansClusterer(List<Song> s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		File[] fileList = new File[tracks.size()];
		for(int i=0; i<tracks.size(); ++i)
			fileList[i] = new File(tracks.get(i).getLocation());
		featureGrabber.run(fileList);
		List<double[]> results = featureGrabber.getNormalisedResults();
		// TODO: normalise (probably inside featureGrabber)
		
		FastVector atts = new FastVector();
		for(int i=0; i<Constants.FEATURES; ++i)
			atts.addElement(new Attribute("num"+i));
		Instances dataset = new Instances("Feature-set", atts, tracks.size());
		
		//double[] weights = (new AudioFeatures()).getWeights();
		// The weighting below is between tracks, not between features
		
		for(int i=0; i<tracks.size(); ++i)
			dataset.add(new Instance(1.0, results.get(i)));
		
		try {
			SimpleKMeans kmeans = new SimpleKMeans();

			kmeans.setSeed(10); // TODO: change?

			// This is the important parameter to set
			kmeans.setPreserveInstancesOrder(true);
			kmeans.setNumClusters(Constants.MAX_CLUSTERS);
			
			kmeans.buildClusterer(dataset);
			
			// This array returns the cluster number (starting with 0) for each instance
			// The array has as many elements as the number of instances
			int[] assignments = kmeans.getAssignments();

			clusters = new ArrayList<ArrayList<Song>>();
			
			int i=0;
			for(int clusterNum : assignments) {
				// add more clusters to result if needed
				while(clusters.size() <= clusterNum)
					clusters.add(new ArrayList<Song>());
				// add track instance to cluster
				clusters.get(clusterNum).add(tracks.get(i));
			    System.out.printf("Instance %d -> Cluster %d", i, clusterNum);
			    System.out.println();
			    i++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			clusters.clear();
			e.printStackTrace();
		}
	}
	
	/*
	 * test weka
	 */
	public static void main(String[] args){
		Attribute num1 = new Attribute("num1");
		Attribute num2 = new Attribute("num2");
		FastVector attributes = new FastVector();
		attributes.addElement(num1);
		attributes.addElement(num2);
		Instances dataset = new Instances("Test-dataset", attributes, 5);
		dataset.add(new Instance(1.0, new double[]{ 0.3, 0.5 }));
		dataset.add(new Instance(1.0, new double[]{ 0.4, 0.6 }));
		dataset.add(new Instance(1.0, new double[]{ 0.3, 0.4 }));
		dataset.add(new Instance(1.0, new double[]{ 0.9, 0.2 }));
		dataset.add(new Instance(1.0, new double[]{ 0.8, 0.3 }));
		
		
		try {
			SimpleKMeans kmeans = new SimpleKMeans();

			kmeans.setSeed(10);

			// This is the important parameter to set
			kmeans.setPreserveInstancesOrder(true);
			kmeans.setNumClusters(2);
			
			kmeans.buildClusterer(dataset);
			
			// This array returns the cluster number (starting with 0) for each instance
			// The array has as many elements as the number of instances
			int[] assignments = kmeans.getAssignments();

			int i=0;
			for(int clusterNum : assignments) {
			    System.out.printf("Instance %d -> Cluster %d", i, clusterNum);
			    System.out.println();
			    i++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
