package jk509.player;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * An iTunes Library file SAX parser.
 */
public class ItunesParser extends DefaultHandler implements LibraryParser {

    private String libraryPath = "";

    private List<Song> tracks;
    private List<Playlist> playlists;

    private String tempVal;

    //to maintain context
    private Song tempTrack;

    private boolean foundTracks = false;

    private String previousTag;
    private String previousTagVal;

    public ItunesParser() {
        tracks = new ArrayList<Song>();
    }
    
    public ItunesParser(String loc) {
    	this();
    	libraryPath = loc;
    }

    public void runExample() {
        parseDocument();
        printData();
    }
    
    public void run() {
    	parseDocument();
    }

    private void parseDocument() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(libraryPath, this);
            
            prune();

        }catch(SAXException se) {
            se.printStackTrace();
        }catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData(){

        System.out.println("No of Tracks '" + tracks.size() + "'.");

        Iterator<Song> it = tracks.iterator();

        while(it.hasNext()) {
            Song song = it.next();
            System.out.println(song.getAlbum() + " - " + song.getName());
        }
    }

    //Event Handlers
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";

        if (foundTracks) {
            if ("key".equals(previousTag) && "dict".equalsIgnoreCase(qName)) {
                //create a new instance
                tempTrack = new Song();
                tracks.add(tempTrack);
            }
        } else {
            if ("key".equals(previousTag) && "Tracks".equalsIgnoreCase(previousTagVal) && "dict".equalsIgnoreCase(qName)) {
                foundTracks = true; // We are now inside the Tracks dict.
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // Append rather than replace: this is the secret to allowing HTML entities to parse correctly
    	tempVal += new String(ch,start,length);
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (foundTracks) {
            if (previousTagVal.equalsIgnoreCase("Name") && qName.equals("string"))
            {
            	tempTrack.setName(tempVal);
            }
            else if (previousTagVal.equalsIgnoreCase("Artist") && qName.equals("string"))
            {
                    tempTrack.setArtist(tempVal);
            }
            else if (previousTagVal.equalsIgnoreCase("Album") && qName.equals("string"))
            {
                    tempTrack.setAlbum(tempVal);
            }
            else if (previousTagVal.equalsIgnoreCase("Play Count") && qName.equals("integer"))
            {
                    Integer value = Integer.parseInt(tempVal);
                    tempTrack.setPlayCount(value.intValue());
            }
            else if (previousTagVal.equalsIgnoreCase("Track Number") && qName.equals("integer"))
            {
                    Integer value = Integer.parseInt(tempVal);
                    tempTrack.setTrackNumber(value.intValue());
            }
            else if (previousTagVal.equalsIgnoreCase("Total Time") && qName.equals("integer"))
            {
                    Integer value = Integer.parseInt(tempVal);
                    tempTrack.setLength(value.intValue() / 1000); // it's in milliseconds
            }
            else if (previousTagVal.equalsIgnoreCase("Genre") && qName.equals("string"))
            {
                    tempTrack.setGenre(tempVal);
            }
            else if (previousTagVal.equalsIgnoreCase("Kind") && qName.equals("string"))
            {
                    tempTrack.setType(tempVal);
            }
            else if (previousTagVal.equalsIgnoreCase("Location") && qName.equals("string"))
            {
            	String loc = tempVal.replaceAll("file://localhost/", "");
        		try {
        			loc = URLDecoder.decode(loc, "UTF-8");
        		} catch (UnsupportedEncodingException e) {
        			e.printStackTrace();
        		}
                tempTrack.setLocation(loc);
            }
            else if (previousTagVal.equalsIgnoreCase("Track ID") && qName.equals("integer"))
            {
                    Integer value = Integer.parseInt(tempVal);
                    tempTrack.setID(value.intValue());
            }
            else if (previousTagVal.equalsIgnoreCase("Date Added") && qName.equals("date"))
            {
                    try {
						tempTrack.setDateAdded(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.ENGLISH).parse(tempVal));
					} catch (ParseException e) {
						e.printStackTrace();
						tempTrack.setDateAdded(new Date());
					}
            }

            // Mark when we come to the end of the "Tracks" dict.
            if ("key".equals(qName) && "Playlists".equalsIgnoreCase(tempVal)) {
                foundTracks = false;
            }
        }

        // Keep track of the previous tag so we can track the context when we're at the second tag in a key, value pair.
        previousTagVal = tempVal;
        previousTag = qName;
    }
    
    private void prune(){
    	// Remove non-audio entries from list
    	// TODO: add support at least for WAV
    	for(int i = 0; i< tracks.size(); ++i){
	    	if(! tracks.get(i).getType().toLowerCase().equals("mpeg audio file")){
	    		tracks.remove(i);
	    		i--; // indicies have been shifted left
	    	}
    	}
    }

	@Override
	public List<Song> getTracks() {
		return tracks;
	}
	
	public List<Playlist> getPlaylists(){
		return playlists;
	}

	@Override
	public int trackCount() {
		return tracks.size();
	}

}
