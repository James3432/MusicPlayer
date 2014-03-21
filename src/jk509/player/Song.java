package jk509.player;

import java.io.Serializable;
import java.util.Date;

/**
 * A representation of audio tracks which may have data drawn from ID3 tags or an iTunes database
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
		if (name == null || name.equals(""))
			this.name = "Unknown track";
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String albumName) {
		this.album = albumName;
		if (albumName == null || albumName.equals(""))
			this.album = "Unknown album";
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artistName) {
		this.artist = artistName;
		if (artistName == null || artistName.equals("") || artistName.equals(null))
			this.artist = "Unknown artist";
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
		if (genre == null)
			this.genre = "";
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
		if (year == null)
			this.year = "";
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
		if (date == null)
			this.added = new Date();
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

	public void incrementPlayCount() {
		if (playCount < 0)
			playCount = 1;
		else
			playCount++;
	}

	public String getLength() {
		return SecondsToString(length);
	}

	public int getLengthS() {
		return length;
	}

	public TrackTime getTrackTime() {
		return new TrackTime(length);
	}

	public void setLength(int l) {
		length = l;
	}

	public void cleanUp() {
		// remove any null fields
		if (name == null || name.equals(""))
			name = "Unknown track";
		if (artist == null || artist.equals(""))
			artist = "Unknown artist";
		if (album == null || album.equals(""))
			album = "Unknown album";
		if (added == null)
			added = new Date();
	}
	
	public boolean search(String q){
		q = q.toLowerCase();
		if((name != null && name.toLowerCase().contains(q)) || 
				(artist != null && artist.toLowerCase().contains(q)) || 
				(album != null && album.toLowerCase().contains(q)))
			return true;
		else
			return false;
	}

	public static String SecondsToString(int length) {
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
