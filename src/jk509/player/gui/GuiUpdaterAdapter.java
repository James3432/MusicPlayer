package jk509.player.gui;

import jk509.player.gui.Updater;

import java.awt.Color;

import javax.swing.SwingUtilities;

import jk509.player.Setup;
import jk509.player.core.Library;

/**
 * This is a thread for executing the DataModel.extractFeatures without tying up the swing dispatch thread.
 * 
 */
public class GuiUpdaterAdapter implements Updater {

	Runnable suspendGUI;

	public Runnable resumeGUI;

	// ErrorGUI errorGUI;

	UpdateGUI updateGUI;

	boolean perFile;

	boolean perWindow;

	String valuesSavePath;

	String definitionSavePath;

	int windowSize;

	double windowOverlap;

	boolean hasRun = false;
	
	Setup form;

	ProgressFrame progressFrame;

	/**
	 * This constructor constructs the thread, partially preparing it for execution
	 * 
	 * @param c
	 *            Near global container for numerous controller and model objects
	 * @param of
	 *            Link to outerframe of the gui. Used to disable the main frame to prevent race conditions in the feature settings.
	 */
	public GuiUpdaterAdapter(final Setup form) {

		suspendGUI = new Runnable() {
			public void run() {
				
				form.lblThisMayTake.setVisible(true);
				form.btnAnalyse.setEnabled(false);
				form.btnAnalyse.setForeground(Color.GRAY);
				form.progressBar.setValue(0);
				form.fileProgressBar.setValue(0);
				form.fileProgressBar.setMaximum(100);
				form.progressBar.setMaximum(100);
				
				form.lblProcessingCount.setText(Integer.toString(form.library.getPlaylists().get(Library.MAIN_PLAYLIST).size()));
				form.lblProcessingName.setText(form.library.getPlaylists().get(Library.MAIN_PLAYLIST).get(0).toString());
				form.lblProcessingStart.setText("1");
				
			}
		};

		resumeGUI = new Runnable() {
			public void run() {
				form.progressBar.setValue(form.progressBar.getMaximum());
				form.fileProgressBar.setValue(form.fileProgressBar.getMaximum());
				form.lblProcessingStart.setText(form.lblProcessingCount.getText());//Integer.toString(form.library.getPlaylist(0).size()));
				form.lblProcessingName.setText("");
				form.lblThisMayTake.setText("Processing complete.");
				form.btnNext5.setEnabled(true);
			}
		};

		updateGUI = new UpdateGUI();

		this.form = form;
		announceUpdate(0, 0);

	}
	
	public void suspend(){
		SwingUtilities.invokeLater(suspendGUI);
	}
	
	public void resume(){
		SwingUtilities.invokeLater(resumeGUI);
	}

	class UpdateGUI implements Runnable {
		int numberOfFiles;

		int file = 0;

		int thisFileLength = 0;

		int pos = 0;

		public void setLengths(int file) {
			numberOfFiles = file;
		}

		public void setMaxWindows(int maxWin) {
			thisFileLength = maxWin;
		}

		public void setPos(int file, int pos) {
			this.file = file;
			this.pos = pos;
		}

		public void setPos(int pos) {
			this.pos = pos;
		}

		public void run() {
			form.fileProgressBar.setMaximum(thisFileLength);
			form.progressBar.setMaximum(numberOfFiles);
			form.fileProgressBar.setValue(pos);
			if(file > form.progressBar.getValue())
				form.progressBar.setValue(file);
			if(file+1 > Integer.parseInt(form.lblProcessingStart.getText()) && file < Integer.parseInt(form.lblProcessingCount.getText()))
				form.lblProcessingStart.setText(Integer.toString(file+1));
			
			if(file < form.library.getPlaylist(0).size())
				form.lblProcessingName.setText(form.library.getPlaylist(0).get(file).toString());
		}
	}

	/**
	 * This is part of the Updater interface. It notifies the gui that a file has been completed.
	 */
	public void announceUpdate(int fileNumber, int fileDone) {
		updateGUI.setPos(fileNumber, fileDone);
		SwingUtilities.invokeLater(updateGUI);
	}

	/**
	 * This is part of the Updater interface. It notifies the gui of an increase in the amount of the file processed.
	 */
	public void announceUpdate(int fileDone) {
		updateGUI.setPos(fileDone);
		SwingUtilities.invokeLater(updateGUI);
	}

	/**
	 * This is part of the Updater interface. It is used to set the total number of files to be processed.
	 */
	public void setNumberOfFiles(int files) {
		updateGUI.setLengths(files);
	}

	/**
	 * This is part of the Updater interface. It is used to notify the gui of the total size of the file (in windows of data).
	 */
	public void setFileLength(int window) {
		updateGUI.setMaxWindows(window);
	}

	/**
	 * Used to prevent this thread from executing twice.
	 * 
	 * @return whether or not this thread has run before
	 */
	public boolean hasRun() {
		return hasRun();
	}
}
