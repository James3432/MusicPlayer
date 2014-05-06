package jk509.player.gui;

import java.io.File;
import java.io.FilenameFilter;

import jk509.player.Constants;

public class GenericExtFilter implements FilenameFilter {
	private String ext;

	public GenericExtFilter(String ext) {
		this.ext = ext;
	}

	public boolean accept(File dir, String name) {
		return (name.toLowerCase().endsWith(ext) && name.length() == Constants.TEMP_FILE_NAME_LENGTH + 4); // file name + extension length = 14 or so
	}
}