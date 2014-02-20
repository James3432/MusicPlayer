package jk509.player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Insets;

import javax.swing.ImageIcon;

public class FormTest {

	private JFrame frmMusicPlayer;
	private JScrollPane scrlMain;
	private JTable tabMain;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnView;
	private JMenu mnSettings;
	private JMenu mnHelp;
	private JMenuItem mntmImportTracks;
	private JMenuItem mntmExit;
	private JMenuItem mntmExportData;
	private JMenuItem mntmOptions;
	private JMenuItem mntmVisualiser;
	private JMenuItem mntmFullScreen;
	private JMenuItem mntmOptions_1;
	private JMenuItem mntmInformation;
	private JMenuItem mntmAbout;
	private JSplitPane splitPlaylists;
	private JMenuItem mntmImportFromDisk;
	private JPanel pnlControls;
	private JScrollPane scrlPlaylists;
	private JList<String> listPlaylists;
	private JTextField txtSearch;
	private JLabel lblSearch;
	private JPanel pnlTrackInfo;
	private JButton btnTrackInfo;
	private JPanel pnlArt;
	private JButton btnArtwork;
	private JButton btnControlsVolume;
	private JSlider slider;
	private JSplitPane splitPane;
	private JLabel lblHi;
	private JLabel lblHo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormTest window = new FormTest();
					window.startup();
					window.frmMusicPlayer.setVisible(true);
					window.frmMusicPlayer.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 *  My variables
	 */
	Library library;
	SoundJLayer player;
	private JPanel pnlListControls;
	private final JPanel panel = new JPanel();
	private JButton btnAdd;
	private JButton btnDelete;
	private JButton btnPlay;
	
	/**
	 * Create the application.
	 */
	public FormTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMusicPlayer = new JFrame();
		frmMusicPlayer.setMinimumSize(new Dimension(800, 600));
		frmMusicPlayer.setTitle("Music Player");
		frmMusicPlayer.setIconImage(Toolkit.getDefaultToolkit().getImage(FormTest.class.getResource("/jk509/player/res/icon.png")));
		frmMusicPlayer.setBounds(100, 100, 838, 539);
		frmMusicPlayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMusicPlayer.getContentPane().setLayout(new BorderLayout(0, 0));
		
		pnlControls = new JPanel();
		frmMusicPlayer.getContentPane().add(pnlControls, BorderLayout.NORTH);
		
		splitPane = new JSplitPane();
		splitPane.setPreferredSize(new Dimension(50, 50));
		splitPane.setMinimumSize(new Dimension(50, 50));
		pnlControls.add(splitPane);
		
		lblHi = new JLabel("hi");
		splitPane.setLeftComponent(lblHi);
		
		lblHo = new JLabel("ho");
		splitPane.setRightComponent(lblHo);
		
		btnControlsVolume = new JButton("Controls, volume");
		pnlControls.add(btnControlsVolume);
		
		btnPlay = new JButton();
		btnPlay.addActionListener(new ButtonActionListener());
		btnPlay.setIcon(new ImageIcon(FormTest.class.getResource("/jk509/player/res/play.png")));
		btnPlay.setMargin(new Insets(0, 0, 0, 0));
		btnPlay.setFocusPainted(false);
		pnlControls.add(btnPlay);
		
		pnlArt = new JPanel();
		pnlControls.add(pnlArt);
		
		btnArtwork = new JButton("Artwork");
		pnlArt.add(btnArtwork);
		
		pnlTrackInfo = new JPanel();
		pnlControls.add(pnlTrackInfo);
		
		btnTrackInfo = new JButton("Track info");
		pnlTrackInfo.add(btnTrackInfo);
		
		slider = new JSlider();
		pnlTrackInfo.add(slider);
		
		lblSearch = new JLabel("Search: ");
		pnlControls.add(lblSearch);
		
		txtSearch = new JTextField();
		txtSearch.setText("Search");
		pnlControls.add(txtSearch);
		txtSearch.setColumns(10);
		
		splitPlaylists = new JSplitPane();
		frmMusicPlayer.getContentPane().add(splitPlaylists, BorderLayout.CENTER);
		splitPlaylists.setContinuousLayout(true);
		splitPlaylists.setBorder(null);
		
		scrlMain = new JScrollPane();
		splitPlaylists.setRightComponent(scrlMain);
		scrlMain.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrlMain.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		tabMain = new JTable();
		tabMain.addMouseListener(new TabMainMouseListener());
		tabMain.setShowVerticalLines(false);
		tabMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabMain.setModel(new DefaultTableModel(
			new Object[][] {
				{"stuff", null, "awsef", null, null},
				{null, "asd", null, null, "we"},
				{null, null, "sdef", "wef", "wef"},
				{null, "EWF", "asf", null, null},
				{null, null, null, null, "wEF"},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, "fwef", "WEF", null, null},
				{null, "wqefr", null, "wef", null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, "WEF", "WEF"},
			},
			new String[] {
				"Name", "Album", "Artist", "Genre", "Date added"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class, String.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, false, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		scrlMain.setViewportView(tabMain);
		tabMain.setModel(new TableSorter(tabMain.getModel(), tabMain.getTableHeader()));
		
		scrlPlaylists = new JScrollPane();
		scrlPlaylists.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrlPlaylists.setMaximumSize(new Dimension(200, 23));
		scrlPlaylists.setPreferredSize(new Dimension(180, 23));
		scrlPlaylists.setMinimumSize(new Dimension(100, 23));
		//splitPlaylists.setLeftComponent(scrlPlaylists);
		
		listPlaylists = new JList<String>(); //TODO: change to Playlist and use custom renderer...
		listPlaylists.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		listPlaylists.setBorder(new EmptyBorder(5, 5, 5, 5));
		listPlaylists.setVisibleRowCount(20);
		listPlaylists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPlaylists.setModel(new AbstractListModel<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] values = new String[] {"Artist", "Album", "Song", "---------", "Playlist 1", "Playlist 2", "..."};
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});
		scrlPlaylists.setViewportView(listPlaylists);
		
