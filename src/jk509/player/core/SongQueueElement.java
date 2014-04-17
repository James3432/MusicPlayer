package jk509.player.core;

public class SongQueueElement {
	public int playlist; // in terms of the list, where 0=default list
	public int index; // model index in that playlist. may change after delete (of any other or this track in playlist)
	public Song song; // the track itself

	public SongQueueElement(Song s, int pl, int i) {
		song = s;
		playlist = pl;
		index = i;
	}

	@Override
	public boolean equals(Object o) {
		SongQueueElement other = (SongQueueElement) o;
		if (other.playlist == this.playlist /*&& other.index == this.index*/ && other.song.equals(this.song))
			return true;
		else
			return false;
	}
}