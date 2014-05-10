package jk509.player.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

import jk509.player.MusicPlayer;
import jk509.player.core.Song;

public class GetInfoDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private List<Song> tracks;
	private JLabel lblSettingInfoFor;
	private JTextField txtName;
	private JTextField txtAlbum;
	private JTextField txtArtist;
	private JTextField txtGenre;
	private JCheckBox checkNum;
	private JCheckBox checkName;
	private JCheckBox checkAlbum;
	private JCheckBox checkArtist;
	private JCheckBox checkGenre;
	private JCheckBox checkPlays;
	private JLabel lblResetPlayCount;
	private JSpinner txtNum;

	/**
	 * Create the dialog.
	 */
	public GetInfoDialog(List<Song> tracks) {
		this.tracks = tracks;
		setTitle("Edit info for track"+(tracks.size() > 1 ? "s" : ""));
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon.png")));
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(null);
		setBounds(100, 100, 275, 290);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		lblSettingInfoFor = new JLabel("Setting info for "+tracks.size()+" tracks");
		lblSettingInfoFor.setFocusable(false);
		lblSettingInfoFor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblSettingInfoFor.setBounds(10, 11, 165, 14);
		contentPanel.add(lblSettingInfoFor);
		{
			JLabel lblTrack = new JLabel("Track #");
			lblTrack.setFocusable(false);
			lblTrack.setHorizontalAlignment(SwingConstants.TRAILING);
			lblTrack.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
			lblTrack.setBounds(20, 50, 46, 14);
			contentPanel.add(lblTrack);
		}
		{
			JLabel lblName = new JLabel("Name");
			lblName.setFocusable(false);
			lblName.setHorizontalAlignment(SwingConstants.TRAILING);
			lblName.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
			lblName.setBounds(20, 75, 46, 14);
			contentPanel.add(lblName);
		}
		{
			JLabel lblAlbum = new JLabel("Album");
			lblAlbum.setFocusable(false);
			lblAlbum.setHorizontalAlignment(SwingConstants.TRAILING);
			lblAlbum.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
			lblAlbum.setBounds(20, 100, 46, 14);
			contentPanel.add(lblAlbum);
		}
		{
			JLabel lblArtist = new JLabel("Artist");
			lblArtist.setFocusable(false);
			lblArtist.setHorizontalAlignment(SwingConstants.TRAILING);
			lblArtist.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
			lblArtist.setBounds(20, 125, 46, 14);
			contentPanel.add(lblArtist);
		}
		{
			JLabel lblGenre = new JLabel("Genre");
			lblGenre.setFocusable(false);
			lblGenre.setHorizontalAlignment(SwingConstants.TRAILING);
			lblGenre.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
			lblGenre.setBounds(20, 150, 46, 14);
			contentPanel.add(lblGenre);
		}
		
		txtName = new JTextField(getStartName());
		txtName.addFocusListener(new TxtNameFocusListener());
		txtName.setBounds(80, 72, 133, 20);
		contentPanel.add(txtName);
		txtName.setColumns(10);
		
		txtAlbum = new JTextField(getStartAlbum());
		txtAlbum.addFocusListener(new TxtAlbumFocusListener());
		txtAlbum.setColumns(10);
		txtAlbum.setBounds(80, 97, 133, 20);
		contentPanel.add(txtAlbum);
		
		txtArtist = new JTextField(getStartArtist());
		txtArtist.addFocusListener(new TxtArtistFocusListener());
		txtArtist.setColumns(10);
		txtArtist.setBounds(80, 122, 133, 20);
		contentPanel.add(txtArtist);
		
		txtGenre = new JTextField(getStartGenre());
		txtGenre.addFocusListener(new TxtGenreFocusListener());
		txtGenre.setColumns(10);
		txtGenre.setBounds(80, 147, 133, 20);
		contentPanel.add(txtGenre);
		
		checkNum = new JCheckBox("");
		checkNum.setFocusable(false);
		checkNum.setBounds(219, 45, 26, 23);
		contentPanel.add(checkNum);
		
		checkName = new JCheckBox("");
		checkName.setFocusable(false);
		checkName.setBounds(219, 70, 31, 23);
		contentPanel.add(checkName);
		
		checkAlbum = new JCheckBox("");
		checkAlbum.setFocusable(false);
		checkAlbum.setBounds(219, 95, 31, 23);
		contentPanel.add(checkAlbum);
		
		checkArtist = new JCheckBox("");
		checkArtist.setFocusable(false);
		checkArtist.setBounds(219, 120, 31, 23);
		contentPanel.add(checkArtist);
		
		checkGenre = new JCheckBox("");
		checkGenre.setFocusable(false);
		checkGenre.setBounds(219, 145, 31, 23);
		contentPanel.add(checkGenre);
		
		checkPlays = new JCheckBox("");
		checkPlays.setFocusable(false);
		checkPlays.setBounds(219, 185, 26, 23);
		contentPanel.add(checkPlays);
		
		lblResetPlayCount = new JLabel("Reset play count");
		lblResetPlayCount.setFocusable(false);
		lblResetPlayCount.addMouseListener(new LblResetPlayCountMouseListener());
		lblResetPlayCount.setHorizontalAlignment(SwingConstants.TRAILING);
		lblResetPlayCount.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		lblResetPlayCount.setBounds(115, 186, 93, 20);
		contentPanel.add(lblResetPlayCount);
		
		txtNum = new JSpinner();
	    JComponent comp = txtNum.getEditor();
	    JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
	    DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
	    formatter.setCommitsOnValidEdit(true);
	    txtNum.addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	        	checkNum.setSelected(true);		        
			}
	    });
		txtNum.addChangeListener(new TxtNumChangeListener());
		txtNum.addFocusListener(new TxtNumFocusListener());
		txtNum.setModel(new SpinnerNumberModel(new Integer(getStartNum()), new Integer(0), null, new Integer(1)));
		txtNum.setBounds(80, 47, 133, 20);
		contentPanel.add(txtNum);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Save changes");
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
		txtName.setCaretPosition(0);
		txtAlbum.setCaretPosition(0);
		txtArtist.setCaretPosition(0);
		txtGenre.setCaretPosition(0);
	}
	
	private int getStartNum(){
		boolean allsame = true;
		for(int i=0; i<tracks.size()-1; ++i)
			if(tracks.get(i).getTrackNumber() < 0 || tracks.get(i+1).getTrackNumber() < 0 || tracks.get(i).getTrackNumber() != tracks.get(i+1).getTrackNumber())
				allsame = false;
		if(allsame)
			return tracks.get(0).getTrackNumber();
		else
			return 1;
	}
	
	private String getStartName(){
		boolean allsame = true;
		for(int i=0; i<tracks.size()-1; ++i)
			if(tracks.get(i).getName() == null || tracks.get(i+1).getName() == null || !tracks.get(i).getName().toLowerCase().equals(tracks.get(i+1).getName().toLowerCase()))
				allsame = false;
		if(allsame)
			return tracks.get(0).getName();
		else
			return "";
	}
	
	private String getStartAlbum(){
		boolean allsame = true;
		for(int i=0; i<tracks.size()-1; ++i)
			if(tracks.get(i).getAlbum() == null || tracks.get(i+1).getAlbum() == null || !tracks.get(i).getAlbum().toLowerCase().equals(tracks.get(i+1).getAlbum().toLowerCase()))
				allsame = false;
		if(allsame)
			return tracks.get(0).getAlbum();
		else
			return "";
	}
	
	private String getStartArtist(){
		boolean allsame = true;
		for(int i=0; i<tracks.size()-1; ++i)
			if(tracks.get(i).getArtist() == null || tracks.get(i+1).getArtist() == null || !tracks.get(i).getArtist().toLowerCase().equals(tracks.get(i+1).getArtist().toLowerCase()))
				allsame = false;
		if(allsame)
			return tracks.get(0).getArtist();
		else
			return "";
	}
	
	private String getStartGenre(){
		boolean allsame = true;
		for(int i=0; i<tracks.size()-1; ++i)
			if(tracks.get(i).getGenre() == null || tracks.get(i+1).getGenre() == null || !tracks.get(i).getGenre().toLowerCase().equals(tracks.get(i+1).getGenre().toLowerCase()))
				allsame = false;
		if(allsame)
			return tracks.get(0).getGenre();
		else
			return "";
	}
	
	private class OkButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			for(int i=0; i<tracks.size(); ++i){
				Song track = tracks.get(i);
				if(checkPlays.isSelected()){
					track.setPlayCount(0);
				}
				if(checkNum.isSelected()){
					track.setTrackNumber((Integer) txtNum.getValue());
				}
				if(checkName.isSelected()){
					track.setName(txtName.getText());	
				}
				if(checkAlbum.isSelected()){
					track.setAlbum(txtAlbum.getText());
				}
				if(checkArtist.isSelected()){
					track.setArtist(txtArtist.getText());
				}
				if(checkGenre.isSelected()){
					track.setGenre(txtGenre.getText());
				}
			}
			dispose();
		}
	}
	private class TxtNumFocusListener extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent arg0) {
			checkNum.setSelected(true);
			
		}
	}
	private class TxtNameFocusListener extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent e) {
			checkName.setSelected(true);
			txtName.selectAll();
		}
	}
	private class TxtAlbumFocusListener extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent e) {
			checkAlbum.setSelected(true);
			txtAlbum.selectAll();
		}
	}
	private class TxtArtistFocusListener extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent e) {
			checkArtist.setSelected(true);
			txtArtist.selectAll();
		}
	}
	private class TxtGenreFocusListener extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent e) {
			checkGenre.setSelected(true);
			txtGenre.selectAll();
		}
	}
	private class CancelButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}
	private class LblResetPlayCountMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			checkPlays.setSelected(!checkPlays.isSelected());
		}
	}
	private class TxtNumChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent arg0) {
			checkNum.setSelected(true);
		}
	}
}
