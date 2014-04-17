package jk509.player.core;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jk509.player.core.TableSorter.Directive;

public class Playlist implements Serializable {

	private static final long serialVersionUID = 516185787632474552L;

	// lists of Songs, or lists of IDs?? (the former currently)

	private String name;
	private List<Song> list;
	private int type;
	private int[] selection;
	private Point viewPos;
	private List<Directive> sort;
	// public JTableHeader header;
	// public int trackPlaying = -1;

	// Types of playlist
	public static final int DEFAULT = 0;
	public static final int USER = 1;
	public static final int AUTO = 2;

	public Playlist() {
		this.name = "";
		list = new ArrayList<Song>();
		type = 0;
		selection = new int[0];
		viewPos = new Point(0, 0);
	}

	public Playlist(String name) {
		this();
		this.name = name;
	}

	public Playlist(String name, int type) {
		this();
		this.name = name;
		this.type = type;
	}

	public Playlist(String name, int type, List<Song> ls) {
		this();
		this.name = name;
		this.type = type;
		this.list = ls;
	}

	public Song get(int i) {
		// try {
		return list.get(i);
		// } catch (IndexOutOfBoundsException e) {
		// e.printStackTrace();
		// return null;
		// }
	}

	public void remove(int i) {
		try {
			list.remove(i);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	public int[] getSelection() {
		return selection;
	}

	public void setSelection(int[] ns) {
		selection = ns;
	}

	public Point getViewPos() {
		return viewPos;
	}

	public void setViewPos(Point p) {
		viewPos = p;
	}

	public List<Directive> getSort() {
		return sort;
	}

	public void setSort(List<Directive> s) {
		sort = s;
	}

	public void add(int index, Song element) {
		list.add(index, element);
	}

	public void add(Song element) {
		list.add(element);
	}

	public void append(Song element) {
		list.add(element);
	}

	public List<Song> getList() {
		return list;
	}

	public void setList(List<Song> l) {
		list = new ArrayList<Song>(l);
	}

	public void append(List<Song> l) {
		list.addAll(l);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int t) {
		type = t;
	}

	public int size() {
		return list.size();
	}

	public void clearViews() {
		selection = null;
		sort = null;
		viewPos = null;
	}

	/*
	 * public List<Song> search(String q, int trackPlaying){ this.trackPlaying = -1; // returns a subset of `tracks' for which track.search returns true List<Song> result = new ArrayList<Song>(); for(int i=0; i<getList().size(); ++i){ if(getList().get(i).search(q)){ result.add(getList().get(i)); if(trackPlaying == i) this.trackPlaying = result.size()-1; } } return result; }
	 */
	int[] toIntArray(List<Integer> list) {
		int[] ret = new int[list.size()];
		int i = 0;
		for (Integer e : list)
			ret[i++] = e.intValue();
		return ret;
	}

	public int[] search(String q) {
		// returns a list of indices into the playlist such that result[i].search is true
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < getList().size(); ++i) {
			if (getList().get(i).search(q)) {
				result.add(i);
			}
		}
		return toIntArray(result);
		// this.trackplaying = modelToView(trackplaying) ie. find the given number in the result list and return its index
	}

	public Shuffle shuffle() {
		List<Song> res = new ArrayList<Song>(getList());
		List<Integer> indexList = new ArrayList<Integer>();
		for (int i = 0; i < res.size(); ++i)
			indexList.add(i);
		long seed = System.nanoTime();
		Collections.shuffle(res, new Random(seed));
		Collections.shuffle(indexList, new Random(seed));
		Shuffle s = new Shuffle(res, indexList);
		return s;
	}

	public int getIndexOf(String loc) {
		for (int i = 0; i < size(); ++i)
			if (get(i).getLocation().equals(loc))
				return i;
		return -1;
	}

	public int getIndexOf(Song s) {
		for (int i = 0; i < size(); ++i)
			if (get(i).equals(s))
				return i;
		return -1;
	}

	public class Shuffle implements Serializable {
		private static final long serialVersionUID = 1L;
		public List<Song> tracks;
		public List<Integer> indices;

		// public int position = 0;
		public Shuffle(List<Song> s, List<Integer> i) {
			tracks = s;
			indices = i;
		}
	}

}