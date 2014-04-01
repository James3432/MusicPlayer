package jk509.player;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;

public class ParseDiskDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JButton btnOk;
	private JButton btnCancel;
	private JTextField txtPath;
	LibraryParser parser;
	private JLabel lblWarningThisWill;

	/**
	 * Create the dialog.
	 */
	public ParseDiskDialog(LibraryParser parser, boolean hideWarning) {
		this.parser = parser;
		setTitle("Import from Disk");
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon.png")));
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 531, 206);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblFolderToImport = new JLabel("Folder to import music from:");
			lblFolderToImport.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblFolderToImport.setBounds(36, 34, 213, 28);
			contentPanel.add(lblFolderToImport);
		}
		{
			txtPath = new JTextField();
			txtPath.setBounds(36, 73, 367, 21);
			contentPanel.add(txtPath);
			txtPath.setColumns(10);
			txtPath.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent arg0) {
				}

				@Override
				public void insertUpdate(DocumentEvent arg0) {
					CheckValidPath();
				}

				@Override
				public void removeUpdate(DocumentEvent arg0) {
					CheckValidPath();
				}
			});
		}
		{
			JButton btnBrowse = new JButton("Browse...");
			btnBrowse.addActionListener(new BtnBrowseActionListener());
			btnBrowse.setBounds(406, 72, 79, 23);
			contentPanel.add(btnBrowse);
		}
		
		lblWarningThisWill = new JLabel("Warning: this will replace all music currently in your library");
		lblWarningThisWill.setForeground(Color.RED);
		lblWarningThisWill.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblWarningThisWill.setBounds(36, 105, 385, 14);
		if(hideWarning)
			lblWarningThisWill.setVisible(false);
		contentPanel.add(lblWarningThisWill);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnOk = new JButton("OK");
				btnOk.setEnabled(false);
				btnOk.addActionListener(new OkButtonActionListener());
				btnOk.setActionCommand("OK");
				buttonPane.add(btnOk);
				getRootPane().setDefaultButton(btnOk);
			}
			{
				btnCancel = new JButton("Cancel");
				btnCancel.addActionListener(new CancelButtonActionListener());
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
			}
		}
	}
	
	private void CheckValidPath() {
		String path = txtPath.getText();
		if (path != null && !path.equals("") && (new File(path)).exists()) {
			btnOk.setEnabled(true);
		} else {
			btnOk.setEnabled(false);
		}
	}

	private class CancelButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			parser.setValid(false);
			dispose();
		}
	}

	private class OkButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			parser.setPath(txtPath.getText());
			parser.setValid(true);
			dispose();
		}
	}

	private class BtnBrowseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser = new JFileChooser();
			
			String startat = "";
			// choose initial folder (first check textfield, otherwise try user home dir)
			try{
				if((new File(txtPath.getText())).exists() && (new File(txtPath.getText())).isDirectory())
					startat = (new File(txtPath.getText())).getPath();
				else
					startat = "";
			}catch(Exception e){
				startat = "";
			}
			
			if(startat.equals("")){
				startat = System.getenv("user.home");
				if (startat == null || startat.equals(""))
					startat = System.getenv("USERPROFILE");
				if (startat == null || startat.equals(""))
					startat = ".";
			}
			chooser.setCurrentDirectory(new File(startat));
			chooser.setDialogTitle("Select music folder");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			// disable the "All files" option.
			chooser.setAcceptAllFileFilterUsed(false);

			int result = chooser.showOpenDialog((Component) arg0.getSource());
			if (result == JFileChooser.APPROVE_OPTION) {
				txtPath.setText(chooser.getSelectedFile().getPath());
			} else {
				//System.out.println("No Selection ");
				//System.out.println(result);
			}

		}
	}
}
