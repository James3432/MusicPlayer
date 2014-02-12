package jk509.player;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class FormTest {

	private JFrame frame;
	private JScrollPane scrollPane;
	private JTable table;
	private JButton btnImportFromItunes;

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
					FormTest window = new FormTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FormTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 838, 539);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		scrollPane = new JScrollPane();
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(30,1));
		//scrollPane.getVerticalScrollBar()
		frame.getContentPane().add(scrollPane);
		
		table = new JTable();
		table.setShowVerticalLines(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"stuff", null, "awsef", null, null},
				{null, "asd", null, null, "we"},
				{null, null, "sdef", "wef", "wef"},
				{null, "EWF", "asf", null, null},
				{null, null, null, null, "wEF"},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, "fwef", "WEF", null, null},
				{null, "wqefr", null, "wef", null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, "WEF", "WEF"},
			},
			new String[] {
				"Name", "Album", "Artist", "Genre", "Date added"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class, String.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, false, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		scrollPane.setViewportView(table);
		table.setModel(new TableSorter(table.getModel(), table.getTableHeader()));
		
		btnImportFromItunes = new JButton("Import from iTunes");
		btnImportFromItunes.addActionListener(new BtnImportFromItunesActionListener());
		frame.getContentPane().add(btnImportFromItunes);
	}
	
	public enum Genre { Classical, Rock }
	
	private class BtnImportFromItunesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			LibraryParser parser = new ItunesParser("E:\\Users\\James\\Music\\iTunes\\iTunes Music Library.xml");
			
			parser.run();
			
			Object[][] rows = new Object[parser.trackCount()][5];
			for(int i=0; i< parser.getTracks().size(); ++i){
				ItunesParser.Song s = parser.getTracks().get(i);
				rows[i][0] = s.getName();
				rows[i][1] = s.getAlbum();
				rows[i][2] = s.getArtist();
				rows[i][3] = s.getGenre();
				rows[i][4] = (new SimpleDateFormat("dd/MM/yyyy")).format(s.getDateAdded());
			}
			
			table.setModel(new DefaultTableModel(
					rows,
					new String[] {
						"Name", "Album", "Artist", "Genre", "Date added"
					}
				) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;
					@SuppressWarnings("rawtypes")
					Class[] columnTypes = new Class[] {
						String.class, String.class, String.class, String.class, String.class
					};
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
					boolean[] columnEditables = new boolean[] {
						false, false, false, false, false
					};
					public boolean isCellEditable(int row, int column) {
						return columnEditables[column];
					}
				});
			table.setModel(new TableSorter(table.getModel(), table.getTableHeader()));
		}
	}
}
