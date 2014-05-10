package jk509.player.gui;

import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import jk509.player.MusicPlayer;

public class ProgressFrame extends JDialog {

	static final long serialVersionUID = 1;

	/**
	 * Progress within this file
	 */
	public JProgressBar fileProgressBar;

	/**
	 * Overall progress (in files)
	 */
	public JProgressBar overallProgressBar;

	/**
	 * Creates the progress window but does not show it.
	 *
	 */
	
	public ProgressFrame(JFrame mainForm, boolean displayfileprogress) {
		super(mainForm);
		setTitle("Progress");
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon.png")));
		setModal(true);
		setAlwaysOnTop(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		fileProgressBar = new JProgressBar();
		fileProgressBar.setStringPainted(true);
		overallProgressBar = new JProgressBar();
		overallProgressBar.setStringPainted(true);
		overallProgressBar.setVisible(displayfileprogress);
		setLayout(new GridLayout(4, 1, 6, 11));
		JLabel tmp = new JLabel("File Progress");
		tmp.setHorizontalAlignment(SwingConstants.CENTER);
		add(tmp);
		add(fileProgressBar);
		tmp = new JLabel("Overall Progress");
		tmp.setHorizontalAlignment(SwingConstants.CENTER);
		tmp.setVisible(displayfileprogress);
		add(tmp);
		add(overallProgressBar);
		pack();
	}

}
