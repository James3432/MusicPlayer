package jk509.player.clustering;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import jk509.player.core.Song;
import jk509.player.features.FeatureGrabber;

public abstract class AbstractClusterer {

	protected List<Song> tracks;
	protected List<ArrayList<Song>> clusters;
	protected FeatureGrabber featureGrabber;
	protected String features_path; //TODO: private
	private String clusters_path;
	
	public AbstractClusterer(List<Song> s){
		tracks = s;
		featureGrabber = new FeatureGrabber();
	}
	
	public abstract void run(JFrame frame);
	
	public List<ArrayList<Song>> getResult(){
		return clusters;
	}
	
	public void setFeatureSavePath(String fp){
		features_path = fp;
	}
			
	public void setClusterSavePath(String fp){
		clusters_path = fp;
	}
	
	protected void saveFeatures(List<double[]> fs){
		/*
		 * write to plain text file
		 */
		try {
			PrintWriter writer = new PrintWriter(new File(features_path.replaceAll("\\.ser", ".txt")));
			writer.println("Music Factory features for "+fs.size()+" tracks, generated on "+(new Date()));
			writer.println(); writer.println();
			for(int i=0; i<fs.size(); ++i){
				if(fs.get(i) != null){
					for(int j=0; j<fs.get(i).length; ++j){
						writer.print(fs.get(i)[j] + "  ");
					}
					writer.println();
					writer.println("----------------");
				}
			}
			if(fs.size() < 1)
				writer.println("No data");
			writer.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*
		 * write serialised object
		 */
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(new File(features_path));
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(fs);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void saveClusters(){
		/*
		 * write to plain text file
		 */
		try {
			PrintWriter writer = new PrintWriter(new File(clusters_path.replaceAll("\\.ser", ".txt")));
			writer.println("Music Factory clusters for "+this.tracks.size()+" tracks, "+clusters.size()+" clusters, generated on "+(new Date()));
			writer.println(); writer.println();
			for(int i=0; i<clusters.size(); ++i){
				writer.println("Cluster "+i+" ---------------- ");
				for(int j=0; j<clusters.get(i).size(); ++j){
					writer.println(clusters.get(i).get(j).getArtist() + " -- " + clusters.get(i).get(j).getName());
				}
				writer.println();
			}
			if(clusters.size() < 1)
				writer.println("No data");
			writer.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*
		 * write serialised object
		 */
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(new File(clusters_path));
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(clusters);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
