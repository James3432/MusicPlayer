package jk509.player.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import jk509.player.MusicPlayer;

public class SmartPlaylistDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtNewPlaylist;
	private JSpinner spinner;
	private JLabel lblPlaylistName;
	private JLabel lblNumberOfTracks;
	private SmartPlaylistResult result = null;

	public SmartPlaylistResult showDialog(){
		setVisible(true);
		return result;
	}
	
	/**
	 * Create the dialog.
	 */
	public SmartPlaylistDialog(int TRACK_COUNT) {
		setTitle("New smart playlist");
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 261, 170);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon.png")));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			txtNewPlaylist = new JTextField();
			txtNewPlaylist.setText("New playlist");
			txtNewPlaylist.setBounds(106, 29, 139, 20);
			contentPanel.add(txtNewPlaylist);
			txtNewPlaylist.setColumns(10);
		}

		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(20, 1, TRACK_COUNT, 1));
		spinner.setBounds(106, 65, 46, 20);
		contentPanel.add(spinner);

		lblPlaylistName = new JLabel("Playlist name:");
		lblPlaylistName.setBounds(10, 24, 86, 30);
		contentPanel.add(lblPlaylistName);

		lblNumberOfTracks = new JLabel("Number of tracks:");
		lblNumberOfTracks.setBounds(10, 60, 115, 30);
		contentPanel.add(lblNumberOfTracks);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new OkButtonActionListener());
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new CancelButtonActionListener());
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public class SmartPlaylistResult {
		public String name;
		public int size;

		public SmartPlaylistResult(String s, int l) {
			name = s;
			size = l;
		}
	}
	private class CancelButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			result = null;
			setVisible(false);
			dispose();
		}
	}
	private class OkButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			result = new SmartPlaylistResult(txtNewPlaylist.getText(), (Integer) spinner.getValue());
			setVisible(false);
			dispose();
		}
	}
}
