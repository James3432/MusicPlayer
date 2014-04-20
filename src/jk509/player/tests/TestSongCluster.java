package jk509.player.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Deque;

import jk509.player.clustering.SongCluster;
import jk509.player.core.Library;
import jk509.player.core.Song;
import jk509.player.learning.UserAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSongCluster {

	SongCluster cluster;
	Library library;
	boolean set = false;
	
	@Before
	public void setUp() throws Exception {
		if(!set){
			File settings = new File("C:\\Users\\James\\Music Factory\\library.ser");
		
			FileInputStream fin;
			try {
				fin = new FileInputStream(settings);
				ObjectInputStream ois = new ObjectInputStream(fin);
				library = (Library) ois.readObject();
				library.setCurrentPlaylist(Library.MAIN_PLAYLIST);
				ois.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			cluster = new SongCluster(library.getPlaylists().get(Library.MAIN_PLAYLIST).getList(), null);
			System.out.println("Test data:");
			for(int i=0; i<100; ++i)
				System.out.println(i+" "+library.get(i)/*.toString()*/);
			System.out.println();
			set = true;
		}
		
		cluster.clearHistory();
	}

	@After
	public void tearDown() throws Exception {
		cluster = null;
		library = null;
	}
	
	@Test
	public void testHistory() {
		cluster.addHistory(library.get(5));
		cluster.addHistory(library.get(4));
		cluster.addHistory(library.get(23));
		cluster.addHistory(library.get(58));
		cluster.addHistory(library.get(1));
		
		System.out.println("Dequeing...");
		Deque<Song> q = cluster.getHistory();
		while(!q.isEmpty()){
			Song s = q.pop();
			System.out.println(s);
		}
	}

	@Test
	public void testGetClusterIndex() {
		int val = cluster.getClusterIndex(library.get(5));
		System.out.println(val);
		assertFalse("All songs should be in a cluster", val < 0);
	}

	@Test
	public void testUpdate() {
		cluster.Update(new UserAction(UserAction.TRACK_FINISHED, 0, library.get(10), library.get(0), null));
		cluster.PrintAllP(0);
	}
	
	@Test
	public void testNextFromScratch(){
		cluster.addHistory(library.get(5));
		cluster.addHistory(library.get(4));
		cluster.addHistory(library.get(23));
		cluster.addHistory(library.get(58));
		cluster.addHistory(library.get(1));
		Song s = cluster.next();
		System.out.println(s);
	}
	@Test
	public void testNextFromSource(){
		cluster.addHistory(library.get(5));
		cluster.addHistory(library.get(4));
		cluster.addHistory(library.get(23));
		cluster.addHistory(library.get(58));
		cluster.addHistory(library.get(1));
		cluster.setPlayingCluster(library.get(5));
		Song s = cluster.next();
		System.out.println(s);
	}

}
