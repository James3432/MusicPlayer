package jk509.player;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import jk509.player.clustering.SongCluster;
import jk509.player.core.FileScanner;
import jk509.player.core.ItunesParser;
import jk509.player.core.Library;
import jk509.player.core.LibraryParser;
import jk509.player.core.Playlist;
import jk509.player.core.Song;
import jk509.player.core.StaticMethods;
import jk509.player.gui.GuiUpdaterAdapter;
import jk509.player.logging.Logger;
import jk509.player.logging.Logger.LogType;
import christophedelory.playlist.SpecificPlaylist;
import christophedelory.playlist.SpecificPlaylistFactory;

import com.worldsworstsoftware.itunes.ItunesLibrary;
import com.worldsworstsoftware.itunes.ItunesPlaylist;
import com.worldsworstsoftware.itunes.ItunesTrack;
import com.worldsworstsoftware.itunes.parser.ItunesLibraryParser;

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
	public JButton btnNext5;
	private JButton btnPrevious6;
	private JButton btnFinish;

	private JDialog dialog;
	private int stage = 1; // which screen, 1-6, we are on
	public Library library;
	private Boolean[] success;
	private List<Playlist> itunesPlaylists;
	private List<String> genericPlaylists;
	private JFileChooser playlistChooser;
	private JFileChooser musicChooser;

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
	private JLabel lblBasicSettings_1;
	private JLabel lblIfYouUse;
	private JLabel lblYouCanAlso;
	private JButton btnBrowseMusic;
	private JLabel lblAudioAnalysis;
	public JButton btnAnalyse;
	public JLabel lblThisMayTake;
	public JProgressBar progressBar;
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
	private JCheckBox chckbxUploadData;
	private JCheckBox chckbxAgree;
	private JLabel lblImportFromItunes_2;
	private JLabel lblImportFromDisk;
	private JLabel lblAudioAnalyses;
	private JLabel lblSetupComplete;
	private JLabel lblToAdjustSettings;
	private JLabel lblFileMenu;
	public JLabel lblProcessingStart;
	private JLabel lblOf;
	public JLabel lblProcessingCount;
	public JLabel lblProcessingName;
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
	private JLabel label_3;
	private JLabel label_4;
	private JList listTracksDisk;
	private JLabel lblYouCanImport;
	private JLabel label_6;
	private JScrollPane scrollPane;
	private JTextArea txtrDataUp;
	private JTextArea txtrAgreement;
	private JScrollPane scrollPaneAgree;
	private JTextArea txtrToProceedYou;
	private JLabel lblAfterSettingUp;
	private JLabel lblPleaseTryTo;
	private JLabel lblNextTrackWill;
	private JLabel lblNewLabel;
	private JLabel lblSmartModeIs;
	private JLabel lblTheBrainIcon;
	private JPanel panel_1;
	public JProgressBar fileProgressBar;
	private JLabel lblCores;
	private JLabel lblLoadIcon;
	private JLabel lblLoadIcon1;
	private JButton btnImportPlaylistFiles;
	private JLabel lblOtherPlaylists;
	private JScrollPane scrollPane_2;
	private JList listPlaylistsDisk;
	private JLabel lblFoundCount;

	/**
	 * Create the dialog.
	 */
	public Setup(Library library, Boolean[] success) {
		super((Frame)null,Dialog.ModalityType.TOOLKIT_MODAL);
		setModalityType(ModalityType.APPLICATION_MODAL);
		dialog = this;
		this.library = library;
		this.success = success;
		itunesPlaylists = new ArrayList<Playlist>();
		genericPlaylists = new ArrayList<String>();
		addWindowListener(new ThisWindowListener());
		setResizable(false);
		setTitle("Music Factory Setup");
		setMinimumSize(new Dimension(800, 600));
		setModal(true);
		// this is set by caller
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setBounds(100, 100, 800, 600);
		List<Image> iconArray = new ArrayList<Image>();
		iconArray.add(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon-16.png")));
		iconArray.add(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon-32.png")));
		setIconImages(iconArray);
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

		lblBasicSettings = new JLabel("Usage agreement");
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

		lblcusersjamesmusicplayer = new JTextField("\"" + StaticMethods.getHomeDir() + "\\Music Factory\"");
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

		lblVBuild = new JLabel("");
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

		lblBasicSettings_1 = new JLabel("Usage agreement");
		lblBasicSettings_1.setForeground(Color.GRAY);
		lblBasicSettings_1.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblBasicSettings_1.setBounds(60, 20, 266, 48);
		panel_21.add(lblBasicSettings_1);

		chckbxUploadData = new JCheckBox("Upload usage data automatically (requires internet connection)");
		chckbxUploadData.addActionListener(new ChckbxUploadDataActionListener());
		chckbxUploadData.setSelected(true);
		chckbxUploadData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxUploadData.setBackground(Color.WHITE);
		chckbxUploadData.setBounds(60, 494, 480, 23);
		panel_21.add(chckbxUploadData);

		chckbxAgree = new JCheckBox("I agree to the above terms");
		chckbxAgree.setMnemonic('a');
		chckbxAgree.setMnemonic(KeyEvent.VK_A);
		chckbxAgree.addActionListener(new ChckbxEnableShortcutsActionListener());
		chckbxAgree.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxAgree.setBackground(Color.WHITE);
		chckbxAgree.setBounds(60, 377, 266, 23);
		panel_21.add(chckbxAgree);

		txtrDataUp = new JTextArea();
		txtrDataUp.setEditable(false);
		txtrDataUp.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtrDataUp.setWrapStyleWord(true);
		txtrDataUp.setLineWrap(true);
		txtrDataUp.setText("Whilst taking part in this study, you will be allowing us to collect limited amounts of information about your usage of the media player, such as how often you skip tracks. All such data will be collected anonymously. If you would prefer to manually submit the relevant data files by email, please untick this box and contact the researcher as soon as possible:");
		txtrDataUp.setBounds(60, 418, 517, 69);
		panel_21.add(txtrDataUp);

		scrollPaneAgree = new JScrollPane();
		scrollPaneAgree.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneAgree.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneAgree.setBounds(60, 137, 517, 233);
		panel_21.add(scrollPaneAgree);

		txtrAgreement = new JTextArea();
		scrollPaneAgree.setViewportView(txtrAgreement);
		txtrAgreement.setWrapStyleWord(true);
		txtrAgreement
				.setText("This project was approved by the University of Cambridge Computer Laboratory ethics committee on 31st March 2014.\r\n\r\nYour first point of contact with any queries relating to: the usage of this software; the project goals; the user study, should be directed to the project researcher:\r\nJames King - jk509@cam.ac.uk\r\n\r\nThe application is only intended for use on Windows operating systems. Support for other OSes is not guaranteed, but you are welcome to contact the researcher with queries.\r\n\r\nBy checking below, you agree to the following terms:\r\n\r\nData Collection:\r\n\r\nThe data to be gathered for this study will be a set of files generated by the software and stored in your \"USER_DIRECTORY\\Music Factory\\\" folder. You will have the option of allowing the software to automatically upload this data periodically to a remote server used by the researchers, or sending the data manually (e.g. by email or requesting for a researcher to collect the data in person with a USB stick). Any automatic data collection will be anonymous. See option below these terms for more details.\r\n\r\nSoftware Support & Updates:\r\n\r\nSoftware updates may be provided at the discretion of the researcher. Feedback on software defects is valuable but no guarantees are made as to when these may be fixed. The software can be easily updated by simply re-running a newer copy of the installer. This preserves all settings and music collection information. You will be informed when any updates are available and advised in what way the usage experience will be improved. Distribution of updates will be as per the original software distribution.\r\n\r\nPromised Functionality:\r\n\r\nThe promised functionality of the software is the ability to playback music already stored on this computer, alongside a number of basic features such as manually creating playlists. There is also an option to create playlists generated by the machine-learning component of the software (which is what will be under evaluation). One or more algorithms will be involved in the generation of these playlists, but no promises pertaining to the quality/performance of these is given or implied. \r\n\r\nYou are welcome to keep using the software beyond the study end-date. In doing so you will acknowledge that updates may or may not be released after the evaluation period, and that you use the software at your own risk after this period (i.e. the researchers will not be liable for any damage caused, although this is a highly unlikely scenario).\r\n\r\nOriginal project description and precautions statements:\r\n\r\nThe aim of this experiment will be to determine the quality of playlists generated by an automated tool as judged by users. The subjects will receive the software to be used as a typical media player with their own music collections for a period of approximately 1 month. The expected number of participants is 20-50.\r\nData will be gathered from users in two ways. Firstly, anonymous usage data will be collected with permission from users, but will only be viewable by the experiment organiser and will be destroyed after the project has concluded. This data will consist of statistics concerning the frequency and order in which songs have been listened to, and data about any playlists generated as a result.  Aggregated statistics concerning the entire data set will be made publicly viewable in the final report/dissertation for this project.\r\nSecondly, a survey will be conducted at the end of the experiment to allow the users a chance to report on their experiences with the software. These questions will be optional and sent as either a paper or digital form to be filled in.\r\n\r\nThe experiment will be carefully controlled, particularly with regards to anonymity in any data collected from the users. Participants will be informed that they may withdraw from the experiment at any time, and will be told exactly what data will be collected if they choose to participate. All participants will be over 18 and understand that their involvement is entirely voluntary.\r\nNo risk of physical or psychological harm to the participants is expected in this study.  Participants will understand that the only music which may be played to them is what they voluntarily give as input to the software, and that they must be legally entitled to use this music for such a purpose. Participants may choose not to input all of their music to the software.\r\nThe user survey will not pose any risk to participants: it will be voluntary and the questions will only cover their experiences of the software and their opinion on the quality of its operation.");
		txtrAgreement.setLineWrap(true);
		txtrAgreement.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtrAgreement.setEditable(false);
		txtrAgreement.setCaretPosition(0);

		txtrToProceedYou = new JTextArea();
		txtrToProceedYou.setLineWrap(true);
		txtrToProceedYou.setWrapStyleWord(true);
		txtrToProceedYou.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtrToProceedYou.setText("To proceed, you will need to read and confirm your acknowledgement of the following usage agreement. If you have any questions, please don't hesitate to contact the provider of this software.");
		txtrToProceedYou.setBounds(60, 70, 517, 52);
		panel_21.add(txtrToProceedYou);

		// ButtonGroup repeats = new ButtonGroup();

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
		btnNext2.setEnabled(false);
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

		lblLoadIcon1 = new JLabel();
		lblLoadIcon1.setDoubleBuffered(true);
		lblLoadIcon1.setVisible(false);
		lblLoadIcon1.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/loading.gif")));
		lblLoadIcon1.setBounds(207, 345, 48, 48);
		panel_31.add(lblLoadIcon1);

		lblNoAudioFiles = new JLabel("(Audio files will not be copied)");
		lblNoAudioFiles.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNoAudioFiles.setBounds(208, 154, 191, 14);
		panel_31.add(lblNoAudioFiles);

		txtMusicItunes = new JTextField();
		txtMusicItunes.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtMusicItunes.setBounds(60, 120, 414, 21);
		txtMusicItunes.setText(StaticMethods.getHomeDir() + "\\Music\\iTunes");
		
		panel_31.add(txtMusicItunes);
		txtMusicItunes.setColumns(10);

		lblItunesLocate = new JLabel("Please locate your iTunes library file:");
		lblItunesLocate.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblItunesLocate.setBounds(60, 100, 250, 14);
		panel_31.add(lblItunesLocate);
		
		btnImportItunes = new JButton("Import Music");
		btnImportItunes.setEnabled(false);
		btnImportItunes.setBackground(Color.WHITE);
		btnImportItunes.setForeground(new Color(0, 128, 0));
		btnImportItunes.setBounds(60, 145, 138, 30);
		btnImportItunes.addActionListener(new BtnImportItunesActionListener());
		btnImportItunes.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel_31.add(btnImportItunes);

		String path = StaticMethods.getHomeDir() + "\\Music\\iTunes\\iTunes Music Library.xml";
		lblFoundItunesLibrary = new JLabel("Couldn't find an iTunes library automatically.");
		lblFoundItunesLibrary.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFoundItunesLibrary.setBounds(60, 75, 500, 14);
		txtMusicItunes.setText(StaticMethods.getHomeDir());
		if ((new File(path)).exists()) {
			lblFoundItunesLibrary.setText("Found an iTunes library: " + path);
			lblFoundItunesLibrary.setBounds(60, 90, 500, 14);
			lblFoundItunesLibrary.setForeground(new Color(0, 128, 0));
			txtMusicItunes.setText(path);
			lblItunesLocate.setVisible(false);
			btnImportItunes.setEnabled(true);
		}
		panel_31.add(lblFoundItunesLibrary);

		btnBrowseItunes = new JButton("Browse...");
		btnBrowseItunes.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBrowseItunes.addActionListener(new BtnBrowseItunesActionListener());
		btnBrowseItunes.setBackground(Color.WHITE);
		btnBrowseItunes.setBounds(481, 119, 79, 23);
		panel_31.add(btnBrowseItunes);

		scrlItunesTable = new JScrollPane();
		scrlItunesTable.setToolTipText("Select one or more tracks and press delete to remove them");
		scrlItunesTable.setBounds(61, 250, 340, 253);
		scrlItunesTable.setPreferredSize(new Dimension(100, 100));
		panel_31.add(scrlItunesTable);

		listTracks = new JList();
		listTracks.setFont(new Font("Tahoma", Font.PLAIN, 11));
		listTracks.setToolTipText("Select one or more tracks and press delete to remove them");
		scrlItunesTable.setViewportView(listTracks);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setToolTipText("Press delete to remove a playlist");
		scrollPane_1.setBounds(417, 250, 143, 253);
		scrollPane_1.setPreferredSize(new Dimension(50, 50));
		panel_31.add(scrollPane_1);

		listPlaylists = new JList();
		listPlaylists.setFont(new Font("Tahoma", Font.PLAIN, 11));
		listPlaylists.addKeyListener(new ListPlaylistsKeyListener());
		listPlaylists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPlaylists.setToolTipText("Press delete to remove a playlist");
		scrollPane_1.setViewportView(listPlaylists);
		/*
		 * listPlaylists.setModel(new AbstractListModel() { String[] values = new String[] {}; public int getSize() { return values.length; } public Object getElementAt(int index) { return values[index]; } });
		 */

		chckbxImportPlaylists = new JCheckBox("Import playlists");
		chckbxImportPlaylists.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxImportPlaylists.setEnabled(false);
		chckbxImportPlaylists.setSelected(true);
		chckbxImportPlaylists.setBackground(Color.WHITE);
		chckbxImportPlaylists.setBounds(417, 506, 143, 23);
		panel_31.add(chckbxImportPlaylists);

		lblPressDeleteTo = new JLabel("Tracks");
		lblPressDeleteTo.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		lblPressDeleteTo.setBounds(61, 225, 153, 23);
		panel_31.add(lblPressDeleteTo);

		lblLoadMoreItunes = new JLabel("You can import additional tracks from disk later if needed.");
		lblLoadMoreItunes.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblLoadMoreItunes.setBounds(61, 510, 289, 14);
		panel_31.add(lblLoadMoreItunes);

		lblImportFromItunes_2 = new JLabel("Import from iTunes (optional)");
		lblImportFromItunes_2.setForeground(Color.GRAY);
		lblImportFromItunes_2.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblImportFromItunes_2.setBounds(60, 20, 335, 48);
		panel_31.add(lblImportFromItunes_2);

		lblPlaylists = new JLabel("iTunes Playlists");
		lblPlaylists.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		lblPlaylists.setBounds(417, 225, 153, 23);
		panel_31.add(lblPlaylists);

		lblPreview = new JLabel("Preview:");
		lblPreview.setForeground(Color.GRAY);
		lblPreview.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPreview.setBounds(60, 190, 335, 34);
		panel_31.add(lblPreview);

		panel_32 = new JPanel();
		panel_32.setPreferredSize(new Dimension(10, 30));
		panel_32.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_32.setBackground(Color.WHITE);
		pnl3.add(panel_32, BorderLayout.SOUTH);
		panel_32.setLayout(new BorderLayout(0, 0));

		btnPrevious3 = new JButton("Previous");
		btnPrevious3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnPrevious3.setBackground(Color.WHITE);
		btnPrevious3.addActionListener(new BtnPrevActionListener());
		btnPrevious3.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/larrow.png")));
		btnPrevious3.setPreferredSize(new Dimension(110, 25));
		btnPrevious3.setMaximumSize(new Dimension(73, 20));
		panel_32.add(btnPrevious3, BorderLayout.WEST);

		btnNext3 = new JButton("Skip");
		btnNext3.setBackground(Color.WHITE);
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

		lblLoadIcon = new JLabel();
		lblLoadIcon.setDoubleBuffered(true);
		lblLoadIcon.setVisible(false);
		lblLoadIcon.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/loading.gif")));
		lblLoadIcon.setBounds(207, 350, 48, 48);
		panel_41.add(lblLoadIcon);

		lblIfYouUse = new JLabel("If you use another player such as Windows Media Player, your music may be stored in \"My Music\".");
		lblIfYouUse.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblIfYouUse.setBounds(60, 75, 517, 14);
		panel_41.add(lblIfYouUse);

		lblYouCanAlso = new JLabel("You can also select any location where music is stored on your computer.");
		lblYouCanAlso.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblYouCanAlso.setBounds(60, 95, 350, 14);
		panel_41.add(lblYouCanAlso);

		txtMusicRoot = new JTextField();
		txtMusicRoot.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtMusicRoot.setBounds(60, 120, 414, 21);
		txtMusicRoot.setText(StaticMethods.getHomeDir() + "\\Music");
		panel_41.add(txtMusicRoot);
		txtMusicRoot.setColumns(10);

		btnBrowseMusic = new JButton("Browse...");
		btnBrowseMusic.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBrowseMusic.addActionListener(new BtnBrowseMusicActionListener());
		btnBrowseMusic.setBackground(Color.WHITE);
		btnBrowseMusic.setBounds(481, 119, 79, 23);
		panel_41.add(btnBrowseMusic);

		btnImportMusic = new JButton("Import Music");
		btnImportMusic.setBackground(Color.WHITE);
		btnImportMusic.setForeground(new Color(0, 128, 0));
		btnImportMusic.setBounds(60, 145, 138, 30);
		btnImportMusic.addActionListener(new BtnImportMusicActionListener());
		btnImportMusic.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel_41.add(btnImportMusic);

		lblImportFromDisk = new JLabel("Import from Disk (optional)");
		lblImportFromDisk.setForeground(Color.GRAY);
		lblImportFromDisk.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblImportFromDisk.setBounds(60, 20, 365, 48);
		panel_41.add(lblImportFromDisk);

		label_3 = new JLabel("Tracks");
		label_3.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		label_3.setBounds(61, 225, 153, 23);
		panel_41.add(label_3);

		label_4 = new JLabel("Preview:");
		label_4.setForeground(Color.GRAY);
		label_4.setFont(new Font("Tahoma", Font.BOLD, 16));
		label_4.setBounds(60, 190, 79, 34);
		panel_41.add(label_4);

		lblYouCanImport = new JLabel("This shows all tracks including iTunes imports. You can import multiple folders from disk.");
		lblYouCanImport.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblYouCanImport.setBounds(60, 510, 500, 14);
		panel_41.add(lblYouCanImport);

		label_6 = new JLabel("(Audio files will not be copied)");
		label_6.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_6.setBounds(208, 154, 191, 14);
		panel_41.add(label_6);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(61, 250, 340, 253);
		panel_41.add(scrollPane);

		listTracksDisk = new JList();
		listTracksDisk.setFont(new Font("Tahoma", Font.PLAIN, 11));
		scrollPane.setViewportView(listTracksDisk);
		listTracksDisk.setToolTipText("Select one or more tracks and press delete to remove them");

		btnImportPlaylistFiles = new JButton("Import playlists...");
		btnImportPlaylistFiles.addActionListener(new BtnImportPlaylistFilesActionListener());
		btnImportPlaylistFiles.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnImportPlaylistFiles.setBounds(417, 477, 143, 27);
		panel_41.add(btnImportPlaylistFiles);
		
		lblOtherPlaylists = new JLabel("Other Playlists");
		lblOtherPlaylists.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		lblOtherPlaylists.setBounds(417, 225, 153, 23);
		panel_41.add(lblOtherPlaylists);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setToolTipText("Press delete to remove a playlist");
		scrollPane_2.setPreferredSize(new Dimension(50, 50));
		scrollPane_2.setBounds(417, 250, 143, 223);
		panel_41.add(scrollPane_2);
		
		listPlaylistsDisk = new JList();
		listPlaylistsDisk.setFont(new Font("Tahoma", Font.PLAIN, 11));
		listPlaylistsDisk.addKeyListener(new ListPlaylistsKeyListener());
		listPlaylistsDisk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPlaylistsDisk.setToolTipText("Press delete to remove a playlist");
		scrollPane_2.setViewportView(listPlaylistsDisk);
		
		lblFoundCount = new JLabel("Found 0 tracks so far");
		lblFoundCount.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		lblFoundCount.setVisible(false);
		lblFoundCount.setBounds(210, 199, 200, 16);
		lblFoundCount.setForeground(new Color(0, 128, 0));
		panel_41.add(lblFoundCount);

		panel_42 = new JPanel();
		panel_42.setPreferredSize(new Dimension(10, 30));
		panel_42.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_42.setBackground(Color.WHITE);
		pnl4.add(panel_42, BorderLayout.SOUTH);
		panel_42.setLayout(new BorderLayout(0, 0));

		btnPrevious4 = new JButton("Previous");
		btnPrevious4.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnPrevious4.setBackground(Color.WHITE);
		btnPrevious4.addActionListener(new BtnPrevActionListener());
		btnPrevious4.setIcon(new ImageIcon(Setup.class.getResource("/jk509/player/res/larrow.png")));
		btnPrevious4.setPreferredSize(new Dimension(110, 25));
		btnPrevious4.setMaximumSize(new Dimension(73, 20));
		panel_42.add(btnPrevious4, BorderLayout.WEST);

		btnNext4 = new JButton("Next");
		btnNext4.addMouseListener(new BtnNext4MouseListener());
		btnNext4.setEnabled(false);
		btnNext4.setBackground(Color.WHITE);
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
		btnAnalyse.setBounds(60, 240, 107, 28);
		btnAnalyse.addActionListener(new BtnGoActionListener());
		panel_51.add(btnAnalyse);

		lblThisMayTake = new JLabel("Analysis may take some time (up to 10s per track). Please wait...");
		lblThisMayTake.setVisible(false);
		lblThisMayTake.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblThisMayTake.setBounds(60, 293, 407, 23);
		panel_51.add(lblThisMayTake);

		progressBar = new JProgressBar();
		progressBar.setBounds(60, 368, 480, 28);
		panel_51.add(progressBar);

		lblSong = new JLabel("Processing track");
		lblSong.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblSong.setBounds(60, 413, 78, 14);
		panel_51.add(lblSong);

		lblAudioAnalyses = new JLabel("Audio analysis");
		lblAudioAnalyses.setForeground(Color.GRAY);
		lblAudioAnalyses.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblAudioAnalyses.setBounds(60, 20, 270, 48);
		panel_51.add(lblAudioAnalyses);

		lblProcessingStart = new JLabel("0");
		lblProcessingStart.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblProcessingStart.setBounds(148, 413, 38, 14);
		panel_51.add(lblProcessingStart);

		lblOf = new JLabel("of");
		lblOf.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblOf.setBounds(190, 413, 23, 14);
		panel_51.add(lblOf);

		lblProcessingCount = new JLabel("0");
		lblProcessingCount.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblProcessingCount.setBounds(223, 413, 46, 14);
		panel_51.add(lblProcessingCount);

		lblProcessingName = new JLabel("");
		lblProcessingName.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblProcessingName.setBounds(60, 438, 503, 23);
		panel_51.add(lblProcessingName);

		lblTimeTaken = new JLabel("Time taken:");
		lblTimeTaken.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblTimeTaken.setBounds(60, 473, 67, 14);
		panel_51.add(lblTimeTaken);

		lblTimeRemaining = new JLabel("Time remaining:");
		lblTimeRemaining.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblTimeRemaining.setBounds(231, 473, 99, 14);
		panel_51.add(lblTimeRemaining);

		lblProcessingTime = new JLabel("0h 0m 0s");
		lblProcessingTime.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblProcessingTime.setBounds(129, 473, 84, 14);
		panel_51.add(lblProcessingTime);

		lblProcessingTimeLeft = new JLabel("0h 0m 0s");
		lblProcessingTimeLeft.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblProcessingTimeLeft.setBounds(318, 473, 78, 14);
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
		lblClickHereTo.setBounds(61, 204, 291, 23);
		panel_51.add(lblClickHereTo);

		fileProgressBar = new JProgressBar();
		fileProgressBar.setStringPainted(true);
		fileProgressBar.setBounds(60, 329, 480, 28);
		panel_51.add(fileProgressBar);

		int threadCount = StaticMethods.getThreadCount();
		lblCores = new JLabel("Using " + threadCount + " processor core" + (threadCount > 1 ? "s" : ""));
		lblCores.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCores.setForeground(Color.GRAY);
		lblCores.setBounds(60, 503, 153, 14);
		panel_51.add(lblCores);

		panel_52 = new JPanel();
		panel_52.setPreferredSize(new Dimension(10, 30));
		panel_52.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_52.setBackground(Color.WHITE);
		pnl5.add(panel_52, BorderLayout.SOUTH);
		panel_52.setLayout(new BorderLayout(0, 0));

		btnPrevious5 = new JButton("Previous");
		btnPrevious5.setFont(new Font("Tahoma", Font.PLAIN, 11));
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
		lblThanksWereAll.setBounds(57, 93, 270, 28);
		panel_61.add(lblThanksWereAll);

		lblAFewInstructions = new JLabel("Click Finish below to start playing music, or navigate back to a previous page");
		lblAFewInstructions.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAFewInstructions.setBounds(57, 140, 520, 28);
		panel_61.add(lblAFewInstructions);

		lblSetupComplete = new JLabel("Setup complete");
		lblSetupComplete.setForeground(Color.GRAY);
		lblSetupComplete.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblSetupComplete.setBounds(60, 20, 270, 48);
		panel_61.add(lblSetupComplete);

		lblToAdjustSettings = new JLabel("to adjust settings. Import more music later using the import options in the");
		lblToAdjustSettings.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblToAdjustSettings.setBounds(57, 167, 520, 28);
		panel_61.add(lblToAdjustSettings);

		lblFileMenu = new JLabel("File menu.");
		lblFileMenu.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblFileMenu.setBounds(57, 195, 520, 28);
		panel_61.add(lblFileMenu);

		lblAfterSettingUp = new JLabel("After setting up any playlists and getting used to using the music player,");
		lblAfterSettingUp.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAfterSettingUp.setBounds(57, 265, 520, 28);
		panel_61.add(lblAfterSettingUp);

		lblPleaseTryTo = new JLabel("please try to use it in \"Smart Mode\" as much as possible. In this mode, the");
		lblPleaseTryTo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPleaseTryTo.setBounds(57, 292, 520, 28);
		panel_61.add(lblPleaseTryTo);

		lblNextTrackWill = new JLabel("next track will always be chosen automatically based on what you're currently");
		lblNextTrackWill.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNextTrackWill.setBounds(57, 320, 520, 28);
		panel_61.add(lblNextTrackWill);

		lblNewLabel = new JLabel("listening to and what you've listened to in the past.");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(57, 348, 520, 28);
		panel_61.add(lblNewLabel);

		lblSmartModeIs = new JLabel("Smart mode is turned on by default. Turn it off later by pressing 's' or clicking");
		lblSmartModeIs.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSmartModeIs.setBounds(57, 404, 479, 28);
		panel_61.add(lblSmartModeIs);

		lblTheBrainIcon = new JLabel("the brain icon near the bottom of the screen.");
		lblTheBrainIcon.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTheBrainIcon.setBounds(57, 432, 520, 28);
		panel_61.add(lblTheBrainIcon);

		panel_1 = new JPanel();
		panel_1.setBackground(new Color(255, 255, 255));
		panel_1.setBorder(new LineBorder(new Color(165, 42, 42), 2, true));
		panel_1.setBounds(38, 249, 520, 229);
		panel_61.add(panel_1);
		panel_1.setLayout(null);

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
		
		txtMusicItunes.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				if(txtMusicItunes != null && btnImportItunes != null){
					String txt = txtMusicItunes.getText();
					if((new File(txt)).exists())
						btnImportItunes.setEnabled(true);
					else
						btnImportItunes.setEnabled(false);
				}
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if(txtMusicItunes != null && btnImportItunes != null){
					String txt = txtMusicItunes.getText();
					if((new File(txt)).exists())
						btnImportItunes.setEnabled(true);
					else
						btnImportItunes.setEnabled(false);
				}
			}
			@Override
			public void changedUpdate(DocumentEvent arg0) {
			}
		});

	}

	/*
	 * private void UpdatePlaylistDisplay(){ listPlaylists.setModel(new AbstractListModel(){ private static final long serialVersionUID = 1L;
	 * 
	 * @Override public Object getElementAt(int arg0) { return library.getPlaylists().get(arg0 + MusicPlayer.FIXED_PLAYLIST_ELEMENTS).getName(); }
	 * 
	 * @Override public int getSize() { return library.getPlaylists().size() - MusicPlayer.FIXED_PLAYLIST_ELEMENTS; }
	 * 
	 * }); }
	 */

	private void UpdateTrackDisplay() {
		//if (stage == 3) {
			// import from itunes
			listTracks.setModel(new AbstractListModel() {
				private static final long serialVersionUID = 1L;

				@Override
				public int getSize() {
					return library.getPlaylists().get(Library.MAIN_PLAYLIST).size();
				}

				@Override
				public Object getElementAt(int arg0) {
					Song track = library.getPlaylists().get(Library.MAIN_PLAYLIST).get(arg0);
					return track.toString();
				}
			});
			listPlaylists.setModel(new AbstractListModel() {
				private static final long serialVersionUID = 1L;

				@Override
				public int getSize() {
					return itunesPlaylists.size();//library.getPlaylists().size() - MusicPlayer.FIXED_PLAYLIST_ELEMENTS;
				}

				@Override
				public Object getElementAt(int index) {
					return itunesPlaylists.get(index).getName();//library.getPlaylists().get(index + MusicPlayer.FIXED_PLAYLIST_ELEMENTS).getName();
				}
			});
			listTracks.repaint();
			listPlaylists.repaint();
		//} else if (stage == 4) {
			// import from disk
			listTracksDisk.setModel(new AbstractListModel() {
				private static final long serialVersionUID = 1L;

				@Override
				public int getSize() {
					return library.getPlaylists().get(Library.MAIN_PLAYLIST).size();
				}

				@Override
				public Object getElementAt(int index) {
					Song track = library.getPlaylists().get(Library.MAIN_PLAYLIST).get(index);
					return track.toString();
				}
			});
			listPlaylistsDisk.setModel(new AbstractListModel() {
				private static final long serialVersionUID = 1L;

				@Override
				public int getSize() {
					return genericPlaylists.size();
				}

				@Override
				public Object getElementAt(int index) {
					return StaticMethods.getFileName(genericPlaylists.get(index));
				}
			});
			listTracksDisk.repaint();
			listPlaylistsDisk.repaint();
		//}
			
			if(library.getPlaylists().get(Library.MAIN_PLAYLIST).size() >= Constants.MIN_LIBRARY_SIZE)
				btnNext4.setEnabled(true);
	}

	private class BtnNextActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(stage == 1)
				Setup.this.setAlwaysOnTop(false);
			if(stage == 4 && library.getPlaylists().get(Library.MAIN_PLAYLIST).size() < Constants.MIN_LIBRARY_SIZE){
				Object[] options = {"OK"};
				JOptionPane.showOptionDialog(Setup.this, "Sorry, you need to add at least "+Constants.MIN_LIBRARY_SIZE+" tracks before proceeding", "Cannot continue", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			}else{
				cardLayout.next(pnlMain);
				stage++;
				UpdateSidebar();
			}
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
			success[0] = true;
			Logger.log("Setup completed successfully", LogType.USAGE_LOG);
			dispose();
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
				List<Song> playlist = StaticMethods.getSongsByLocation(locs, library.getTracks());
				Playlist p = new Playlist(StaticMethods.getFileName(fpath), Playlist.USER, playlist);
				return p;
			}
		} catch (Exception e) {
			Logger.log(e, LogType.ERROR_LOG);
		}
		return null;
	}

	private class BtnGoActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			btnPrevious5.setEnabled(false);
			lblThisMayTake.setText("Importing playlists...");
			
			if (library.getPlaylists().size() >= MusicPlayer.FIXED_PLAYLIST_ELEMENTS && library.getPlaylists().get(Library.MAIN_PLAYLIST).size() > 0) {

				// Thread which does main analysis
				final Setup context = Setup.this;
				(new Thread() {
					@Override
					public void run() {
						processPlaylists();
						try {
							SwingUtilities.invokeAndWait(new Runnable() {
								@Override public void run() {
									lblThisMayTake.setText("Analysis may take some time (up to 10s per track). Please wait...");
								}
							});
						} catch (Exception e) {
							Logger.log(e, LogType.ERROR_LOG);
						} 
						
						SongCluster clusters = new SongCluster(library.getMainList(), new GuiUpdaterAdapter(context));
						library.setClusters(clusters);
					}
				}).start();

				// Timer thread just for updating times 
				(new Thread() {
					@Override
					public void run() {
						long startTime = System.currentTimeMillis();
						int time = 0;
						while (!lblThisMayTake.getText().equals("Processing complete.")) {
							if ((System.currentTimeMillis() - startTime) / 1000 > time) {
								time = (int) (System.currentTimeMillis() - startTime) / 1000;
								lblProcessingTime.setText((time / 3600) + "h " + ((time % 3600) / 60) + "m " + (time % 60) + "s");
								if (Integer.parseInt(lblProcessingStart.getText()) > 0) {
									int timeleft = (time / Integer.parseInt(lblProcessingStart.getText())) * (library.getPlaylists().get(Library.MAIN_PLAYLIST).size() - Integer.parseInt(lblProcessingStart.getText()));
									lblProcessingTimeLeft.setText((timeleft / 3600) + "h " + ((timeleft % 3600) / 60) + "m " + (timeleft % 60) + "s");
								}
							}
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								Logger.log(e, LogType.ERROR_LOG);
							}
						}
						lblProcessingTimeLeft.setText("0h 0m 0s");
					}
				}).start();

			} else {
				btnNext5.setEnabled(true);
			}
		}
	}
	
	public class FileScannerUpdater {
		private int count = 0;
		public void update(){
			count++;
			lblFoundCount.setText("Found "+count+" track"+(count > 1 ? "s" : "")+" so far");
		}
	}

	private class BtnImportMusicActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			lblFoundCount.setVisible(true);
			btnNext4.setEnabled(false);
			lblLoadIcon.setVisible(true);
			//label_6.setVisible(false);
			btnPrevious4.setEnabled(false);
			btnBrowseMusic.setEnabled(false);
			listTracksDisk.setModel(new AbstractListModel() {
				private static final long serialVersionUID = 1L;

				public int getSize() {
					return 1;
				}

				public Object getElementAt(int index) {
					return " Loading... this may a while for large collections";
				}
			});

			btnImportMusic.setEnabled(false);
			btnImportMusic.setText("Importing...");
			btnImportMusic.invalidate();
			btnImportMusic.repaint();
			// dialog.invalidate(); dialog.repaint();

			(new Thread() {
				@Override
				public void run() {
					String folder = txtMusicRoot.getText();
					FileScanner parser = new FileScanner(new FileScannerUpdater());
					parser.setPath(folder);
					parser.setValid(true);
					if (AddToLibrary(parser)) {
						// btnImportMusic.setEnabled(false);
						btnImportMusic.setText("Done");
						(new Thread(){
							@Override public void run(){ try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								Logger.log(e, LogType.ERROR_LOG);
							} 
							btnImportMusic.setEnabled(true); 
							btnImportMusic.setText("Import Music"); 
							}
						}).start();
						btnNext4.setEnabled(true);
						btnBrowseMusic.setEnabled(true);
						lblYouCanImport.setVisible(true);
						lblFoundCount.setVisible(false);
					}
					UpdateTrackDisplay();
					btnPrevious4.setEnabled(true);
					lblLoadIcon.setVisible(false);
				}
			}).start();

		}
	}

	private class BtnImportItunesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			lblLoadIcon1.setVisible(true);
			btnNext3.setEnabled(false);
			btnPrevious3.setEnabled(false);
			(new Thread() {
				@Override
				public void run() {
					String loc = txtMusicItunes.getText();
					LibraryParser parser = new ItunesParser();
					parser.setPath(loc);
					parser.setValid(true);
					if (Import(parser)) {
						ImportItunesPlaylists();
						btnImportItunes.setEnabled(false);
						btnNext3.setText("Next");
						btnNext3.setEnabled(true);
						chckbxImportPlaylists.setEnabled(true);
						UpdateTrackDisplay();
					}
					
					lblLoadIcon1.setVisible(false);
					btnPrevious3.setEnabled(true);
				}
			}).start();

			/*try {

				
				 * File file = new File(txtMusicItunes.getText()); SpecificPlaylist specificPlaylist = SpecificPlaylistFactory.getInstance().readFrom(file); if(specificPlaylist != null){ Playlist pl = specificPlaylist.toPlaylist(); PlaylistConverter converter = new PlaylistConverter(); SpecificPlaylist newSpecificPlaylist = converter.toSpecificPlaylist(pl); PlaylistToString adapter = new PlaylistToString(); adapter.beginVisitPlaylist(pl); //adapter.endVisitPlaylist(pl); System.out.println(adapter.toString()); }
				 
			} catch (Exception e) {
				Logger.log(e, LogType.ERROR_LOG);
			}*/
		}
	}

	@SuppressWarnings("unchecked")
	private void ImportItunesPlaylists() {
		try{
			ItunesLibrary ituneslibrary = ItunesLibraryParser.parseLibrary(txtMusicItunes.getText());
		
			List<ItunesPlaylist> playlists = ituneslibrary.getPlaylists();
			//outerloop:
			for (int i = 1; i < playlists.size(); ++i) { // skip 1 as it's all tracks
				ItunesPlaylist playlist = (ItunesPlaylist) playlists.get(i);
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
						Song s = StaticMethods.GetSongByLoc(loc, library.getMainList());
						if(s != null)
							//continue outerloop;
							pl.add(s);
					}catch(NullPointerException e){}
				}
				if(pl.size() > 0)
					itunesPlaylists.add(pl);
			}
		}catch(RuntimeException e){ 
			
		}
	}

	private class ThisWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			if (JOptionPane.showConfirmDialog(dialog, "You will not be able to use the music player until setup has completed.\nAre you sure you want to exit?"/* "The music player will not be useful until setup has completed.\nAre you sure you want to exit?" */, "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				success[0] = false;
				Logger.log("Setup exited prematurely", LogType.USAGE_LOG);
				dispose();
			}
		}
	}

	private class BtnBrowseMusicActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(musicChooser == null){
				musicChooser = new JFileChooser();
	
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
				musicChooser.setCurrentDirectory(new java.io.File(startat));
				musicChooser.setDialogTitle("Select music folder");
				musicChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	
				// disable the "All files" option.
				musicChooser.setAcceptAllFileFilterUsed(false);
			
			}else{
				String startat = musicChooser.getCurrentDirectory().getAbsolutePath();
				// choose initial folder (first check textfield, otherwise try user
				// home dir)
				try {
					if ((new File(txtMusicRoot.getText())).exists() && (new File(txtMusicRoot.getText())).isDirectory())
						startat = (new File(txtMusicRoot.getText())).getPath();
					else
						startat = musicChooser.getCurrentDirectory().getAbsolutePath();
				} catch (Exception e2) {
					startat = musicChooser.getCurrentDirectory().getAbsolutePath();
				}
	
				if (startat.equals("")) {
					startat = System.getenv("user.home");
					if (startat == null || startat.equals(""))
						startat = System.getenv("USERPROFILE");
					if (startat == null || startat.equals(""))
						startat = ".";
				}
				musicChooser.setCurrentDirectory(new java.io.File(startat));
			}

			int result = musicChooser.showOpenDialog(dialog);
			if (result == JFileChooser.APPROVE_OPTION) {
				txtMusicRoot.setText(musicChooser.getSelectedFile().getPath());
			} else {
				// System.out.println("No Selection ");
				// System.out.println(result);
				txtMusicRoot.setText(musicChooser.getCurrentDirectory().getAbsolutePath());
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

	private class ChckbxEnableShortcutsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			btnNext2.setEnabled(chckbxAgree.isSelected());
		}
	}

	private class ChckbxUploadDataActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			library.autoupload = chckbxUploadData.isSelected();
		}
	}
	private class BtnImportPlaylistFilesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if(playlistChooser == null){
				playlistChooser = new JFileChooser();
				
				String startat = StaticMethods.getHomeDir();
				
				if(new File(StaticMethods.getHomeDir() + "\\Music\\Playlists").exists() && new File(StaticMethods.getHomeDir() + "\\Music\\Playlists").isDirectory())
					startat = StaticMethods.getHomeDir() + "\\Music\\Playlists";
				
				playlistChooser.setCurrentDirectory(new File(startat));
				//playlistChooser.setFileFilter(new MP3filter());
				
				playlistChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				playlistChooser.setAcceptAllFileFilterUsed(true);
				playlistChooser.setDialogTitle("Choose playlist files");
				playlistChooser.setMultiSelectionEnabled(true);
			}
			
			int result = playlistChooser.showOpenDialog(Setup.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File[] res = playlistChooser.getSelectedFiles();
				if(res != null && res.length > 0){
					for(int i=0; i<res.length; ++i){
						String fpath = res[i].getAbsolutePath();
						genericPlaylists.add(fpath);
					}
				}
				UpdateTrackDisplay();
			}
		}
	}
	private class ListPlaylistsKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent arg0) {
			if(arg0.getKeyCode() == KeyEvent.VK_DELETE){
				// Delete selected playlist
				if(stage == 3){
					itunesPlaylists.remove(listPlaylists.getSelectedIndex());
				}else if(stage == 4){
					genericPlaylists.remove(listPlaylistsDisk.getSelectedIndex());
				}
				UpdateTrackDisplay();
			}
		}
	}
	private class BtnNext4MouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if(!btnNext4.isEnabled()){
				Object[] options = {"OK"};
				JOptionPane.showOptionDialog(Setup.this, "Sorry, you need to add at least "+Constants.MIN_LIBRARY_SIZE+" tracks before proceeding", "Cannot continue", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			}
		}
	}
	
	private void processPlaylists(){
		if(chckbxImportPlaylists.isSelected()){
			library.addPlaylists(itunesPlaylists);
		}
		for(int i=0; i<genericPlaylists.size(); ++i){
			library.addPlaylist(importPlaylist(genericPlaylists.get(i)));
		}
	}

	private boolean Import(LibraryParser parser) {

		if (!parser.isValid())
			return false;

		parser.run();

		List<Song> toimport = library.getAllNotContained(parser.getTracks());
		
		if(toimport.size() > 0){
			library.clearViews();
			library.setPlaylist(Library.MAIN_PLAYLIST, toimport);
	
			return true;
		}else
			library.setPlaylist(Library.MAIN_PLAYLIST, new ArrayList<Song>());
			return false;

	}

	private boolean AddToLibrary(LibraryParser parser) {

		if (!parser.isValid())
			return false;

		parser.run();

		List<Song> toimport = library.getAllNotContained(parser.getTracks());
		library.clearViews();
		library.addToPlaylist(Library.MAIN_PLAYLIST, toimport);

		return true;

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

			String extension = StaticMethods.getExtension(f);
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
