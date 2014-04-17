package jk509.player;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import jk509.player.clustering.AbstractCluster;
import jk509.player.clustering.LeafCluster;
import jk509.player.clustering.SongCluster;
import jk509.player.core.FileScanner;
import jk509.player.core.ItunesParser;
import jk509.player.core.JLayerPlayerPausable;
import jk509.player.core.Library;
import jk509.player.core.LibraryParser;
import jk509.player.core.Playlist;
import jk509.player.core.Song;
import jk509.player.core.SongQueueElement;
import jk509.player.core.SoundJLayer;
import jk509.player.core.TableSorter;
import jk509.player.core.TableSorter.Directive;
import jk509.player.core.TrackTime;
import jk509.player.gui.ParseDiskDialog;
import jk509.player.gui.ParseItunesDialog;
import jk509.player.gui.SmartPlaylistDialog;
import jk509.player.gui.SmartPlaylistDialog.SmartPlaylistResult;
import jk509.player.gui.SwingDragImages;

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
					// window.SetVolume(1.0f);
					new SwingDragImages.GlobalImageProvider();
					window.frmMusicPlayer.setLocationRelativeTo(null);
					window.frmMusicPlayer.setExtendedState(window.frmMusicPlayer.getExtendedState() | JFrame.MAXIMIZED_BOTH);
					window.frmMusicPlayer.setVisible(true);
					// window.startup();
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
	private JPanel pnlPlaylistCtrls;
	private JButton btnAdd;
	private JButton btnDelete;
	private JButton btnPlay;
	private boolean DontRefreshMainScreen = false;
	private boolean stopped = true; // whether anything is playing/paused
	private boolean searching = false;
	private boolean playingInSearch = false; // whether tracks in the search window are playing
	private boolean smartPlay = false;
	private int playlistSearching = -1; // in terms of playlist view
	private int playlistShuffling = -1;
	private int rowPlaying = -1;
	private int trackPlaying = -1;
	private int playlistPlaying = -1; // which playlist the row/track playing
										// ints
										// refer to
	private TableSorter playlistPlayingSorter; // TableSorter for the currently
												// playing playlist : TODO needs
												// to keep updated with current,
												// or ignore if
												// playlistPlaying==selectedPlaylist
	private TableSorter currentTableSorter; // TableSorter for the current view
											// (because the constructor setTable
											// casts this to a TableModel and we
											// lose it otherwise)
	private int rowSelectionRoot; // changes according to sort
	// private int playlistSelected = 0;
	public static int FIXED_PLAYLIST_ELEMENTS = 1; // number of system-set
													// playlists (ie. tracks,
													// artists, albums)
	public static int UPDATE_PLAY_COUNT_WINDOW = 20; // no. seconds off end of song within which a skip will still cause the play count to be incremented
	private Point start;
	private PlaybackListener playbackListener = new PlaybackListener();
	private int THREAD_SLEEP = 10;
	private int MAX_SLIDER_RANGE = 300;
	private boolean repeat = false;
	private boolean repeatone = false;
	private boolean shuffle = false;
	private boolean mouseIsDown = false;
	private boolean mouseIsDownVol = false;
	private int seconds = 0; // seconds through current track
	private float milliseconds = 0; // just like seconds but in millis
	private int timing_offset = 0; // The time through the song (in ms) which we
									// were before last restart (after pause or
									// skip)
	private boolean leftDown = false;
	private boolean rightDown = false;
	private boolean enterDown = false;
	private boolean spaceDown = false;
	private DataFlavor songFlavor;
	private DataFlavor playlistFlavor;

	// DEBUG FLAGS
	boolean HIDE_SETUP_DIALOG = false;
	private JButton btnBack;
	private JButton btnFwd;
	private JLabel lbl_time;
	private JLabel lbl_endtime;
	private JLabel lblTrack;
	private JLabel lblArtistAlbum;
	private JPanel pnlTrackTime;
	private JButton btnRepeat;
	private JButton btnShuffle;
	private JPanel pnlPlayPause;
	private JPanel pnlSearch;
	private JPanel pnlNames;
	private JPanel pnlTop;
	private JPanel pnlBtm;
	private JPanel pnlContain;
	private JPanel spacerL;
	private JPanel spacerR;
	private JPanel mainPanel;
	private JSlider sliderVol;
	private JPopupMenu mnuAddPlaylist;
	private JMenuItem mntmNewPlaylist;
	private JMenuItem mntmSmartPlaylistFrom;
	private JMenuItem mntmSmartPlaylistFrom_1;
	private JButton btnCancelSearch;
	private JPanel pnlSearchTxt;
	private JMenuItem mntmResetFactoryDefaults;
	private JMenuItem mntmNowPlaying;
	private JPopupMenu mnuRCTrack;
	private JMenuItem mntmRCplay;
	private JMenuItem mntmaddQ;
	private JMenuItem mntmPlaynext;
	private JMenuItem mntmDel;
	private JPopupMenu mnuRCPlaylist;
	private JMenuItem mntmSwitch;
	private AbstractButton mntmStartTop;
	private JMenuItem mntmDelete;
	private JMenuItem mntmNewPlaylist2;
	private JMenuItem mntmSmart2;
	private JMenuItem mntmSmart;
	private JMenuItem mntmnewpl;
	private JMenu addToPlaylist;
	private JMenuItem mntmAddAudioFiles;
	private JButton btnSmart;
	private JPanel pnlSmart;
	private JButton btnSmartBar;
	private JLabel lblSmartMode;
	private JLabel lblRandomness;
	private JLabel lblUpNext;
	private JSlider sliderRand;
	private JScrollPane scrollPane;
	private JList listUpNext;
	private JButton btnClear;
	private JLabel lblBar1;
	private JLabel lblBar2;
	private JMenuItem mntmToggleSmartBar;
	private JMenuItem mntmAddFiles;
	private JMenuItem mntmResetSongClusters;
	private JMenuItem mntmClearSongFeatures;
	private JMenuItem mntmSaveClusters;

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
		frmMusicPlayer.addWindowListener(new FrmMusicPlayerWindowListener());
		frmMusicPlayer.addKeyListener(new FrmMusicPlayerKeyListener());
		// KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new
		// MyDispatcher());
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
				if (event instanceof MouseEvent) {
					MouseEvent evt = (MouseEvent) event;
					if (evt.getID() == MouseEvent.MOUSE_PRESSED && !evt.getSource().equals(txtSearch)) {
						txtSearch.setFocusable(false);
						frmMusicPlayer.requestFocus();
					}
				}
			}
		}, AWTEvent.MOUSE_EVENT_MASK);
		frmMusicPlayer.setMinimumSize(new Dimension(900, 600));
		frmMusicPlayer.setTitle("Music Factory");
		// frmMusicPlayer.setIconImage(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon.png")));
		List<Image> iconArray = new ArrayList<Image>();
		iconArray.add(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon-16.png")));
		iconArray.add(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon-32.png")));
		frmMusicPlayer.setIconImages(iconArray);
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
		pnlControls.setPreferredSize(new Dimension(10, 70));
		pnlControls.setMinimumSize(new Dimension(10, 70));
		frmMusicPlayer.getContentPane().add(pnlControls, BorderLayout.NORTH);
		pnlControls.setLayout(new BorderLayout(0, 0));

		pnlPlayPause = new JPanel();
		pnlPlayPause.setOpaque(false);
		pnlControls.add(pnlPlayPause, BorderLayout.WEST);
		GridBagLayout gbl_pnlPlayPause = new GridBagLayout();
		// gbl_pnlPlayPause.columnWidths = new int[]{57, 27, 45, 47, 0};
		// gbl_pnlPlayPause.rowHeights = new int[]{27, 0};
		gbl_pnlPlayPause.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_pnlPlayPause.rowWeights = new double[] { 0.0, 0.0 };
		pnlPlayPause.setLayout(gbl_pnlPlayPause);

		btnBack = new JButton();
		GridBagConstraints gbc_btnBack = new GridBagConstraints();
		gbc_btnBack.insets = new Insets(0, 20, 0, 5);
		gbc_btnBack.gridx = 0;
		gbc_btnBack.gridy = 1;
		pnlPlayPause.add(btnBack, gbc_btnBack);
		btnBack.addActionListener(new BtnNewButtonActionListener());
		btnBack.setFocusable(false);
		btnBack.setOpaque(false);
		btnBack.setContentAreaFilled(false);
		btnBack.setBorderPainted(false);
		btnBack.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/back_grey.png")));
		// btnBack.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/back_down.png")));

		btnPlay = new JButton();
		GridBagConstraints gbc_btnPlay = new GridBagConstraints();
		gbc_btnPlay.insets = new Insets(0, 0, 0, 5);
		gbc_btnPlay.gridx = 1;
		gbc_btnPlay.gridy = 1;
		pnlPlayPause.add(btnPlay, gbc_btnPlay);
		btnPlay.setFocusable(false);
		btnPlay.setOpaque(false);
		btnPlay.setContentAreaFilled(false);
		btnPlay.setBorderPainted(false);
		btnPlay.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/play.png")));
		btnPlay.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/play_down.png")));
		btnPlay.addActionListener(new ButtonActionListener());
		btnPlay.setMargin(new Insets(0, 0, 0, 0));
		btnPlay.setFocusPainted(false);

		btnFwd = new JButton();
		btnFwd.setPreferredSize(new Dimension(45, 20));
		btnFwd.setMaximumSize(new Dimension(25, 9));
		btnFwd.setMinimumSize(new Dimension(25, 9));
		GridBagConstraints gbc_btnFwd = new GridBagConstraints();
		gbc_btnFwd.insets = new Insets(0, 0, 0, 5);
		gbc_btnFwd.gridx = 2;
		gbc_btnFwd.gridy = 1;
		pnlPlayPause.add(btnFwd, gbc_btnFwd);
		btnFwd.addActionListener(new BtnNewButton_1ActionListener());
		btnFwd.setFocusable(false);
		btnFwd.setOpaque(false);
		btnFwd.setContentAreaFilled(false);
		btnFwd.setBorderPainted(false);
		btnFwd.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/fwd_grey.png")));
		// btnFwd.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/fwd_down.png")));

		sliderVol = new JSlider();
		sliderVol.addMouseWheelListener(new SliderVolMouseWheelListener());
		sliderVol.addChangeListener(new SliderVolChangeListener());
		sliderVol.setMaximumSize(new Dimension(100, 23));
		sliderVol.setMinimumSize(new Dimension(100, 23));
		sliderVol.setPreferredSize(new Dimension(100, 23));
		sliderVol.setValue(100);
		sliderVol.setOpaque(false);
		sliderVol.setFocusable(false);
		sliderVol.setExtent(1);
		sliderVol.setDoubleBuffered(true);
		final CustomVolumeSlider uiVol = new CustomVolumeSlider(sliderVol);
		sliderVol.setUI(uiVol);
		MouseListener[] listeners2 = sliderVol.getMouseListeners();
		MouseMotionListener[] mmls2 = sliderVol.getMouseMotionListeners();
		for (MouseListener l : listeners2)
			sliderVol.removeMouseListener(l); // remove UI-installed
												// TrackListener
		for (MouseMotionListener l : mmls2)
			sliderVol.removeMouseMotionListener(l); // remove UI-installed
													// TrackListener
		BasicSliderUI.TrackListener tl2 = uiVol.new TrackListener() {
			// this is where we jump to absolute value of click
			@Override
			public void mousePressed(MouseEvent e) {
				mouseIsDownVol = true;
				Point p = e.getPoint();
				int value = uiVol.valueForXPosition(p.x);
				sliderVol.setValue(value);
				sliderVol.repaint();
				SetVolume((float) value / 100f);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseIsDownVol = false;
				sliderVol.repaint();
			}

			// disable check that will invoke scrollDueToClickInTrack
			@Override
			public boolean shouldScroll(int dir) {
				return false;
			}
		};
		BasicSliderUI.TrackListener mml2 = uiVol.new TrackListener() {
			// this is where we jump to absolute value of click
			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = e.getPoint();
				int value = uiVol.valueForXPosition(p.x);
				sliderVol.setValue(value);
				SetVolume((float) value / 100f);
			}

			// disable check that will invoke scrollDueToClickInTrack
			@Override
			public boolean shouldScroll(int dir) {
				return false;
			}
		};
		sliderVol.addMouseListener(tl2);
		sliderVol.addMouseMotionListener(mml2);
		GridBagConstraints gbc_sliderVol = new GridBagConstraints();
		gbc_sliderVol.insets = new Insets(0, 0, 0, 20);
		gbc_sliderVol.gridx = 4;
		gbc_sliderVol.gridy = 1;
		pnlPlayPause.add(sliderVol, gbc_sliderVol);

		pnlTrackInfo = new JPanel();
		pnlTrackInfo.setMaximumSize(new Dimension(700, 32767));
		pnlTrackInfo.setOpaque(false);
		pnlControls.add(pnlTrackInfo, BorderLayout.CENTER);
		pnlTrackInfo.setLayout(new BorderLayout(0, 0));

		pnlDetails = new JPanel();
		pnlDetails.setPreferredSize(new Dimension(100, 30));
		pnlDetails.setOpaque(false);
		pnlTrackInfo.add(pnlDetails, BorderLayout.CENTER);
		pnlDetails.setMinimumSize(new Dimension(100, 30));
		pnlDetails.setLayout(new BorderLayout(0, 0));

		pnlNames = new JPanel();
		pnlNames.setPreferredSize(new Dimension(100, 20));
		pnlNames.setMinimumSize(new Dimension(100, 20));
		pnlNames.setOpaque(false);
		pnlDetails.add(pnlNames, BorderLayout.CENTER);
		pnlNames.setLayout(new BorderLayout(0, 0));

		pnlTop = new JPanel();
		pnlTop.setMinimumSize(new Dimension(10, 18));
		pnlTop.setPreferredSize(new Dimension(10, 18));
		pnlTop.setOpaque(false);
		pnlNames.add(pnlTop, BorderLayout.CENTER);
		pnlTop.setLayout(new BorderLayout(0, 0));

		lblTrack = new JLabel("");
		lblTrack.setVerticalAlignment(SwingConstants.BOTTOM);
		lblTrack.setForeground(Color.DARK_GRAY);
		lblTrack.setHorizontalAlignment(SwingConstants.CENTER);
		lblTrack.setFont(new Font("Segoe UI", Font.BOLD, 13));
		pnlTop.add(lblTrack);

		pnlBtm = new JPanel();
		pnlBtm.setPreferredSize(new Dimension(10, 18));
		pnlBtm.setOpaque(false);
		pnlNames.add(pnlBtm, BorderLayout.SOUTH);
		pnlBtm.setLayout(new BorderLayout(0, 0));

		lblArtistAlbum = new JLabel("");
		lblArtistAlbum.setVerticalTextPosition(SwingConstants.BOTTOM);
		lblArtistAlbum.setHorizontalAlignment(SwingConstants.CENTER);
		lblArtistAlbum.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
		pnlBtm.add(lblArtistAlbum);

		pnlContain = new JPanel();
		pnlContain.setOpaque(false);
		pnlDetails.add(pnlContain, BorderLayout.SOUTH);
		pnlContain.setLayout(new BoxLayout(pnlContain, BoxLayout.Y_AXIS));

		pnlTrackTime = new JPanel();
		pnlContain.add(pnlTrackTime);
		pnlTrackTime.setOpaque(false);
		pnlTrackTime.setLayout(new BoxLayout(pnlTrackTime, BoxLayout.X_AXIS));

		btnRepeat = new JButton();
		btnRepeat.setToolTipText("Repeat");
		btnRepeat.setVisible(false);
		btnRepeat.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlTrackTime.add(btnRepeat);
		btnRepeat.addActionListener(new BtnRepeatActionListener());
		btnRepeat.setOpaque(false);
		btnRepeat.setMargin(new Insets(0, 0, 0, 0));
		btnRepeat.setFocusable(false);
		btnRepeat.setFocusPainted(false);
		btnRepeat.setContentAreaFilled(false);
		btnRepeat.setBorderPainted(false);
		btnRepeat.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/repeat.png")));

		spacerL = new JPanel();
		spacerL.setOpaque(false);
		spacerL.setMaximumSize(new Dimension(10, 10));
		pnlTrackTime.add(spacerL);

		lbl_time = new JLabel("0:00");
		lbl_time.setVisible(false);
		lbl_time.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlTrackTime.add(lbl_time);

		slider = new JSlider();
		slider.addMouseWheelListener(new SliderMouseWheelListener());
		slider.setVisible(false);
		slider.setMinimumSize(new Dimension(150, 23));
		slider.setDoubleBuffered(true);
		slider.setMaximumSize(new Dimension(600, 23));
		slider.setPreferredSize(new Dimension(150, 23));
		pnlTrackTime.add(slider);
		slider.setValue(0);
		slider.setMaximum(240);
		slider.addChangeListener(new SliderChangeListener());
		slider.setFocusable(false);
		slider.setOpaque(false);
		final CustomSlider ui = new CustomSlider(slider);
		slider.setUI(ui);
		MouseListener[] listeners = slider.getMouseListeners();
		MouseMotionListener[] mmls = slider.getMouseMotionListeners();

		lbl_endtime = new JLabel("0:00");
		lbl_endtime.setVisible(false);
		lbl_endtime.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlTrackTime.add(lbl_endtime);

		spacerR = new JPanel();
		spacerR.setOpaque(false);
		spacerR.setMaximumSize(new Dimension(10, 10));
		pnlTrackTime.add(spacerR);

		btnShuffle = new JButton();
		btnShuffle.setToolTipText("Shuffle");
		btnShuffle.setVisible(false);
		btnShuffle.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlTrackTime.add(btnShuffle);
		btnShuffle.addActionListener(new BtnShuffleActionListener());
		btnShuffle.setOpaque(false);
		btnShuffle.setMargin(new Insets(0, 0, 0, 0));
		btnShuffle.setFocusable(false);
		btnShuffle.setFocusPainted(false);
		btnShuffle.setContentAreaFilled(false);
		btnShuffle.setBorderPainted(false);
		btnShuffle.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/shuffle.png")));

		pnlArt = new JPanel();
		pnlTrackInfo.add(pnlArt, BorderLayout.WEST);
		pnlArt.setOpaque(false);

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
				slider.repaint();
				// update time label
				lbl_time.setText(Song.SecondsToString((int) (((double) value * library.getPlaylist(playlistPlaying).get(trackPlaying).getLengthS()) / (double) slider.getMaximum())));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseIsDown = false;
				Point p = e.getPoint();
				int value = ui.valueForXPosition(p.x);
				slider.setValue(value);
				slider.repaint();
				// update time label
				double secs = ((double) value * (double) library.getPlaylist(playlistPlaying).get(trackPlaying).getLengthS()) / (double) slider.getMaximum();
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
				lbl_time.setText(Song.SecondsToString((int) (((double) value * library.getPlaylist(playlistPlaying).get(trackPlaying).getLengthS()) / (double) slider.getMaximum())));
			}

			// disable check that will invoke scrollDueToClickInTrack
			@Override
			public boolean shouldScroll(int dir) {
				return false;
			}
		};
		slider.addMouseListener(tl);
		slider.addMouseMotionListener(mml);

		pnlSearch = new JPanel();
		pnlSearch.setOpaque(false);
		pnlControls.add(pnlSearch, BorderLayout.EAST);
		GridBagLayout gbl_pnlSearch = new GridBagLayout();
		// gbl_pnlSearch.columnWidths = new int[]{18, 86, 0};
		// gbl_pnlSearch.rowHeights = new int[]{20, 0, 0};
		gbl_pnlSearch.columnWeights = new double[] { 0.0, 0.0 };
		gbl_pnlSearch.rowWeights = new double[] { 0.0, 0.0 };
		pnlSearch.setLayout(gbl_pnlSearch);

		lblSearch = new JLabel();
		lblSearch.setToolTipText("Search");
		lblSearch.addMouseListener(new LblSearchMouseListener());

		pnlSearchTxt = new JPanel();
		pnlSearchTxt.setOpaque(false);
		GridBagConstraints gbc_pnlSearchTxt = new GridBagConstraints();
		gbc_pnlSearchTxt.insets = new Insets(0, 0, 0, 20);
		gbc_pnlSearchTxt.fill = GridBagConstraints.BOTH;
		gbc_pnlSearchTxt.gridx = 1;
		gbc_pnlSearchTxt.gridy = 1;
		pnlSearch.add(pnlSearchTxt, gbc_pnlSearchTxt);
		pnlSearchTxt.setLayout(new BorderLayout(0, 0));
		txtSearch = new JTextField();
		pnlSearchTxt.add(txtSearch, BorderLayout.CENTER);
		txtSearch.addKeyListener(new TxtSearchKeyListener());
		txtSearch.setPreferredSize(new Dimension(60, 20));
		txtSearch.addMouseListener(new TxtSearchMouseListener());
		txtSearch.setFocusable(false);
		txtSearch.addFocusListener(new TxtSearchFocusListener());
		txtSearch.setForeground(Color.GRAY);
		txtSearch.setText(" Search");
		txtSearch.setColumns(15);

		btnCancelSearch = new JButton();
		btnCancelSearch.setToolTipText("Cancel search");
		btnCancelSearch.setPreferredSize(new Dimension(16, 10));
		btnCancelSearch.setMinimumSize(new Dimension(11, 10));
		btnCancelSearch.setMaximumSize(new Dimension(11, 10));
		btnCancelSearch.setMargin(new Insets(0, 2, 0, 0));
		btnCancelSearch.setBounds(new Rectangle(0, 0, 11, 10));
		pnlSearchTxt.add(btnCancelSearch, BorderLayout.EAST);
		btnCancelSearch.addMouseListener(new BtnCancelSearchMouseListener());
		btnCancelSearch.setVisible(false);
		btnCancelSearch.setFocusable(false);
		btnCancelSearch.setOpaque(false);
		btnCancelSearch.setContentAreaFilled(false);
		btnCancelSearch.setBorderPainted(false);
		btnCancelSearch.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search_cancel.png")));
		btnCancelSearch.setRolloverEnabled(true);
		btnCancelSearch.setRolloverIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search_cancel_hover.png")));
		GridBagConstraints gbc_lblSearch = new GridBagConstraints();
		gbc_lblSearch.insets = new Insets(0, 20, 0, 5);
		gbc_lblSearch.gridx = 0;
		gbc_lblSearch.gridy = 1;
		pnlSearch.add(lblSearch, gbc_lblSearch);
		lblSearch.setOpaque(false);
		lblSearch.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search.png")));

		splitPlaylists = new JSplitPane();
		frmMusicPlayer.getContentPane().add(splitPlaylists, BorderLayout.CENTER);
		splitPlaylists.setContinuousLayout(true);
		splitPlaylists.setBorder(null);

		scrlMain = new JScrollPane();
		scrlMain.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrlMain.setAutoscrolls(true);

		scrlMain.setMinimumSize(new Dimension(400, 23));
		mainPanel = new JPanel();
		mainPanel.setBackground(new Color(250, 250, 250));
		mainPanel.setLayout(new BorderLayout(0, 0));
		mainPanel.add(scrlMain);
		splitPlaylists.setRightComponent(mainPanel);
		scrlMain.getViewport().setBackground(new Color(250, 250, 250));
		scrlMain.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

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

			@Override
			protected void processMouseEvent(MouseEvent e) {
				if (e.getID() == MouseEvent.MOUSE_PRESSED && SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown() && !e.isControlDown()) {
					Point pt = e.getPoint();
					int row = rowAtPoint(pt);
					int col = columnAtPoint(pt);
					if (row >= 0 && col >= 0 && !super.isCellSelected(row, col))
						changeSelection(row, col, false, false);
				}
				super.processMouseEvent(e);
			}

		};
		tabMain.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (tabMain.getSelectedRowCount() == 1)
					rowSelectionRoot = tabMain.getSelectedRow();
			}
		});

		tabMain.setDropMode(DropMode.INSERT_ROWS);
		tabMain.setDragEnabled(true);
		tabMain.setTransferHandler(new MainTabTransferHandler());

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
		tabMain.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tabMain.getSelectionModel().setSelectionInterval(0, 0);
		scrlMain.setViewportView(tabMain);
		JScrollBar sb = scrlMain.getVerticalScrollBar();
		sb.setPreferredSize(new Dimension(25, 50));
		scrlMain.setVerticalScrollBar(sb);

		pnlSmart = new JPanel() {
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
		// pnlSmart.setVisible(false);
		mainPanel.add(pnlSmart, BorderLayout.SOUTH);
		GridBagLayout gbl_pnlSmart = new GridBagLayout();
		gbl_pnlSmart.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_pnlSmart.rowHeights = new int[] { 0 };
		gbl_pnlSmart.columnWeights = new double[] { 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0.0, 0.0, 0.0, 1 };
		gbl_pnlSmart.rowWeights = new double[] { 0.0 };
		pnlSmart.setLayout(gbl_pnlSmart);

		btnSmart = new JButton();
		GridBagConstraints gbc_btnSmart = new GridBagConstraints();
		gbc_btnSmart.gridx = 0;
		gbc_btnSmart.gridy = 0;
		pnlSmart.add(btnSmart, gbc_btnSmart);
		btnSmart.setFocusable(false);
		btnSmart.setToolTipText("Continuous smart play mode");
		btnSmart.addActionListener(new BtnSmartActionListener());
		btnSmart.setOpaque(false);
		btnSmart.setContentAreaFilled(false);
		btnSmart.setBorderPainted(false);
		btnSmart.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/brain.png")));
		btnSmart.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/brain_press.png")));

		lblSmartMode = new JLabel("Smart mode off");
		lblSmartMode.setFocusable(false);
		lblSmartMode.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblSmartMode.setForeground(Color.DARK_GRAY);
		GridBagConstraints gbc_lblSmartMode = new GridBagConstraints();
		gbc_lblSmartMode.insets = new Insets(0, 0, 3, 0);
		gbc_lblSmartMode.anchor = GridBagConstraints.WEST;
		gbc_lblSmartMode.gridx = 1;
		gbc_lblSmartMode.gridy = 0;
		pnlSmart.add(lblSmartMode, gbc_lblSmartMode);

		lblBar1 = new JLabel();
		lblBar1.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/vbar.png")));
		GridBagConstraints gbc_lblBar1 = new GridBagConstraints();
		gbc_lblBar1.insets = new Insets(0, 10, 0, 10);
		gbc_lblBar1.gridx = 3;
		gbc_lblBar1.gridy = 0;
		pnlSmart.add(lblBar1, gbc_lblBar1);

		lblRandomness = new JLabel("Randomness");
		lblRandomness.setFocusable(false);
		lblRandomness.setForeground(Color.DARK_GRAY);
		lblRandomness.setFont(new Font("Segoe UI", Font.BOLD, 14));
		GridBagConstraints gbc_lblRandomness = new GridBagConstraints();
		gbc_lblRandomness.insets = new Insets(0, 0, 3, 10);
		gbc_lblRandomness.gridx = 5;
		gbc_lblRandomness.gridy = 0;
		pnlSmart.add(lblRandomness, gbc_lblRandomness);

		sliderRand = new JSlider();
		sliderRand.setMaximumSize(new Dimension(100, 23));
		sliderRand.setMinimumSize(new Dimension(100, 23));
		sliderRand.setFocusable(false);
		sliderRand.setPreferredSize(new Dimension(100, 23));
		sliderRand.setOpaque(false);
		sliderRand.setExtent(1);
		sliderRand.setDoubleBuffered(true);
		final CustomVolumeSlider uiRand = new CustomVolumeSlider(sliderRand);
		sliderRand.setUI(uiRand);
		MouseListener[] listeners3 = sliderRand.getMouseListeners();
		MouseMotionListener[] mmls3 = sliderRand.getMouseMotionListeners();
		for (MouseListener l : listeners3)
			sliderRand.removeMouseListener(l); // remove UI-installed
												// TrackListener
		for (MouseMotionListener l : mmls3)
			sliderRand.removeMouseMotionListener(l); // remove UI-installed
														// TrackListener
		BasicSliderUI.TrackListener tl3 = uiRand.new TrackListener() {
			// this is where we jump to absolute value of click
			@Override
			public void mousePressed(MouseEvent e) {
				mouseIsDownVol = true;
				Point p = e.getPoint();
				int value = uiRand.valueForXPosition(p.x);
				sliderRand.setValue(value);
				sliderRand.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseIsDownVol = false;
				sliderRand.repaint();
			}

			// disable check that will invoke scrollDueToClickInTrack
			@Override
			public boolean shouldScroll(int dir) {
				return false;
			}
		};
		BasicSliderUI.TrackListener mml3 = uiRand.new TrackListener() {
			// this is where we jump to absolute value of click
			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = e.getPoint();
				int value = uiRand.valueForXPosition(p.x);
				sliderRand.setValue(value);
			}

			// disable check that will invoke scrollDueToClickInTrack
			@Override
			public boolean shouldScroll(int dir) {
				return false;
			}
		};
		sliderRand.addMouseListener(tl3);
		sliderRand.addMouseMotionListener(mml3);
		GridBagConstraints gbc_sliderRand = new GridBagConstraints();
		gbc_sliderRand.insets = new Insets(2, 0, 0, 0);
		gbc_sliderRand.gridx = 6;
		gbc_sliderRand.gridy = 0;
		pnlSmart.add(sliderRand, gbc_sliderRand);

		lblBar2 = new JLabel();
		lblBar2.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/vbar.png")));
		GridBagConstraints gbc_lblBar2 = new GridBagConstraints();
		gbc_lblBar2.insets = new Insets(0, 10, 0, 10);
		gbc_lblBar2.gridx = 8;
		gbc_lblBar2.gridy = 0;
		pnlSmart.add(lblBar2, gbc_lblBar2);

		lblUpNext = new JLabel("Up Next");
		lblUpNext.setFocusable(false);
		lblUpNext.setForeground(Color.DARK_GRAY);
		lblUpNext.setFont(new Font("Segoe UI", Font.BOLD, 14));
		GridBagConstraints gbc_lblUpNext = new GridBagConstraints();
		gbc_lblUpNext.insets = new Insets(0, 0, 3, 5);
		gbc_lblUpNext.gridx = 10;
		gbc_lblUpNext.gridy = 0;
		pnlSmart.add(lblUpNext, gbc_lblUpNext);

		scrollPane = new JScrollPane();
		scrollPane.setMaximumSize(new Dimension(400, 50));
		scrollPane.setMinimumSize(new Dimension(150, 50));
		scrollPane.setFocusable(false);
		scrollPane.setPreferredSize(new Dimension(400, 50));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(5, 5, 5, 0);
		gbc_scrollPane.gridx = 11;
		gbc_scrollPane.gridy = 0;
		pnlSmart.add(scrollPane, gbc_scrollPane);

		listUpNext = new JList();
		listUpNext.setFocusable(false);
		listUpNext.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listUpNext.setBackground(new Color(213, 219, 226));
		listUpNext.setForeground(Color.DARK_GRAY);
		listUpNext.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		MouseListener[] listeners4 = listUpNext.getMouseListeners();
		MouseMotionListener[] mmls4 = listUpNext.getMouseMotionListeners();
		// TrackListener
		scrollPane.setViewportView(listUpNext);

		btnClear = new JButton();
		btnClear.addActionListener(new BtnClearActionListener());
		btnClear.setToolTipText("Clear \"Up Next\"");
		btnClear.setPreferredSize(new Dimension(16, 10));
		btnClear.setMinimumSize(new Dimension(11, 10));
		btnClear.setMaximumSize(new Dimension(11, 10));
		btnClear.setMargin(new Insets(0, 2, 0, 0));
		btnClear.setBounds(new Rectangle(0, 0, 11, 10));
		btnClear.setVisible(false);
		btnClear.setFocusable(false);
		btnClear.setOpaque(false);
		btnClear.setContentAreaFilled(false);
		btnClear.setBorderPainted(false);
		btnClear.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search_cancel.png")));
		btnClear.setRolloverEnabled(true);
		btnClear.setRolloverIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search_cancel_hover.png")));

		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.insets = new Insets(0, 5, 0, 0);
		gbc_btnClear.anchor = GridBagConstraints.WEST;
		gbc_btnClear.gridx = 12;
		gbc_btnClear.gridy = 0;
		pnlSmart.add(btnClear, gbc_btnClear);

		for (MouseListener l : listeners4)
			listUpNext.removeMouseListener(l); // remove UI-installed
												// TrackListener
		for (MouseMotionListener l : mmls4)
			listUpNext.removeMouseMotionListener(l); // remove UI-installed
		// tabMain.setModel(new TableSorter(tabMain.getModel(),
		// tabMain.getTableHeader()));
		scrlPlaylists = new JScrollPane();
		scrlPlaylists.setAutoscrolls(true);
		scrlPlaylists.setBorder(new LineBorder(Color.LIGHT_GRAY));
		scrlPlaylists.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrlPlaylists.setMaximumSize(new Dimension(200, 23));
		scrlPlaylists.setPreferredSize(new Dimension(180, 23));
		scrlPlaylists.setMinimumSize(new Dimension(100, 23));
		scrlPlaylists.getVerticalScrollBar().setUnitIncrement(8);

		listPlaylists = new JList(); // TODO: change to Playlist and use
		listPlaylists.addMouseListener(new ListPlaylistsMouseListener());
		listPlaylists.addListSelectionListener(new ListPlaylistsListSelectionListener());
		listPlaylists.setDropMode(DropMode.ON);
		listPlaylists.setDragEnabled(true);
		listPlaylists.setTransferHandler(new PlaylistTransferHandler());
		listPlaylists.setFixedCellHeight(25);
		listPlaylists.setFocusable(false);
		listPlaylists.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		listPlaylists.setBackground(new Color(213, 219, 226));
		listPlaylists.setSelectionBackground(new Color(139, 167, 201));
		listPlaylists.setSelectionForeground(new Color(250, 250, 250));
		listPlaylists.setVisibleRowCount(20);
		listPlaylists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPlaylists.setCellRenderer(new PlaylistRenderer());
		// RefreshPlaylists();
		listPlaylists.setSelectedIndex(0);
		// scrlPlaylists.setViewportView(listPlaylists);
		JPanel pnlT = new JPanel();
		pnlT.setBackground(new Color(213, 219, 226));
		pnlT.setLayout(new BorderLayout(0, 0));
		pnlT.add(listPlaylists, BorderLayout.NORTH);
		scrlPlaylists.setViewportView(pnlT);

		pnlPlaylistCtrls = new JPanel();
		pnlPlaylistCtrls.setBorder(new LineBorder(Color.LIGHT_GRAY));
		pnlPlaylistCtrls.setBackground(new Color(213, 219, 226));

		pnlListControls = new JPanel();
		pnlListControls.setMaximumSize(new Dimension(500, 32767));
		splitPlaylists.setLeftComponent(pnlListControls);
		pnlListControls.setLayout(new BorderLayout(0, 0));
		pnlListControls.add(scrlPlaylists);
		pnlListControls.add(pnlPlaylistCtrls, BorderLayout.SOUTH);

		// New playlist button right click menu
		mnuAddPlaylist = new JPopupMenu();

		mntmSmartPlaylistFrom = new JMenuItem("Smart playlist from selection");
		mntmSmartPlaylistFrom.addActionListener(new NewPlaylistListener(2));
		mnuAddPlaylist.add(mntmSmartPlaylistFrom);

		mntmSmartPlaylistFrom_1 = new JMenuItem("Smart playlist from scratch");
		mntmSmartPlaylistFrom_1.addActionListener(new NewPlaylistListener(1));
		mnuAddPlaylist.add(mntmSmartPlaylistFrom_1);

		mnuAddPlaylist.addSeparator();

		mntmNewPlaylist = new JMenuItem("New playlist");
		mntmNewPlaylist.addActionListener(new NewPlaylistListener(0));
		mnuAddPlaylist.add(mntmNewPlaylist);

		// Playlist list right click menu
		mnuRCPlaylist = new JPopupMenu();

		mntmStartTop = new JMenuItem("Start playing from top");
		mntmStartTop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tabMain.getRowCount() > 0) {
					tabMain.getSelectionModel().setSelectionInterval(0, 0);
					playSelected();
				}
			}
		});
		mnuRCPlaylist.add(mntmStartTop);

		mnuRCPlaylist.addSeparator();

		mntmSwitch = new JMenuItem("Rename");
		mntmSwitch.addActionListener(new RenamePlaylistListener());
		mnuRCPlaylist.add(mntmSwitch);

		mntmDelete = new JMenuItem("Delete");
		mntmDelete.addActionListener(new BtnDeleteActionListener());
		mnuRCPlaylist.add(mntmDelete);

		mnuRCPlaylist.addSeparator();

		mntmNewPlaylist2 = new JMenuItem("New playlist");
		mntmNewPlaylist2.addActionListener(new NewPlaylistListener(0));
		mnuRCPlaylist.add(mntmNewPlaylist2);

		mntmSmart2 = new JMenuItem("New smart playlist");
		mntmSmart2.addActionListener(new NewPlaylistListener(1));
		mnuRCPlaylist.add(mntmSmart2);

		// Track list right click menu
		mnuRCTrack = new JPopupMenu();

		mntmRCplay = new JMenuItem("Play");
		mntmRCplay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				playSelected();
			}
		});
		mnuRCTrack.add(mntmRCplay);

		mntmPlaynext = new JMenuItem("Play next");
		mntmPlaynext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				playSelectedNext();
			}
		});
		mnuRCTrack.add(mntmPlaynext);
		mntmaddQ = new JMenuItem("Add to \"Up Next\" queue");
		mntmaddQ.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addSelectedToQueue();
			}
		});
		mnuRCTrack.add(mntmaddQ);

		mnuRCTrack.addSeparator();

		mntmDel = new JMenuItem("Delete");
		mntmDel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DeleteSelected();
			}
		});
		mnuRCTrack.add(mntmDel);

		mnuRCTrack.addSeparator();

		mntmnewpl = new JMenuItem("New playlist from selection");
		mntmnewpl.addActionListener(new NewPlaylistListener(3));
		mnuRCTrack.add(mntmnewpl);

		mntmSmart = new JMenuItem("Smart playlist from selection");
		mntmSmart.addActionListener(new NewPlaylistListener(2));
		mnuRCTrack.add(mntmSmart);

		addToPlaylist = new JMenu("Add to playlist");
		mnuRCTrack.add(addToPlaylist);

		GridBagLayout gbl_pnlPlaylistCtrls = new GridBagLayout();
		// gbl_pnlPlaylistCtrls.columnWidths = new int[]{55, 55, 0};
		// gbl_pnlPlaylistCtrls.rowHeights = new int[]{31, 0};
		gbl_pnlPlaylistCtrls.columnWeights = new double[] { 0.0, 0.0, 0.0 };
		gbl_pnlPlaylistCtrls.rowWeights = new double[] { 0.0 };
		pnlPlaylistCtrls.setLayout(gbl_pnlPlaylistCtrls);

		btnDelete = new JButton();
		btnDelete.setToolTipText("Remove playlist");
		btnDelete.addActionListener(new BtnDeleteActionListener());

		btnAdd = new JButton();
		btnAdd.setToolTipText("Add playlist");
		btnAdd.addActionListener(new BtnAddActionListener());
		btnAdd.addMouseListener(new BtnAddMouseListener());

		btnSmartBar = new JButton();
		btnSmartBar.addActionListener(new ButtonActionListener_1());
		GridBagConstraints gbc_btnSmartBar = new GridBagConstraints();
		gbc_btnSmartBar.anchor = GridBagConstraints.WEST;
		gbc_btnSmartBar.gridx = 2;
		gbc_btnSmartBar.gridy = 0;
		pnlPlaylistCtrls.add(btnSmartBar, gbc_btnSmartBar);
		btnSmartBar.setFocusable(false);
		btnSmartBar.setToolTipText("Continuous smart play mode");
		btnSmartBar.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/SmartBar.png")));
		btnSmartBar.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/SmartBar_down.png")));
		btnSmartBar.setOpaque(false);
		btnSmartBar.setContentAreaFilled(false);
		btnSmartBar.setBorderPainted(false);
		// btnAdd.setComponentPopupMenu(mnuAddPlaylist);
		btnAdd.setFocusable(false);
		btnAdd.setOpaque(false);
		btnAdd.setContentAreaFilled(false);
		btnAdd.setBorderPainted(false);
		btnAdd.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/add.png")));
		btnAdd.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/add_down.png")));
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnAdd.insets = new Insets(5, 5, 0, 0);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 0;
		pnlPlaylistCtrls.add(btnAdd, gbc_btnAdd);
		btnDelete.setFocusable(false);
		btnDelete.setOpaque(false);
		btnDelete.setContentAreaFilled(false);
		btnDelete.setBorderPainted(false);
		btnDelete.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/delete.png")));
		btnDelete.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/delete_down.png")));
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.insets = new Insets(5, 5, 0, 5);
		gbc_btnDelete.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnDelete.gridx = 1;
		gbc_btnDelete.gridy = 0;
		pnlPlaylistCtrls.add(btnDelete, gbc_btnDelete);

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
		mntmExportData.setEnabled(false);
		mntmExportData.addActionListener(new BtnExportDataActionListener());

		mntmAddAudioFiles = new JMenuItem("Add folder...");
		mntmAddAudioFiles.addActionListener(new MntmAddAudioFilesActionListener());
		mnFile.add(mntmAddAudioFiles);

		mntmAddFiles = new JMenuItem("Add files...");
		mntmAddFiles.addActionListener(new MntmAddFilesActionListener());
		mnFile.add(mntmAddFiles);
		mnFile.add(mntmExportData);

		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new BtnExitActionListener());
		mnFile.add(mntmExit);

		mnView = new JMenu("View");
		menuBar.add(mnView);

		mntmNowPlaying = new JMenuItem("Now playing");
		mntmNowPlaying.addActionListener(new MntmNowPlayingActionListener());
		mnView.add(mntmNowPlaying);

		mntmToggleSmartBar = new JMenuItem("Toggle smart bar");
		mntmToggleSmartBar.addActionListener(new MntmToggleSmartBarActionListener());
		mnView.add(mntmToggleSmartBar);

		mntmVisualiser = new JMenuItem("Visualiser");
		mntmVisualiser.setEnabled(false);
		mnView.add(mntmVisualiser);

		mntmFullScreen = new JMenuItem("Full screen");
		mntmFullScreen.setEnabled(false);
		mnView.add(mntmFullScreen);

		mntmOptions = new JMenuItem("Options...");
		mntmOptions.setEnabled(false);
		mnView.add(mntmOptions);

		mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		mntmResetFactoryDefaults = new JMenuItem("Factory Reset");
		mntmResetFactoryDefaults.addActionListener(new MntmResetFactoryDefaultsActionListener());
		mnSettings.add(mntmResetFactoryDefaults);

		mntmResetSongClusters = new JMenuItem("Reset Song Clusters");
		mntmResetSongClusters.addActionListener(new MntmResetSongClustersActionListener());
		mnSettings.add(mntmResetSongClusters);
		
		mntmClearSongFeatures = new JMenuItem("Clear Song Features");
		mntmClearSongFeatures.addActionListener(new MntmClearSongFeaturesActionListener());
		mnSettings.add(mntmClearSongFeatures);
		
		mntmSaveClusters = new JMenuItem("Save Clusters");
		mntmSaveClusters.addActionListener(new MntmSaveClustersActionListener());
		mnSettings.add(mntmSaveClusters);

		mntmOptions_1 = new JMenuItem("Options...");
		mntmOptions_1.setEnabled(false);
		mnSettings.add(mntmOptions_1);

		mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		mntmInformation = new JMenuItem("Information");
		mntmInformation.setEnabled(false);
		mnHelp.add(mntmInformation);

		mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new MntmAboutActionListener());
		mnHelp.add(mntmAbout);

		try {
			songFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + jk509.player.core.Song.class.getName() + "\"");
			playlistFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=jk509.player.core.Playlist");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// TODO: remove?
	public enum Genre {
		Classical, Rock
	}

	private void startup() {
		if (HIDE_SETUP_DIALOG) {
			library = new Library();
			return;
		}

		// first-time run checks

		String homedir = System.getenv("user.home");
		if (homedir == null)
			homedir = System.getenv("USERPROFILE");
		File settings = new File(homedir + "\\Music Factory\\library.ser");

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
				try {
					sliderVol.setValue(library.getVolume());
				} catch (Exception e) {
					library.setVolume(100);
					sliderVol.setValue(100);
				}
				sliderVol.repaint();
				if (library.getVolume() < 0)
					library.setVolume(100);
				SetVolume((float) library.getVolume() / 100f);
				DisplayLibrary();
				RefreshPlaylists();
				// System.out.println("lib read");
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
		UpdateLibrary();
		DisplayLibrary();
		RefreshPlaylists();
	}

	private void UpdateLibrary() {
		String homedir = System.getenv("user.home");
		if (homedir == null)
			homedir = System.getenv("USERPROFILE");
		File settings = new File(homedir + "\\Music Factory\\library.ser");

		File theDir = new File(homedir + "\\Music Factory");

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			theDir.mkdir();
		}

		FileOutputStream fout;
		try {
			fout = new FileOutputStream(settings);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			// mustn't save library with currentplaylist non-0
			Library libOut = (Library) library.clone();
			libOut.setCurrentPlaylist(2);
			libOut.searching = false;
			libOut.playingInSearch = false;
			libOut.setQueue(null);
			oos.writeObject(libOut);
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
			LibraryParser parser = new ItunesParser();
			ParseItunesDialog worker = new ParseItunesDialog(parser);
			worker.setLocationRelativeTo(frmMusicPlayer);
			worker.setVisible(true);

			Import(parser);
		}
	}

	private class BtnImportFromDiskActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			final LibraryParser parser = new FileScanner();
			ParseDiskDialog worker = new ParseDiskDialog(parser, false);
			worker.setLocationRelativeTo(frmMusicPlayer);
			worker.setVisible(true);

			Import(parser);
		}
	}

	private void AddToLibrary(LibraryParser parser) {
		// Like Import, but doesn't replace existing tracks
		if (!parser.isValid())
			return;

		parser.run();
		library.addToPlaylist(Library.MAIN_PLAYLIST, parser.getTracks());
		// library.addToPlaylist(1, parser.getTracks());
		// library.addToPlaylist(2, parser.getTracks());

		library.setSelection(tabMain.getSelectedRows());
		library.setViewPos(((JViewport) tabMain.getParent()).getViewPosition());
		library.setSort(((TableSorter) tabMain.getModel()).getFullSortingStatus());

		DisplayLibrary();

		UpdateLibrary();

		RefreshPlaylists();
	}

	private void Import(LibraryParser parser) {

		if (!parser.isValid())
			return;

		parser.run();

		Stop();

		library.clearViews();
		library.setPlaylist(Library.MAIN_PLAYLIST, parser.getTracks());
		// library.setPlaylist(1, parser.getTracks());
		// library.setPlaylist(2, parser.getTracks());
		// library.addTracks(parser.getTracks());

		DisplayLibrary();

		// TODO could move to OnClose() for window
		UpdateLibrary();

		RefreshPlaylists();

	}

	private void DisplayLibrary() {
		Object[][] rows = new Object[library.size()][10];
		for (int i = 0; i < library.size(); ++i) {
			Song s = library.get(i);
			rows[i][0] = null;
			rows[i][1] = s.getTrackNumber();
			rows[i][2] = s.getName();
			rows[i][3] = s.getAlbum();
			rows[i][4] = s.getArtist();
			rows[i][5] = s.getGenre();
			rows[i][6] = s.getTrackTime();
			rows[i][7] = s.getPlayCount();
			/*
			 * try { rows[i][8] = (new SimpleDateFormat("dd/MM/yyyy")).format(s.getDateAdded()); } catch (NullPointerException e) { rows[i][8] = "01/01/2000"; }
			 */
			rows[i][8] = s.getDateAdded();
			rows[i][9] = s.getLocation();
		}

		// new: save col widths all the time
		int[] prevColWidths = new int[9];
		if (tabMain.getColumnCount() < 9) {
			prevColWidths = library.getColWidths();
			if (prevColWidths == null) {
				prevColWidths = new int[] { 25, 25, 300, 200, 200, 100, 80, 80, 100 };
				library.setColWidths(prevColWidths);
			}
		} else {
			for (int i = 0; i < prevColWidths.length; ++i) {
				int col = tabMain.getColumnModel().getColumn(i).getModelIndex();
				prevColWidths[col] = tabMain.getColumnModel().getColumn(i).getWidth();
				library.setColWidths(prevColWidths);
			}
		}

		tabMain.setModel(new DefaultTableModel(rows, new String[] { "", "#", "Name", "Album", "Artist", "Genre", "Time", "Plays", "Date Added", "Location" }) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { ImageIcon.class, Integer.class, String.class, String.class, String.class, String.class, TrackTime.class, Integer.class, Date.class, String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			// boolean[] columnEditables = new boolean[] { false, false, false,
			// false, false, false, false, false, false };

			public boolean isCellEditable(int row, int column) {
				// return columnEditables[column];
				return false;
			}
		});

		try {
			currentTableSorter.clearListeners();
		} catch (Exception e) {
			// e.printStackTrace();
		}

		/*
		 * if(playlistPlaying == listPlaylists.getSelectedIndex()) currentTableSorter = playlistPlayingSorter; else currentTableSorter = new TableSorter(tabMain.getModel(), tabMain.getTableHeader(), new TableRowSortedListener(), tabMain);
		 */
		// Could do that, but need to reattach listeners and redraw table header
		// /////////
		// JTableHeader h = library.getPlaylists().get(library.getCurrentPlaylist()).header;
		// if(h==null)
		boolean isPlaylist = listPlaylists.getSelectedIndex() >= 1;
		currentTableSorter = new TableSorter(tabMain.getModel(), tabMain.getTableHeader(), new TableRowSortedListener(), tabMain, isPlaylist);
		// else
		// currentTableSorter = new TableSorter(tabMain.getModel(), h, new TableRowSortedListener(), tabMain);
		// /////////
		if (playlistPlaying == listPlaylists.getSelectedIndex())
			playlistPlayingSorter = currentTableSorter;

		tabMain.setModel(currentTableSorter);

		tabMain.getSelectionModel().setSelectionInterval(0, 0);

		int miniWidth = 25;
		tabMain.getColumnModel().getColumn(0).setPreferredWidth(miniWidth);
		tabMain.getColumnModel().getColumn(0).setMinWidth(miniWidth);
		tabMain.getColumnModel().getColumn(0).setMaxWidth(miniWidth);
		tabMain.getColumnModel().getColumn(1).setPreferredWidth(miniWidth);
		tabMain.getColumnModel().getColumn(1).setMinWidth(miniWidth);
		tabMain.getColumnModel().getColumn(1).setMaxWidth(miniWidth);

		tabMain.getColumnModel().getColumn(2).setMinWidth(100);
		tabMain.getColumnModel().getColumn(2).setPreferredWidth(300);
		tabMain.getColumnModel().getColumn(3).setMinWidth(100);
		tabMain.getColumnModel().getColumn(3).setPreferredWidth(200);
		tabMain.getColumnModel().getColumn(4).setMinWidth(100);
		tabMain.getColumnModel().getColumn(4).setPreferredWidth(200);
		tabMain.getColumnModel().getColumn(5).setMinWidth(100);
		tabMain.getColumnModel().getColumn(5).setPreferredWidth(100);
		tabMain.getColumnModel().getColumn(6).setMaxWidth(100);
		tabMain.getColumnModel().getColumn(6).setMinWidth(50);
		tabMain.getColumnModel().getColumn(7).setMaxWidth(100);
		tabMain.getColumnModel().getColumn(7).setMinWidth(50);
		tabMain.getColumnModel().getColumn(6).setPreferredWidth(80);
		tabMain.getColumnModel().getColumn(7).setPreferredWidth(80);
		tabMain.getColumnModel().getColumn(8).setMinWidth(100);
		tabMain.getColumnModel().getColumn(8).setPreferredWidth(100);

		// tabMain.setAutoResizeMode(tabMain.AUTO_RESIZE_OFF);

		tabMain.removeColumn(tabMain.getColumnModel().getColumn(9));

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
				if (playlistPlaying == listPlaylists.getSelectedIndex() && row == rowPlaying && (player != null) && !player.isStopped() && !(searching && !playingInSearch)) {
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

		tabMain.setDefaultRenderer(TrackTime.class, rightRenderer);
		tabMain.setDefaultRenderer(int.class, rightRenderer);
		tabMain.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				try {
					((JLabel) cell).setText("    " + (new SimpleDateFormat("dd/MM/yyyy")).format((Date) value));
				} catch (NullPointerException e) {
					((JLabel) cell).setText("    " + "01/01/2000");
				}
				((JLabel) cell).setHorizontalAlignment(JLabel.LEFT);
				return cell;
			}
		});

		for (int i = 0; i < prevColWidths.length; ++i) {
			tabMain.getColumnModel().getColumn(i).setPreferredWidth(prevColWidths[i]);
		}

		try {
			SetView(library.getSort(), library.getSelection(), library.getViewPos());
		} catch (NullPointerException e) {
			SetView(null, null, null);
		}

	}

	private void SetView(List<Directive> sort, int[] sel, Point pos) {
		if (sort != null && sort.size() > 0)
			((TableSorter) tabMain.getModel()).setFullSortingStatus(sort);
		else {
			sort = new ArrayList<Directive>();
			if (listPlaylists.getSelectedIndex() < 1) {
				sort.add(new Directive(4, 1));
				sort.add(new Directive(3, 1));
				sort.add(new Directive(1, 1));
				((TableSorter) tabMain.getModel()).setFullSortingStatus(sort);
			}
			library.setSort(sort);
		}
		if (sel == null || pos == null || sel.length < 1 || (sel.length > 0 && sel[0] < 0)) {
			tabMain.getSelectionModel().setSelectionInterval(0, 0);
			tabMain.scrollRectToVisible(tabMain.getCellRect(0, 0, true));
		} else {

			/*
			 * tabMain.clearSelection(); for (int i = 0; i < sel.length; ++i) { tabMain.getSelectionModel().addSelectionInterval(sel[i], sel[i]); }
			 */
			SetSelectedRows(sel);
			// tabMain.scrollRectToVisible(tabMain.getCellRect(sel[0], 0, true));
			((JViewport) tabMain.getParent()).setViewPosition(pos);
		}
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

	private boolean ArrayContains(int[] ns, int n) {
		for (int i = 0; i < ns.length; ++i)
			if (ns[i] == n)
				return true;
		return false;
	}

	private int ArrayMin(int[] ns) {
		int min = ns[0];
		for (int i = 1; i < ns.length; ++i)
			if (ns[i] < min)
				min = ns[i];
		return min;
	}

	private class TabMainMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (SwingUtilities.isLeftMouseButton(arg0)) {
				if (arg0.getClickCount() == 2)
					playSelected();
			}
			if (SwingUtilities.isRightMouseButton(arg0)) {
				final int row = tabMain.rowAtPoint(arg0.getPoint());
				if (ArrayContains(tabMain.getSelectedRows(), row)) {
					// keep selection
				} else {
					// change selection to clicked row
					tabMain.getSelectionModel().setSelectionInterval(row, row);
				}
				addToPlaylist.removeAll();
				for (int i = FIXED_PLAYLIST_ELEMENTS + Library.HIDDEN_PLAYLISTS; i < library.getPlaylists().size(); i++) {
					final int playlist = i;
					JMenuItem item = new JMenuItem(library.getPlaylists().get(i).getName());
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							AddSelectionToPlaylist(playlist);
						}
					});
					addToPlaylist.add(item);
				}
				mntmaddQ.setEnabled(!stopped);
				mntmPlaynext.setEnabled(!stopped);
				mnuRCTrack.show(tabMain, arg0.getX(), arg0.getY());
			}
			if (SwingUtilities.isMiddleMouseButton(arg0)) {

			}
		}
	}

	private void AddSelectionToPlaylist(int pl) {
		for (int i = 0; i < tabMain.getSelectedRowCount(); ++i)
			library.getPlaylists().get(pl).add(library.get(ViewToModel(tabMain.getSelectedRows()[i])));
	}

	public class TableRowSortedListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// get new row - convert back to model format then into new view
			// format
			if (trackPlaying > -1 && playlistPlaying == listPlaylists.getSelectedIndex() && (!searching || playingInSearch))
				rowPlaying = playlistPlayingSorter.viewIndex(trackPlaying);
			// rowPlaying = ((TableSorter)
			// tabMain.getModel()).viewIndex(trackPlaying);
			int[] rowsSelected = ((TableSorter) arg0.getSource()).selectedRowsInView;
			if (rowsSelected.length > 0) {
				/*
				 * tabMain.clearSelection(); for (int i = 0; i < rowsSelected.length; ++i) tabMain.getSelectionModel().addSelectionInterval(rowsSelected[i], rowsSelected[i]);
				 */
				SetSelectedRows(rowsSelected);
				// force selection to be at top of screen
				int minSel = ArrayMin(rowsSelected);
				tabMain.scrollRectToVisible(tabMain.getCellRect(Math.min(minSel + 100, tabMain.getRowCount() - 1), 0, true));
				tabMain.scrollRectToVisible(tabMain.getCellRect(minSel, 0, true));
			}
			RefreshUpNext();
		}
	}

	private void playSelectedNext() {
		int[] sel = tabMain.getSelectedRows();
		if (sel.length > 0 && !library.hasQueue())
			library.createQueue(new SongQueueElement(library.getPlaylist(playlistPlaying).get(trackPlaying), playlistPlaying, (searching ? library.searchToNormalModel(trackPlaying) : trackPlaying)));
		for (int i = sel.length - 1; i >= 0; --i) {
			int row = ((TableSorter) tabMain.getModel()).modelIndex(sel[i]);
			Song track = library.getPlaylist(listPlaylists.getSelectedIndex()).get(row);
			SongQueueElement el = new SongQueueElement(track, listPlaylists.getSelectedIndex(), (searching ? library.searchToNormalModel(row) : row));
			library.getQueue().PlayNext(el);
		}
		RefreshUpNext();
	}

	private void addSelectedToQueue() {
		int[] sel = tabMain.getSelectedRows();
		if (sel.length > 0 && !library.hasQueue())
			library.createQueue(new SongQueueElement(library.getPlaylist(playlistPlaying).get(trackPlaying), playlistPlaying, (searching ? library.searchToNormalModel(trackPlaying) : trackPlaying)));
		for (int i = 0; i < sel.length; ++i) {
			int row = ((TableSorter) tabMain.getModel()).modelIndex(sel[i]);
			Song track = library.getPlaylist(listPlaylists.getSelectedIndex()).get(row);
			SongQueueElement el = new SongQueueElement(track, listPlaylists.getSelectedIndex(), (searching ? library.searchToNormalModel(row) : row));
			library.getQueue().AddToTail(el);
		}
		RefreshUpNext();
	}

	private TableSorter getPlaylistSorter(int playlist) {
		if (playlist == listPlaylists.getSelectedIndex() && !searching)
			return ((TableSorter) tabMain.getModel());
		if (playlist == playlistPlaying && !playingInSearch)
			return playlistPlayingSorter;

		TableSorter sorter;
		Object[][] rows = new Object[library.getPlaylists().get(Library.HIDDEN_PLAYLISTS + playlist).size()][10];
		for (int i = 0; i < library.getPlaylists().get(Library.HIDDEN_PLAYLISTS + playlist).size(); ++i) {
			Song s = library.getPlaylists().get(Library.HIDDEN_PLAYLISTS + playlist).get(i);
			rows[i][0] = null;
			rows[i][1] = s.getTrackNumber();
			rows[i][2] = s.getName();
			rows[i][3] = s.getAlbum();
			rows[i][4] = s.getArtist();
			rows[i][5] = s.getGenre();
			rows[i][6] = s.getTrackTime();
			rows[i][7] = s.getPlayCount();
			/*
			 * try { rows[i][8] = (new SimpleDateFormat("dd/MM/yyyy")).format(s.getDateAdded()); } catch (NullPointerException e) { rows[i][8] = "01/01/2000"; }
			 */
			rows[i][8] = s.getDateAdded();
			rows[i][9] = s.getLocation();
		}

		JTable tabHidden = new JTable();
		tabHidden.setModel(new DefaultTableModel(rows, new String[] { "", "#", "Name", "Album", "Artist", "Genre", "Time", "Plays", "Date Added", "Location" }) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { ImageIcon.class, Integer.class, String.class, String.class, String.class, String.class, TrackTime.class, Integer.class, Date.class, String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {
				// return columnEditables[column];
				return false;
			}
		});

		TableRowSortedListener nullListener = new TableRowSortedListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				return;
			}
		};
		boolean isPlaylist = playlist >= 1;
		sorter = new TableSorter(tabHidden.getModel(), tabHidden.getTableHeader(), nullListener, tabHidden, isPlaylist);

		List<Directive> sort = library.getPlaylists().get(Library.HIDDEN_PLAYLISTS + playlist).getSort();
		if (sort != null && sort.size() > 0)
			sorter.setFullSortingStatus(sort);
		else {
			sort = new ArrayList<Directive>();
			if (playlist < 1) {
				sort.add(new Directive(4, 1));
				sort.add(new Directive(3, 1));
				sort.add(new Directive(1, 1));
				sorter.setFullSortingStatus(sort);
			}
			library.getPlaylists().get(Library.HIDDEN_PLAYLISTS + playlist).setSort(sort);
		}
		return sorter;
	}

	private int GetNextInQueue() {
		int nextUp = -1;

		SongQueueElement next = library.getQueue().next();
		if (next == null) {
			// Stop();
			library.deleteQueue();
			RefreshUpNext();
			return -1;
		}

		if (library.getQueue().isValid() && playlistPlaying != next.playlist) {
			playlistPlaying = next.playlist;
			playlistPlayingSorter = getPlaylistSorter(playlistPlaying);
		}

		if (!library.getQueue().isValid()) {
			library.deleteQueue();
			if (searching)
				nextUp = ((TableSorter) tabMain.getModel()).viewIndex(library.normalToSearchModel(next.index)) + 1;
			else {
				playlistPlayingSorter = getPlaylistSorter(next.playlist);
				nextUp = playlistPlayingSorter.viewIndex(next.index) + 1;
			}
			playlistPlaying = next.playlist;
		} else {
			if (searching)
				nextUp = ((TableSorter) tabMain.getModel()).viewIndex(library.normalToSearchModel((next.index)));
			else
				nextUp = playlistPlayingSorter.viewIndex(next.index);
		}

		RefreshUpNext();
		RefreshPlaylists();

		return nextUp;
	}

	private int GetPrevInQueue() {
		int nextUp = -1;

		if (library.getQueue().position < 0) {
			library.deleteQueue();
			nextUp = rowPlaying - 1;
		} else {
			SongQueueElement next = library.getQueue().prev();
			if (next == null) {
				// Stop();
				library.deleteQueue();
				RefreshUpNext();
				return -1;
			}
			if (playlistPlaying != next.playlist) {
				playlistPlaying = next.playlist;
				playlistPlayingSorter = getPlaylistSorter(playlistPlaying);
			}
			if (searching)
				nextUp = ((TableSorter) tabMain.getModel()).viewIndex(library.normalToSearchModel((next.index)));
			else
				nextUp = playlistPlayingSorter.viewIndex(next.index);
		}

		RefreshUpNext();
		RefreshPlaylists();

		return nextUp;
	}

	private void playSelected() {
		if (tabMain.getSelectedRowCount() < 1 || tabMain.getRowCount() < 1)
			return;
		// rowPlaying = ((TableSorter)
		// tabMain.getModel()).modelIndex(tabMain.getSelectedRow());
		UpdatePlayCount();
		// This is the only method which can start playing tracks from a
		// different playlist. So save the new playlistPlaying and table model
		playlistPlaying = listPlaylists.getSelectedIndex();
		playlistPlayingSorter = (TableSorter) tabMain.getModel();

		if (shuffle && playlistPlaying != playlistShuffling) {
			library.shuffle(playlistPlaying);
			playlistShuffling = playlistPlaying;
		}

		if (searching) {
			playingInSearch = true;
			library.playingInSearch = true;
		}

		// make sure playing icon gets updated on playlist list
		listPlaylists.invalidate();
		listPlaylists.repaint();

		library.deleteQueue();
		RefreshUpNext();

		rowPlaying = tabMain.getSelectedRow();
		play(rowPlaying);
	}

	@SuppressWarnings("unused")
	private void playFirst() {
		UpdatePlayCount();
		play(0);
	}

	@SuppressWarnings("unused")
	private void playAgain() {
		UpdatePlayCount();
		play(rowPlaying);
	}

	@SuppressWarnings("unused")
	private void playRandom() {
		UpdatePlayCount();
		// int tracks = library.getPlaylist(playlistPlaying).size();
		// int rand = (int) (Math.random() * tracks);
		int nextUp = playlistPlayingSorter.viewIndex(library.shuffleIndexToModel(library.modelIndexToShuffle(playlistPlayingSorter.modelIndex(rowPlaying)) + 1));
		play(nextUp);
	}

	private void playNext() {

		if (player == null || stopped)
			return;

		if (searching && !playingInSearch) {
			Stop();
			return;
		}

		UpdatePlayCount();

		int nextUp;
		// if (stopped)
		// return;
		if (repeatone) {
			nextUp = rowPlaying;
		} else {
			if (library.hasQueue()) {
				nextUp = GetNextInQueue();
			} else if (shuffle) {
				// nextUp = (int) (Math.random() * library.getPlaylist(playlistPlaying).size());
				nextUp = playlistPlayingSorter.viewIndex(library.shuffleIndexToModel(library.modelIndexToShuffle(playlistPlayingSorter.modelIndex(rowPlaying)) + 1));
			} else if (rowPlaying == library.getPlaylist(playlistPlaying).size() - 1) {
				if (repeat)
					nextUp = 0;
				else {
					Stop();
					return;
				}
			} else {
				nextUp = rowPlaying + 1;
			}
		}

		/*
		 * if (rowPlaying == library.getPlaylists().get(playlistPlaying).size() - 1) { Stop(); return; }
		 */

		boolean paused = player.isPaused();
		play(nextUp);

		if (paused && !stopped) {
			btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/play.png")));
			btnPlay.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/play_down.png")));
			boolean initialised = false;
			int escape = 100;
			while (!initialised)
				try {
					escape--;
					Thread.sleep(THREAD_SLEEP);
					// if (player != null && !player.isPaused() &&
					// !player.isStopped()) {
					if (player == null || player.isPaused() || player.isStopped())
						initialised = true;
					player.pause();
					initialised = true;
					// }
				} catch (Exception e) {// InterruptedException e) {
					if (escape < 1)
						initialised = true;
					else
						initialised = false;
				}
		}
	}

	private void playPrev() {

		if (player == null)
			return;

		UpdatePlayCount();

		boolean paused = player.isPaused();

		// restart current if not near beginning
		if (timing_offset > 3000 || milliseconds > 3000 /* || rowPlaying == 0 */) {
			play(rowPlaying);

		} else {
			int nextUp;
			if (stopped)
				return;
			if (searching && !playingInSearch) {
				Stop();
				return;
			}
			if (repeatone) {
				nextUp = rowPlaying;
			} else {
				if (library.hasQueue()) {
					nextUp = GetPrevInQueue();
				} else if (shuffle) {
					// nextUp = (int) (Math.random() * library.getPlaylist(playlistPlaying).size());
					nextUp = playlistPlayingSorter.viewIndex(library.shuffleIndexToModel(library.modelIndexToShuffle(playlistPlayingSorter.modelIndex(rowPlaying)) - 1));
				} else if (rowPlaying == 0) {
					Stop();
					return;
				} else {
					nextUp = rowPlaying - 1;
				}
			}
			/*
			 * if (rowPlaying == 0) { Stop(); return; }
			 */
			play(nextUp);
		}

		if (paused && !stopped) {
			btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/play.png")));
			btnPlay.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/play_down.png")));
			boolean initialised = false;
			int escape = 100;
			while (!initialised)
				try {
					escape--;
					Thread.sleep(THREAD_SLEEP);
					if (player != null && !player.isPaused() && !player.isStopped()) {
						player.pause();
						initialised = true;
					}
				} catch (InterruptedException e) {
					if (escape < 1)
						initialised = true;
					else
						initialised = false;
				}
		}
	}

	private void play(int r) {
		try {
			/*
			 * String loc = (String) tabMain.getModel().getValueAt(row, 8); if (loc == null || loc.equals("")) return;
			 * 
			 * if (new File(loc).exists()) { if (player != null && !player.isStopped()) player.stop(); player = new SoundJLayer(loc, playbackListener); player.play(); btnPlay.setIcon(new ImageIcon(this .getClass().getResource("/jk509/player/res/pause.png"))); rowPlaying = row; RefreshMainList(); }
			 */

			// get row in terms of underlying model

			if (r >= 0 && r <= playlistPlayingSorter.getRowCount() - 1) {
				// int row = ((TableSorter) tabMain.getModel()).modelIndex(r);
				int row = playlistPlayingSorter.modelIndex(r);
				String loc = library.getPlaylist(playlistPlaying).get(row).getLocation(); // library.get(row).getLocation();

				if (loc != null && !loc.equals("") && new File(loc).exists()) {
					if (player != null && !player.isStopped() && !player.isPaused())
						try {
							Thread.sleep(THREAD_SLEEP);
							player.pause();
						} catch (NullPointerException e) {
							// track had just finished
						}
					try {
						player.pause();
					} catch (Exception e) {
					}
					player = null; // try to force gc?
					player = new SoundJLayer(loc, playbackListener);
					player.play();
					btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
					btnPlay.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/pause_down.png")));
					btnBack.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/back.png")));
					btnFwd.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/fwd.png")));
					btnBack.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/back_down.png")));
					btnFwd.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/fwd_down.png")));
					rowPlaying = r;
					// trackPlaying = ((TableSorter)
					// tabMain.getModel()).modelIndex(rowPlaying);
					trackPlaying = playlistPlayingSorter.modelIndex(rowPlaying);
					RefreshMainList();
					UpdateTrackDisplay();
				} else {
					Song s = library.getPlaylist(playlistPlaying).get(row);
					Stop();
					int resp = JOptionPane.showConfirmDialog(frmMusicPlayer, "File error: do you want to remove from library?", "File error", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (resp == JOptionPane.YES_OPTION) {
						for (int pl = 0; pl < library.getPlaylists().size(); pl++) {
							for (int i = 0; i < library.getPlaylists().get(pl).size(); i++) {
								if (library.getPlaylists().get(pl).get(i).equals(s)) {
									library.getPlaylists().get(pl).remove(i);
									// may have multiple instances in a playlist
									if (i < library.getPlaylists().get(pl).size() - 1)
										i--;
								}
							}
						}
						// remember: viewpos, sort, selection
						library.setSelection(tabMain.getSelectedRows());
						library.setViewPos(((JViewport) tabMain.getParent()).getViewPosition());
						library.setSort(((TableSorter) tabMain.getModel()).getFullSortingStatus());
						DisplayLibrary();
						RefreshMainList();
					}
				}
			} else
				Stop();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void playAt(/* int row, */int ms) {
		// try {
		// if (row >= 0 && row <= tabMain.getModel().getRowCount() - 1) {
		// String loc = library.get(row).getLocation();
		// if (loc == null || loc.equals(""))
		// return;

		// if (new File(loc).exists()) {
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
		// player = new SoundJLayer(loc, playbackListener);
		player.play(ms);
		btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
		btnPlay.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/pause_down.png")));
		// rowPlaying = row;
		RefreshMainList();
		// UpdateTrackDisplay();
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	private void SkipTo(double s) {
		// s = time in track to skip to
		// boolean paused = player.isPaused();

		// play(rowPlaying);

		int row = trackPlaying;

		try {
			if (row >= 0 && row <= playlistPlayingSorter.getRowCount() - 1) {
				// String loc = (String)
				// tabMain.getModel().getValueAt(rowPlaying, 8);
				String loc = library.getPlaylist(playlistPlaying).get(row).getLocation();
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
					try {
						player.pause();
					} catch (Exception e) {
					}
					player = new SoundJLayer(loc, playbackListener);
					player.play();
					btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
					btnPlay.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/pause_down.png")));
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

		// This is the line that actually does the jump - all the above is just
		// to make sure the track has played a little bit so that a header frame
		// has been read (to establish frames/ms)
		playAt(/* tabMain.getSelectedRow(), */(int) (s * 1000));

		/*
		 * if (paused) { try { Thread.sleep(THREAD_SLEEP); } catch (InterruptedException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * TogglePause(); }
		 */
	}

	private void UpdateTrackDisplay() {
		try {
			int row = trackPlaying;
			if (row >= 0 && row <= playlistPlayingSorter.getRowCount() - 1) {
				stopped = false;
				btnRepeat.setVisible(true);
				btnShuffle.setVisible(true);
				slider.setVisible(true);
				lbl_endtime.setVisible(true);
				lbl_time.setVisible(true);
				String name = library.getPlaylist(playlistPlaying).get(row).getName();
				String album = library.getPlaylist(playlistPlaying).get(row).getAlbum();
				String artist = library.getPlaylist(playlistPlaying).get(row).getArtist();
				int length = library.getPlaylist(playlistPlaying).get(row).getLengthS();
				String len = library.getPlaylist(playlistPlaying).get(row).getLength();
				lblTrack.setText(name);
				if (artist == null || artist.equals("null"))
					artist = "";
				if (album == null || album.equals("null"))
					album = "";
				lblArtistAlbum.setText(artist + "  \u2014  " + album);
				lbl_endtime.setText(len);
				slider.setMaximum(Math.min(length, MAX_SLIDER_RANGE));
				slider.setValue(0);
				lbl_time.setText("0:00");
				milliseconds = 0;
				seconds = 0;
				timing_offset = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void Search(String query) {
		// save view
		library.setSelection(tabMain.getSelectedRows());
		library.setViewPos(((JViewport) tabMain.getParent()).getViewPosition());
		library.setSort(((TableSorter) tabMain.getModel()).getFullSortingStatus());

		// perform CancelSearch
		searching = false;
		// btnCancelSearch.setVisible(false);
		// txtSearch.setSize(txtSearch.getWidth() + 30, txtSearch.getHeight());
		// txtSearch.setText(" Search");
		// txtSearch.setColumns(txtSearch.getColumns() + 2);
		library.cancelSearch(listPlaylists.getSelectedIndex());
		// DisplayLibrary();
		if (playlistPlaying == playlistSearching && playingInSearch) {
			playingInSearch = false;
			trackPlaying = library.getTrackIndex(library.getPlaylists().get(0).get(trackPlaying), playlistPlaying);
			try {
				rowPlaying = playlistPlayingSorter.viewIndex(trackPlaying);
			} catch (ArrayIndexOutOfBoundsException e) {
				rowPlaying = -1;
			}
		}
		// playlistSearching = -1;
		// if(shuffle)
		// library.shuffle(playlistPlaying);
		// /////////

		txtSearch.setFocusable(false);
		frmMusicPlayer.requestFocus();

		searching = true;
		playlistSearching = listPlaylists.getSelectedIndex();
		// TODO
		// idea: create temporary playlist without changing playlistlist
		// display. Adjust other onclicks so it can't be cancelled
		if (!btnCancelSearch.isVisible()) {
			btnCancelSearch.setVisible(true);
			// txtSearch.setSize(txtSearch.getWidth() - 30,
			// txtSearch.getHeight());
			txtSearch.setColumns(txtSearch.getColumns() - 2);
		}

		int oldTrackPlaying = -1;
		if (playlistPlaying == listPlaylists.getSelectedIndex())
			oldTrackPlaying = trackPlaying;
		int newTrackPlaying = library.search(query, listPlaylists.getSelectedIndex(), oldTrackPlaying);
		if (newTrackPlaying > -1) {
			trackPlaying = newTrackPlaying;
			playingInSearch = true;
		}
		// todo: special playlists 0 and 1 for search and shuffle? or just library.searchPlaylist and .shufflePlaylist. Will need special code in play() anyway because of needing to still display trackPlaying with playingplaylist selected...

		// playlistplaying, rowplaying, trackplaying
		DisplayLibrary();
		try {
			rowPlaying = playlistPlayingSorter.viewIndex(trackPlaying);
		} catch (Exception e) {
			rowPlaying = -1;
		}

		if (shuffle)
			library.shuffle(playlistPlaying);

	}

	private void CancelSearch() {
		searching = false;
		btnCancelSearch.setVisible(false);
		// txtSearch.setSize(txtSearch.getWidth() + 30, txtSearch.getHeight());
		txtSearch.setText(" Search");
		txtSearch.setColumns(txtSearch.getColumns() + 2);
		library.cancelSearch(listPlaylists.getSelectedIndex());
		DisplayLibrary();
		if (playlistPlaying == playlistSearching && playingInSearch) {
			playingInSearch = false;
			trackPlaying = library.getTrackIndex(library.getPlaylists().get(0).get(trackPlaying), playlistPlaying);
			try {
				rowPlaying = playlistPlayingSorter.viewIndex(trackPlaying);
			} catch (ArrayIndexOutOfBoundsException e) {
				rowPlaying = -1;
			}
		}
		playlistSearching = -1;
		if (shuffle)
			library.shuffle(playlistPlaying);
	}

	private void UpdatePlayCount() {
		try {
			if (((double) seconds + (timing_offset / 1000.0)) > (double) library.getPlaylist(playlistPlaying).get(playlistPlayingSorter.modelIndex(rowPlaying)).getLengthS() - UPDATE_PLAY_COUNT_WINDOW) {
				library.getPlaylist(playlistPlaying).get(playlistPlayingSorter.modelIndex(rowPlaying)).incrementPlayCount();
				if (playlistPlaying == listPlaylists.getSelectedIndex()) {
					int[] sel = tabMain.getSelectedRows();
					tabMain.setValueAt(library.getPlaylist(playlistPlaying).get(playlistPlayingSorter.modelIndex(rowPlaying)).getPlayCount(), rowPlaying, 7);
					RefreshMainList();
					SetSelectedRows(sel);
				}
			}
		} catch (Exception e) {
			// no problem
		}
	}

	public class PlaybackListener extends JLayerPlayerPausable.PlaybackAdapter {
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

			// Update playcount
			// if(!stopped){
			// UpdatePlayCount();
			// }
			if (playlistPlaying < 0 || rowPlaying < 0)
				return;

			if (!playbackEvent.source.getPath().equals(library.getPlaylist(playlistPlaying).get(playlistPlayingSorter.modelIndex(rowPlaying)).getLocation())) {
				// user changed song before this one finished
				return;
			} else {
				/*
				 * if (stopped) return; if (searching && !playingInSearch) { Stop(); return; } if (repeatone) { playAgain(); } else { if (shuffle) { playRandom(); } else if (rowPlaying == library.getPlaylist(playlistPlaying).size() - 1) { if (repeat) playFirst(); else { UpdatePlayCount(); Stop(); } } else { playNext(); } }
				 */
				playNext(); // which does all the above commented-out stuff
			}
		}

		@Override
		public void frameDecoded(JLayerPlayerPausable.DecodeEvent event) {
			if (!event.path.equals(library.getPlaylist(playlistPlaying).get(trackPlaying).getLocation()) && !(searching && !playingInSearch && event.path.equals(library.getPlaylist(playlistPlaying).get(trackPlaying).getLocation()))) {
				// This is not the current track: stop it
				// System.out.println("!");
				try {
					event.source.pause();
				} catch (Exception e) {
				}
				try {
					event.source.close();
				} catch (Exception e) {
				}
			} else {
				milliseconds = event.position;
				// if(s > seconds){
				if (!player.isPaused() && milliseconds > (seconds + 1) * 1000) {
					// System.out.println("***************");
					seconds = (int) Math.floor(milliseconds / 1000.0);
					if (!mouseIsDown) {
						lbl_time.setText(Song.SecondsToString(seconds + (int) (timing_offset / 1000.0)));
						slider.setValue((int) ((double) slider.getMaximum() * ((double) seconds + (timing_offset / 1000.0)) / (double) library.getPlaylist(playlistPlaying).get(trackPlaying).getLengthS()));
					}
				} // else {
					// System.out.println(milliseconds);
					// System.out.println(seconds * 1000);
				// }
			}
		}
	}

	private void Stop() {
		stopped = true;
		try {
			player.stop();
		} catch (Exception e) {
			// wasn't playing
			// e.printStackTrace();
		}
		library.deleteQueue();
		player = null;
		rowPlaying = -1;
		trackPlaying = -1;
		playlistPlaying = -1;
		playlistPlayingSorter = null;
		slider.setValue(0);
		lblTrack.setText("");
		lblArtistAlbum.setText("");
		lbl_time.setText("0:00");
		lbl_endtime.setText("0:00");
		milliseconds = 0;
		seconds = 0;
		timing_offset = 0;
		btnRepeat.setVisible(false);
		btnShuffle.setVisible(false);
		slider.setVisible(false);
		lbl_endtime.setVisible(false);
		lbl_time.setVisible(false);
		btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/play.png")));
		btnPlay.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/play_down.png")));
		btnBack.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/back_grey.png")));
		btnFwd.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/fwd_grey.png")));
		btnBack.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/back_grey.png")));
		btnFwd.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/fwd_grey.png")));
		RefreshMainList();
		RefreshUpNext();
		listPlaylists.invalidate();
		listPlaylists.repaint();
	}

	private void RefreshMainList() {
		// int[] sel = tabMain.getSelectedRows();
		tabMain.invalidate();
		tabMain.repaint();
		// SetSelectedRows(sel);
	}

	private void SetSelectedRows(int[] sel) {
		tabMain.clearSelection();
		for (int i = 0; i < sel.length; ++i)
			if (sel[i] > 0 && sel[i] < tabMain.getRowCount())
				tabMain.getSelectionModel().addSelectionInterval(sel[i], sel[i]);
	}

	private class ButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			TogglePause();
		}
	}

	private class TxtSearchFocusListener extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent arg0) {
			if (txtSearch.getText().equals(" Search"))
				txtSearch.setText("");
			else
				txtSearch.selectAll();
			txtSearch.setForeground(Color.black);
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			if (txtSearch.getText().replace(" ", "").equals(""))
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

	private class TxtSearchMouseListener extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent arg0) {
			txtSearch.setFocusable(true);
		}
	}

	private void ToggleRepeat() {
		if (repeat) {
			btnRepeat.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/repeat_one.png")));
			repeat = !repeat;
			repeatone = !repeatone;
		} else if (repeatone) {
			btnRepeat.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/repeat.png")));
			repeatone = !repeatone;
		} else {
			btnRepeat.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/repeat_all.png")));
			repeat = !repeat;
		}

	}

	private void ToggleShuffle() {
		if (shuffle) {
			btnShuffle.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/shuffle.png")));
		} else {
			btnShuffle.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/shuffle_on.png")));
			if (playlistPlaying < 0)
				library.shuffle(0); // probably shouldn't be shuffling if nothing playing...
			else
				library.shuffle(playlistPlaying);
			playlistShuffling = playlistPlaying; // what if playlistplaying < 0 above?
		}
		shuffle = !shuffle;
	}

	private void TogglePause() {
		if (player != null) {
			if (player.isPaused() && !player.isStopped()) {
				player.play();
				btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
				btnPlay.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/pause_down.png")));
			} else if (!player.isStopped()) {
				player.pause();
				btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/play.png")));
				btnPlay.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/play_down.png")));
			}
			RefreshMainList();
		} else {
			playSelected();
		}
	}

	private void DeleteSelected() {
		if (tabMain.getSelectedRowCount() < 1 || tabMain.getRowCount() < 1)
			return;
		int reply;
		if (((Playlist) listPlaylists.getSelectedValue()).getType() == Playlist.DEFAULT)
			if (tabMain.getSelectedRowCount() > 1)
				reply = JOptionPane.showConfirmDialog(frmMusicPlayer, "Are you sure you want to delete these tracks from your library?\nThey will also be removed from any playlists containing them.\nThe audio files will not be removed from your computer.", "Confirm deletion", JOptionPane.YES_NO_OPTION);
			else
				reply = JOptionPane.showConfirmDialog(frmMusicPlayer, "Are you sure you want to delete this track from your library?\nIt will also be removed from any playlists it is in.\nThe audio file will not be removed from your computer.", "Confirm deletion", JOptionPane.YES_NO_OPTION);
		else if (tabMain.getSelectedRowCount() > 1)
			reply = JOptionPane.showConfirmDialog(frmMusicPlayer, "Are you sure you want to remove these tracks from the playlist?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
		else
			reply = JOptionPane.showConfirmDialog(frmMusicPlayer, "Are you sure you want to remove this track from the playlist?", "Confirm deletion", JOptionPane.YES_NO_OPTION);

		if (reply == JOptionPane.YES_OPTION) {
			int[] selectedRows = tabMain.getSelectedRows();
			for (int i = selectedRows.length - 1; i >= 0; --i) {
				// System.out.println("before: rowplay:"+rowPlaying+" trackplay:"+trackPlaying+" rowsel:"+selectedRows[i]+" tracksel:"+((TableSorter) tabMain.getModel()).modelIndex(selectedRows[i]));
				int rowV = selectedRows[i];
				boolean last = false;
				if (rowV == tabMain.getRowCount() - 1)
					last = true;
				// int rowM = ((TableSorter) tabMain.getModel()).modelIndex(rowV);
				int rowM = ViewToModel(rowV);

				if (((Playlist) listPlaylists.getSelectedValue()).getType() == Playlist.DEFAULT)
					DeleteInPlaylists(library.get(rowM));

				if (library.hasQueue())
					if (((Playlist) listPlaylists.getSelectedValue()).getType() == Playlist.DEFAULT)
						library.getQueue().Delete(library.get(rowM));
					else
						library.getQueue().Delete(new SongQueueElement(library.get(rowM), listPlaylists.getSelectedIndex(), rowM)); // TODO: if searching, rowM as final arg is wrong? Fixed by ignoring row during delete test.

				Delete(rowM);

				// adjust rowPlaying (not trackplaying)
				if ((playlistPlaying == listPlaylists.getSelectedIndex()) && rowPlaying > rowV) {
					// rowPlaying--;
					// turns out that the tablesortlistener already updates this from trackplaying
				} else if (playlistPlaying == listPlaylists.getSelectedIndex() && rowPlaying < rowV) {
					// do nothing
				} // do nothing if rowplaying was deleted

				// adjust selection
				if (last)
					rowV = rowV - 1;
				if (rowV >= 0) {
					tabMain.getSelectionModel().setSelectionInterval(rowV, rowV);
					tabMain.scrollRectToVisible(tabMain.getCellRect(rowV, 0, true));
				}
				// System.out.println("after:  rowplay:"+rowPlaying+" trackplay:"+trackPlaying+" rowsel:"+selectedRows[i]+" tracksel:"+rowM);
			}
		}
	}

	// These are view-type conversion methods
	// 'model' here means the un-search-filtered model
	public int ViewToModel(int v) {
		int m = 0;
		if (searching) {
			m = ((TableSorter) tabMain.getModel()).modelIndex(v);
			// m = library.searchToNormalModel(m);
		} else {
			m = ((TableSorter) tabMain.getModel()).modelIndex(v);
		}
		return m;
	}

	public int ModelToView(int m) {
		int v = 0;
		if (searching) {
			v = library.normalToSearchModel(m);
			if (v < 0)
				return -1;
			v = ((TableSorter) tabMain.getModel()).viewIndex(v);
		} else {
			v = ((TableSorter) tabMain.getModel()).viewIndex(m);
		}
		return v;
	}

	// These are playing-type conversion methods
	// TODO: may not be needed, since playlistplayingsorter gets updated after search (although so does tabmain.getmodel)
	public int RowToModel(int r) {
		int m = 0;
		if (searching) {// TODO: not if(searching) but if(playlistplaying = seachingplaylist or whatever or playinginseach)
			m = playlistPlayingSorter.modelIndex(r);
			m = library.searchToNormalModel(m);
		} else {
			m = playlistPlayingSorter.modelIndex(r);
		}
		return m;
	}

	public int ModelToRow(int m) {
		int r = 0;
		if (searching) {
			r = library.normalToSearchModel(m);
			if (r < 0)
				return -1;
			r = playlistPlayingSorter.viewIndex(r);
		} else {
			r = playlistPlayingSorter.viewIndex(m);
		}
		return r;
	}

	private void Delete(int row) {
		// row is the row of the model, not the view
		if (playlistPlaying == listPlaylists.getSelectedIndex() && trackPlaying == row) {
			if (player != null && !player.isPaused() && !player.isStopped())
				TogglePause();
			Stop();
		}

		// remove song from main playlist if we're searching or shuffling the main list
		if (library.getCurrentPlaylist() == 0)
			library.getPlaylists().get(Library.HIDDEN_PLAYLISTS + playlistSearching).remove(library.searchToNormalModel(row));
		// if(library.getCurrentPlaylist() == 1)
		// library.getPlaylists().get(Library.MAIN_PLAYLIST).remove(row);

		library.remove(row);

		if (playlistPlaying == listPlaylists.getSelectedIndex() && row < trackPlaying) {
			trackPlaying--;
		}

		if (shuffle) {
			library.shuffle(playlistShuffling);
		}

		// remember: viewpos, sort, selection
		library.setSelection(tabMain.getSelectedRows());
		library.setViewPos(((JViewport) tabMain.getParent()).getViewPosition());
		library.setSort(((TableSorter) tabMain.getModel()).getFullSortingStatus());
		DisplayLibrary();
		RefreshMainList();
	}

	private void DeleteInPlaylists(Song s) {
		// scan all user/auto playlists and remove this song, based on it's
		// location (for now)
		for (int pl = FIXED_PLAYLIST_ELEMENTS + Library.HIDDEN_PLAYLISTS; pl < library.getPlaylists().size(); pl++) {
			for (int i = 0; i < library.getPlaylists().get(pl).size(); i++) {
				if (library.getPlaylists().get(pl).get(i).equals(s)) {
					library.getPlaylists().get(pl).remove(i);
					// may have multiple instances in a playlist
					if (i < library.getPlaylists().get(pl).size() - 1)
						i--;
				}
			}
		}
	}
	
	/*
	 * The machine learning methods
	 */
	private Song GetSmartSeedSong(){
		// TODO just pick most likely track/cluster overall
		return null;
	}
	private Song GetSmartTrack(Song seed){
		// TODO choose next song from given seed
		return null;
	}

	private class FrmMusicPlayerKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int row = tabMain.getSelectedRow();
			int rowNew, page_height;
			switch (e.getKeyCode()) {
			case KeyEvent.VK_DOWN:
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					sliderVol.setValue(Math.max(0, sliderVol.getValue() - 5));
					SetVolume((float) sliderVol.getValue() / 100f);
				} else if ((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
					if (tabMain.getSelectedRowCount() < 1)
						break;
					if (row == rowSelectionRoot) {
						row = tabMain.getSelectedRows()[tabMain.getSelectedRowCount() - 1];
						if (row < tabMain.getRowCount() - 1)
							rowNew = row + 1;
						else
							rowNew = row;
						tabMain.getSelectionModel().addSelectionInterval(rowNew, rowNew);
						tabMain.scrollRectToVisible(tabMain.getCellRect(rowNew, 0, true));
					} else {
						tabMain.getSelectionModel().removeSelectionInterval(row, row);
					}
				} else {
					if (tabMain.getSelectedRowCount() < 1)
						break;
					row = tabMain.getSelectedRows()[tabMain.getSelectedRowCount() - 1];
					if (row < tabMain.getRowCount() - 1)
						rowNew = row + 1;
					else
						rowNew = row;
					tabMain.getSelectionModel().setSelectionInterval(rowNew, rowNew);
					tabMain.scrollRectToVisible(tabMain.getCellRect(rowNew, 0, true));
				}
				break;
			case KeyEvent.VK_UP:
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					sliderVol.setValue(Math.min(sliderVol.getMaximum(), sliderVol.getValue() + 5));
					SetVolume((float) sliderVol.getValue() / 100f);
				} else if ((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
					if (tabMain.getSelectedRowCount() < 1)
						break;
					if (row == rowSelectionRoot && tabMain.getSelectedRowCount() > 1) {
						tabMain.getSelectionModel().removeSelectionInterval(tabMain.getSelectedRows()[tabMain.getSelectedRowCount() - 1], tabMain.getSelectedRows()[tabMain.getSelectedRowCount() - 1]);
					} else {
						if (row > 0)
							rowNew = row - 1;
						else
							rowNew = row;
						tabMain.getSelectionModel().addSelectionInterval(rowNew, rowNew);
						tabMain.scrollRectToVisible(tabMain.getCellRect(rowNew, 0, true));
					}
				} else {
					if (tabMain.getSelectedRowCount() < 1)
						break;
					if (row > 0)
						rowNew = row - 1;
					else
						rowNew = row;
					tabMain.getSelectionModel().setSelectionInterval(rowNew, rowNew);
					tabMain.scrollRectToVisible(tabMain.getCellRect(rowNew, 0, true));
				}
				break;
			case KeyEvent.VK_F:
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					txtSearch.setFocusable(true);
					txtSearch.requestFocus();
				}
				break;
			case KeyEvent.VK_A:
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					if (tabMain.getRowCount() > 0)
						tabMain.getSelectionModel().setSelectionInterval(0, tabMain.getRowCount() - 1);
				}
				break;
			case KeyEvent.VK_LEFT:
				if (!leftDown) {
					playPrev();
					leftDown = true;
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (!rightDown) {
					playNext();
					rightDown = true;
				}
				break;
			case KeyEvent.VK_ENTER:
				if (!enterDown) {
					playSelected();
					enterDown = true;
				}
				break;
			case KeyEvent.VK_SPACE:
				if (!spaceDown) {
					TogglePause();
					spaceDown = true;
				}
				break;
			case KeyEvent.VK_HOME:
				tabMain.getSelectionModel().setSelectionInterval(0, 0);
				tabMain.scrollRectToVisible(tabMain.getCellRect(0, 0, true));
				break;
			case KeyEvent.VK_END:
				tabMain.getSelectionModel().setSelectionInterval(tabMain.getRowCount() - 1, tabMain.getRowCount() - 1);
				tabMain.scrollRectToVisible(tabMain.getCellRect(tabMain.getRowCount() - 1, 0, true));
				break;
			case KeyEvent.VK_PAGE_UP:
				if (tabMain.getSelectedRowCount() < 1)
					break;
				page_height = getRowsInView() - 1;
				if (row > page_height)
					rowNew = row - page_height;
				else
					rowNew = 0;
				tabMain.getSelectionModel().setSelectionInterval(rowNew, rowNew);
				tabMain.scrollRectToVisible(tabMain.getCellRect(rowNew, 0, true));
				break;
			case KeyEvent.VK_PAGE_DOWN:
				if (tabMain.getSelectedRowCount() < 1)
					break;
				page_height = getRowsInView() - 1;
				row = tabMain.getSelectedRows()[tabMain.getSelectedRowCount() - 1];
				if (row < tabMain.getRowCount() - page_height)
					rowNew = row + page_height;
				else
					rowNew = tabMain.getRowCount() - 1;
				tabMain.getSelectionModel().setSelectionInterval(rowNew, rowNew);
				tabMain.scrollRectToVisible(tabMain.getCellRect(rowNew, 0, true));
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				leftDown = false;
				break;
			case KeyEvent.VK_RIGHT:
				rightDown = false;
				break;
			case KeyEvent.VK_ENTER:
				enterDown = false;
				break;
			case KeyEvent.VK_SPACE:
				spaceDown = false;
				break;
			case KeyEvent.VK_DELETE:
				DeleteSelected();
				break;
			case KeyEvent.VK_N:
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					btnAdd.doClick();
				}
				break;
			case KeyEvent.VK_Q:
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					btnSmartBar.doClick();
				}
				break;
			case KeyEvent.VK_M:
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					btnSmart.doClick();
				}
				break;
			case KeyEvent.VK_S:
				btnSmart.doClick();
				break;
			}
		}
	}

	private int getRowsInView() {
		Rectangle vr = tabMain.getVisibleRect();
		int first = tabMain.rowAtPoint(vr.getLocation());
		vr.translate(0, vr.height);
		int last = tabMain.rowAtPoint(vr.getLocation());
		if (last < 0) // end of list
			last = tabMain.getRowCount() - 1;
		return last - first;
	}

	private class FrmMusicPlayerWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			library.setSelection(tabMain.getSelectedRows());
			library.setViewPos(((JViewport) tabMain.getParent()).getViewPosition());
			library.setSort(((TableSorter) tabMain.getModel()).getFullSortingStatus());
			UpdateLibrary();
		}
	}

	private class SliderVolChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent arg0) {
			sliderVol.invalidate();
			sliderVol.repaint();
		}
	}

	private class BtnAddMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			mnuAddPlaylist.show(pnlPlaylistCtrls, ((JButton) e.getSource()).getX() + 10, ((JButton) e.getSource()).getY() - 76);
		}
	}

	private class BtnAddActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			mnuAddPlaylist.show(pnlPlaylistCtrls, ((JButton) e.getSource()).getX() + 10, ((JButton) e.getSource()).getY() - 76);
		}
	}

	private class ListPlaylistsListSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (!DontRefreshMainScreen) {
				library.setSelection(tabMain.getSelectedRows());
				library.setViewPos(((JViewport) tabMain.getParent()).getViewPosition());
				library.setSort(((TableSorter) tabMain.getModel()).getFullSortingStatus());
				// library.getPlaylists().get(library.getCurrentPlaylist()).header = ((TableSorter)tabMain.getModel()).getTableHeader();
				// Update song display
				int playlistSelected = listPlaylists.getSelectedIndex();
				if (playlistSelected > -1) {
					if (searching)
						CancelSearch();
					library.setCurrentPlaylist(playlistSelected + Library.HIDDEN_PLAYLISTS);
					DisplayLibrary();
					RefreshMainList();
				}
			}
		}
	}

	private class RenamePlaylistListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Playlist current = (Playlist) listPlaylists.getSelectedValue();
			if (current.getType() == Playlist.USER || current.getType() == Playlist.AUTO) {
				String oldName = current.getName();
				String name = (String) JOptionPane.showInputDialog(frmMusicPlayer, "Playlist name: ", "Rename Playlist", JOptionPane.QUESTION_MESSAGE, null, null, oldName);
				if (name != null && !name.equals("")) {
					current.setName(name);
					RefreshPlaylists();
				}
			}
		}
	}

	private class BtnDeleteActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Playlist current = (Playlist) listPlaylists.getSelectedValue();
			if (current.getType() == Playlist.USER || current.getType() == Playlist.AUTO) {
				if (JOptionPane.showConfirmDialog(frmMusicPlayer, "Are you sure you want to delete this playlist?", "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					if (playlistPlaying == listPlaylists.getSelectedIndex()) {
						if (player != null && !player.isPaused() && !player.isStopped())
							TogglePause();
						Stop();
					}
					library.getPlaylists().remove(listPlaylists.getSelectedIndex() + Library.HIDDEN_PLAYLISTS);
					int index = Math.min(listPlaylists.getSelectedIndex(), listPlaylists.getModel().getSize() - 2);
					library.setCurrentPlaylist(index + Library.HIDDEN_PLAYLISTS);
					RefreshPlaylists();
					listPlaylists.setSelectedIndex(index);
					DisplayLibrary();
				}
			}
		}
	}

	private class NewPlaylistListener implements ActionListener {
		int type; // 0=user, 1=smart, 2=smart from selected song, 3=user from selected song

		public NewPlaylistListener(int type) {
			this.type = type;
		}

		public void actionPerformed(ActionEvent e) {
			if (type == 0 || type == 3) {
				// user playlist
				String name = (String) JOptionPane.showInputDialog(frmMusicPlayer, "Playlist name: ", "New Playlist", JOptionPane.QUESTION_MESSAGE, null, null, "New playlist");

				if (name != null && !name.equals("")) {
					Playlist pl = new Playlist(name, Playlist.USER);
					library.addPlaylist(pl);
					RefreshPlaylists();
					if (type == 3) {
						for (int i = 0; i < tabMain.getSelectedRowCount(); ++i)
							pl.add(library.get(ViewToModel(tabMain.getSelectedRows()[i])));
					}
				}
			} else {
				// smart playlist
				SmartPlaylistDialog dl = new SmartPlaylistDialog(library.getPlaylists().get(Library.MAIN_PLAYLIST).size());
				dl.setLocationRelativeTo(frmMusicPlayer);
				SmartPlaylistResult res = dl.showDialog();
				String name = res.name;
				int size = res.size;

				if (name != null && !name.equals("")) {
					Playlist pl = new Playlist(name, Playlist.AUTO);
					library.addPlaylist(pl);
					RefreshPlaylists();
					Song seed = null;
					if (type == 2) {
						// smart playlist from selection
						for (int i = 0; i < tabMain.getSelectedRowCount(); ++i)
							pl.add(library.get(ViewToModel(tabMain.getSelectedRows()[i])));
						seed = library.get(ViewToModel(tabMain.getSelectedRows()[tabMain.getSelectedRowCount()-1]));
						// TODO: does 'size' mean total including the user-selected tracks?
						size = size - tabMain.getSelectedRowCount();
					}else{
						// smart playlist from all
						seed = GetSmartSeedSong();
					}
					for(int i=0; i<size; ++i){
						Song next = GetSmartTrack(seed);
						pl.add(next);
						seed = next;
					}
				}
			}
		}
	}

	private class TxtSearchKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				if (txtSearch.getText() != null && !txtSearch.getText().equals(""))
					Search(txtSearch.getText());
			}
			if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (searching)
					CancelSearch();
				txtSearch.setText(" Search");
				frmMusicPlayer.requestFocus();
				txtSearch.setFocusable(false);
			}
		}
	}

	private class LblSearchMouseListener extends MouseAdapter {
		boolean over = false;

		@Override
		public void mouseEntered(MouseEvent e) {
			over = true;
			lblSearch.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search_hover.png")));
		}

		@Override
		public void mouseExited(MouseEvent e) {
			over = false;
			lblSearch.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search.png")));
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (txtSearch.getText() != null && !txtSearch.getText().equals("") && !txtSearch.getText().equals(" Search"))
				Search(txtSearch.getText());
		}

		@Override
		public void mousePressed(MouseEvent e) {
			lblSearch.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search_down.png")));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (over)
				lblSearch.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search_hover.png")));
		}
	}

	private class BtnCancelSearchMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			CancelSearch();
		}
	}

	private class MntmAboutActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			JOptionPane about = new JOptionPane(new JLabel("<html><div style=\"text-align: center;\">Music Factory<br><br>James King \u00a9 2014<br><br>www.github.com/James3432/MusicPlayer<br><br></html>", JLabel.CENTER));
			JDialog dialog = about.createDialog(frmMusicPlayer, "About");
			dialog.setModal(true);
			dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon-16.png")));
			dialog.setVisible(true);
			// JOptionPane.showMessageDialog(frmMusicPlayer,
			// "Music Factory\n\nJames King \u00a9 2014\n\nwww.github.com/James3432/MusicPlayer\n",
			// "About", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private class MntmResetFactoryDefaultsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int response = JOptionPane.showConfirmDialog(frmMusicPlayer, "Are you sure you want to revert to factory defaults?\nAll library data, settings and learned preferences will be deleted.", "Confirm factory reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (response == JOptionPane.YES_OPTION) {
				String homedir = System.getenv("user.home");
				if (homedir == null)
					homedir = System.getenv("USERPROFILE");
				File settings = new File(homedir + "\\Music Factory\\library.ser");
				if (settings.exists()) {
					settings.delete();
				}
				JOptionPane.showMessageDialog(frmMusicPlayer, "Music Factory will now close to finish reverting settings.", "Settings deleted", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
		}
	}

	private class SliderVolMouseWheelListener implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			if (notches < 0) {
				sliderVol.setValue(Math.min(sliderVol.getMaximum(), sliderVol.getValue() + 5));
			} else if (notches > 0) {
				sliderVol.setValue(Math.max(0, sliderVol.getValue() - 5));
			}
			SetVolume((float) sliderVol.getValue() / 100f);
		}
	}

	private class SliderMouseWheelListener implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			int value = slider.getValue();
			if (notches < 0) {
				value = slider.getValue() + (slider.getMaximum() / 50);
			} else if (notches > 0) {
				value = Math.max(0, slider.getValue() - (slider.getMaximum() / 50));
			}
			slider.setValue(value);
			// update time label
			double secs = ((double) value * (double) library.getPlaylist(playlistPlaying).get(trackPlaying).getLengthS()) / (double) slider.getMaximum();
			lbl_time.setText(Song.SecondsToString((int) secs));
			// skip song to this point
			SkipTo(secs);
			timing_offset = (int) Math.floor(1000 * secs);
			try {
				Thread.sleep(10 * THREAD_SLEEP);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private class ListPlaylistsMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (SwingUtilities.isLeftMouseButton(arg0)) {
				if (arg0.getClickCount() == 2) {
					if (tabMain.getRowCount() > 0) {
						tabMain.getSelectionModel().setSelectionInterval(0, 0);
						playSelected();
					}
				}
			}
			if (SwingUtilities.isRightMouseButton(arg0)) {
				int pl = listPlaylists.locationToIndex(arg0.getPoint());
				listPlaylists.setSelectedIndex(pl);

				if (pl < 1) {
					mntmDelete.setEnabled(false);
					mntmSwitch.setEnabled(false);
				} else {
					mntmDelete.setEnabled(true);
					mntmSwitch.setEnabled(true);
				}
				mnuRCPlaylist.show(listPlaylists, arg0.getX(), arg0.getY());
			}
			if (SwingUtilities.isMiddleMouseButton(arg0)) {

			}

		}
	}

	private class MntmNowPlayingActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if (playlistPlaying > -1 && rowPlaying > -1) {
				listPlaylists.setSelectedIndex(playlistPlaying);
				tabMain.getSelectionModel().setSelectionInterval(rowPlaying, rowPlaying);
				tabMain.scrollRectToVisible(tabMain.getCellRect(Math.min(rowPlaying + 100, tabMain.getRowCount() - 1), 0, true));
				tabMain.scrollRectToVisible(tabMain.getCellRect(rowPlaying, 0, true));
			}
		}
	}

	private class MntmAddAudioFilesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			final LibraryParser parser = new FileScanner();
			ParseDiskDialog worker = new ParseDiskDialog(parser, true);
			worker.setLocationRelativeTo(frmMusicPlayer);
			worker.setVisible(true);

			AddToLibrary(parser);
		}
	}

	private class BtnSmartActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (smartPlay) {
				btnSmart.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/brain.png")));
				btnSmart.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/brain_press.png")));
				lblSmartMode.setText("Smart mode off");
			} else {
				btnSmart.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/brain_down.png")));
				btnSmart.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/brain_down_press.png")));
				lblSmartMode.setText("Smart mode on");
			}
			smartPlay = !smartPlay;
		}
	}

	private class BtnClearActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			library.deleteQueue();
			RefreshUpNext();
		}
	}

	private class ButtonActionListener_1 implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			pnlSmart.setVisible(!pnlSmart.isVisible());
		}
	}

	private class MntmToggleSmartBarActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			btnSmartBar.doClick();
		}
	}

	private class MntmAddFilesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser = new JFileChooser();
			String startat = ".";
			startat = System.getenv("user.home");
			if (startat == null || startat.equals(""))
				startat = System.getenv("USERPROFILE");
			if (startat == null || startat.equals(""))
				startat = ".";
			chooser.setCurrentDirectory(new File(startat));
			chooser.setMultiSelectionEnabled(true);
			chooser.setDialogTitle("Choose files to add to library");
			chooser.setFileFilter(new MP3filter());
			int result = chooser.showOpenDialog(frmMusicPlayer);
			if (result == JFileChooser.APPROVE_OPTION) {
				File[] res = chooser.getSelectedFiles();
				LibraryParser parser = new FileScanner();
				parser.addFileList(res);
				if (res.length > 0) {
					parser.setValid(true);
					AddToLibrary(parser);
					listPlaylists.setSelectedIndex(0);
				}
			} else {
				// System.out.println("No Selection ");
			}
		}
	}

	private class MntmResetSongClustersActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {

			(new Thread() {
				@Override
				public void run() {
					SongCluster clusters = new SongCluster(library.getPlaylists().get(Library.MAIN_PLAYLIST).getList(), frmMusicPlayer);
					library.setClusters(clusters);
					PrintClusters(clusters, "");
					UpdateLibrary();
				}
			}).start();

		}
	}
	private class MntmClearSongFeaturesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			for(int i=0; i<library.getPlaylists().get(Library.MAIN_PLAYLIST).size(); ++i)
				library.getPlaylists().get(Library.MAIN_PLAYLIST).get(i).setAudioFeatures(null);
			UpdateLibrary();
		}
	}
	private class MntmSaveClustersActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			SongCluster cs = library.getClusters();
			PrintClustersToFile(cs, "clustering.txt");
		}
	}
	
	//@SuppressWarnings("unused")
	private void PrintClusters(SongCluster cs, String branch) {
		List<AbstractCluster> nested = cs.getChildren();
		int i = 0;
		for(AbstractCluster c : nested){
			if(c instanceof LeafCluster){
				Song s = ((LeafCluster) c).getTrack();
				System.out.println("Branch "+(branch+"."+i)+" Track: "+s.getName()+" - "+s.getArtist());
			}else{
				PrintClusters((SongCluster) c, branch+"."+i);
				System.out.println("--------------------------------------");
			}
			i++;
		}
	}
	
	private void PrintClustersToFile(SongCluster cs, String file) {
		File out = new File(file);
		try {
			PrintWriter writer = new PrintWriter(out);
			writer.println("Music Factory clusters, generated on " + (new Date()));
			writer.println();
			writer.println();
			PrintSubClustersToFile(cs, "", writer);
			writer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	private void PrintSubClustersToFile(SongCluster cs, String branch, PrintWriter out){
		List<AbstractCluster> nested = cs.getChildren();
		int i = 0;
		for(AbstractCluster c : nested){
			if(c instanceof LeafCluster){
				Song s = ((LeafCluster) c).getTrack();
				out.println("Branch "+(branch+"."+i)+" Track: "+s.getName()+" - "+s.getArtist());
			}else{
				PrintSubClustersToFile((SongCluster) c, branch+"."+i, out);
				out.println("--------------------------------------");
			}
			i++;
		}
	}

	private void RefreshUpNext() {
		if (!library.hasQueue()) {
			listUpNext.setModel(new AbstractListModel() {
				private static final long serialVersionUID = 1L;

				public int getSize() {
					return 0;
				}

				public Object getElementAt(int index) {
					return null;
				}
			});
			listUpNext.invalidate();
			listUpNext.repaint();
			btnClear.setVisible(false);
			return;
		}

		Song[] upnext2 = library.getQueue().getNextUpSongArray();
		// if at end of upnext queue
		if (upnext2.length == 0 && library.getQueue().isValid() && library.getQueue().position == library.getQueue().size() - 1 && library.getQueue().getStart() != null)
			try {
				if (searching && playlistSearching == library.getQueue().getStart().playlist && library.normalToSearchModel(library.getQueue().getStart().index) > -1)
					upnext2 = new Song[] { library.getPlaylists().get(Library.HIDDEN_PLAYLISTS + library.getQueue().getStart().playlist).get(library.searchToNormalModel(((TableSorter) tabMain.getModel()).modelIndex(((TableSorter) tabMain.getModel()).viewIndex(library.normalToSearchModel(library.getQueue().getStart().index)) + 1))) };
				else
					upnext2 = new Song[] { library.getPlaylists().get(Library.HIDDEN_PLAYLISTS + library.getQueue().getStart().playlist).get((getPlaylistSorter(library.getQueue().getStart().playlist)).modelIndex((getPlaylistSorter(library.getQueue().getStart().playlist)).viewIndex(library.getQueue().getStart().index) + 1)) };
			} catch (Exception e) {
				upnext2 = new Song[] {};
			}
		final Song[] upnext = upnext2;
		listUpNext.setModel(new AbstractListModel() {
			private static final long serialVersionUID = 1L;
			Song[] values = upnext;

			public int getSize() {
				return values.length;
			}

			public Object getElementAt(int index) {
				return values[index];
			}
		});
		listUpNext.invalidate();
		listUpNext.repaint();

		if (listUpNext.getModel().getSize() > 0)
			btnClear.setVisible(true);
		else
			btnClear.setVisible(false);
	}

	private void RefreshPlaylists() {
		int oldIndex = -1;
		if (listPlaylists != null && listPlaylists.getSelectedIndex() > -1)
			oldIndex = listPlaylists.getSelectedIndex();
		listPlaylists.setModel(new PlaylistModel(library));
		DontRefreshMainScreen = true;
		if (oldIndex > -1)
			listPlaylists.setSelectedIndex(oldIndex);
		else
			listPlaylists.setSelectedIndex(0);
		DontRefreshMainScreen = false;
		listPlaylists.invalidate();
		listPlaylists.repaint();
	}

	private class PlaylistModel extends AbstractListModel {
		private static final long serialVersionUID = 1L;
		Playlist[] values;

		public PlaylistModel(Library library) {
			Playlist[] user = library.getPlaylistsAsArray();
			// Playlist[] system = new Playlist[] { new Playlist("Tracks", 0),
			// new Playlist("Artists", 0), new Playlist("Albums", 0) };
			// values = new Playlist[user.length + system.length];
			values = user;
			// System.arraycopy(system, 0, values, 0, system.length);
			// System.arraycopy(user, 0, values, system.length, user.length);
		}

		public int getSize() {
			return values.length;
		}

		public Object getElementAt(int index) {
			return values[index];
		}
	}

	private void SetVolume(float vol) {
		Info source = Port.Info.SPEAKER;
		if (AudioSystem.isLineSupported(source)) {
			try {
				Port outline = (Port) AudioSystem.getLine(source);
				outline.open();
				FloatControl volumeControl = (FloatControl) outline.getControl(FloatControl.Type.VOLUME);
				volumeControl.setValue(vol);
				library.setVolume((int) Math.min(100, vol * 100));
			} catch (LineUnavailableException ex) {
				ex.printStackTrace();
			}
		}
	}

	/*
	 * private class MyDispatcher implements KeyEventDispatcher {
	 * 
	 * @Override public boolean dispatchKeyEvent(KeyEvent e) { int row = tabMain.getSelectedRow();
	 * 
	 * switch (e.getKeyCode()) { case KeyEvent.VK_DOWN: if (e.getID() == KeyEvent.KEY_PRESSED) { tabMain.getSelectionModel().setSelectionInterval(row++, row++); tabMain.scrollRectToVisible(tabMain.getCellRect(row + 1, 0, true)); } break; case KeyEvent.VK_UP: if (e.getID() == KeyEvent.KEY_PRESSED) { tabMain.getSelectionModel().setSelectionInterval(row--, row--); tabMain.scrollRectToVisible(tabMain.getCellRect(row - 1, 0, true)); } break; case KeyEvent.VK_LEFT: if (e.getID() == KeyEvent.KEY_RELEASED) { playPrev(); } break; case KeyEvent.VK_RIGHT: if (e.getID() == KeyEvent.KEY_RELEASED) { playNext(); } break; case KeyEvent.VK_ENTER: if (e.getID() == KeyEvent.KEY_RELEASED) playSelected(); break; case KeyEvent.VK_SPACE: if (e.getID() == KeyEvent.KEY_RELEASED) TogglePause(); break; }
	 * 
	 * 
	 * if (e.getID() == KeyEvent.KEY_PRESSED) { System.out.println("tester"); } else if (e.getID() == KeyEvent.KEY_RELEASED) { System.out.println("2test2"); } else if (e.getID() == KeyEvent.KEY_TYPED) { System.out.println("3test3"); }
	 * 
	 * 
	 * return false; } }
	 */

	private class MainTabTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		// EXPORT
		public int getSourceActions(JComponent c) {
			return COPY;
		}

		public Transferable createTransferable(JComponent c) {
			return new Transferable() {

				@Override
				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
					Song[] sels = new Song[tabMain.getSelectedRowCount()];
					for (int i = 0; i < tabMain.getSelectedRowCount(); ++i)
						sels[i] = library.get(ViewToModel(tabMain.getSelectedRows()[i]));
					return sels;// library.get(((TableSorter) tabMain.getModel()).modelIndex(tabMain.getSelectedRow()));
				}

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { songFlavor };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					if (flavor.equals(songFlavor))
						return true;
					else
						return false;
				}

			};
		}

		public void exportDone(JComponent c, Transferable t, int action) {
			// no work to do
		}

		// IMPORT
		public boolean canImport(TransferSupport supp) {
			return false;
		}

		public boolean importData(TransferSupport supp) {
			return false;
		}

	}

	private class PlaylistTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		// EXPORT
		public int getSourceActions(JComponent c) {
			return COPY;
		}

		public Transferable createTransferable(JComponent c) {
			return new Transferable() {

				@Override
				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
					return listPlaylists.getSelectedValue();
				}

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { playlistFlavor };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					return (flavor.equals(playlistFlavor));
				}

			};
		}

		public void exportDone(JComponent c, Transferable t, int action) {
			/*
			 * if (action == MOVE) { c.removeSelection(); }
			 */
		}

		// IMPORT
		public boolean canImport(TransferSupport supp) {
			// Check for String flavor
			if (!supp.isDataFlavorSupported(songFlavor)) {
				return false;
			}

			// Fetch the drop location
			JList.DropLocation loc = (javax.swing.JList.DropLocation) supp.getDropLocation();

			if (loc.getIndex() < FIXED_PLAYLIST_ELEMENTS)
				return false;

			// Return whether we accept the location
			// return shouldAcceptDropLocation(loc);
			return true;
		}

		public boolean importData(TransferSupport supp) {
			if (!canImport(supp)) {
				return false;
			}

			// Fetch the Transferable and its data
			Transferable t = supp.getTransferable();
			Song[] data;
			try {
				data = (Song[]) t.getTransferData(songFlavor);
				// Fetch the drop location
				JList.DropLocation loc = (JList.DropLocation) supp.getDropLocation();

				// Insert the data at this location
				for (int i = 0; i < data.length; ++i)
					AddSongToPlaylist(loc.getIndex(), data[i]);

				return true;
			} catch (UnsupportedFlavorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		}
	}

	private void AddSongToPlaylist(int index, Song data) {
		if (index < FIXED_PLAYLIST_ELEMENTS) {
			// do nothing: can't insert into default playlists
		} else {
			library.getPlaylists().get(index + Library.HIDDEN_PLAYLISTS).append(data);
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
			try {
				if (mouseIsDown)
					this.knobImage = ImageIO.read(this.getClass().getResource("/jk509/player/res/knob_down.png"));
				else
					this.knobImage = ImageIO.read(this.getClass().getResource("/jk509/player/res/knob.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			g.drawImage(this.knobImage, thumbRect.x + 3, thumbRect.y + 3, 6, 13, null);
		}

		/*
		 * @Override protected void scrollDueToClickInTrack(int direction) { // this is the default behaviour, let's comment that out //scrollByBlock(direction);
		 * 
		 * int value = slider.getValue(); value = this.valueForXPosition(slider.getMousePosition().x); slider.setValue(value); }
		 */

	};

	private class CustomVolumeSlider extends BasicSliderUI {

		Image knobImage;

		public CustomVolumeSlider(JSlider aSlider) {
			super(aSlider);
			try {
				this.knobImage = ImageIO.read(this.getClass().getResource("/jk509/player/res/knob3.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void paintThumb(Graphics g) {
			try {
				if (mouseIsDownVol)
					this.knobImage = ImageIO.read(this.getClass().getResource("/jk509/player/res/knob3_down.png"));
				else
					this.knobImage = ImageIO.read(this.getClass().getResource("/jk509/player/res/knob3.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			g.drawImage(this.knobImage, thumbRect.x - 2, thumbRect.y + 2, 15, 15, null);
		}

	};

	class MP3filter extends FileFilter {

		public MP3filter() {
			super();
		}

		@Override
		public String getDescription() {
			String des = "MP3 files";
			return des;
		}

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			if (extension != null) {
				if (extension.equals("mp3") || extension.equals("mpeg3")) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}

	}

	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public class PlaylistRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 1L;
		private ImageIcon image[];
		Font fontPlain;
		Font fontBold;

		// Hack to overcome core bug (#6700748)
		@Override
		public boolean isVisible() {
			return false;
		}

		public PlaylistRenderer() {
			ImageIcon[] image = new ImageIcon[6];
			try {
				image[0] = new ImageIcon(ImageIO.read(MusicPlayer.class.getResourceAsStream("/jk509/player/res/playlist.png")));
				image[1] = new ImageIcon(ImageIO.read(MusicPlayer.class.getResourceAsStream("/jk509/player/res/playlist_user.png")));
				image[2] = new ImageIcon(ImageIO.read(MusicPlayer.class.getResourceAsStream("/jk509/player/res/playlist_auto.png")));
				image[3] = new ImageIcon(ImageIO.read(MusicPlayer.class.getResourceAsStream("/jk509/player/res/playlist_playing.png")));
				image[4] = new ImageIcon(ImageIO.read(MusicPlayer.class.getResourceAsStream("/jk509/player/res/playlist_user_playing.png")));
				image[5] = new ImageIcon(ImageIO.read(MusicPlayer.class.getResourceAsStream("/jk509/player/res/playlist_auto_playing.png")));
			} catch (NullPointerException e) {
				System.out.println("Image load error");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			setOpaque(true);

			// fontPlain = new Font( "Arial", Font.PLAIN, 14 );
			fontBold = new Font("Trebuchet MS", Font.BOLD, 12);

			this.image = image;
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Playlist pl = (Playlist) value;

			// Display the text for this item
			setText(" " + pl.getName());

			setFont(fontBold);

			setBorder(new EmptyBorder(6, 6, 6, 6));

			// Set the correct image
			switch (pl.getType()) {
			case Playlist.DEFAULT:
				if (index == playlistPlaying)
					setIcon(image[3]);
				else
					setIcon(image[0]);
				break;
			case Playlist.USER:
				if (index == playlistPlaying)
					setIcon(image[4]);
				else
					setIcon(image[1]);
				break;
			case Playlist.AUTO:
				if (index == playlistPlaying)
					setIcon(image[5]);
				else
					setIcon(image[2]);
				break;
			default:
				if (index == playlistPlaying)
					setIcon(image[3]);
				else
					setIcon(image[0]);
				break;
			}

			// check if this cell represents the current DnD drop location
			JList.DropLocation dropLocation = list.getDropLocation();
			if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) {
				setBackground(new Color(94, 136, 188));
				setForeground(new Color(250, 250, 250));
			} else if (isSelected) {
				// Set the color and font for a selected item
				setBackground(new Color(139, 167, 201));
				setForeground(new Color(250, 250, 250));
				// setFont(fontBold);

			} else {
				// Set the color and font for an unselected item
				setBackground(new Color(213, 219, 226));
				setForeground(Color.black);
				// setFont(fontPlain);
			}

			return this;
		}

	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		start = e.getPoint();
		txtSearch.setFocusable(false);
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
			if (start != null) {
				c.setLocation((int) (p.getX() - start.getX()), (int) (p.getY() - start.getY()));
				c.repaint();
			}
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

}
