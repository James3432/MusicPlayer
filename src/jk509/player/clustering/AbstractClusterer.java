package jk509.player.clustering;

import java.util.ArrayList;
import java.util.List;

import jk509.player.core.Song;
import jk509.player.features.FeatureGrabber;

public abstract class AbstractClusterer {

	protected List<Song> tracks;
	protected List<ArrayList<Song>> clusters;
	protected FeatureGrabber featureGrabber;
	
	public AbstractClusterer(List<Song> s){
		tracks = s;
		featureGrabber = new FeatureGrabber();
	}
	
	public abstract void run();
	
	public List<ArrayList<Song>> getResult(){
		return clusters;
	}
	
}
