package jk509.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {

	private static final long serialVersionUID = 516185787632474552L;
	
	// lists of Songs, or lists of IDs?? (the latter currently)

	private String name;
	private List<Integer> list;
	private int type;
	
	// Types of playlist
	public static final int DEFAULT = 0;
	public static final int USER = 1;
	public static final int AUTO = 2;
	
	public Playlist(){
		this.name = "";
		list = new ArrayList<Integer>();
		type = 0;
	}
	public Playlist(String name){
		super();
		this.name = name;
	}
	public Playlist(String name, int type){
		super();
		this.name = name;
		this.type = type;
	}
	public Playlist(String name, int type, List<Integer> ls){
		super();
		this.name = name;
		this.type = type;
		this.list = ls;
	}
	
	public int get(int i){
		return list.get(i);
	}
	
	public void remove(int i){
		list.remove(i);
	}
	
	public void add(int index, int element){
		list.add(index, element);
	}
	
	public void append(int element){
		list.add(element);
	}

	public List<Integer> getList() {
		return list;
	}

	public void setList(List<Integer> l){
		list = l;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public int getType(){
		return type;
	}
	
	public void setType(int t){
		type = t;
	}

}