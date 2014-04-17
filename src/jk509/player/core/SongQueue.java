package jk509.player.core;

import java.util.LinkedList;
import java.util.List;

public class SongQueue {
	private List<SongQueueElement> queue;
	public int position = -1; // index into queue
	private SongQueueElement start; // song playing when "up next" was created
	private boolean valid = false;

	private SongQueue() {
		queue = new LinkedList<SongQueueElement>();
	}

	public SongQueue(SongQueueElement start) {
		this();
		this.start = start;
	}

	public SongQueueElement getCurrent() {
		return queue.get(position);
	}

	public SongQueueElement next() {
		++position;
		if (position >= queue.size()) {
			valid = false;
			return start;
		} else
			valid = true;
		return queue.get(position);
	}

	public SongQueueElement prev() {
		--position;
		if (position < 0) {
			valid = false;
			return start;
		} else {
			valid = true;
			return queue.get(position);
		}
	}

	public void PlayNext(SongQueueElement el) {
		valid = true;
		if (size() < 1)
			queue.add(el);
		else
			queue.add(position + 1, el);
	}

	public void AddToTail(SongQueueElement el) {
		valid = true;
		queue.add(el);
	}

	public void Delete(SongQueueElement el) {
		// update valid, position
		for (int i = 0; i < size(); ++i) {
			if (queue.get(i).equals(el)) {
				queue.remove(i);
				if (i < size() - 1)
					i--;
				if (i < position)
					position--;
			}
		}
		if (position < 0) {
			position = -1;
			valid = false;
		} else if (position >= size()) {
			position = size();
			valid = false;
		}

		if (start.equals(el))
			start = null;
	}

	public void Delete(Song s) {
		// update valid, position
		for (int i = 0; i < size(); ++i) {
			if (queue.get(i).song.equals(s)) {
				queue.remove(i);
				if (i < size() - 1)
					i--;
				if (i < position)
					position--;
			}
		}
		if (position < 0) {
			position = -1;
			valid = false;
		} else if (position >= size()) {
			position = size();
			valid = false;
		}

		if (start.song.equals(s))
			start = null;
	}

	public SongQueueElement getStart() {
		return start;
	}

	public Song[] getSongArray() {
		if (size() > 0) {
			Song[] res = new Song[size()];
			// res[size()] = end.song;
			for (int i = 0; i < size(); ++i)
				res[i] = queue.get(i).song;
			return res;
		} else
			return new Song[] {};
	}

	public Song[] getNextUpSongArray() {
		if (size() > 0) {
			Song[] res = new Song[size() - (position + 1)];
			// res[size()] = end.song;
			for (int i = position + 1; i < size(); ++i)
				res[i - position - 1] = queue.get(i).song;
			return res;
		} else
			return new Song[] {};
	}

	public int size() {
		return queue.size();
	}

	public boolean isValid() {
		return valid;
	}

}