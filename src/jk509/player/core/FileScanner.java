package jk509.player.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jk509.player.Setup.FileScannerUpdater;
import jk509.player.logging.Logger;
import jk509.player.logging.Logger.LogType;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class FileScanner implements LibraryParser {

	private String libraryPath = "";
	private List<Song> tracks;
	// private Map<String, BufferedImage> artwork;

	private boolean valid = false;
	private Song tempTrack;
	
	private FileScannerUpdater updater;

	public FileScanner() {
		tracks = new ArrayList<Song>();
		// artwork = new HashMap<String, BufferedImage>();
	}
	
	public FileScanner(FileScannerUpdater updater){
		this();
		this.updater = updater;
	}

	public FileScanner(String path) {
		this();
		libraryPath = path;
		valid = true;
	}

	public void setPath(String path) {
		libraryPath = path;
	}

	@Override
	public void run() {
		scan(libraryPath);
	}

	public void runExample() {
		scan(libraryPath);
		printData();
	}

	public void addFileList(File[] ls) {
		for (int i = 0; i < ls.length; ++i) {
			AddSong(ls[i]);
		}
	}

	private void scan(String p) {
		File f = new File(p);
		File l[] = f.listFiles();
		if (l != null && l.length > 0) {
			for (File x : l) {
				if (x == null) {
					return;
				}
				if (x.isHidden() || !x.canRead())
					continue;
				if (x.isDirectory())
					scan(x.getPath());
				else if (x.getName().toLowerCase().endsWith(".mp3") || x.getName().toLowerCase().endsWith(".wav"))
					AddSong(x);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void AddSong(File sourceFile) {
		Mp3File mp3file;
		tempTrack = new Song();
		
		if(StaticMethods.getExtension(sourceFile).equals("wav")){
			try{
				tempTrack.setLocation(sourceFile.getPath());
				tempTrack.setTrackNumber(1);
				String file_name = StripTitle(tempTrack.getLocation());
				tempTrack.setName(file_name);
				tempTrack.setAlbum(RemNull(null));
				tempTrack.setArtist(RemNull(null));
				tempTrack.setGenre(RemNull(null));
				tempTrack.setYear(RemNull(null));
				try{
					tempTrack.setLength(StaticMethods.getWavDuration(sourceFile));
				}catch(Exception e){
					tempTrack.setLength(0);
				}
				tempTrack.setDateAdded(new Date(sourceFile.lastModified()));
				
				// cleanup dateadded
				if (tempTrack.getDateAdded() == null || tempTrack.getDateAdded().getYear() + 1900 < 1970)
					tempTrack.setDateAdded(new Date());
			
				tracks.add(tempTrack);
				
				if(updater != null)
					updater.update();
				
			}catch(Exception e){
				Logger.log(e, LogType.ERROR_LOG);
			}
			
		}else{

			try {
				mp3file = new Mp3File(sourceFile.getPath());
	
				if (mp3file.hasId3v2Tag()) {
					// ID3v2
					ID3v2 tag = mp3file.getId3v2Tag();
					try {
						tempTrack.setTrackNumber(Integer.parseInt(RemNull(RemSlash(tag.getTrack()))));
					} catch (NumberFormatException e) {
						tempTrack.setTrackNumber(0);
					}
					tempTrack.setName(RemNull(tag.getTitle()));
					tempTrack.setAlbum(RemNull(tag.getAlbum()));
					String artist = tag.getArtist();
					if (artist == null || artist.equals(""))
						artist = tag.getAlbumArtist();
					if (artist == null || artist.equals(""))
						artist = tag.getOriginalArtist();
					if (artist == null || artist.equals(""))
						artist = tag.getComposer();
					tempTrack.setArtist(RemNull(artist));
					tempTrack.setGenre(RemNull(tag.getGenreDescription()));
					tempTrack.setYear(RemNull(tag.getYear()));
					tempTrack.setLength((int) mp3file.getLengthInSeconds());
					tempTrack.setDateAdded(new Date(mp3file.getLastModified()));
					tempTrack.setLocation(sourceFile.getPath());
	
					/*
					 * byte[] albumImageData = tag.getAlbumImage(); if (albumImageData != null) { // System.out.println("Have album image data, length: " + albumImageData.length + " bytes"); // System.out.println("Album image mime type: " + tag.getAlbumImageMimeType()); try { BufferedImage img = ImageIO.read(new ByteArrayInputStream(albumImageData));
					 * 
					 * artwork.put(sourceFile.getPath(), img); tempTrack.setArtwork(true);
					 * 
					 * } catch (IOException e) { tempTrack.setArtwork(false); } }
					 */
				} else if (mp3file.hasId3v1Tag()) {
					// ID3v1
					ID3v1 tag = mp3file.getId3v1Tag();
					try {
						tempTrack.setTrackNumber(Integer.parseInt(RemNull(RemSlash(tag.getTrack()))));
					} catch (NumberFormatException e) {
						tempTrack.setTrackNumber(0);
					}
					tempTrack.setName(RemNull(tag.getTitle()));
					tempTrack.setAlbum(RemNull(tag.getAlbum()));
					tempTrack.setArtist(RemNull(tag.getArtist()));
					tempTrack.setGenre(RemNull(tag.getGenreDescription()));
					tempTrack.setYear(RemNull(tag.getYear()));
					tempTrack.setLength((int) mp3file.getLengthInSeconds());
					tempTrack.setDateAdded(new Date(mp3file.getLastModified()));
					tempTrack.setLocation(sourceFile.getPath());
	
				} else {
					// no tag
					tempTrack.setLocation(sourceFile.getPath());
				}
	
				// cleanup name
				if (tempTrack.getName() == null || tempTrack.getName().equals("Unknown") || tempTrack.getName().equals("")) {
					String file_name = StripTitle(tempTrack.getLocation());
					tempTrack.setName(file_name);
				}
				// cleanup dateadded
				if (tempTrack.getDateAdded() == null || tempTrack.getDateAdded().getYear() + 1900 < 1970)
					tempTrack.setDateAdded(new Date());
	
				tracks.add(tempTrack);
				
				if(updater != null)
					updater.update();
	
			} catch (InvalidDataException e) {
				// no tag
				return;
	
			} catch (UnsupportedTagException e) {
				// no tag
				return;
			} catch (IOException e) {
				return;
			}
		}
	}

	private String RemSlash(String s) {
		if (s == null)
			return null;
		if (s.contains("\\"))
			return s.substring(0, s.indexOf("\\"));
		else if (s.contains("/"))
			return s.substring(0, s.indexOf("/"));
		else
			return s;
	}

	private String RemNull(String s) {
		if (s == null || s.toLowerCase().equals("null") || s.equals("-1"))
			return "Unknown";
		else
			return s;
	}

	private String StripTitle(String s) {
		String res = "";
		int start = s.lastIndexOf("\\");
		if (start == s.length())
			return "Unknown";
		start += 1;
		int end = s.lastIndexOf(".") - 1;
		if (end == s.length())
			return "Unknown";
		end += 1;
		res = s.substring(start, end);
		if (res == null || res.length() < 1)
			return "Unknown";
		return res;
	}

	/**
	 * Iterate through the list and print the contents
	 */
	private void printData() {

		System.out.println("No of Tracks '" + tracks.size() + "'.");

		Iterator<Song> it = tracks.iterator();

		while (it.hasNext()) {
			Song song = it.next();
			System.out.println(song.getAlbum() + " - " + song.getName());
		}
	}

	@Override
	public List<Song> getTracks() {
		return StaticMethods.deduplicate(tracks);
	}

	/*public Map<String, BufferedImage> getArtwork() {
		return artwork;
	}*/

	@Override
	public int trackCount() {
		return tracks.size();
	}

	@Override
	public void setValid(boolean b) {
		valid = b;
	}

	@Override
	public boolean isValid() {
		return valid;
	}
}
