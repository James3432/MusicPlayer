package jk509.player;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jk509.player.TableSorter.Directive;

public class Playlist implements Serializable {

	private static final long serialVersionUID = 516185787632474552L;

	// lists of Songs, or lists of IDs?? (the former currently)

	private String name;
	private List<Song> list;
	private int type;
	private int[] selection;
	private Point viewPos;
	private List<Directive> sort;
	//public JTableHeader header;
	
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
		return list.get(i);
	}

	public void remove(int i) {
		list.remove(i);
	}
	
	public int[] getSelection(){
		return selection;
	}
	
	public void setSelection(int[] ns){
		selection = ns;
	}
	
	public Point getViewPos(){
		return viewPos;
	}
	
	public void setViewPos(Point p){
		viewPos = p;
	}
	
	public List<Directive> getSort(){
		return sort;
	}
	
	public void setSort(List<Directive> s){
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

}