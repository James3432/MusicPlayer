package jk509.player;

import java.io.Serializable;
import java.util.Date;

/**
 * A representation of audio tracks which may have data drawn from ID3 tags or
 * an iTunes database
 */
public class Song implements Serializable {

	private static final long serialVersionUID = 516185787632474552L;

	private String name;
	private String album;
	private String artist;
	private String genre;
	private String year;
	private String type; // to ignore videos and non-playable audio. keep if
							// "*audio file"
	private String location;

	private boolean hasArtwork;

	private Date added;

	private int id;
	private int trackNumber;
	private int playCount;
	private int length; // in seconds

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String albumName) {
		this.album = albumName;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artistName) {
		this.artist = artistName;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getType() {
		return type;
	}

	public void setType(String kind) {
		this.type = kind;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String loc) {
		this.location = loc;
	}

	public boolean hasArtwork() {
		return hasArtwork;
	}

	public void setArtwork(boolean b) {
		this.hasArtwork = b;
	}

	public Date getDateAdded() {
		return added;
	}

	public void setDateAdded(Date date) {
		this.added = date;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public String getLength() {
		return SecondsToString(length);
	}

	public int getLengthS() {
		return length;
	}

	public void setLength(int l) {
		length = l;
	}
	
	public static String SecondsToString(int length){
		String s = Integer.toString(length % 60);
		if (s.length() == 0)
			s = "00";
		if (s.length() == 1)
			s = "0" + s;
		if (s.length() > 2)
			s = s.substring(0, 2);
		return (length / 60 + ":" + s);
	}
}
