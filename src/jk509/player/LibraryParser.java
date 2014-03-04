package jk509.player;

import java.util.List;

public interface LibraryParser {

	public void run();
	public List<Song> getTracks();
	public int trackCount();
	public void setPath(String s);
	public void setValid(boolean b);
	public boolean isValid();
	
}
