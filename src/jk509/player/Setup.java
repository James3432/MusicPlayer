package jk509.player;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.filechooser.FileFilter;

public class Setup extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblBasicSettings;
	private JLabel lblImportFromItunes;
	private JSplitPane splitPane;
	private JPanel pnlSidebar;
	private JLabel lblImportFromMy;
	private JLabel lblAnalyseAudio;
	private JLabel lblIntroduction;
	private JLabel lblFinished;
	private JLabel lblbg;
	private JPanel pnlOverview;
	private JLabel blt5;
	private JLabel blt2;
	private JLabel blt1;
	private JLabel blt6;
	private JLabel blt4;
	private JLabel blt3;
	private JPanel pnlMain;
	private JPanel pnl1;
	private JPanel pnl2;
	private JPanel pnl3;
	private JPanel pnl4;
	private JPanel pnl5;
	private JPanel pnl6;
	private CardLayout cardLayout;
	private JPanel panel_11;
	private JPanel panel_12;
	private JPanel panel_21;
	private JPanel panel_22;
	private JPanel panel_31;
	private JPanel panel_32;
	private JPanel panel_41;
	private JPanel panel_42;
	private JPanel panel_51;
	private JPanel panel_52;
	private JPanel panel_61;
	private JPanel panel_62;
	private JButton btnNext1;
	private JButton btnPrevious2;
	private JButton btnNext2;
	private JButton btnPrevious3;
	private JButton btnNext3;
	private JButton btnPrevious4;
	private JButton btnNext4;
	private JButton btnPrevious5;
	private JButton btnNext5;
	private JButton btnPrevious6;
	private JButton btnFinish;

	private JDialog dialog;
	private int stage = 1; // which screen, 1-6, we are on
	private Library library;

	private JLabel lblItLooksLike;
	private JLabel lblHi;
	private JLabel lblThisSetupProcess;
	private JLabel lblYourMusicWill;
	private JLabel lblYourMusicPreferences;
	private JTextField lblcusersjamesmusicplayer;
	private JLabel lblPleaseDontMove;
	private JLabel lblYourAudioFiles;
	private JCheckBox chckbxImportPlaylists;
	private JLabel lblNoAudioFiles;
	private JLabel lblFoundItunesLibrary;
	private JButton btnBrowseItunes;
	private JLabel lblItunesLocate;
	private JTextField txtMusicRoot;
	private JTextField txtMusicItunes;
	private JScrollPane scrlItunesTable;
	private JList listPlaylists;
	private JScrollPane scrollPane_1;
	private JButton btnSkipItunes;
	private JLabel lblBasicSettings_1;
	private JLabel lblIfYouUse;
	private JLabel lblYouCanAlso;
	private JButton btnBrowseMusic;
	private JButton btnSkipMusic;
	private JLabel lblAudioAnalysis;
	private JButton btnAnalyse;
	private JLabel lblThisMayTake;
	private JProgressBar progressBar;
	private JLabel lblSong;
	private JLabel lblThanksWereAll;
	private JLabel lblAFewInstructions;
	private JLabel lblPressDeleteTo;
	private JButton btnImportItunes;
	private JButton btnImportMusic;
	private JLabel lblLoadMoreItunes;
	private JPanel panel;
	private JLabel lblListeningPreferences;
	private JLabel lblTakesUpVery;
	private JLabel lblVBuild;
	private JLabel label;
	private JLabel lblDefaultSortOrder;
	private JCheckBox chckbxTryToDownload;
	private JLabel lblDefaultRepeatBehaviour;
	private JCheckBox chckbxEnableShortcuts;
	private JLabel lblImportFromItunes_2;
	private JLabel lblImportFromDisk;
	private JLabel lblAudioAnalyses;
	private JLabel lblSetupComplete;
	private JLabel lblToAdjustSettings;
	private JLabel lblFileMenu;
	private JList list;
	private JRadioButton rdbtnOne;
	private JRadioButton rdbtnRepeatAll;
	private JRadioButton rdbtnRepeatNone;
	private JPanel panel_1;
	private JPanel panel_2;
	private JLabel lblProcessingStart;
	private JLabel lblOf;
	private JLabel lblProcessingCount;
	private JLabel lblProcessingName;
	private JLabel lblTimeTaken;
	private JLabel lblTimeRemaining;
	private JLabel lblProcessingTime;
	private JLabel lblProcessingTimeLeft;
	private JLabel lblThisInformationWill;
	private JLabel lblPreferencesHaveBeen;
	private JLabel lblClickHereTo;
	private JList listTracks;
	private JLabel lblPlaylists;
	private JLabel lblPreview;
	private JLabel lblNoteThisWill;
	private JLabel label_3;
	private JLabel label_4;
	private JList listTracksDisk;
	private JLabel lblYouCanImport;
	private JLabel label_6;
	private JScrollPane scrollPane;

	/**
	 * Create the dialog.
	 */
	public Setup(Library library) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		dialog = this;
		this.library = library;
		addWindowListener(new ThisWindowListener());
		setResizable(false);
		setTitle("Music Factory Setup");
		setMinimumSize(new Dimension(800, 600));
		setModal(true);
		// TODO: this needs setting by caller
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setBounds(100, 100, 800, 600);
		setIconImage((new ImageIcon(this.getClass().getResource("/jk509/player/res/icon.png"))).getImage());

		splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		splitPane.setDividerLocation(0.5);
		getContentPane().add(splitPane, BorderLayout.CENTER);

		pnlSidebar = new JPanel();

		pnlSidebar.setBackground(new Color(248, 248, 248));
		splitPane.setLeftComponent(pnlSidebar);
		pnlSidebar.setPreferredSize(new Dimension(200, 400));
		pnlSidebar.setLayout(new BorderLayout(0, 0));

		lblbg = new JLabel();
		lblbg.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/setupbg.png")));
		lblbg.setMinimumSize(new Dimension(100, 200));
		pnlSidebar.add(lblbg, BorderLayout.SOUTH);

		pnlOverview = new JPanel();
		pnlOverview.setBackground(new Color(248, 248, 248));
		pnlOverview.setMinimumSize(new Dimension(200, 200));
		pnlSidebar.add(pnlOverview, BorderLayout.CENTER);
		pnlOverview.setLayout(null);

		lblBasicSettings = new JLabel("Basic settings");
		lblBasicSettings.setEnabled(false);
		lblBasicSettings.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		lblBasicSettings.setBounds(50, 72, 140, 16);
		pnlOverview.add(lblBasicSettings);

		lblImportFromItunes = new JLabel("Import from iTunes");
		lblImportFromItunes.setEnabled(false);
		lblImportFromItunes.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		lblImportFromItunes.setBounds(50, 112, 140, 16);
		pnlOverview.add(lblImportFromItunes);

		lblImportFromMy = new JLabel("Import from Disk");
		lblImportFromMy.setEnabled(false);
		lblImportFromMy.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		lblImportFromMy.setBounds(50, 152, 140, 16);
		pnlOverview.add(lblImportFromMy);

		lblAnalyseAudio = new JLabel("Audio analysis");
		lblAnalyseAudio.setEnabled(false);
		lblAnalyseAudio.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		lblAnalyseAudio.setBounds(50, 193, 140, 16);
		pnlOverview.add(lblAnalyseAudio);

		lblIntroduction = new JLabel("Introduction");
		lblIntroduction.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		lblIntroduction.setBounds(50, 33, 140, 16);
		pnlOverview.add(lblIntroduction);

		lblFinished = new JLabel("Finished");
		lblFinished.setEnabled(false);
		lblFinished.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		lblFinished.setBounds(50, 232, 140, 16);
		pnlOverview.add(lblFinished);

		blt5 = new JLabel();
		blt5.setEnabled(false);
		blt5.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/bullet.png")));
		blt5.setBounds(20, 184, 22, 32);
		pnlOverview.add(blt5);

		blt2 = new JLabel();
		blt2.setEnabled(false);
		blt2.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/bullet.png")));
		blt2.setBounds(20, 64, 22, 32);
		pnlOverview.add(blt2);

		blt1 = new JLabel();
		blt1.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/bullet.png")));
		blt1.setBounds(20, 24, 22, 32);
		pnlOverview.add(blt1);

		blt6 = new JLabel();
		blt6.setEnabled(false);
		blt6.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/bullet.png")));
		blt6.setBounds(20, 224, 22, 32);
		pnlOverview.add(blt6);

		blt4 = new JLabel();
		blt4.setEnabled(false);
		blt4.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/bullet.png")));
		blt4.setBounds(20, 144, 22, 32);
		pnlOverview.add(blt4);

		blt3 = new JLabel();
		blt3.setEnabled(false);
		blt3.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/bullet.png")));
		blt3.setBounds(20, 104, 22, 32);
		pnlOverview.add(blt3);

		pnlMain = new JPanel();
		pnlMain.setBackground(Color.WHITE);
		splitPane.setRightComponent(pnlMain);
		cardLayout = new CardLayout(0, 0);
		pnlMain.setLayout(cardLayout);

		pnl1 = new JPanel();
		pnl1.setBackground(Color.WHITE);
		pnlMain.add(pnl1, "name_178256116362810");
		pnl1.setLayout(new BorderLayout(5, 5));

		panel_11 = new JPanel();
		panel_11.setBackground(Color.WHITE);
		pnl1.add(panel_11, BorderLayout.CENTER);
		panel_11.setLayout(null);

		lblHi = new JLabel("Hi!");
		lblHi.setForeground(Color.GRAY);
		lblHi.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblHi.setBounds(36, 95, 141, 57);
		panel_11.add(lblHi);

		lblItLooksLike = new JLabel("It looks like this is the first time you've run Music Factory.");
		lblItLooksLike.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblItLooksLike.setBounds(36, 163, 500, 22);
		panel_11.add(lblItLooksLike);

		lblThisSetupProcess = new JLabel("This setup process will guide you through importing your music from other software.");
		lblThisSetupProcess.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblThisSetupProcess.setBounds(36, 196, 541, 22);
		panel_11.add(lblThisSetupProcess);

		lblYourMusicWill = new JLabel("Your music will be analysed, and then the software will be ready to start learning your");
		lblYourMusicWill.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblYourMusicWill.setBounds(36, 229, 541, 22);
		panel_11.add(lblYourMusicWill);

		panel = new JPanel();
		panel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBackground(Color.WHITE);
		panel.setBounds(36, 301, 521, 211);
		panel_11.add(panel);
		panel.setLayout(null);

		lblYourMusicPreferences = new JLabel("Your settings and music preference data will be stored in: ");
		lblYourMusicPreferences.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblYourMusicPreferences.setBounds(22, 30, 445, 17);
		panel.add(lblYourMusicPreferences);

		String homedir = System.getenv("user.home");
		if (homedir == null)
			homedir = System.getenv("USERPROFILE");
		lblcusersjamesmusicplayer = new JTextField("\"" + homedir + "\\Music Factory\"");
		lblcusersjamesmusicplayer.setEditable(false);
		lblcusersjamesmusicplayer.setBounds(22, 62, 485, 25);
		panel.add(lblcusersjamesmusicplayer);
		lblcusersjamesmusicplayer.setFont(new Font("Tahoma", Font.BOLD, 14));

		lblPleaseDontMove = new JLabel("Please don't move or change this folder or its contents.");
		lblPleaseDontMove.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPleaseDontMove.setBounds(22, 104, 485, 23);
		panel.add(lblPleaseDontMove);

		lblYourAudioFiles = new JLabel("Your audio files will not be moved or copied here, and the preference data");
		lblYourAudioFiles.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblYourAudioFiles.setBounds(22, 134, 485, 32);
		panel.add(lblYourAudioFiles);

		lblTakesUpVery = new JLabel("takes up very little space.");
		lblTakesUpVery.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTakesUpVery.setBounds(22, 158, 330, 25);
		panel.add(lblTakesUpVery);

		lblListeningPreferences = new JLabel("listening preferences.");
		lblListeningPreferences.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblListeningPreferences.setBounds(36, 252, 541, 22);
		panel_11.add(lblListeningPreferences);

		label = new JLabel();
		label.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/setupbg3.png")));
		label.setBounds(0, -23, 587, 153);
		panel_11.add(label);

		panel_12 = new JPanel();
		panel_12.setPreferredSize(new Dimension(10, 30));
		panel_12.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_12.setBackground(Color.WHITE);
		pnl1.add(panel_12, BorderLayout.SOUTH);
		panel_12.setLayout(new BorderLayout(0, 0));

		btnNext1 = new JButton("Next");
		btnNext1.setBackground(Color.WHITE);
		btnNext1.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNext1.addActionListener(new BtnNextActionListener());
		btnNext1.setHorizontalTextPosition(SwingConstants.LEADING);
		btnNext1.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/rarrow.png")));
		btnNext1.setPreferredSize(new Dimension(110, 25));
		panel_12.add(btnNext1, BorderLayout.EAST);

		lblVBuild = new JLabel("             v1.0.0 Build 1 01/02/2014 22:28");
		lblVBuild.setForeground(Color.LIGHT_GRAY);
		lblVBuild.setFont(new Font("Tahoma", Font.BOLD, 10));
		panel_12.add(lblVBuild, BorderLayout.CENTER);

		pnl2 = new JPanel();
		pnl2.setBackground(Color.WHITE);
		pnlMain.add(pnl2, "name_1782561163628101");
		pnl2.setLayout(new BorderLayout(5, 5));

		panel_21 = new JPanel();
		panel_21.setBackground(Color.WHITE);
		pnl2.add(panel_21, BorderLayout.CENTER);
		panel_21.setLayout(null);

		lblBasicSettings_1 = new JLabel("Basic settings");
		lblBasicSettings_1.setForeground(Color.GRAY);
		lblBasicSettings_1.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblBasicSettings_1.setBounds(60, 20, 176, 48);
		panel_21.add(lblBasicSettings_1);

		chckbxTryToDownload = new JCheckBox("Try to download album artwork automatically");
		chckbxTryToDownload.setEnabled(false);
		chckbxTryToDownload.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxTryToDownload.setBackground(Color.WHITE);
		chckbxTryToDownload.setBounds(60, 262, 379, 23);
		panel_21.add(chckbxTryToDownload);

		chckbxEnableShortcuts = new JCheckBox("Enable keyboard shortcuts");
		chckbxEnableShortcuts.setEnabled(false);
		chckbxEnableShortcuts.setSelected(true);
		chckbxEnableShortcuts.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxEnableShortcuts.setBackground(Color.WHITE);
		chckbxEnableShortcuts.setBounds(60, 459, 266, 23);
		panel_21.add(chckbxEnableShortcuts);

		ButtonGroup repeats = new ButtonGroup();

		panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(49, 87, 456, 152);
		panel_21.add(panel_1);
		panel_1.setLayout(null);

		lblDefaultSortOrder = new JLabel("Default sorting order for tracks:");
		lblDefaultSortOrder.setBounds(20, 26, 200, 23);
		panel_1.add(lblDefaultSortOrder);
		lblDefaultSortOrder.setFont(new Font("Tahoma", Font.PLAIN, 14));

		list = new JList();
		list.setEnabled(false);
		list.setBounds(242, 25, 176, 101);
		panel_1.add(list);
		list.setVisibleRowCount(5);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFont(new Font("Tahoma", Font.PLAIN, 14));
		list.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, Color.LIGHT_GRAY, Color.GRAY, null, null));
		list.setModel(new AbstractListModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] values = new String[] { "By track name", "By artist, then album", "By artist, then track name", "By genre, then artist", "By year, then artist" };

			public int getSize() {
				return values.length;
			}

			public Object getElementAt(int index) {
				return values[index];
			}
		});
		list.setSelectedIndices(new int[] { 1 });

		panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_2.setBackground(Color.WHITE);
		panel_2.setBounds(49, 311, 456, 120);
		panel_21.add(panel_2);

		rdbtnOne = new JRadioButton("Repeat one");
		rdbtnOne.setEnabled(false);
		rdbtnOne.setBounds(20, 65, 109, 23);
		panel_2.add(rdbtnOne);
		rdbtnOne.setBackground(Color.WHITE);
		repeats.add(rdbtnOne);

		rdbtnRepeatAll = new JRadioButton("Repeat all");
		rdbtnRepeatAll.setEnabled(false);
		rdbtnRepeatAll.setBounds(135, 65, 109, 23);
		panel_2.add(rdbtnRepeatAll);
		rdbtnRepeatAll.setSelected(true);
		rdbtnRepeatAll.setBackground(Color.WHITE);
		repeats.add(rdbtnRepeatAll);

		rdbtnRepeatNone = new JRadioButton("Repeat none");
		rdbtnRepeatNone.setEnabled(false);
		rdbtnRepeatNone.setBounds(250, 65, 109, 23);
		panel_2.add(rdbtnRepeatNone);
		rdbtnRepeatNone.setBackground(Color.WHITE);
		repeats.add(rdbtnRepeatNone);

		lblDefaultRepeatBehaviour = new JLabel("Default repeat behaviour for songs:");
		lblDefaultRepeatBehaviour.setBounds(20, 24, 217, 23);
		panel_2.add(lblDefaultRepeatBehaviour);
		lblDefaultRepeatBehaviour.setFont(new Font("Tahoma", Font.PLAIN, 14));

		panel_22 = new JPanel();
		panel_22.setPreferredSize(new Dimension(10, 30));
		panel_22.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_22.setBackground(Color.WHITE);
		pnl2.add(panel_22, BorderLayout.SOUTH);
		panel_22.setLayout(new BorderLayout(0, 0));

		btnPrevious2 = new JButton("Previous");
		btnPrevious2.setBackground(Color.WHITE);
		btnPrevious2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnPrevious2.addActionListener(new BtnPrevActionListener());
		btnPrevious2.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/larrow.png")));
		btnPrevious2.setPreferredSize(new Dimension(110, 25));
		btnPrevious2.setMaximumSize(new Dimension(73, 20));
		panel_22.add(btnPrevious2, BorderLayout.WEST);

		btnNext2 = new JButton("Next");
		btnNext2.setBackground(Color.WHITE);
		btnNext2.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNext2.addActionListener(new BtnNextActionListener());
		btnNext2.setHorizontalTextPosition(SwingConstants.LEADING);
		btnNext2.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/rarrow.png")));
		btnNext2.setPreferredSize(new Dimension(110, 25));
		panel_22.add(btnNext2, BorderLayout.EAST);

		pnl3 = new JPanel();
		pnl3.setBackground(Color.WHITE);
		pnlMain.add(pnl3, "name_1782561163628102");
		pnl3.setLayout(new BorderLayout(5, 5));

		panel_31 = new JPanel();
		panel_31.setBackground(Color.WHITE);
		pnl3.add(panel_31, BorderLayout.CENTER);
		panel_31.setLayout(null);

		lblNoAudioFiles = new JLabel("(Audio files will not be copied)");
		lblNoAudioFiles.setBounds(178, 141, 191, 14);
		panel_31.add(lblNoAudioFiles);

		txtMusicItunes = new JTextField();
		txtMusicItunes.setBounds(30, 101, 445, 21);
		homedir = System.getenv("user.home");
		if (homedir == null)
			homedir = System.getenv("USERPROFILE");
		txtMusicItunes.setText(homedir + "\\Music\\iTunes");
		panel_31.add(txtMusicItunes);
		txtMusicItunes.setColumns(10);

		lblItunesLocate = new JLabel("Please locate your itunes folder:");
		lblItunesLocate.setBounds(30, 76, 170, 14);
		panel_31.add(lblItunesLocate);

		String path = homedir + "\\Music\\iTunes\\iTunes Music Library.xml";
		if ((new File(path)).exists()) {
			lblFoundItunesLibrary = new JLabel("Found an iTunes library: " + path);
			lblFoundItunesLibrary.setBounds(30, 76, 500, 14);
			lblFoundItunesLibrary.setForeground(new Color(0, 128, 0));
			txtMusicItunes.setText(path);
			lblItunesLocate.setVisible(false);
			panel_31.add(lblFoundItunesLibrary);
		}

		btnBrowseItunes = new JButton("Browse...");
		btnBrowseItunes.addActionListener(new BtnBrowseItunesActionListener());
		btnBrowseItunes.setBackground(Color.WHITE);
		btnBrowseItunes.setBounds(481, 100, 79, 23);
		panel_31.add(btnBrowseItunes);

		btnImportItunes = new JButton("Import Music");
		btnImportItunes.setBackground(Color.WHITE);
		btnImportItunes.setForeground(new Color(0, 128, 0));
		btnImportItunes.setBounds(30, 133, 136, 30);
		btnImportItunes.addActionListener(new BtnImportItunesActionListener());
		btnImportItunes.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel_31.add(btnImportItunes);

		scrlItunesTable = new JScrollPane();
		scrlItunesTable.setToolTipText("Select one or more tracks and press delete to remove them");
		scrlItunesTable.setBounds(30, 250, 339, 240);
		scrlItunesTable.setPreferredSize(new Dimension(100, 100));
		panel_31.add(scrlItunesTable);

		listTracks = new JList();
		listTracks.setToolTipText("Select one or more tracks and press delete to remove them");
		scrlItunesTable.setViewportView(listTracks);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setToolTipText("Select one or more playlists and press delete to remove them");
		scrollPane_1.setBounds(390, 250, 170, 240);
		scrollPane_1.setPreferredSize(new Dimension(50, 50));
		panel_31.add(scrollPane_1);

		listPlaylists = new JList();
		listPlaylists.setToolTipText("Select one or more playlists and press delete to remove them");
		scrollPane_1.setViewportView(listPlaylists);
		/*
		 * listPlaylists.setModel(new AbstractListModel() { String[] values =
		 * new String[] {}; public int getSize() { return values.length; }
		 * public Object getElementAt(int index) { return values[index]; } });
		 */

		chckbxImportPlaylists = new JCheckBox("Import playlists");
		chckbxImportPlaylists.setSelected(true);
		chckbxImportPlaylists.setBackground(Color.WHITE);
		chckbxImportPlaylists.setBounds(386, 492, 143, 23);
		panel_31.add(chckbxImportPlaylists);

		lblPressDeleteTo = new JLabel("Tracks");
		lblPressDeleteTo.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		lblPressDeleteTo.setBounds(32, 225, 153, 23);
		panel_31.add(lblPressDeleteTo);

		lblLoadMoreItunes = new JLabel("You can import additional tracks from iTunes later if needed.");
		lblLoadMoreItunes.setBounds(30, 496, 289, 14);
		panel_31.add(lblLoadMoreItunes);

		lblImportFromItunes_2 = new JLabel("Import from iTunes (optional)");
		lblImportFromItunes_2.setForeground(Color.GRAY);
		lblImportFromItunes_2.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblImportFromItunes_2.setBounds(60, 20, 335, 48);
		panel_31.add(lblImportFromItunes_2);

		btnSkipItunes = new JButton("Skip");
		btnSkipItunes.setBackground(Color.WHITE);
		btnSkipItunes.setBounds(470, 33, 90, 24);
		panel_31.add(btnSkipItunes);
		btnSkipItunes.setFont(new Font("Tahoma", Font.BOLD, 11));

		lblPlaylists = new JLabel("Playlists");
		lblPlaylists.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		lblPlaylists.setBounds(390, 225, 153, 23);
		panel_31.add(lblPlaylists);

		lblPreview = new JLabel("Preview:");
		lblPreview.setForeground(Color.GRAY);
		lblPreview.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPreview.setBounds(32, 174, 335, 48);
		panel_31.add(lblPreview);
		btnSkipItunes.addActionListener(new BtnSkipActionListener());

		panel_32 = new JPanel();
		panel_32.setPreferredSize(new Dimension(10, 30));
		panel_32.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_32.setBackground(Color.WHITE);
		pnl3.add(panel_32, BorderLayout.SOUTH);
		panel_32.setLayout(new BorderLayout(0, 0));

		btnPrevious3 = new JButton("Previous");
		btnPrevious3.setBackground(Color.WHITE);
		btnPrevious3.addActionListener(new BtnPrevActionListener());
		btnPrevious3.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/larrow.png")));
		btnPrevious3.setPreferredSize(new Dimension(110, 25));
		btnPrevious3.setMaximumSize(new Dimension(73, 20));
		panel_32.add(btnPrevious3, BorderLayout.WEST);

		btnNext3 = new JButton("Next");
		btnNext3.setBackground(Color.WHITE);
		btnNext3.setEnabled(false);
		btnNext3.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNext3.addActionListener(new BtnNextActionListener());
		btnNext3.setHorizontalTextPosition(SwingConstants.LEADING);
		btnNext3.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/rarrow.png")));
		btnNext3.setPreferredSize(new Dimension(110, 25));
		panel_32.add(btnNext3, BorderLayout.EAST);

		pnl4 = new JPanel();
		pnl4.setBackground(Color.WHITE);
		pnlMain.add(pnl4, "name_1782561163628103");
		pnl4.setLayout(new BorderLayout(5, 5));

		panel_41 = new JPanel();
		panel_41.setBackground(Color.WHITE);
		pnl4.add(panel_41, BorderLayout.CENTER);
		panel_41.setLayout(null);

		btnSkipMusic = new JButton("Skip");
		btnSkipMusic.setBackground(Color.WHITE);
		btnSkipMusic.setBounds(470, 33, 90, 24);
		btnSkipMusic.addActionListener(new BtnSkip_1ActionListener());
		btnSkipMusic.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_41.add(btnSkipMusic);

		lblIfYouUse = new JLabel("If you use another player such as \"Windows Media Player\", your music may be stored in \"My Music\".");
		lblIfYouUse.setBounds(60, 84, 517, 14);
		panel_41.add(lblIfYouUse);

		lblYouCanAlso = new JLabel("You can also select any location where music is stored on your computer.");
		lblYouCanAlso.setBounds(60, 104, 350, 14);
		panel_41.add(lblYouCanAlso);

		txtMusicRoot = new JTextField();
		txtMusicRoot.setBounds(60, 129, 415, 21);
		txtMusicRoot.setText(homedir + "\\Music");
		panel_41.add(txtMusicRoot);
		txtMusicRoot.setColumns(10);

		btnBrowseMusic = new JButton("Browse...");
		btnBrowseMusic.addActionListener(new BtnBrowseMusicActionListener());
		btnBrowseMusic.setBackground(Color.WHITE);
		btnBrowseMusic.setBounds(481, 128, 79, 23);
		panel_41.add(btnBrowseMusic);

		btnImportMusic = new JButton("Import Music");
		btnImportMusic.setBackground(Color.WHITE);
		btnImportMusic.setForeground(new Color(0, 128, 0));
		btnImportMusic.setBounds(60, 161, 138, 34);
		btnImportMusic.addActionListener(new BtnImportMusicActionListener());
		btnImportMusic.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel_41.add(btnImportMusic);

		lblImportFromDisk = new JLabel("Import from Disk (optional)");
		lblImportFromDisk.setForeground(Color.GRAY);
		lblImportFromDisk.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblImportFromDisk.setBounds(60, 20, 365, 48);
		panel_41.add(lblImportFromDisk);

		lblNoteThisWill = new JLabel("Note: this will replace any music imported from iTunes");
		lblNoteThisWill.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNoteThisWill.setForeground(Color.RED);
		lblNoteThisWill.setBounds(217, 180, 343, 15);
		panel_41.add(lblNoteThisWill);

		label_3 = new JLabel("Tracks");
		label_3.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		label_3.setBounds(62, 239, 153, 23);
		panel_41.add(label_3);

		label_4 = new JLabel("Preview:");
		label_4.setForeground(Color.GRAY);
		label_4.setFont(new Font("Tahoma", Font.BOLD, 16));
		label_4.setBounds(60, 208, 335, 34);
		panel_41.add(label_4);

		lblYouCanImport = new JLabel("You can import additional tracks from disk later if needed.");
		lblYouCanImport.setBounds(60, 510, 289, 14);
		panel_41.add(lblYouCanImport);

		label_6 = new JLabel("(Audio files will not be copied)");
		label_6.setBounds(217, 161, 191, 14);
		panel_41.add(label_6);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(61, 265, 450, 238);
		panel_41.add(scrollPane);

		listTracksDisk = new JList();
		scrollPane.setViewportView(listTracksDisk);
		listTracksDisk.setToolTipText("Select one or more tracks and press delete to remove them");

		panel_42 = new JPanel();
		panel_42.setPreferredSize(new Dimension(10, 30));
		panel_42.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_42.setBackground(Color.WHITE);
		pnl4.add(panel_42, BorderLayout.SOUTH);
		panel_42.setLayout(new BorderLayout(0, 0));

		btnPrevious4 = new JButton("Previous");
		btnPrevious4.setBackground(Color.WHITE);
		btnPrevious4.addActionListener(new BtnPrevActionListener());
		btnPrevious4.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/larrow.png")));
		btnPrevious4.setPreferredSize(new Dimension(110, 25));
		btnPrevious4.setMaximumSize(new Dimension(73, 20));
		panel_42.add(btnPrevious4, BorderLayout.WEST);

		btnNext4 = new JButton("Next");
		btnNext4.setBackground(Color.WHITE);
		btnNext4.setEnabled(false);
		btnNext4.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNext4.addActionListener(new BtnNextActionListener());
		btnNext4.setHorizontalTextPosition(SwingConstants.LEADING);
		btnNext4.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/rarrow.png")));
		btnNext4.setPreferredSize(new Dimension(110, 25));
		panel_42.add(btnNext4, BorderLayout.EAST);

		pnl5 = new JPanel();
		pnl5.setBackground(Color.WHITE);
		pnlMain.add(pnl5, "name_1782561163628104");
		pnl5.setLayout(new BorderLayout(5, 5));

		panel_51 = new JPanel();
		panel_51.setBackground(Color.WHITE);
		pnl5.add(panel_51, BorderLayout.CENTER);
		panel_51.setLayout(null);

		lblAudioAnalysis = new JLabel("The tracks being imported will now be analysed to determine their musical 'feel'.");
		lblAudioAnalysis.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAudioAnalysis.setBounds(60, 86, 503, 23);
		panel_51.add(lblAudioAnalysis);

		btnAnalyse = new JButton("Analyse");
		btnAnalyse.setBackground(Color.WHITE);
		btnAnalyse.setForeground(new Color(0, 128, 0));
		btnAnalyse.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnAnalyse.setBounds(60, 272, 107, 28);
		btnAnalyse.addActionListener(new BtnGoActionListener());
		panel_51.add(btnAnalyse);

		lblThisMayTake = new JLabel("This may take several minutes. Please wait...");
		lblThisMayTake.setVisible(false);
		lblThisMayTake.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblThisMayTake.setBounds(60, 322, 367, 23);
		panel_51.add(lblThisMayTake);

		progressBar = new JProgressBar();
		progressBar.setBounds(60, 386, 480, 28);
		panel_51.add(progressBar);

		lblSong = new JLabel("Processing track");
		lblSong.setBounds(60, 431, 78, 14);
		panel_51.add(lblSong);

		lblAudioAnalyses = new JLabel("Audio analysis");
		lblAudioAnalyses.setForeground(Color.GRAY);
		lblAudioAnalyses.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblAudioAnalyses.setBounds(60, 20, 270, 48);
		panel_51.add(lblAudioAnalyses);

		lblProcessingStart = new JLabel("0");
		lblProcessingStart.setBounds(148, 431, 38, 14);
		panel_51.add(lblProcessingStart);

		lblOf = new JLabel("of");
		lblOf.setBounds(190, 431, 23, 14);
		panel_51.add(lblOf);

		lblProcessingCount = new JLabel("0");
		lblProcessingCount.setBounds(223, 431, 46, 14);
		panel_51.add(lblProcessingCount);

		lblProcessingName = new JLabel("");
		lblProcessingName.setBounds(60, 456, 503, 23);
		panel_51.add(lblProcessingName);

		lblTimeTaken = new JLabel("Time taken:");
		lblTimeTaken.setBounds(60, 490, 67, 14);
		panel_51.add(lblTimeTaken);

		lblTimeRemaining = new JLabel("Time remaining:");
		lblTimeRemaining.setBounds(231, 490, 99, 14);
		panel_51.add(lblTimeRemaining);

		lblProcessingTime = new JLabel("0m 0s");
		lblProcessingTime.setBounds(129, 490, 46, 14);
		panel_51.add(lblProcessingTime);

		lblProcessingTimeLeft = new JLabel("0m 0s");
		lblProcessingTimeLeft.setBounds(318, 490, 46, 14);
		panel_51.add(lblProcessingTimeLeft);

		lblThisInformationWill = new JLabel("This information will be used to help create smart playlists once your listening");
		lblThisInformationWill.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblThisInformationWill.setBounds(60, 123, 503, 23);
		panel_51.add(lblThisInformationWill);

		lblPreferencesHaveBeen = new JLabel("preferences have been learned.");
		lblPreferencesHaveBeen.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPreferencesHaveBeen.setBounds(60, 145, 503, 23);
		panel_51.add(lblPreferencesHaveBeen);

		lblClickHereTo = new JLabel("Click here to begin analysis:");
		lblClickHereTo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblClickHereTo.setBounds(60, 231, 291, 23);
		panel_51.add(lblClickHereTo);

		panel_52 = new JPanel();
		panel_52.setPreferredSize(new Dimension(10, 30));
		panel_52.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_52.setBackground(Color.WHITE);
		pnl5.add(panel_52, BorderLayout.SOUTH);
		panel_52.setLayout(new BorderLayout(0, 0));

		btnPrevious5 = new JButton("Previous");
		btnPrevious5.setBackground(Color.WHITE);
		btnPrevious5.addActionListener(new BtnPrevActionListener());
		btnPrevious5.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/larrow.png")));
		btnPrevious5.setPreferredSize(new Dimension(110, 25));
		btnPrevious5.setMaximumSize(new Dimension(73, 20));
		panel_52.add(btnPrevious5, BorderLayout.WEST);

		btnNext5 = new JButton("Next");
		btnNext5.setBackground(Color.WHITE);
		btnNext5.setEnabled(false);
		btnNext5.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNext5.addActionListener(new BtnNextActionListener());
		btnNext5.setHorizontalTextPosition(SwingConstants.LEADING);
		btnNext5.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/rarrow.png")));
		btnNext5.setPreferredSize(new Dimension(110, 25));
		panel_52.add(btnNext5, BorderLayout.EAST);

		pnl6 = new JPanel();
		pnl6.setBackground(Color.WHITE);
		pnlMain.add(pnl6, "name_1782561163628105");
		pnl6.setLayout(new BorderLayout(5, 5));

		panel_61 = new JPanel();
		panel_61.setBackground(Color.WHITE);
		pnl6.add(panel_61, BorderLayout.CENTER);
		panel_61.setLayout(null);

		lblThanksWereAll = new JLabel("Thanks, we're all done here. ");
		lblThanksWereAll.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblThanksWereAll.setBounds(57, 79, 270, 28);
		panel_61.add(lblThanksWereAll);

		lblAFewInstructions = new JLabel("Click 'finish' below to start playing music, or navigate back to a previous page");
		lblAFewInstructions.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAFewInstructions.setBounds(57, 126, 520, 28);
		panel_61.add(lblAFewInstructions);

		lblSetupComplete = new JLabel("Setup complete");
		lblSetupComplete.setForeground(Color.GRAY);
		lblSetupComplete.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblSetupComplete.setBounds(60, 20, 270, 48);
		panel_61.add(lblSetupComplete);

		lblToAdjustSettings = new JLabel("to adjust settings. Import more music later using the import options in the");
		lblToAdjustSettings.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblToAdjustSettings.setBounds(57, 153, 520, 28);
		panel_61.add(lblToAdjustSettings);

		lblFileMenu = new JLabel("'File' menu.");
		lblFileMenu.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblFileMenu.setBounds(57, 181, 520, 28);
		panel_61.add(lblFileMenu);

		panel_62 = new JPanel();
		panel_62.setPreferredSize(new Dimension(10, 30));
		panel_62.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_62.setBackground(Color.WHITE);
		pnl6.add(panel_62, BorderLayout.SOUTH);
		panel_62.setLayout(new BorderLayout(0, 0));

		btnPrevious6 = new JButton("Previous");
		btnPrevious6.setBackground(Color.WHITE);
		btnPrevious6.addActionListener(new BtnPrevActionListener());
		btnPrevious6.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/larrow.png")));
		btnPrevious6.setPreferredSize(new Dimension(110, 25));
		btnPrevious6.setMaximumSize(new Dimension(73, 20));
		panel_62.add(btnPrevious6, BorderLayout.WEST);

		btnFinish = new JButton("Finish");
		btnFinish.setBackground(Color.WHITE);
		btnFinish.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnFinish.addActionListener(new BtnFinishActionListener());
		btnFinish.setHorizontalTextPosition(SwingConstants.LEADING);
		btnFinish.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/tick.png")));
		btnFinish.setPreferredSize(new Dimension(110, 25));
		panel_62.add(btnFinish, BorderLayout.EAST);

	}

	private void UpdateTrackDisplay() {
		if (stage == 3) {
			// import from itunes
			listTracks.setModel(new AbstractListModel() {
				private static final long serialVersionUID = 1L;

				@Override
				public int getSize() {
					return library.getPlaylists().get(0).size();
				}

				@Override
				public Object getElementAt(int arg0) {
					Song track = library.getPlaylists().get(0).get(arg0);
					return track.getName() + " - " + track.getAlbum() + " - " + track.getArtist();
				}
			});
			listPlaylists.setModel(new AbstractListModel() {
				private static final long serialVersionUID = 1L;

				@Override
				public int getSize() {
					return library.getPlaylists().size() - MusicPlayer.FIXED_PLAYLIST_ELEMENTS;
				}

				@Override
				public Object getElementAt(int index) {
					return library.getPlaylists().get(index - MusicPlayer.FIXED_PLAYLIST_ELEMENTS).getName();
				}
			});
			listTracks.repaint();
			listPlaylists.repaint();
		} else if (stage == 4) {
			// import from disk
			listTracksDisk.setModel(new AbstractListModel() {
				private static final long serialVersionUID = 1L;

				@Override
				public int getSize() {
					return library.getPlaylists().get(0).size();
				}

				@Override
				public Object getElementAt(int index) {
					Song track = library.getPlaylists().get(0).get(index);
					return track.getName() + " - " + track.getAlbum() + " - " + track.getArtist();
				}
			});
			listTracksDisk.repaint();
		}
	}

	private class BtnNextActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cardLayout.next(pnlMain);
			stage++;
			UpdateSidebar();
		}
	}

	private class BtnPrevActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cardLayout.previous(pnlMain);
			stage--;
			UpdateSidebar();
		}
	}

	private class BtnFinishActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// save()
			dispose();
		}
	}

	private class BtnSkipActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cardLayout.next(pnlMain);
			stage++;
			UpdateSidebar();
		}
	}

	private class BtnGoActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			btnNext5.setEnabled(true);
			lblThisMayTake.setVisible(true);
			btnAnalyse.setEnabled(false);
			btnAnalyse.setForeground(Color.GRAY);
			progressBar.setValue(progressBar.getMaximum());
			if (library.size() > 0 && library.getPlaylists().get(0).size() > 0) {
				lblProcessingCount.setText(Integer.toString(library.getPlaylists().get(0).size()));
				lblProcessingName.setText(library.getPlaylists().get(0).get(0).getName() + " - " + library.getPlaylists().get(0).get(0).getAlbum() + " - " + library.getPlaylists().get(0).get(0).getArtist());
			}
		}
	}

	private class BtnSkip_1ActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cardLayout.next(pnlMain);
			stage++;
			UpdateSidebar();
		}
	}

	private class BtnImportMusicActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {

			btnImportMusic.setEnabled(false);
			btnImportMusic.setText("Importing...");
			btnImportMusic.invalidate();
			btnImportMusic.repaint();
			// dialog.invalidate(); dialog.repaint();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					String folder = txtMusicRoot.getText();
					FileScanner parser = new FileScanner();
					parser.setPath(folder);
					parser.setValid(true);
					if (Import(parser)) {
						btnImportMusic.setEnabled(false);
						btnImportMusic.setText("Done");
						btnNext4.setEnabled(true);
					}
					UpdateTrackDisplay();
				}
			});

		}
	}

	private class BtnImportItunesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			String loc = txtMusicItunes.getText();
			LibraryParser parser = new ItunesParser();
			parser.setPath(loc);
			parser.setValid(true);
			if (Import(parser)) {
				btnImportItunes.setEnabled(false);
				btnNext3.setEnabled(true);
			}
			UpdateTrackDisplay();
		}
	}

	private class ThisWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			if (JOptionPane.showConfirmDialog(dialog, "The music player will not be useful until setup has completed.\nAre you sure you want to exit?", "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				dispose();
		}
	}

	private class BtnBrowseMusicActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();

			String startat = "";
			// choose initial folder (first check textfield, otherwise try user
			// home dir)
			try {
				if ((new File(txtMusicRoot.getText())).exists() && (new File(txtMusicRoot.getText())).isDirectory())
					startat = (new File(txtMusicRoot.getText())).getPath();
				else
					startat = "";
			} catch (Exception e2) {
				startat = "";
			}

			if (startat.equals("")) {
				startat = System.getenv("user.home");
				if (startat == null || startat.equals(""))
					startat = System.getenv("USERPROFILE");
				if (startat == null || startat.equals(""))
					startat = ".";
			}
			chooser.setCurrentDirectory(new java.io.File(startat));
			chooser.setDialogTitle("Select music folder");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			// disable the "All files" option.
			chooser.setAcceptAllFileFilterUsed(false);

			int result = chooser.showOpenDialog(dialog);
			if (result == JFileChooser.APPROVE_OPTION) {
				txtMusicRoot.setText(chooser.getSelectedFile().getPath());
			} else {
				// System.out.println("No Selection ");
				// System.out.println(result);
			}
		}
	}

	private class BtnBrowseItunesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new OBJfilter());
			// fc.type = 1;

			String startat = "";
			// choose initial folder (first check textfield, otherwise try user
			// home dir)
			try {
				if (new File((new File(txtMusicItunes.getText())).getParent()).exists())
					startat = (new File(txtMusicItunes.getText())).getParent();
				else
					startat = "";
			} catch (Exception e2) {
				startat = "";
			}

			fc.setCurrentDirectory(new File(startat));

			int returnVal = fc.showOpenDialog(dialog);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				txtMusicItunes.setText(file.getPath());
			} else {
				// System.out.println("not loaded");
			}
		}
	}

	private boolean Import(LibraryParser parser) {

		if (!parser.isValid())
			return false;

		parser.run();

		// TODO: sort
		library.setPlaylist(0, parser.getTracks());
		library.setPlaylist(1, parser.getTracks());
		library.setPlaylist(2, parser.getTracks());

		return true;

		// TODO: import playlists, artwork
	}

	public void UpdateSidebar() {
		switch (stage) {
		case 1:
			blt1.setEnabled(true);
			blt2.setEnabled(false);
			blt3.setEnabled(false);
			blt4.setEnabled(false);
			blt5.setEnabled(false);
			blt6.setEnabled(false);
			lblIntroduction.setEnabled(true);
			lblBasicSettings.setEnabled(false);
			lblImportFromItunes.setEnabled(false);
			lblImportFromMy.setEnabled(false);
			lblAnalyseAudio.setEnabled(false);
			lblFinished.setEnabled(false);
			break;
		case 2:
			blt1.setEnabled(false);
			blt2.setEnabled(true);
			blt3.setEnabled(false);
			blt4.setEnabled(false);
			blt5.setEnabled(false);
			blt6.setEnabled(false);
			lblIntroduction.setEnabled(false);
			lblBasicSettings.setEnabled(true);
			lblImportFromItunes.setEnabled(false);
			lblImportFromMy.setEnabled(false);
			lblAnalyseAudio.setEnabled(false);
			lblFinished.setEnabled(false);
			break;
		case 3:
			blt1.setEnabled(false);
			blt2.setEnabled(false);
			blt3.setEnabled(true);
			blt4.setEnabled(false);
			blt5.setEnabled(false);
			blt6.setEnabled(false);
			lblIntroduction.setEnabled(false);
			lblBasicSettings.setEnabled(false);
			lblImportFromItunes.setEnabled(true);
			lblImportFromMy.setEnabled(false);
			lblAnalyseAudio.setEnabled(false);
			lblFinished.setEnabled(false);
			break;
		case 4:
			blt1.setEnabled(false);
			blt2.setEnabled(false);
			blt3.setEnabled(false);
			blt4.setEnabled(true);
			blt5.setEnabled(false);
			blt6.setEnabled(false);
			lblIntroduction.setEnabled(false);
			lblBasicSettings.setEnabled(false);
			lblImportFromItunes.setEnabled(false);
			lblImportFromMy.setEnabled(true);
			lblAnalyseAudio.setEnabled(false);
			lblFinished.setEnabled(false);
			break;
		case 5:
			blt1.setEnabled(false);
			blt2.setEnabled(false);
			blt3.setEnabled(false);
			blt4.setEnabled(false);
			blt5.setEnabled(true);
			blt6.setEnabled(false);
			lblIntroduction.setEnabled(false);
			lblBasicSettings.setEnabled(false);
			lblImportFromItunes.setEnabled(false);
			lblImportFromMy.setEnabled(false);
			lblAnalyseAudio.setEnabled(true);
			lblFinished.setEnabled(false);
			break;
		case 6:
			blt1.setEnabled(false);
			blt2.setEnabled(false);
			blt3.setEnabled(false);
			blt4.setEnabled(false);
			blt5.setEnabled(false);
			blt6.setEnabled(true);
			lblIntroduction.setEnabled(false);
			lblBasicSettings.setEnabled(false);
			lblImportFromItunes.setEnabled(false);
			lblImportFromMy.setEnabled(false);
			lblAnalyseAudio.setEnabled(false);
			lblFinished.setEnabled(true);
			break;
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

	class OBJfilter extends FileFilter {

		public OBJfilter() {
			super();
		}

		@Override
		public String getDescription() {
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
				if (extension.equals("ser") || extension.equals("xml")) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}

	}
}
