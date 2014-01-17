package jk509.player;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;

public class FrmMain  implements MouseListener, MouseMotionListener {

	private JFrame frmMusicPlayer;
	private String filename = "C:\\Users\\James\\Music\\iTunes\\iTunes Media\\Music\\Muse\\Absolution\\08 Hysteria.mp3";
	JButton btnPlay;
	JLayerPlayerPausable play;
	SoundJLayer player;
	private boolean paused = true;
	private JButton btnExit;
	private JButton btnMin;
	private JButton btnMax;
	private JButton btnUnmax;

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
					FrmMain window = new FrmMain();
					window.frmMusicPlayer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FrmMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMusicPlayer = new JFrame();
		frmMusicPlayer.getContentPane().setBackground(new Color(100, 149, 237));
		frmMusicPlayer.setBackground(new Color(100, 149, 237));
		frmMusicPlayer.setUndecorated(true);
		frmMusicPlayer.setIconImage((new ImageIcon(this.getClass().getResource("/jk509/player/res/icon.png"))).getImage());
		frmMusicPlayer.setTitle("Music Player");
		frmMusicPlayer.setBounds(100, 100, 700, 450);
		frmMusicPlayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMusicPlayer.getContentPane().setLayout(null);
		frmMusicPlayer.setLocationRelativeTo(null);
		frmMusicPlayer.addMouseListener(this);
		frmMusicPlayer.addMouseMotionListener(this);
		
		btnExit = new JButton();
		btnExit.setFocusPainted(false);
		btnExit.setMargin(new Insets(0, 0, 0, 0));
		btnExit.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/cross.png")));
		btnExit.setRolloverIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/cross_over.png")));
		btnExit.setPressedIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/cross_over_sel.png")));
		btnExit.addActionListener(new BtnTestActionListener());
		btnExit.setBounds(663, 10, 25, 25);
		frmMusicPlayer.getContentPane().add(btnExit);
		
		btnMin = new JButton();
		btnMin.setFocusPainted(false);
		btnMin.setMargin(new Insets(0, 0, 0, 0));
		btnMin.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/minimise.png")));
		btnMin.setRolloverIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/minimise_over.png")));
		btnMin.setPressedIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/minimise_over_sel.png")));
		btnMin.addActionListener(new BtnMinActionListener());
		btnMin.setBounds(589, 10, 25, 25);
		frmMusicPlayer.getContentPane().add(btnMin);
		
		btnMax = new JButton();
		btnMax.setFocusPainted(false);
		btnMax.setMargin(new Insets(0, 0, 0, 0));
		btnMax.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/maximise.png")));
		btnMax.setRolloverIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/maximise_over.png")));
		btnMax.setPressedIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/maximise_over_sel.png")));
		btnMax.addActionListener(new BtnMaxActionListener());
		btnMax.setBounds(626, 10, 25, 25);
		frmMusicPlayer.getContentPane().add(btnMax);
		
		btnUnmax = new JButton();
		btnUnmax.setFocusPainted(false);
		btnUnmax.setMargin(new Insets(0, 0, 0, 0));
		btnUnmax.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/unmaximise.png")));
		btnUnmax.setRolloverIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/unmaximise_over.png")));
		btnUnmax.setPressedIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/unmaximise_over_sel.png")));
		btnUnmax.setVisible(false);
		btnUnmax.addActionListener(new BtnUnmaxActionListener());
		btnUnmax.setBounds(629, 10, 25, 25);
		frmMusicPlayer.getContentPane().add(btnUnmax);
		
		panel = new JPanel();
		panel.setBackground(new Color(255, 255, 255));
		panel.setBounds(12, 60, 676, 356);
		frmMusicPlayer.getContentPane().add(panel);
		panel.setLayout(null);
		
		panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(100, 149, 237), 5));
		panel_1.setBackground(new Color(255, 255, 255));
		panel_1.setBounds(-5, -5, 181, 388);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		list = new JList();
		list.setModel(new AbstractListModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] values = new String[] {"Music", "Artists", "Albums", "Playlist 1", "Playlist 2", "..."};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		list.setBounds(12, 13, 157, 319);
		panel_1.add(list);
		
		btnNew = new JButton("New");
		btnNew.setBounds(12, 335, 76, 23);
		panel_1.add(btnNew);
		
		btnDelete = new JButton("Delete");
		btnDelete.setBounds(93, 335, 76, 23);
		panel_1.add(btnDelete);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setEnabled(false);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"", "Muse", "Absolution", "2008", "Alternative"},
				{"Give it Away", "Red Hot Chili Peppers", "By The Way", "1991", "Rock"},
				{"Knights of Cydonia", "Muse", "Black Holes", "1999", "Alternative"},
				{"Pompeii", "Bastille", "Pompeii", "2013", "Rock"},
			},
			new String[] {
				"Title", "Artist", "Album", "Year", "Genre"
			}
		));
		table.setBounds(188, 13, 476, 336);
		panel.add(table);
		
		panel_2 = new JPanel();
		panel_2.setBackground(new Color(211,211,211));
		panel_2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_2.setBounds(12, 10, 542, 39);
		frmMusicPlayer.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		pnlArt = new JPanel();
		pnlArt.setBackground(new Color(255, 255, 255));
		pnlArt.setBounds(1, 1, 37, 37);
		panel_2.add(pnlArt);
		pnlArt.setLayout(null);
		
		label = new JLabel();
		label.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/cdcover.png")));
		label.setBounds(0, 0, 37, 37);
		pnlArt.add(label);
		
		btnPlay = new JButton();
		btnPlay.setFocusPainted(false);
		btnPlay.setMargin(new Insets(0, 0, 0, 0));
		btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/play.png")));
		btnPlay.setBounds(200, 4, 32, 32);
		panel_2.add(btnPlay);
		
		lblHysteriaMuse = new JLabel("Hysteria - Muse");
		lblHysteriaMuse.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblHysteriaMuse.setBounds(52, 11, 201, 16);
		panel_2.add(lblHysteriaMuse);
		
		slider = new JSlider();
		slider.setValue(0);
		slider.setMaximum(300);
		slider.setBackground(new Color(211,211,211));
		slider.setBounds(244, 8, 286, 23);
		panel_2.add(slider);
		btnPlay.addActionListener(new BtnPlayActionListener());
		
		panel_3 = new JPanel();
		panel_3.setBounds(12, 421, 676, 19);
		frmMusicPlayer.getContentPane().add(panel_3);
		
		player = new SoundJLayer(filename);
		
	}
	
		
	private class BtnPlayActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(paused){
				// paused
				paused = false;
				player.play();
				btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/pause.png")));
			}else{
				// playing
				paused = true;
				player.pause(); 
				btnPlay.setIcon(new ImageIcon(this.getClass().getResource("/jk509/player/res/play.png")));
			}
		}
	}
	private class BtnTestActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	private class BtnMinActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			frmMusicPlayer.setState(JFrame.ICONIFIED);
		}
	}
	private class BtnMaxActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			frmMusicPlayer.setExtendedState(JFrame.MAXIMIZED_BOTH);
			btnMax.setVisible(false);
			btnUnmax.setVisible(true);
		}
	}
	private class BtnUnmaxActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			frmMusicPlayer.setExtendedState(JFrame.NORMAL);
			btnMax.setVisible(true);
			btnUnmax.setVisible(false);
		}
	}

	private Point start;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_3;
	private JPanel pnlArt;
	private JLabel lblHysteriaMuse;
	private JLabel label;
	private JSlider slider;
	private JTable table;
	private JList list;
	private JButton btnNew;
	private JButton btnDelete;

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
	Point p = e.getLocationOnScreen();
	Component c = e.getComponent();
	c.setLocation((int)(p.getX() - start.getX()), (int)(p.getY() - start.getY()));
	c.repaint();
	}

	public void mouseMoved(MouseEvent e) {
	}
}
