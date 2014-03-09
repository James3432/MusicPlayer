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
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
					window.SetVolume(1.0f);
					new SwingDragImages.GlobalImageProvider();
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
	private JPanel pnlPlaylistCtrls;
	private JButton btnAdd;
	private JButton btnDelete;
	private JButton btnPlay;
	private boolean stopped = true; // whether anything is playing/paused
	private int rowPlaying = -1;
	private int trackPlaying = -1;
	private int playlistPlaying = -1; // which playlist the row/track playing ints
									// refer to
	private TableSorter playlistPlayingSorter; // TableSorter for the currently
												// playing playlist : TODO needs
												// to keep updated with current,
												// or ignore if
												// playlistPlaying==selectedPlaylist
	//private int rowSelected; // changes according to sort
	// private int playlistSelected = 0;
	public static int FIXED_PLAYLIST_ELEMENTS = 3; // number of system-set
													// playlists (ie. tracks,
													// artists, albums)
	private Point start;
	private PlaybackListener playbackListener = new PlaybackListener();
	private int THREAD_SLEEP = 10;
	private int MAX_SLIDER_RANGE = 300;
	private boolean repeat = false;
	private boolean repeatone = false;
	private boolean shuffle = false;
	private boolean mouseIsDown = false;
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
	private JSlider sliderVol;
	private JPopupMenu mnuAddPlaylist;
	private JMenuItem mntmNewPlaylist;
	private JMenuItem mntmSmartPlaylistFrom;
	private JMenuItem mntmSmartPlaylistFrom_1;
	private JButton btnCancelSearch;
	private JPanel pnlSearchTxt;

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

		sliderVol = new JSlider();
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
				Point p = e.getPoint();
				int value = uiVol.valueForXPosition(p.x);
				sliderVol.setValue(value);
				SetVolume((float) value / 100f);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
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
		lblTrack.setHorizontalAlignment(SwingConstants.CENTER);
		lblTrack.setFont(new Font("Segoe UI Light", Font.BOLD, 12));
		pnlTop.add(lblTrack);

		pnlBtm = new JPanel();
		pnlBtm.setPreferredSize(new Dimension(10, 18));
		pnlBtm.setOpaque(false);
		pnlNames.add(pnlBtm, BorderLayout.SOUTH);
		pnlBtm.setLayout(new BorderLayout(0, 0));

		lblArtistAlbum = new JLabel("");
		lblArtistAlbum.setHorizontalAlignment(SwingConstants.CENTER);
		lblArtistAlbum.setVerticalTextPosition(SwingConstants.TOP);
		lblArtistAlbum.setVerticalAlignment(SwingConstants.TOP);
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
				// update time label
				lbl_time.setText(Song.SecondsToString((int) (((double) value * library.getPlaylists().get(playlistPlaying).get(trackPlaying).getLengthS()/*library.getTracks().get(trackPlaying).getLengthS()*/) / (double) slider.getMaximum())));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseIsDown = false;
				Point p = e.getPoint();
				int value = ui.valueForXPosition(p.x);
				slider.setValue(value);
				// update time label
				double secs = ((double) value * (double) library.getPlaylists().get(playlistPlaying).get(trackPlaying).getLengthS()/*library.getTracks().get(trackPlaying).getLengthS()*/) / (double) slider.getMaximum();
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
				lbl_time.setText(Song.SecondsToString((int) (((double) value * library.getPlaylists().get(playlistPlaying).get(trackPlaying).getLengthS()/*library.getTracks().get(trackPlaying).getLengthS()*/) / (double) slider.getMaximum())));
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
		scrlMain.setAutoscrolls(true);

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
		tabMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabMain.getSelectionModel().setSelectionInterval(0, 0);
		scrlMain.setViewportView(tabMain);
		JScrollBar sb = scrlMain.getVerticalScrollBar();
		sb.setPreferredSize(new Dimension(25, 50));
		scrlMain.setVerticalScrollBar(sb);
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

		mnuAddPlaylist = new JPopupMenu();

		mntmNewPlaylist = new JMenuItem("New playlist");
		mntmNewPlaylist.addActionListener(new NewPlaylistListener(0));
		mnuAddPlaylist.add(mntmNewPlaylist);

		mntmSmartPlaylistFrom = new JMenuItem("Smart playlist from selection");
		mntmSmartPlaylistFrom.addActionListener(new NewPlaylistListener(1));
		mnuAddPlaylist.add(mntmSmartPlaylistFrom);

		mntmSmartPlaylistFrom_1 = new JMenuItem("Smart playlist from scratch");
		mntmSmartPlaylistFrom_1.addActionListener(new NewPlaylistListener(2));
		mnuAddPlaylist.add(mntmSmartPlaylistFrom_1);
		GridBagLayout gbl_pnlPlaylistCtrls = new GridBagLayout();
		// gbl_pnlPlaylistCtrls.columnWidths = new int[]{55, 55, 0};
		// gbl_pnlPlaylistCtrls.rowHeights = new int[]{31, 0};
		gbl_pnlPlaylistCtrls.columnWeights = new double[] { 0.0, 0.0 };
		gbl_pnlPlaylistCtrls.rowWeights = new double[] { 0.0 };
		pnlPlaylistCtrls.setLayout(gbl_pnlPlaylistCtrls);

		btnDelete = new JButton();
		btnDelete.addActionListener(new BtnDeleteActionListener());

		btnAdd = new JButton();
		btnAdd.addActionListener(new BtnAddActionListener());
		btnAdd.addMouseListener(new BtnAddMouseListener());
		// btnAdd.setComponentPopupMenu(mnuAddPlaylist);
		btnAdd.setFocusable(false);
		btnAdd.setOpaque(false);
		btnAdd.setContentAreaFilled(false);
		btnAdd.setBorderPainted(false);
		btnAdd.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/add.png")));
		btnAdd.setPressedIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/add_down.png")));
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnAdd.insets = new Insets(5, 5, 5, 5);
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
		gbc_btnDelete.insets = new Insets(5, 0, 5, 0);
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

		try {
			songFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=jk509.player.Song");
			playlistFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=jk509.player.Playlist");
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
		File settings = new File(homedir + "\\MusicPlayer\\library.ser");

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
	}

	private void UpdateLibrary() {
		String homedir = System.getenv("user.home");
		if (homedir == null)
			homedir = System.getenv("USERPROFILE");
		File settings = new File(homedir + "\\MusicPlayer\\library.ser");

		File theDir = new File(homedir + "\\MusicPlayer");

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			theDir.mkdir();
		}

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
			LibraryParser parser = new ItunesParser();
			ParseItunesDialog worker = new ParseItunesDialog(parser);
			worker.setLocationRelativeTo(frmMusicPlayer);
			worker.setVisible(true);

			Import(parser);
		}
	}

	private class BtnImportFromDiskActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			LibraryParser parser = new FileScanner();
			ParseDiskDialog worker = new ParseDiskDialog(parser);
			worker.setLocationRelativeTo(frmMusicPlayer);
			worker.setVisible(true);

			Import(parser);
		}
	}

	private void Import(LibraryParser parser) {

		if (!parser.isValid())
			return;

		parser.run();

		// TODO: sort
		library.setPlaylist(0, parser.getTracks());
		library.setPlaylist(1, parser.getTracks());
		library.setPlaylist(2, parser.getTracks());
		// library.addTracks(parser.getTracks());

		DisplayLibrary();

		// TODO could move to OnClose() for window
		UpdateLibrary();

		RefreshPlaylists();

	}

	private void DisplayLibrary() {
		Object[][] rows = new Object[library.size()][9];
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
				if (playlistPlaying == listPlaylists.getSelectedIndex() && row == rowPlaying && (player != null) && !player.isStopped()) {
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
			// get new row - convert back to model format then into new view
			// format
			if (trackPlaying > -1 && playlistPlaying == listPlaylists.getSelectedIndex())
				rowPlaying = playlistPlayingSorter.viewIndex(trackPlaying);
				//rowPlaying = ((TableSorter) tabMain.getModel()).viewIndex(trackPlaying);
			int rowSelected = arg0.getModifiers();
			tabMain.getSelectionModel().setSelectionInterval(rowSelected, rowSelected);
			// force selection to be at top of screen
			tabMain.scrollRectToVisible(tabMain.getCellRect(Math.min(rowSelected + 100, tabMain.getRowCount() - 1), 0, true));
			tabMain.scrollRectToVisible(tabMain.getCellRect(rowSelected, 0, true));
		}
	}

	private void playSelected() {
		// rowPlaying = ((TableSorter)
		// tabMain.getModel()).modelIndex(tabMain.getSelectedRow());
		
		// This is the only method which can start playing tracks from a different playlist. So save the new playlistPlaying and table model
		playlistPlaying = listPlaylists.getSelectedIndex();
		playlistPlayingSorter = (TableSorter) tabMain.getModel();
		
		// make sure playing icon gets updated on playlist list
		listPlaylists.invalidate();
		listPlaylists.repaint();
		
		rowPlaying = tabMain.getSelectedRow();
		play(rowPlaying);
	}

	private void playFirst() {
		play(0);
	}

	private void playAgain() {
		play(rowPlaying);
	}

	private void playRandom() {
		int tracks = tabMain.getRowCount();
		int rand = (int) (Math.random() * tracks);
		play(rand);
	}

	private void playNext() {
		if (player == null)
			return;
		
		if(rowPlaying == library.getPlaylists().get(playlistPlaying).size() - 1){
			Stop();
			return;
		}

		boolean paused = player.isPaused();
		play(rowPlaying + 1);

		if (paused) {
			btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/play.png")));
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
					if(escape < 1)
						initialised = true;
					else
						initialised = false;
				}
		}
	}

	private void playPrev() {
		if (player == null)
			return;

		boolean paused = player.isPaused();

		// restart current if not near beginning
		if (timing_offset > 3000 || milliseconds > 3000 /*|| rowPlaying == 0*/) {
			play(rowPlaying);

		} else {
			if(rowPlaying == 0){
				Stop();
				return;
			}
			play(rowPlaying - 1);
		}

		if (paused) {
			btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/play.png")));
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
					if(escape < 1)
						initialised = true;
					else
						initialised = false;
				}
		}
	}

	private void play(int r) {
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

			// get row in terms of underlying model

			if (r >= 0 && r <= playlistPlayingSorter.getRowCount() - 1 /*r <= tabMain.getModel().getRowCount() - 1*/) {
				//int row = ((TableSorter) tabMain.getModel()).modelIndex(r);
				int row = playlistPlayingSorter.modelIndex(r);
				String loc = library.getPlaylists().get(playlistPlaying).get(row).getLocation(); //library.get(row).getLocation();
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
					player = null; // try to force gc?
					player = new SoundJLayer(loc, playbackListener);
					player.play();
					btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
					btnBack.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/back.png")));
					btnFwd.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/fwd.png")));
					rowPlaying = r;
					//trackPlaying = ((TableSorter) tabMain.getModel()).modelIndex(rowPlaying);
					trackPlaying = playlistPlayingSorter.modelIndex(rowPlaying);
					RefreshMainList();
					UpdateTrackDisplay();
				}
			}
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
			if (row >= 0 && row <= playlistPlayingSorter.getRowCount() - 1 /*row <= tabMain.getModel().getRowCount() - 1*/) {
				// String loc = (String)
				// tabMain.getModel().getValueAt(rowPlaying, 8);
				String loc = library.getPlaylists().get(playlistPlaying).get(row).getLocation();
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

		// This is the line that actually does the jump - all the above is just
		// to make sure the track has played a little bit so that a header frame
		// has been read (to establish frames/ms)
		playAt(/* tabMain.getSelectedRow(), */(int) (s * 1000));

		/*
		 * if (paused) { try { Thread.sleep(THREAD_SLEEP); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * TogglePause(); }
		 */
	}

	private void UpdateTrackDisplay() {
		try {
			int row = trackPlaying;
			if (row >= 0 && row <= playlistPlayingSorter.getRowCount() - 1 /*row <= tabMain.getModel().getRowCount() - 1*/) {
				stopped = false;
				btnRepeat.setVisible(true);
				btnShuffle.setVisible(true);
				slider.setVisible(true);
				lbl_endtime.setVisible(true);
				lbl_time.setVisible(true);
				String name = library.getPlaylists().get(playlistPlaying).get(row).getName();
				String album = library.getPlaylists().get(playlistPlaying).get(row).getAlbum();
				String artist = library.getPlaylists().get(playlistPlaying).get(row).getArtist();
				int length = library.getPlaylists().get(playlistPlaying).get(row).getLengthS();
				String len = library.getPlaylists().get(playlistPlaying).get(row).getLength();
				lblTrack.setText(name);
				lblArtistAlbum.setText(artist + " - " + album);
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

	private void Search(String query){
		//TODO
		// idea: create temporary playlist without changing playlistlist display. Adjust other onclicks so it can't be cancelled
		if(!btnCancelSearch.isVisible()){
			btnCancelSearch.setVisible(true);
			//txtSearch.setSize(txtSearch.getWidth() - 30, txtSearch.getHeight());
			txtSearch.setColumns(txtSearch.getColumns()-2);
		}
		
	}
	
	private void CancelSearch(){
		btnCancelSearch.setVisible(false);
		//txtSearch.setSize(txtSearch.getWidth() + 30, txtSearch.getHeight());
		txtSearch.setText(" Search");
		txtSearch.setColumns(txtSearch.getColumns()+2);
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
			if(stopped)
				return;
			if (repeatone) {
				playAgain();
			} else {
				if (shuffle) {
					playRandom();
				} else if (rowPlaying == library.getPlaylists().get(playlistPlaying).size() - 1/*tabMain.getRowCount() - 1*/) {
					if (repeat)
						playFirst();
					else
						Stop();
				} else {
					playNext();
				}
			}
		}

		@Override
		public void frameDecoded(JLayerPlayerPausable.DecodeEvent event) {
			milliseconds = event.position;
			// if(s > seconds){
			if (!player.isPaused() && milliseconds > (seconds + 1) * 1000) {
				// System.out.println("***************");
				seconds = (int) Math.floor(milliseconds / 1000.0);
				if (!mouseIsDown) {
					lbl_time.setText(Song.SecondsToString(seconds + (int) (timing_offset / 1000.0)));
					slider.setValue((int) ((double) slider.getMaximum() * ((double) seconds + (timing_offset / 1000.0)) / (double) library.getPlaylists().get(playlistPlaying).get(trackPlaying).getLengthS()));
				}
			} // else {
				// System.out.println(milliseconds);
				// System.out.println(seconds * 1000);
			// }
		}
	}

	private void Stop() {
		stopped = true;
		try{
			player.stop();
		}catch(Exception e){}
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
		btnBack.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/back_grey.png")));
		btnFwd.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/fwd_grey.png")));
		RefreshMainList();
		listPlaylists.invalidate();
		listPlaylists.repaint();
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
			if (txtSearch.getText().equals(" Search"))
				txtSearch.setText("");
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
		} else {
			playSelected();
		}
	}

	private void DeleteSelected() {
		int rowV = tabMain.getSelectedRow();
		boolean last = false;
		if (rowV == tabMain.getRowCount() - 1)
			last = true;
		//int rowM = ((TableSorter) tabMain.getModel()).modelIndex(rowV);
		int rowM = playlistPlayingSorter.modelIndex(rowV);
		int reply = JOptionPane.showConfirmDialog(frmMusicPlayer, "Are you sure you want to delete this track from your library?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			Delete(rowM);
		}

		// adjust rowPlaying (not trackplaying)
		if (playlistPlaying == listPlaylists.getSelectedIndex() && rowPlaying > rowV) {
			rowPlaying--;
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
	}

	private void Delete(int row) {
		// row is the row of the model, not the view
		if (playlistPlaying == listPlaylists.getSelectedIndex() && trackPlaying == row) {
			if (player != null && !player.isPaused() && !player.isStopped())
				TogglePause();
			Stop();
		}
		library.remove(row);
		DisplayLibrary();
		RefreshMainList();
	}

	private class FrmMusicPlayerKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int row = tabMain.getSelectedRow();
			switch (e.getKeyCode()) {
			case KeyEvent.VK_DOWN:
				tabMain.getSelectionModel().setSelectionInterval(row++, row++);
				tabMain.scrollRectToVisible(tabMain.getCellRect(row - 1, 0, true));
				break;
			case KeyEvent.VK_UP:
				tabMain.getSelectionModel().setSelectionInterval(row--, row--);
				tabMain.scrollRectToVisible(tabMain.getCellRect(row + 1, 0, true));
				break;
			case KeyEvent.VK_F:
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					txtSearch.setFocusable(true);
					txtSearch.requestFocus();
				}
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
			}
		}
	}

	private class FrmMusicPlayerWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
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
			mnuAddPlaylist.show(pnlPlaylistCtrls, ((JButton) e.getSource()).getX() - 20, ((JButton) e.getSource()).getY() - 45);
		}
	}

	private class BtnAddActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			mnuAddPlaylist.show(pnlPlaylistCtrls, ((JButton) e.getSource()).getX() - 20, ((JButton) e.getSource()).getY() - 45);
		}
	}

	private class ListPlaylistsListSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			// Update song display
			int playlistSelected = listPlaylists.getSelectedIndex();
			if (playlistSelected > -1) {
				library.setCurrentPlaylist(playlistSelected);
				DisplayLibrary();
				RefreshMainList();
			}
		}
	}

	private class BtnDeleteActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Playlist current = (Playlist) listPlaylists.getSelectedValue();
			if (current.getType() == Playlist.USER || current.getType() == Playlist.AUTO) {
				if (JOptionPane.showConfirmDialog(frmMusicPlayer, "Are you sure you want to delete this playlist?", "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					library.getPlaylists().remove(listPlaylists.getSelectedIndex());
					int index = Math.min(listPlaylists.getSelectedIndex(), listPlaylists.getModel().getSize() - 2);
					RefreshPlaylists();
					listPlaylists.setSelectedIndex(index);
				}
			}
		}
	}

	private class NewPlaylistListener implements ActionListener {
		int type; // 0=user, 1=smart, 2=smart from selected song

		public NewPlaylistListener(int type) {
			this.type = type;
		}

		public void actionPerformed(ActionEvent e) {
			String name = JOptionPane.showInputDialog(frmMusicPlayer, "Playlist name: ", "New Playlist", JOptionPane.QUESTION_MESSAGE);
			if (name != null && !name.equals("")) {
				Playlist pl = new Playlist(name, ((type == 0) ? 1 : 2));
				library.addPlaylist(pl);
				RefreshPlaylists();
			}
		}
	}
	private class TxtSearchKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent arg0) {
			if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
				if(txtSearch.getText() != null && !txtSearch.getText().equals(""))
					Search(txtSearch.getText());
			}
		}
	}
	private class LblSearchMouseListener extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent e) {
			lblSearch.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search_hover.png")));
		}
		@Override
		public void mouseExited(MouseEvent e) {
			lblSearch.setIcon(new ImageIcon(MusicPlayer.class.getResource("/jk509/player/res/search.png")));
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			if(txtSearch.getText() != null && !txtSearch.getText().equals("") && !txtSearch.getText().equals(" Search"))
				Search(txtSearch.getText());
		}
	}
	private class BtnCancelSearchMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			CancelSearch();
		}
	}

	private void RefreshPlaylists() {
		int oldIndex = -1;
		if (listPlaylists != null && listPlaylists.getSelectedIndex() > -1)
			oldIndex = listPlaylists.getSelectedIndex();
		listPlaylists.setModel(new PlaylistModel(library));
		if (oldIndex > -1)
			listPlaylists.setSelectedIndex(oldIndex);
		else
			listPlaylists.setSelectedIndex(0);
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
			} catch (LineUnavailableException ex) {
				ex.printStackTrace();
			}
		}
	}

	/*
	 * private class MyDispatcher implements KeyEventDispatcher {
	 * 
	 * @Override public boolean dispatchKeyEvent(KeyEvent e) { int row =
	 * tabMain.getSelectedRow();
	 * 
	 * switch (e.getKeyCode()) { case KeyEvent.VK_DOWN: if (e.getID() ==
	 * KeyEvent.KEY_PRESSED) {
	 * tabMain.getSelectionModel().setSelectionInterval(row++, row++);
	 * tabMain.scrollRectToVisible(tabMain.getCellRect(row + 1, 0, true)); }
	 * break; case KeyEvent.VK_UP: if (e.getID() == KeyEvent.KEY_PRESSED) {
	 * tabMain.getSelectionModel().setSelectionInterval(row--, row--);
	 * tabMain.scrollRectToVisible(tabMain.getCellRect(row - 1, 0, true)); }
	 * break; case KeyEvent.VK_LEFT: if (e.getID() == KeyEvent.KEY_RELEASED) {
	 * playPrev(); } break; case KeyEvent.VK_RIGHT: if (e.getID() ==
	 * KeyEvent.KEY_RELEASED) { playNext(); } break; case KeyEvent.VK_ENTER: if
	 * (e.getID() == KeyEvent.KEY_RELEASED) playSelected(); break; case
	 * KeyEvent.VK_SPACE: if (e.getID() == KeyEvent.KEY_RELEASED) TogglePause();
	 * break; }
	 * 
	 * 
	 * if (e.getID() == KeyEvent.KEY_PRESSED) { System.out.println("tester"); }
	 * else if (e.getID() == KeyEvent.KEY_RELEASED) {
	 * System.out.println("2test2"); } else if (e.getID() == KeyEvent.KEY_TYPED)
	 * { System.out.println("3test3"); }
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
					return library.get(((TableSorter)tabMain.getModel()).modelIndex(tabMain.getSelectedRow()));
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
			Song data;
			try {
				data = (Song) t.getTransferData(songFlavor);
				// Fetch the drop location
				JList.DropLocation loc = (JList.DropLocation) supp.getDropLocation();

				// Insert the data at this location
				AddSongToPlaylist(loc.getIndex(), data);

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
			library.getPlaylists().get(index).append(data);
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
			g.drawImage(this.knobImage, thumbRect.x - 2, thumbRect.y + 2, 15, 15, null);
		}

	};

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
				if(index == playlistPlaying)
					setIcon(image[3]);
				else
					setIcon(image[0]);
				break;
			case Playlist.USER:
				if(index == playlistPlaying)
					setIcon(image[4]);
				else
					setIcon(image[1]);
				break;
			case Playlist.AUTO:
				if(index == playlistPlaying)
					setIcon(image[5]);
				else
					setIcon(image[2]);
				break;
			default:
				if(index == playlistPlaying)
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
