package jk509.player.learning;

import java.io.Serializable;

import jk509.player.core.Song;

//The "actions" in the Markov Decision Process
public class UserAction implements Serializable {
	private static final long serialVersionUID = 1L;

	/*
	 * The types of user action which may affect the learning in different ways
	 */
	public final static int TRACK_FINISHED = 0;    // user listened to 'target' all the way through: positive link with 'source'
	public final static int TRACK_SKIPPED = 1;     // user skipped 'target' when chosen after 'source' after (value * target.length) seconds through song
	public final static int TRACK_CHANGED = 2;     // user picked song 'chosen' after (value * target.length) seconds through 'target' which was chosen after 'source'
	public final static int PLAYLIST_SHARED = 3;   // 'source' and 'target' are in the same user-playlist (imported or otherwise)
	public final static int PLAYLIST_ADJACENT = 4; // 'target' directly follows 'source' in a user-playlist (imported or otherwise)
	public final static int TRACK_QUEUED = 5;      // 'source' was playing and then user added 'target' to the play queue

	public int type;     // one of the above
	public double value; // 0.0 - 1.0 eg. fraction of way through track at which user skipped
	public Song source;
	public Song target;
	public Song chosen;

	public UserAction(int type, double val, Song s, Song t, Song c) {
		this.type = type;
		this.value = val;
		this.source = s;
		this.target = t;
		this.chosen = c;
	}

	public boolean requiresChosen() {
		return (type == TRACK_CHANGED);
	}
	
	@Override
	public String toString(){
		String str = "";
		switch(this.type){
		case TRACK_FINISHED: str += "Track finished"; break;
		case TRACK_SKIPPED: str += "Track skipped"; break;
		case TRACK_CHANGED: str += "Track changed"; break;
		case PLAYLIST_SHARED: str += "Tracks in playlist"; break;
		case PLAYLIST_ADJACENT: str += "Tracks adjacent"; break;
		case TRACK_QUEUED: str += "Track queued"; break;
		}
		str += "  Value: "+value;
		str += "  Source: "+source.getName();
		str += "  Target: "+target.getName();
		if(chosen != null)
			str += "  Chosen: "+chosen.getName();
		return str;
	}
}
