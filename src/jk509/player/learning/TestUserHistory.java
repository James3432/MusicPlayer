package jk509.player.learning;

import java.util.List;

import jk509.player.core.Song;

public class TestUserHistory {
	List<Song> tracks;
	public UserAction[] array = { new UserAction(UserAction.TRACK_SKIPPED, 0.5, tracks.get(1), tracks.get(1), tracks.get(1)) };
	public TestUserHistory(List<Song> t){
		tracks = t;
	}
}