		pnlListControls = new JPanel();
		splitPlaylists.setLeftComponent(pnlListControls);
		pnlListControls.setLayout(new BorderLayout(0, 0));
		pnlListControls.add(scrlPlaylists);
		pnlListControls.add(panel, BorderLayout.SOUTH);
		
		btnAdd = new JButton("Add");
		panel.add(btnAdd);
		
		btnDelete = new JButton("Delete");
		panel.add(btnDelete);
		
		menuBar = new JMenuBar();
		frmMusicPlayer.setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmImportTracks = new JMenuItem("Import from iTunes");
		mntmImportTracks.addActionListener(new BtnImportFromItunesActionListener());
		mnFile.add(mntmImportTracks);
		
		mntmImportFromDisk = new JMenuItem("Import from Disk");
		mnFile.add(mntmImportFromDisk);
		
		mntmExportData = new JMenuItem("Export data...");
		mnFile.add(mntmExportData);
		
		mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		mnView = new JMenu("View");
		menuBar.add(mnView);
		
		mntmOptions = new JMenuItem("Options...");
		mnView.add(mntmOptions);
		
		mntmVisualiser = new JMenuItem("Visualiser");
		mnView.add(mntmVisualiser);
		
		mntmFullScreen = new JMenuItem("Full screen");
		mnView.add(mntmFullScreen);
		
		mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);
		
		mntmOptions_1 = new JMenuItem("Options...");
		mnSettings.add(mntmOptions_1);
		
		mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		mntmInformation = new JMenuItem("Information");
		mnHelp.add(mntmInformation);
		
		mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
	}
	
	//TODO: remove?
	public enum Genre { Classical, Rock }
	
	private void startup(){
		// first-time run checks
		
		String homedir = System.getenv("user.home");
		if(homedir == null)
			homedir = System.getenv("USERPROFILE");
		File settings = new File(homedir+"\\library.ser");
		
		if(! settings.exists()){
			library = new Library();
			Setup s = new Setup(library);
			s.setLocationRelativeTo(frmMusicPlayer);
			s.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			s.addWindowListener(new WindowAdapter() {
			    @Override
			    public void windowClosed(WindowEvent e) {
			        SetupStatus();
			    }
			});
			s.setVisible(true);
		}else{
			FileInputStream fin;
			try {
				fin = new FileInputStream(settings);
	            ObjectInputStream ois = new ObjectInputStream(fin);
	            library = (Library) ois.readObject();
	            ois.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void SetupStatus(){
		// TODO (check valid library, whether setup closed early or successfully)
	}
	
	@SuppressWarnings("unused")
	private void UpdateLibrary(){
		String homedir = System.getenv("user.home");
		if(homedir == null)
			homedir = System.getenv("USERPROFILE");
		File settings = new File(homedir+"\\library.ser");
		
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(settings);
	        ObjectOutputStream oos = new ObjectOutputStream(fout);
	        oos.writeObject(library);
	        oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class BtnImportFromItunesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			LibraryParser parser = new ItunesParser("C:\\Users\\James\\Music\\iTunes\\iTunes Music Library.xml");
			
			parser.run();
			
			Object[][] rows = new Object[parser.trackCount()][6];
			for(int i=0; i< parser.getTracks().size(); ++i){
				Song s = parser.getTracks().get(i);
				rows[i][0] = s.getName();
				rows[i][1] = s.getAlbum();
				rows[i][2] = s.getArtist();
				rows[i][3] = s.getGenre();
				rows[i][4] = (new SimpleDateFormat("dd/MM/yyyy")).format(s.getDateAdded());
				rows[i][5] = s.getLocation();
			}
			
			tabMain.setModel(new DefaultTableModel(
					rows,
					new String[] {
						"Name", "Album", "Artist", "Genre", "Date added", "location"
					}
				) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;
					@SuppressWarnings("rawtypes")
					Class[] columnTypes = new Class[] {
						String.class, String.class, String.class, String.class, String.class, String.class
					};
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
					boolean[] columnEditables = new boolean[] {
						false, false, false, false, false, false
					};
					public boolean isCellEditable(int row, int column) {
						return columnEditables[column];
					}
				});
			tabMain.setModel(new TableSorter(tabMain.getModel(), tabMain.getTableHeader()));
		}
	}
	private class TabMainMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			int row = tabMain.getSelectedRow();
			String loc = (String) tabMain.getModel().getValueAt(row, 5);
			loc = loc.replaceAll("file://localhost/", "");
			try {
				loc = URLDecoder.decode(loc, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO: problem is the leading file:/ bit. May not need the above line. Move this processing into the parser (or at least the bit which gets s.getLocation())
			if(player != null)
				player.stop();
			player = new SoundJLayer(loc);
			player.play();
			btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
		}
	}
	private class ButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(player != null){
				if(player.isPaused() && !player.isStopped()){
					player.play();
					btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
				}else{
					player.pause();
					btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/play.png")));
				}	
			}
		}
	}


}
