package jk509.player;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

public class ParseItunesDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtPath;
	private JButton btnOk;
	private JButton btnCancel;
	LibraryParser parser;
	JFileChooser fc;

	/**
	 * Create the dialog.
	 */
	public ParseItunesDialog(LibraryParser parser) {
		this.parser = parser;
		setTitle("Import from iTunes");
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MusicPlayer.class.getResource("/jk509/player/res/icon.png")));
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 530, 250);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			txtPath = new JTextField();
			txtPath.setBounds(35, 74, 367, 21);
			contentPanel.add(txtPath);
			txtPath.setColumns(8);
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
			JLabel lblItunesMusicLibrary = new JLabel("iTunes music library location:");
			lblItunesMusicLibrary.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblItunesMusicLibrary.setBounds(35, 49, 247, 20);
			contentPanel.add(lblItunesMusicLibrary);
		}
		{
			JButton btnBrowse = new JButton("Browse...");
			btnBrowse.addActionListener(new BtnBrowseActionListener());
			btnBrowse.setBounds(405, 73, 79, 23);
			contentPanel.add(btnBrowse);
		}
		{
			JLabel lblTheLibraryFile = new JLabel("The library file must end in '.xml'");
			lblTheLibraryFile.setBounds(35, 116, 153, 14);
			contentPanel.add(lblTheLibraryFile);
		}
		{
			JLabel lblegCusersmemusic = new JLabel("(e.g. C:\\Users\\Me\\Music\\iTunes\\iTunes Music Library.xml)");
			lblegCusersmemusic.setBounds(35, 141, 276, 14);
			contentPanel.add(lblegCusersmemusic);
		}
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
		{
			String homedir = System.getenv("user.home");
			if (homedir == null)
				homedir = System.getenv("USERPROFILE");
			String path = homedir + "\\Music\\iTunes\\iTunes Music Library.xml";
			if((new File(path)).exists()){
				JLabel lblFound = new JLabel("Found an iTunes library: "+path);
				lblFound.setBounds(35, 24, 450, 14);
				lblFound.setForeground(new Color(0, 128, 0));
				contentPanel.add(lblFound);
				txtPath.setText(path);
				CheckValidPath();
			}
		}
	}
	
	private void CheckValidPath(){
		String path = txtPath.getText();
		if(path.endsWith(".xml") && (new File(path)).exists()){
			btnOk.setEnabled(true);
		}else{
			btnOk.setEnabled(false);
		}
	}
	
	public File addExt(File f, String ext){
		String path = f.getAbsolutePath();
	
	    if(!path.endsWith(ext))
	    {
	      f = new File(path + ext);
	    }
	    
	    return f;
	}
	
	/*
     * Get the extension of a file.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

	private class CancelButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
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
			fc = new JFileChooser();
			fc.setFileFilter(new OBJfilter());
			//fc.type = 1;
			
			String startat = "";
			// choose initial folder (first check textfield, otherwise try user home dir)
			try{
				if(new File((new File(txtPath.getText())).getParent()).exists())
					startat = (new File(txtPath.getText())).getParent();
				else
					startat = "";
			}catch(Exception e){
				startat = "";
			}
			
			fc.setCurrentDirectory(new File(startat));

			int returnVal = fc.showOpenDialog((Component) arg0.getSource());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				txtPath.setText(file.getPath());
			}else{
				//System.out.println("not loaded");
			}
		}
	}
	public class OpenFileChooser extends JFileChooser {
		
		private static final long serialVersionUID = 1L;
		public int type; // 1=ser, 2=xml

		@Override
	    public void approveSelection(){
	        File f = getSelectedFile();
	        if((f.exists() || (addExt(f,".xml").exists() && type==2) || (addExt(f,".ser").exists() && type==1) ) && getDialogType() == OPEN_DIALOG){
	            int result = JOptionPane.showConfirmDialog(this,"The file already exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
	            switch(result){
	                case JOptionPane.YES_OPTION:
	                    super.approveSelection();
	                    return;
	                case JOptionPane.NO_OPTION:
	                    return;
	                case JOptionPane.CLOSED_OPTION:
	                    return;
	                case JOptionPane.CANCEL_OPTION:
	                    cancelSelection();
	                    return;
	            }
	        }
	        super.approveSelection();
	    }
	
	}
	class OBJfilter extends FileFilter {
	
	public OBJfilter(){
		super();	
	}
		
	@Override
	public String getDescription(){
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
