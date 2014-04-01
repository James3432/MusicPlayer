package jk509.player;

import java.util.List;

public abstract class AbstractClusterer {

	protected List<Song> tracks;
	protected List<List<Song>> clusters;
	
	public AbstractClusterer(List<Song> s){
		tracks = s;
	}
	
	public abstract void run();
	
	public List<List<Song>> getResult(){
		return clusters;
	}
	
}
