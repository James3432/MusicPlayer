package jk509.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class MusicPlayer implements MouseListener, MouseMotionListener {

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
	private JList listPlaylists;
	private JTextField txtSearch;
	private JLabel lblSearch;
	private JPanel pnlTrackInfo;
	private JPanel pnlArt;
	private JButton btnArtwork;
	private JButton btnControlsVolume;
	private JSlider slider;
	private JPanel pnlDetails;

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
					MusicPlayer window = new MusicPlayer();
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
	 * My variables
	 */
	Library library;
	SoundJLayer player;
	private JPanel pnlListControls;
	private JPanel panel;
	private JButton btnAdd;
	private JButton btnDelete;
	private JButton btnPlay;
	private int rowPlaying;
	private int rowSelected; // changes according to sort
	private Point start;
	private PlaybackListener playbackListener = new PlaybackListener();
	private int THREAD_SLEEP = 10;
	private int MAX_SLIDER_RANGE = 300;
	private boolean repeat = false;
	private boolean shuffle = false;
	private boolean mouseIsDown = false;
	private int seconds = 0; // seconds through current track
	private float milliseconds = 0; // just like seconds but in millis
	private int timing_offset = 0; // The time through the song (in ms) which we were before last restart (after pause or skip)

	// DEBUG FLAGS
	boolean HIDE_SETUP_DIALOG = true;
	private JButton btnShuffleRepeat;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JLabel lbl_time;
	private JLabel lbl_endtime;
	private JLabel lblTrack;
	private JLabel lblArtistAlbum;
	private JPanel pnlTrackTime;
	private JButton btnRepeat;
	private JButton btnShuffle;

	/**
	 * Create the application.
	 */
	public MusicPlayer() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMusicPlayer = new JFrame();
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyDispatcher());
		frmMusicPlayer.setMinimumSize(new Dimension(900, 600));
		frmMusicPlayer.setTitle("Music Player");
		frmMusicPlayer.setIconImage(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon.png")));
		frmMusicPlayer.setBounds(100, 100, 838, 539);
		frmMusicPlayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMusicPlayer.getContentPane().setLayout(new BorderLayout(0, 0));
		frmMusicPlayer.addMouseListener(this);
		frmMusicPlayer.addMouseMotionListener(this);

		pnlControls = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				Color color1 = getBackground();
				Color color2 = color1.darker();
				int w = getWidth();
				int h = getHeight();
				GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, w, h);
			}
		};
		frmMusicPlayer.getContentPane().add(pnlControls, BorderLayout.NORTH);

		btnShuffleRepeat = new JButton("Shfle,repeat");
		btnShuffleRepeat.setOpaque(false);
		pnlControls.add(btnShuffleRepeat);

		btnControlsVolume = new JButton("vol");
		btnControlsVolume.setFocusable(false);
		btnControlsVolume.setOpaque(false);
		pnlControls.add(btnControlsVolume);

		btnPlay = new JButton();
		btnPlay.setFocusable(false);
		btnPlay.setOpaque(false);
		btnPlay.setContentAreaFilled(false);
		btnPlay.setBorderPainted(false);
		btnPlay.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/play.png")));
		btnPlay.addActionListener(new ButtonActionListener());

		btnNewButton = new JButton();
		btnNewButton.addActionListener(new BtnNewButtonActionListener());
		btnNewButton.setFocusable(false);
		btnNewButton.setOpaque(false);
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setBorderPainted(false);
		btnNewButton.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/back.png")));
		pnlControls.add(btnNewButton);
		btnPlay.setMargin(new Insets(0, 0, 0, 0));
		btnPlay.setFocusPainted(false);
		pnlControls.add(btnPlay);

		btnNewButton_1 = new JButton();
		btnNewButton_1.addActionListener(new BtnNewButton_1ActionListener());
		btnNewButton_1.setFocusable(false);
		btnNewButton_1.setOpaque(false);
		btnNewButton_1.setContentAreaFilled(false);
		btnNewButton_1.setBorderPainted(false);
		btnNewButton_1.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/fwd.png")));
		pnlControls.add(btnNewButton_1);

		pnlArt = new JPanel();
		pnlArt.setOpaque(false);
		pnlControls.add(pnlArt);

		btnArtwork = new JButton("Artwork");
		btnArtwork.setFocusable(false);
		btnArtwork.setOpaque(false);
		pnlArt.add(btnArtwork);

		pnlTrackInfo = new JPanel();
		pnlTrackInfo.setOpaque(false);
		pnlControls.add(pnlTrackInfo);
		pnlTrackInfo.setLayout(new BorderLayout(0, 0));

		pnlDetails = new JPanel();
		pnlDetails.setPreferredSize(new Dimension(100, 30));
		pnlDetails.setOpaque(false);
		pnlTrackInfo.add(pnlDetails, BorderLayout.NORTH);
		pnlDetails.setMinimumSize(new Dimension(100, 30));

		lblTrack = new JLabel("");
		pnlDetails.add(lblTrack);

		lblArtistAlbum = new JLabel("");
		pnlDetails.add(lblArtistAlbum);

		pnlTrackTime = new JPanel();
		pnlTrackTime.setOpaque(false);
		pnlTrackInfo.add(pnlTrackTime, BorderLayout.SOUTH);

		btnRepeat = new JButton();
		btnRepeat.addActionListener(new BtnRepeatActionListener());
		btnRepeat.setOpaque(false);
		btnRepeat.setMargin(new Insets(0, 0, 0, 0));
		btnRepeat.setFocusable(false);
		btnRepeat.setFocusPainted(false);
		btnRepeat.setContentAreaFilled(false);
		btnRepeat.setBorderPainted(false);
		btnRepeat.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/repeat.png")));
		pnlTrackTime.add(btnRepeat);

		lbl_time = new JLabel("0:00");
		pnlTrackTime.add(lbl_time);

		slider = new JSlider();
		pnlTrackTime.add(slider);
		slider.setValue(0);
		slider.setMaximum(240);
		slider.addChangeListener(new SliderChangeListener());
		slider.setFocusable(false);
		slider.setOpaque(false);
		final CustomSlider ui = new CustomSlider(slider);
		slider.setUI(ui);

		lbl_endtime = new JLabel("0:00");
		pnlTrackTime.add(lbl_endtime);

		btnShuffle = new JButton();
		btnShuffle.addActionListener(new BtnShuffleActionListener());
		btnShuffle.setOpaque(false);
		btnShuffle.setMargin(new Insets(0, 0, 0, 0));
		btnShuffle.setFocusable(false);
		btnShuffle.setFocusPainted(false);
		btnShuffle.setContentAreaFilled(false);
		btnShuffle.setBorderPainted(false);
		btnShuffle.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/shuffle.png")));
		pnlTrackTime.add(btnShuffle);
		MouseListener[] listeners = slider.getMouseListeners();
		MouseMotionListener[] mmls = slider.getMouseMotionListeners();

		for (MouseListener l : listeners)
			slider.removeMouseListener(l); // remove UI-installed TrackListener
		for (MouseMotionListener l : mmls)
			slider.removeMouseMotionListener(l); // remove UI-installed
													// TrackListener
		BasicSliderUI.TrackListener tl = ui.new TrackListener() {
			// this is where we jump to absolute value of click
			@Override
			public void mousePressed(MouseEvent e) {
				mouseIsDown = true;
				Point p = e.getPoint();
				int value = ui.valueForXPosition(p.x);
				slider.setValue(value);
				// update time label
				lbl_time.setText(Song.SecondsToString((int) (((double) value * library.getTracks().get(rowPlaying).getLengthS()) / (double) slider.getMaximum())));
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				mouseIsDown = false;
				Point p = e.getPoint();
				int value = ui.valueForXPosition(p.x);
				slider.setValue(value);
				// update time label
				double secs = ((double) value * (double) library.getTracks().get(rowPlaying).getLengthS()) / (double) slider.getMaximum();
				lbl_time.setText(Song.SecondsToString((int) secs));
				// skip song to this point
				SkipTo(secs);
				timing_offset = (int) Math.floor(1000 * secs);
			}

			// disable check that will invoke scrollDueToClickInTrack
			@Override
			public boolean shouldScroll(int dir) {
				return false;
			}
		};
		BasicSliderUI.TrackListener mml = ui.new TrackListener() {
			// this is where we jump to absolute value of click
			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = e.getPoint();
				int value = ui.valueForXPosition(p.x);
				slider.setValue(value);
				// update time label
				lbl_time.setText(Song.SecondsToString((int) (((double) value * library.getTracks().get(rowPlaying).getLengthS()) / (double) slider.getMaximum())));
			}

			// disable check that will invoke scrollDueToClickInTrack
			@Override
			public boolean shouldScroll(int dir) {
				return false;
			}
		};
		slider.addMouseListener(tl);
		slider.addMouseMotionListener(mml);

		lblSearch = new JLabel();
		pnlControls.add(lblSearch);
		lblSearch.setOpaque(false);
		lblSearch.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search.png")));
		txtSearch = new JTextField();
		txtSearch.addFocusListener(new TxtSearchFocusListener());
		txtSearch.setForeground(Color.GRAY);
		txtSearch.setText(" Search");
		pnlControls.add(txtSearch);
		txtSearch.setColumns(10);

		splitPlaylists = new JSplitPane();
		frmMusicPlayer.getContentPane().add(splitPlaylists, BorderLayout.CENTER);
		splitPlaylists.setContinuousLayout(true);
		splitPlaylists.setBorder(null);

		scrlMain = new JScrollPane();

		scrlMain.setMinimumSize(new Dimension(400, 23));
		splitPlaylists.setRightComponent(scrlMain);
		scrlMain.getViewport().setBackground(new Color(250, 250, 250));
		scrlMain.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrlMain.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		tabMain = new JTable() {
			private static final long serialVersionUID = 1L;
			private Border paddingBorder = BorderFactory.createEmptyBorder(3, 8, 3, 5); // top
																						// left
																						// bottom
																						// right

			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				// custom rendering
				if (!c.getBackground().equals(new Color(139, 167, 201))) {
					if (row % 2 == 0)
						c.setBackground(new Color(241, 244, 247));
					else
						c.setBackground(new Color(250, 250, 250));
				}

				if (JComponent.class.isInstance(c)) {
					((JComponent) c).setBorder(paddingBorder);
				}

				return c;
			}

		};

		tabMain.setFocusable(false);
		tabMain.setBackground(new Color(250, 250, 250));
		tabMain.setIntercellSpacing(new Dimension(0, 1));
		tabMain.setSelectionBackground(new Color(139, 167, 201));
		tabMain.setGridColor(new Color(223, 223, 223));
		tabMain.setRowHeight(22);
		tabMain.setDoubleBuffered(true);
		tabMain.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		tabMain.addMouseListener(new TabMainMouseListener());
		tabMain.setShowVerticalLines(false);
		tabMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		/*
		 * tabMain.setModel(new DefaultTableModel( new Object[][] { {"stuff",
		 * null, "awsef", null, null, null}, {null, "asd", null, null, "we",
		 * null}, {null, null, "sdef", "wef", "wef", null}, {null, "EWF", "asf",
		 * null, null, null}, {null, null, null, null, "wEF", null}, {null,
		 * null, null, null, null, null}, {null, null, null, null, null, null},
		 * {null, null, null, null, null, null}, {null, null, null, null, null,
		 * null}, {null, null, null, null, null, null}, {null, "fwef", "WEF",
		 * null, null, null}, {null, "wqefr", null, "wef", null, null}, {null,
		 * null, null, null, null, null}, {null, null, null, null, null, null},
		 * {null, null, null, null, null, null}, {null, null, null, "WEF",
		 * "WEF", null}, }, new String[] { "Name", "Album", "Artist", "Genre",
		 * "Date Added", "Location" } ));
		 */
		tabMain.getSelectionModel().setSelectionInterval(0, 0);
		scrlMain.setViewportView(tabMain);
		JScrollBar sb = scrlMain.getVerticalScrollBar();
		sb.setPreferredSize(new Dimension(25, 50));
		scrlMain.setVerticalScrollBar(sb);
		// tabMain.setModel(new TableSorter(tabMain.getModel(),
		// tabMain.getTableHeader()));
		scrlPlaylists = new JScrollPane();
		scrlPlaylists.setBorder(new LineBorder(Color.LIGHT_GRAY));
		scrlPlaylists.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrlPlaylists.setMaximumSize(new Dimension(200, 23));
		scrlPlaylists.setPreferredSize(new Dimension(180, 23));
		scrlPlaylists.setMinimumSize(new Dimension(100, 23));

		listPlaylists = new JList(); // TODO: change to Playlist and use
		listPlaylists.setFocusable(false);
		// custom renderer...
		listPlaylists.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		listPlaylists.setBorder(new EmptyBorder(5, 5, 5, 5));
		listPlaylists.setBackground(new Color(213, 219, 226));
		listPlaylists.setSelectionBackground(new Color(139, 167, 201));
		listPlaylists.setSelectionForeground(new Color(250, 250, 250));
		listPlaylists.setVisibleRowCount(20);
		listPlaylists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPlaylists.setModel(new AbstractListModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] values = new String[] { "Artist", "Album", "Song", "---------", "Playlist 1", "Playlist 2", "..." };

			public int getSize() {
				return values.length;
			}

			public String getElementAt(int index) {
				return values[index];
			}
		});
		listPlaylists.setSelectedIndex(0);
		// scrlPlaylists.setViewportView(listPlaylists);
		JPanel pnlT = new JPanel();
		pnlT.setBackground(new Color(213, 219, 226));
		pnlT.setLayout(new BorderLayout(0, 0));
		pnlT.add(listPlaylists, BorderLayout.NORTH);
		scrlPlaylists.setViewportView(pnlT);

		panel = new JPanel();
		panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel.setBackground(new Color(213, 219, 226));

		pnlListControls = new JPanel();
		splitPlaylists.setLeftComponent(pnlListControls);
		pnlListControls.setLayout(new BorderLayout(0, 0));
		pnlListControls.add(scrlPlaylists);
		pnlListControls.add(panel, BorderLayout.SOUTH);

		btnAdd = new JButton();
		btnAdd.setFocusable(false);
		btnAdd.setOpaque(false);
		btnAdd.setContentAreaFilled(false);
		btnAdd.setBorderPainted(false);
		btnAdd.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/add.png")));
		btnAdd.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/add_down.png")));
		panel.add(btnAdd);

		btnDelete = new JButton();
		btnDelete.setFocusable(false);
		btnDelete.setOpaque(false);
		btnDelete.setContentAreaFilled(false);
		btnDelete.setBorderPainted(false);
		btnDelete.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/delete.png")));
		btnDelete.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/delete_down.png")));
		panel.add(btnDelete);

		menuBar = new JMenuBar();
		frmMusicPlayer.setJMenuBar(menuBar);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmImportTracks = new JMenuItem("Import from iTunes");
		mntmImportTracks.addActionListener(new BtnImportFromItunesActionListener());
		mnFile.add(mntmImportTracks);

		mntmImportFromDisk = new JMenuItem("Import from Disk");
		mntmImportFromDisk.addActionListener(new BtnImportFromDiskActionListener());
		mnFile.add(mntmImportFromDisk);

		mntmExportData = new JMenuItem("Export data...");
		mntmExportData.addActionListener(new BtnExportDataActionListener());
		mnFile.add(mntmExportData);

		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new BtnExitActionListener());
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

	// TODO: remove?
	public enum Genre {
		Classical, Rock
	}

	private void startup() {
		if (HIDE_SETUP_DIALOG){
			library = new Library();
			return;
		}

		// first-time run checks

		String homedir = System.getenv("user.home");
		if (homedir == null)
			homedir = System.getenv("USERPROFILE");
		File settings = new File(homedir + "\\library.ser");

		if (!settings.exists()) {
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
		} else {
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

	private void SetupStatus() {
		// TODO (check valid library, whether setup closed early or
		// successfully)
	}

	@SuppressWarnings("unused")
	private void UpdateLibrary() {
		String homedir = System.getenv("user.home");
		if (homedir == null)
			homedir = System.getenv("USERPROFILE");
		File settings = new File(homedir + "\\library.ser");

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
			Import(parser);
		}
	}

	private class BtnImportFromDiskActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			LibraryParser parser = new FileScanner("E:\\Users\\James\\Music\\iTunes\\iTunes Media\\Music"); // with
																											// trailing
																											// slashes??
			Import(parser);
		}
	}

	private void Import(LibraryParser parser) {
		parser.run();

		library.addTracks(parser.getTracks());

		Object[][] rows = new Object[parser.trackCount()][9];
		for (int i = 0; i < library.size(); ++i) {
			Song s = library.get(i);
			rows[i][0] = null;
			rows[i][1] = s.getTrackNumber();
			rows[i][2] = s.getName();
			rows[i][3] = s.getAlbum();
			rows[i][4] = s.getArtist();
			rows[i][5] = s.getGenre();
			try {
				rows[i][6] = (new SimpleDateFormat("dd/MM/yyyy")).format(s.getDateAdded());
			} catch (NullPointerException e) {
				rows[i][6] = "01/01/2000";
			}
			rows[i][7] = s.getLength();
			rows[i][8] = s.getLocation();
		}

		tabMain.setModel(new DefaultTableModel(rows, new String[] { "", "", "Name", "Album", "Artist", "Genre", "Date Added", "Time", "Location" }) {
			/**
				 * 
				 */
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { ImageIcon.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tabMain.setModel(new TableSorter(tabMain.getModel(), tabMain.getTableHeader(), new TableRowSortedListener(), tabMain));

		tabMain.getSelectionModel().setSelectionInterval(0, 0);

		int miniWidth = 25;
		tabMain.getColumnModel().getColumn(0).setPreferredWidth(miniWidth);
		tabMain.getColumnModel().getColumn(0).setMinWidth(miniWidth);
		tabMain.getColumnModel().getColumn(0).setMaxWidth(miniWidth);
		tabMain.getColumnModel().getColumn(1).setPreferredWidth(miniWidth);
		tabMain.getColumnModel().getColumn(1).setMinWidth(miniWidth);
		tabMain.getColumnModel().getColumn(1).setMaxWidth(miniWidth);

		tabMain.removeColumn(tabMain.getColumnModel().getColumn(8));

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		tabMain.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

		tabMain.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

				Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				// NOT NEEDED: it's always 0
				// if (column == 0) {
				if (row == rowPlaying && (player != null) && !player.isStopped()) {
					if (isSelected)
						if (player.isPaused())
							((JLabel) cell).setIcon((Icon) new ImageIcon(this.getClass().getResource("/jk509/player/res/paused_s.png")));
						else
							((JLabel) cell).setIcon((Icon) new ImageIcon(this.getClass().getResource("/jk509/player/res/playing_s.png")));
					else if (player.isPaused())
						((JLabel) cell).setIcon((Icon) new ImageIcon(this.getClass().getResource("/jk509/player/res/paused.png")));
					else
						((JLabel) cell).setIcon((Icon) new ImageIcon(this.getClass().getResource("/jk509/player/res/playing.png")));
				} else
					((JLabel) cell).setIcon((Icon) null);

				((JLabel) cell).setText("");
				((JLabel) cell).setHorizontalAlignment(JLabel.CENTER);

				return cell;

			}
		});

	}

	private class BtnExportDataActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			// TODO
		}
	}

	private class BtnExitActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	}

	private class TabMainMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (arg0.getClickCount() == 2)
				playSelected();
		}
	}

	class TableRowSortedListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			rowPlaying = ((TableSorter) tabMain.getModel()).viewIndex(rowPlaying);
			rowSelected = arg0.getModifiers();
			tabMain.getSelectionModel().setSelectionInterval(rowSelected, rowSelected);
			// force selection to be at top of screen
			tabMain.scrollRectToVisible(tabMain.getCellRect(Math.min(rowSelected + 100, tabMain.getRowCount() - 1), 0, true));
			tabMain.scrollRectToVisible(tabMain.getCellRect(rowSelected, 0, true));
		}
	}

	private void playSelected() {
		play(tabMain.getSelectedRow());
	}

	private void playNext() {
		boolean paused = player.isPaused();
		play(rowPlaying + 1);
		if (paused) {
			try {
				Thread.sleep(THREAD_SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			TogglePause();
		}
	}

	private void playPrev() {
		// TODO: restart current if not near beginning
		boolean paused = player.isPaused();
		play(rowPlaying - 1);
		if (paused) {
			try {
				Thread.sleep(THREAD_SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			TogglePause();
		}
	}

	private void play(int row) {
		try {
			/*
			 * String loc = (String) tabMain.getModel().getValueAt(row, 8); if
			 * (loc == null || loc.equals("")) return;
			 * 
			 * if (new File(loc).exists()) { if (player != null &&
			 * !player.isStopped()) player.stop(); player = new SoundJLayer(loc,
			 * playbackListener); player.play(); btnPlay.setIcon(new
			 * ImageIcon(this
			 * .getClass().getResource("/jk509/player/res/pause.png")));
			 * rowPlaying = row; RefreshMainList(); }
			 */
			if (row >= 0 && row <= tabMain.getModel().getRowCount() - 1) {
				// String loc = (String) tabMain.getModel().getValueAt(row, 8);
				String loc = library.get(row).getLocation();
				if (loc == null || loc.equals(""))
					return;

				if (new File(loc).exists()) {
					if (player != null && !player.isStopped() && !player.isPaused())
						try {
							Thread.sleep(THREAD_SLEEP);
							player.pause();
						} catch (NullPointerException e) {
							// track had just finished
						}
					player = new SoundJLayer(loc, playbackListener);
					player.play();
					btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
					rowPlaying = row;
					RefreshMainList();
					UpdateTrackDisplay();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void playAt(/*int row,*/ int ms) {
		//try {
		//	if (row >= 0 && row <= tabMain.getModel().getRowCount() - 1) {
			//	String loc = library.get(row).getLocation();
			//	if (loc == null || loc.equals(""))
			//		return;

			//	if (new File(loc).exists()) {
					if (player != null && !player.isStopped() && !player.isPaused())
						try {
							Thread.sleep(THREAD_SLEEP);
							player.pause();
						} catch (NullPointerException e) {
							// track had just finished
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					//player = new SoundJLayer(loc, playbackListener);
					player.play(ms);
					btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
			//		rowPlaying = row;
					//RefreshMainList();
					//UpdateTrackDisplay();
		//		}
		//	}
		//} catch (Exception e) {
		//	e.printStackTrace();
		//}

	}
	
	private void SkipTo(double s){
		// s = time in track to skip to
		//boolean paused = player.isPaused();
		
		//play(rowPlaying);
		
		try {
			if (rowPlaying >= 0 && rowPlaying <= tabMain.getModel().getRowCount() - 1) {
				// String loc = (String) tabMain.getModel().getValueAt(rowPlaying, 8);
				String loc = library.get(rowPlaying).getLocation();
				if (loc == null || loc.equals(""))
					return;

				if (new File(loc).exists()) {
					if (player != null && !player.isStopped() && !player.isPaused())
						try {
							Thread.sleep(THREAD_SLEEP);
							player.pause();
						} catch (NullPointerException e) {
							// track had just finished
						}
					player = new SoundJLayer(loc, playbackListener);
					player.play();
					btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(THREAD_SLEEP);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// This is the line that actually does the jump - all the above is just to make sure the track has played a little bit so that a header frame has been read (to establish frames/ms)
		playAt(/*tabMain.getSelectedRow(),*/ (int) (s*1000));
		
		/*if (paused) {
			try {
				Thread.sleep(THREAD_SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			TogglePause();
		}*/
	}

	private void UpdateTrackDisplay() {
		try {
			if (rowPlaying >= 0 && rowPlaying <= tabMain.getModel().getRowCount() - 1) {
				String name = library.get(rowPlaying).getName();
				String album = library.get(rowPlaying).getAlbum();
				String artist = library.get(rowPlaying).getArtist();
				int length = library.get(rowPlaying).getLengthS();
				String len = library.get(rowPlaying).getLength();
				lblTrack.setText(name);
				lblArtistAlbum.setText(artist + " - " + album);
				lbl_endtime.setText(len);
				slider.setMaximum(Math.min(length, MAX_SLIDER_RANGE));
				slider.setValue(0);
				lbl_time.setText("0:00");
				seconds = 0;
				timing_offset = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class PlaybackListener extends JLayerPlayerPausable.PlaybackAdapter {
		// PlaybackListener members
		@Override
		public void playbackStarted(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
			// System.err.println("PlaybackStarted()");
		}

		@Override
		public void playbackPaused(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
			// System.err.println("PlaybackPaused()");
			timing_offset += (int) milliseconds;
			milliseconds = 0;
			seconds = 0;
		}

		@Override
		public void playbackFinished(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
			// System.err.println("PlaybackStopped()");
			playNext();
		}
		
		@Override
		public void frameDecoded(JLayerPlayerPausable.DecodeEvent event) {
			milliseconds = event.position;
			//if(s > seconds){
			if(milliseconds > (seconds+1) * 1000){
				seconds = (int) Math.floor( milliseconds / 1000.0);
				if(!mouseIsDown){
					lbl_time.setText(Song.SecondsToString(seconds + (int)(timing_offset / 1000.0)));
					slider.setValue((int) ( (double) slider.getMaximum() * ((double) seconds + (timing_offset / 1000.0)) / (double) library.getTracks().get(rowPlaying).getLengthS()));
				}
			}else{
				System.out.println(milliseconds);
				System.out.println(seconds * 1000);
			}
		}
	}
	
	private void RefreshMainList() {
		tabMain.invalidate();
		tabMain.repaint();
	}

	private class ButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			TogglePause();
		}
	}

	private class TxtSearchFocusListener extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent arg0) {
			txtSearch.setText("");
			txtSearch.setForeground(Color.black);
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			txtSearch.setText(" Search");
			txtSearch.setForeground(Color.GRAY);
		}
	}

	private class SliderChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent arg0) {
			slider.invalidate();
			slider.repaint();
		}
	}

	private class BtnNewButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			playPrev();
		}
	}

	private class BtnNewButton_1ActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			playNext();
		}
	}

	private class BtnRepeatActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			ToggleRepeat();

		}
	}

	private class BtnShuffleActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ToggleShuffle();

		}
	}

	private void ToggleRepeat() {
		if (repeat) {
			btnRepeat.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/repeat.png")));
		} else {
			btnRepeat.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/repeat_all.png")));
		}
		repeat = !repeat;
	}

	private void ToggleShuffle() {
		if (shuffle) {
			btnShuffle.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/shuffle.png")));
		} else {
			btnShuffle.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/shuffle_on.png")));
		}
		shuffle = !shuffle;
	}

	private void TogglePause() {
		if (player != null) {
			if (player.isPaused() && !player.isStopped()) {
				player.play();
				btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
			} else if (!player.isStopped()) {
				player.pause();
				btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/play.png")));
			}
			RefreshMainList();
		}
	}

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			int row = tabMain.getSelectedRow();

			switch (e.getKeyCode()) {
			case KeyEvent.VK_DOWN:
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					tabMain.getSelectionModel().setSelectionInterval(row++, row++);
					tabMain.scrollRectToVisible(tabMain.getCellRect(row + 1, 0, true));
				}
				break;
			case KeyEvent.VK_UP:
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					tabMain.getSelectionModel().setSelectionInterval(row--, row--);
					tabMain.scrollRectToVisible(tabMain.getCellRect(row - 1, 0, true));
				}
				break;
			case KeyEvent.VK_LEFT:
				if (e.getID() == KeyEvent.KEY_RELEASED) {
					playPrev();
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (e.getID() == KeyEvent.KEY_RELEASED) {
					playNext();
				}
				break;
			case KeyEvent.VK_ENTER:
				if (e.getID() == KeyEvent.KEY_RELEASED)
					playSelected();
				break;
			case KeyEvent.VK_SPACE:
				if (e.getID() == KeyEvent.KEY_RELEASED)
					TogglePause();
				break;
			}

			/*
			 * if (e.getID() == KeyEvent.KEY_PRESSED) {
			 * System.out.println("tester"); } else if (e.getID() ==
			 * KeyEvent.KEY_RELEASED) { System.out.println("2test2"); } else if
			 * (e.getID() == KeyEvent.KEY_TYPED) { System.out.println("3test3");
			 * }
			 */

			return false;
		}
	}

	private class CustomSlider extends BasicSliderUI {

		Image knobImage;

		public CustomSlider(JSlider aSlider) {
			super(aSlider);
			try {
				this.knobImage = ImageIO.read(this.getClass().getResource("/jk509/player/res/knob.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void paintThumb(Graphics g) {
			g.drawImage(this.knobImage, thumbRect.x + 3, thumbRect.y + 3, 6, 13, null);
		}

		/*
		 * @Override protected void scrollDueToClickInTrack(int direction) { //
		 * this is the default behaviour, let's comment that out
		 * //scrollByBlock(direction);
		 * 
		 * int value = slider.getValue(); value =
		 * this.valueForXPosition(slider.getMousePosition().x);
		 * slider.setValue(value); }
		 */

	};

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		start = e.getPoint();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if (frmMusicPlayer.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
			Point p = e.getLocationOnScreen();
			Component c = e.getComponent();
			c.setLocation((int) (p.getX() - start.getX()), (int) (p.getY() - start.getY()));
			c.repaint();
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

}
