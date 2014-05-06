package jk509.player.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import jk509.player.MusicPlayer;
import jk509.player.core.Library;
import jk509.player.core.Playlist;
import jk509.player.core.Song;
import jk509.player.core.StaticMethods;
import jk509.player.logging.Logger;
import jk509.player.logging.Logger.LogType;
import christophedelory.playlist.SpecificPlaylist;
import christophedelory.playlist.SpecificPlaylistFactory;

import com.worldsworstsoftware.itunes.ItunesLibrary;
import com.worldsworstsoftware.itunes.ItunesPlaylist;
import com.worldsworstsoftware.itunes.ItunesTrack;
import com.worldsworstsoftware.itunes.parser.ItunesLibraryParser;

public class AddPlaylistDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JButton btnOk;
	private JButton btnCancel;
	Library library;
	List<Playlist> playlists;
	JFileChooser fc;
	String itunespath;
	List<String> playlistpaths;
	JFileChooser playlistChooser1;
	JFileChooser playlistChooser2;
	private JLabel lblFoundOk;
	private JLabel lblSupportedFormatsItunes;
	private JRadioButton rdbtnItunes;
	private JRadioButton rdbtnOther;
	private JButton button;
	private JButton btnBrowse;
	private JLabel lblFoundN;

	/**
	 * Create the dialog.
	 */
	public AddPlaylistDialog(Library library) {
		this.library = library;
		playlistpaths = new ArrayList<String>();
		setTitle("Import library files");
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon.png")));
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 530, 214);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblItunesMusicLibrary = new JLabel("Select one or more playlist files:");
			lblItunesMusicLibrary.setFocusable(false);
			lblItunesMusicLibrary.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			lblItunesMusicLibrary.setBounds(21, 48, 196, 20);
			contentPanel.add(lblItunesMusicLibrary);
		}
		{
			btnBrowse = new JButton("Choose XML file");
			btnBrowse.setEnabled(false);
			btnBrowse.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			btnBrowse.addActionListener(new BtnBrowseActionListener());
			btnBrowse.setBounds(329, 91, 123, 24);
			contentPanel.add(btnBrowse);
		}
		
		lblFoundOk = new JLabel("Found OK");
		lblFoundOk.setFocusable(false);
		lblFoundOk.setVisible(false);
		lblFoundOk.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblFoundOk.setBounds(329, 126, 103, 14);
		contentPanel.add(lblFoundOk);
		
		lblSupportedFormatsItunes = new JLabel("Supported formats: iTunes, PLIST, XML, WPL (Windows Media Player), M3U, ASX, RSS, and more...");
		lblSupportedFormatsItunes.setFocusable(false);
		lblSupportedFormatsItunes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblSupportedFormatsItunes.setBounds(10, 11, 504, 14);
		contentPanel.add(lblSupportedFormatsItunes);
		
		rdbtnItunes = new JRadioButton("iTunes");
		rdbtnItunes.setFocusable(false);
		rdbtnItunes.addActionListener(new RdbtnItunesActionListener());
		rdbtnItunes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		rdbtnItunes.setBounds(262, 92, 63, 23);
		contentPanel.add(rdbtnItunes);
		
		rdbtnOther = new JRadioButton("Other");
		rdbtnOther.setFocusable(false);
		rdbtnOther.addActionListener(new RdbtnOtherActionListener());
		rdbtnOther.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		rdbtnOther.setSelected(true);
		rdbtnOther.setBounds(21, 91, 63, 23);
		contentPanel.add(rdbtnOther);
		
		ButtonGroup rdbtns = new ButtonGroup();
		rdbtns.add(rdbtnItunes);
		rdbtns.add(rdbtnOther);
		
		button = new JButton("Choose files");
		button.addActionListener(new ButtonActionListener());
		button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		button.setBounds(87, 90, 123, 24);
		contentPanel.add(button);
		
		lblFoundN = new JLabel("Found 0 playlists");
		lblFoundN.setFocusable(false);
		lblFoundN.setVisible(false);
		lblFoundN.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblFoundN.setBounds(87, 126, 130, 14);
		contentPanel.add(lblFoundN);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnOk = new JButton("Import");
				btnOk.setEnabled(false);
				btnOk.addActionListener(new OkButtonActionListener());
				btnOk.setActionCommand("OK");
				buttonPane.add(btnOk);
				getRootPane().setDefaultButton(btnOk);
			}
			{
				btnCancel = new JButton("Cancel");
				btnCancel.addActionListener(new CancelButtonActionListener());
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
			}
		}
	}
	
	private boolean isItunesPath(){
		if(itunespath.endsWith(".xml") && (new File(itunespath)).exists()){
			return true;
		}else{
			return false;
		}
	}
	
	public File addExt(File f, String ext){
		String path = f.getAbsolutePath();
	
	    if(!path.endsWith(ext))
	    {
	      f = new File(path + ext);
	    }
	    
	    return f;
	}
	
	/*
     * Get the extension of a file.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

	private class CancelButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}
	private class OkButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			playlists = new ArrayList<Playlist>();
			if(rdbtnOther.isSelected()){
				processPlaylists();
			}else{
				ImportItunesPlaylists(itunespath);
			}
			library.addPlaylists(playlists);
			dispose();
		}
	}
	private class BtnBrowseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if(playlistChooser2 == null){
				playlistChooser2 = new JFileChooser();
				playlistChooser2.setFileFilter(new OBJfilter());
				String startat = StaticMethods.getHomeDir();
				playlistChooser2.setCurrentDirectory(new File(startat));
				
				playlistChooser2.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//playlistChooser2.setAcceptAllFileFilterUsed(true);
				playlistChooser2.setDialogTitle("Choose iTunes XML file");
				playlistChooser2.setMultiSelectionEnabled(false);
			}
			
			int result = playlistChooser2.showOpenDialog(AddPlaylistDialog.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File res = playlistChooser2.getSelectedFile();
				if(res != null && !res.equals("")){
					itunespath = res.getAbsolutePath();
					if(isItunesPath()){
						lblFoundOk.setText("Found OK");
						lblFoundOk.setVisible(true);
						btnOk.setEnabled(true);
					}else{
						lblFoundOk.setText("File error");
						lblFoundOk.setVisible(true);
					}
				}
			}
		}
	}
	private class ButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(playlistChooser1 == null){
				playlistChooser1 = new JFileChooser();
			
				String startat = StaticMethods.getHomeDir();
				playlistChooser1.setCurrentDirectory(new File(startat));
				
				playlistChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
				playlistChooser1.setAcceptAllFileFilterUsed(true);
				playlistChooser1.setDialogTitle("Choose playlist files");
				playlistChooser1.setMultiSelectionEnabled(true);
			}
			
			int result = playlistChooser1.showOpenDialog(AddPlaylistDialog.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File[] res = playlistChooser1.getSelectedFiles();
				if(res != null && res.length > 0){
					for(int i=0; i<res.length; ++i){
						String fpath = res[i].getAbsolutePath();
						playlistpaths.add(fpath);
					}
					lblFoundN.setText("Found "+res.length+" playlist"+(res.length > 1 ? "s" : ""));
					lblFoundN.setVisible(true);
					btnOk.setEnabled(true);
				}
			}
		}
	}
	private class RdbtnOtherActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			button.setEnabled(true);
			btnBrowse.setEnabled(false);
		}
	}
	private class RdbtnItunesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			button.setEnabled(false);
			btnBrowse.setEnabled(true);
		}
	}
	
	private Playlist importPlaylist(String fpath){
		try {
			File file = new File(fpath);
			SpecificPlaylist specificPlaylist;

			specificPlaylist = SpecificPlaylistFactory.getInstance().readFrom(file);

			if (specificPlaylist == null) {
				// error
			} else {
				christophedelory.playlist.Playlist genericPlaylist = specificPlaylist.toPlaylist();
				List<String> locs = new ArrayList<String>();
				StaticMethods.playlistConverter(genericPlaylist.getRootSequence(), locs);
				List<Song> playlist = StaticMethods.getSongsByLocation(locs, library.getPlaylists().get(Library.MAIN_PLAYLIST).getList());
				Playlist p = new Playlist(StaticMethods.getFileName(fpath), Playlist.USER, playlist);
				return p;
			}
		} catch (Exception e) {
			Logger.log(e, LogType.ERROR_LOG);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void ImportItunesPlaylists(String path) {
		ItunesLibrary ituneslibrary = ItunesLibraryParser.parseLibrary(path);
		List<ItunesPlaylist> iplaylists = ituneslibrary.getPlaylists();
		//outerloop:
		for (int i = 1; i < iplaylists.size(); ++i) { // skip 1 as it's all tracks
			ItunesPlaylist playlist = (ItunesPlaylist) iplaylists.get(i);
			String t = playlist.getName().toLowerCase();
			if(t.equals("library") || t.equals("music") || t.equals("films") || t.equals("tv programmes") || t.equals("music videos"))
				continue;
			//System.out.println(playlist.getName());
			Playlist pl = new Playlist(playlist.getName(), Playlist.USER);
			List<ItunesTrack> tracks = playlist.getPlaylistItems();
			for (int j = 0; j < tracks.size(); ++j) {
				try{
					ItunesTrack track = (ItunesTrack) tracks.get(j);
					String loc = track.getLocation().replaceAll("file://localhost/", "");
					try {
						loc = loc.replaceAll("\\+", "%2b");
						loc = URLDecoder.decode(loc, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						Logger.log(e, LogType.ERROR_LOG);
					}
					Song s = StaticMethods.GetSongByLoc(loc, library.getPlaylists().get(Library.MAIN_PLAYLIST).getList());
					if(s != null)
						//continue outerloop;
						pl.add(s);
				}catch(NullPointerException e){}
			}
			if(pl.size() > 0)
				playlists.add(pl);
		}
	}
	
	private void processPlaylists(){
		for(int i=0; i<playlistpaths.size(); ++i){
			playlists.add(importPlaylist(playlistpaths.get(i)));
		}
	}
	
	/*public class OpenFileChooser extends JFileChooser {
		
		private static final long serialVersionUID = 1L;
		public int type; // 1=ser, 2=xml

		@Override
	    public void approveSelection(){
	        File f = getSelectedFile();
	        if((f.exists() || (addExt(f,".xml").exists() && type==2) || (addExt(f,".ser").exists() && type==1) ) && getDialogType() == OPEN_DIALOG){
	            int result = JOptionPane.showConfirmDialog(this,"The file already exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
	            switch(result){
	                case JOptionPane.YES_OPTION:
	                    super.approveSelection();
	                    return;
	                case JOptionPane.NO_OPTION:
	                    return;
	                case JOptionPane.CLOSED_OPTION:
	                    return;
	                case JOptionPane.CANCEL_OPTION:
	                    cancelSelection();
	                    return;
	            }
	        }
	        super.approveSelection();
	    }
	
	}*/
	class OBJfilter extends FileFilter {
		
		public OBJfilter(){
			super();	
		}
			
		@Override
		public String getDescription(){
			String des = "Library files";
			return des;
		}
		
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
		        return true;
		    }
	
		    String extension = getExtension(f);
		    if (extension != null) {
		        if (extension.equals("xml")) {
		                return true;
		        } else {
		            return false;
		        }
		    }
	
		    return false;
		}
	
	}
}
