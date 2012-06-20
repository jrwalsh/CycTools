package simpleCycBrowser;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.TextArea;
import java.awt.TextField;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import java.awt.Component;
import javax.swing.JSeparator;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JTabbedPane;
import javax.swing.JProgressBar;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JTree;
import java.awt.Dimension;
import javax.swing.JComboBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JSplitPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;


public class SimpleBrowser extends JFrame {
	QueryEngine query = new QueryEngine();

	private JPanel contentPane;
	private JPanel panel;
	private JTextField txtEnterFrameid;
	private JButton btnSearch;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnAbout;
	private JMenuItem mntmNewMenuItem;
	private JPanel panel_2;
	private JTextField txtServer;
	private JTextField txtPort;
	private JButton btnConnect;
	private JLabel lblConnection;
	private JTabbedPane tabbedPane;
	private JPanel panel_1;
	private JPanel panel_3;
	private JLabel lblCurrentAction;
	private JProgressBar progressBar;
	private final Action actionSubmit = new ActionSubmit();
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JLabel lblNotConnected;
	private final Action actionConnect = new ActionConnect();
	private JPanel panel_4;
	private JScrollPane scrollPane_1;
	private JTree tree;
	private JScrollPane scrollPane_2;
	private JTextArea textArea_1;
	private JScrollPane scrollPane_3;
	private JTree tree_1;
	private JMenu mnEdit;
	private JMenuItem mntmUndo;
	private JMenuItem mntmRedo;
	private JSeparator separator;
	private JMenuItem mntmCut;
	private JMenuItem mntmCopy;
	private JMenuItem mntmPaste;
	private JButton btnDisconnect;
	private final Action actionDisconnect = new ActionDisconnect();
	private JComboBox comboBoxOrganism;
	private final Action actionLoadTable = new ActionLoadTable();
	private JSplitPane splitPane;
	private JPanel panel_5;
	private JButton btnLoadFile;
	private JTextField txtFilePath;
	private JScrollPane scrollPane_4;
	private JButton btnTestAction;
	private final Action actionTest = new ActionTest();
	private JButton btnRevertDb;
	private final Action actionRevertDB = new ActionRevertDB();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimpleBrowser frame = new SimpleBrowser();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SimpleBrowser() {
		actionSubmit.setEnabled(false);
		actionDisconnect.setEnabled(false);
		setMinimumSize(new Dimension(800, 525));
		setTitle("SimpleBrowser");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 525);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmNewMenuItem = new JMenuItem("Exit");
		mnFile.add(mntmNewMenuItem);
		
		mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		mntmUndo = new JMenuItem("Undo");
		mnEdit.add(mntmUndo);
		
		mntmRedo = new JMenuItem("Redo");
		mnEdit.add(mntmRedo);
		
		separator = new JSeparator();
		mnEdit.add(separator);
		
		mntmCut = new JMenuItem("Cut");
		mnEdit.add(mntmCut);
		
		mntmCopy = new JMenuItem("Copy");
		mnEdit.add(mntmCopy);
		
		mntmPaste = new JMenuItem("Paste");
		mnEdit.add(mntmPaste);
		
		mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		panel_4 = new JPanel();
		panel.add(panel_4);
		
		txtEnterFrameid = new JTextField();
		txtEnterFrameid.setEnabled(false);
		panel_4.add(txtEnterFrameid);
		txtEnterFrameid.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit");
		txtEnterFrameid.getActionMap().put("submit", actionSubmit);
		txtEnterFrameid.setColumns(10);
		
		btnSearch = new JButton("Search");
		panel_4.add(btnSearch);
		btnSearch.setAction(actionSubmit);
		
		panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.add(panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{160, 86, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 23, 0, 0};
		gbl_panel_2.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		lblConnection = new JLabel("Connection");
		GridBagConstraints gbc_lblConnection = new GridBagConstraints();
		gbc_lblConnection.gridwidth = 2;
		gbc_lblConnection.insets = new Insets(0, 0, 5, 0);
		gbc_lblConnection.gridx = 0;
		gbc_lblConnection.gridy = 0;
		panel_2.add(lblConnection, gbc_lblConnection);
		
		txtServer = new JTextField();
		txtServer.setText("jrwalsh.student.iastate.edu");
		GridBagConstraints gbc_txtServer = new GridBagConstraints();
		gbc_txtServer.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtServer.insets = new Insets(0, 0, 5, 5);
		gbc_txtServer.gridx = 0;
		gbc_txtServer.gridy = 1;
		panel_2.add(txtServer, gbc_txtServer);
		txtServer.setColumns(10);
		
		lblNotConnected = new JLabel("not connected");
		GridBagConstraints gbc_lblNotConnected = new GridBagConstraints();
		gbc_lblNotConnected.insets = new Insets(0, 0, 5, 0);
		gbc_lblNotConnected.gridx = 1;
		gbc_lblNotConnected.gridy = 1;
		panel_2.add(lblNotConnected, gbc_lblNotConnected);
		
		txtPort = new JTextField();
		txtPort.setText("4444");
		GridBagConstraints gbc_txtPort = new GridBagConstraints();
		gbc_txtPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPort.insets = new Insets(0, 0, 5, 5);
		gbc_txtPort.gridx = 0;
		gbc_txtPort.gridy = 2;
		panel_2.add(txtPort, gbc_txtPort);
		txtPort.setColumns(10);
		
		btnConnect = new JButton("Connect");
		btnConnect.setAction(actionConnect);
		GridBagConstraints gbc_btnConnect = new GridBagConstraints();
		gbc_btnConnect.insets = new Insets(0, 0, 5, 0);
		gbc_btnConnect.anchor = GridBagConstraints.SOUTH;
		gbc_btnConnect.gridx = 1;
		gbc_btnConnect.gridy = 2;
		panel_2.add(btnConnect, gbc_btnConnect);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		scrollPane = new JScrollPane();
		tabbedPane.addTab("Frame", null, scrollPane, null);
		
		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setRows(7);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setColumns(70);
		scrollPane.setViewportView(textArea);
		
		scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("TreeView", null, scrollPane_1, null);
		
		tree = new JTree();
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("JTree") {
				{
				}
			}
		));
		tree.setRootVisible(false);
		scrollPane_1.setViewportView(tree);
		
		scrollPane_2 = new JScrollPane();
		tabbedPane.addTab("Template", null, scrollPane_2, null);
		
		textArea_1 = new JTextArea();
		scrollPane_2.setViewportView(textArea_1);
		
		scrollPane_3 = new JScrollPane();
		tabbedPane.addTab("Hierarchy", null, scrollPane_3, null);
		
		tree_1 = new JTree();
		scrollPane_3.setViewportView(tree_1);
		
		panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		contentPane.add(panel_1, BorderLayout.WEST);
		
		panel_3 = new JPanel();
		contentPane.add(panel_3, BorderLayout.SOUTH);
		
		lblCurrentAction = new JLabel("Current Action");
		panel_3.add(lblCurrentAction);
		
		progressBar = new JProgressBar();
		panel_3.add(progressBar);
		
		
		
		// Manual Code
		//TODO
		installContextMenu(txtEnterFrameid);
		installContextMenu(txtServer);
		installContextMenu(txtPort);
		
		//================
		
		
		
		comboBoxOrganism = new JComboBox();
		comboBoxOrganism.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (comboBoxOrganism.getItemCount() != 0) {
					query.selectOrganism(comboBoxOrganism.getSelectedItem().toString());
				}
			}
		});
		comboBoxOrganism.setEnabled(false);
		GridBagConstraints gbc_comboBoxOrganism = new GridBagConstraints();
		gbc_comboBoxOrganism.insets = new Insets(0, 0, 0, 5);
		gbc_comboBoxOrganism.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxOrganism.gridx = 0;
		gbc_comboBoxOrganism.gridy = 3;
		panel_2.add(comboBoxOrganism, gbc_comboBoxOrganism);
		
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.setAction(actionDisconnect);
		GridBagConstraints gbc_btnDisconnect = new GridBagConstraints();
		gbc_btnDisconnect.gridx = 1;
		gbc_btnDisconnect.gridy = 3;
		panel_2.add(btnDisconnect, gbc_btnDisconnect);
		
		btnTestAction = new JButton("Test Action");
		btnTestAction.setAction(actionTest);
		panel.add(btnTestAction);
		
		btnRevertDb = new JButton("Revert DB");
		btnRevertDb.setAction(actionRevertDB);
		panel.add(btnRevertDb);
		installContextMenu(textArea);
		
		splitPane = new JSplitPane();
		tabbedPane.addTab("Bulk Load", null, splitPane, null);
		
		scrollPane_4 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_4);
		
		panel_5 = new JPanel();
		splitPane.setLeftComponent(panel_5);
		
		btnLoadFile = new JButton("Load");
		btnLoadFile.setAction(actionLoadTable);
		
		txtFilePath = new JTextField();
		txtFilePath.setText("C:\\Users\\Jesse\\Desktop\\test.csv");
		txtFilePath.setColumns(10);
		panel_5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel_5.add(btnLoadFile);
		panel_5.add(txtFilePath);
	}
	
	
	
	//http://www.coderanch.com/t/346220/GUI/java/Copy-paste-popup-menu
    private void installContextMenu(final JTextField component) {
    	component.addMouseListener(new MouseAdapter() {
    		public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void showMenu(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					final JPopupMenu menu = new JPopupMenu();
					JMenuItem item;
					item = new JMenuItem(new DefaultEditorKit.CopyAction());
					item.setText("Copy");
					item.setEnabled(component.getSelectionStart() != component.getSelectionEnd());
					menu.add(item);
					item = new JMenuItem(new DefaultEditorKit.CutAction());
					item.setText("Cut");
					item.setEnabled(component.isEditable() && component.getSelectionStart() != component.getSelectionEnd());
					menu.add(item);
					item = new JMenuItem(new DefaultEditorKit.PasteAction());
					item.setText("Paste");
					item.setEnabled(component.isEditable());
					menu.add(item);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
    	});
    }
    private void installContextMenu(final JTextArea component) {
    	component.addMouseListener(new MouseAdapter() {
    		public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void showMenu(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					final JPopupMenu menu = new JPopupMenu();
					JMenuItem item;
					item = new JMenuItem(new DefaultEditorKit.CopyAction());
					item.setText("Copy");
					item.setEnabled(component.getSelectionStart() != component.getSelectionEnd());
					menu.add(item);
					item = new JMenuItem(new DefaultEditorKit.CutAction());
					item.setText("Cut");
					item.setEnabled(component.isEditable() && component.getSelectionStart() != component.getSelectionEnd());
					menu.add(item);
					item = new JMenuItem(new DefaultEditorKit.PasteAction());
					item.setText("Paste");
					item.setEnabled(component.isEditable());
					menu.add(item);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
    	});
    }
	
	
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	private class ActionSubmit extends AbstractAction {
		public ActionSubmit() {
			putValue(NAME, "Submit");
			putValue(SHORT_DESCRIPTION, "Submits query from text field and returns results in the primary tab.");
		}
		public void actionPerformed(ActionEvent e) {
			textArea.setText(query.frameToString(txtEnterFrameid.getText()));
			tree = new JTree(query.frameToTree(txtEnterFrameid.getText()));
			scrollPane_1.setViewportView(tree);
		}
	}
	private class ActionConnect extends AbstractAction {
		public ActionConnect() {
			putValue(NAME, "Connect");
			putValue(SHORT_DESCRIPTION, "Initiallizes the connection to a database using JavaCycO.");
		}
		public void actionPerformed(ActionEvent e) {
			try {
				query.connect(txtServer.getText(), Integer.parseInt(txtPort.getText()));
			} catch (Exception exception) {
				
			}
			updateContent();
		}
		private void updateContent() {
			if (query.isConnected()) {
				for (String org : query.getAvailableOrganisms()) {
					comboBoxOrganism.addItem(org);
				}
				comboBoxOrganism.setEnabled(true);
				
				txtEnterFrameid.setEnabled(true);
				txtServer.setEnabled(false);
				txtPort.setEnabled(false);
				lblNotConnected.setText("Connected");
				actionConnect.setEnabled(false);
				actionDisconnect.setEnabled(true);
				actionSubmit.setEnabled(true);
			} else {
				comboBoxOrganism.removeAllItems();
				comboBoxOrganism.setEnabled(false);
				
				txtEnterFrameid.setEnabled(false);
				txtServer.setEnabled(true);
				txtPort.setEnabled(true);
				lblNotConnected.setText("Not Connected");
				actionConnect.setEnabled(true);
				actionDisconnect.setEnabled(false);
				actionSubmit.setEnabled(false);
			}
		}
	}
	private class ActionDisconnect extends AbstractAction {
		public ActionDisconnect() {
			putValue(NAME, "Disconnect");
			putValue(SHORT_DESCRIPTION, "Disconnect from database.");
		}
		public void actionPerformed(ActionEvent e) {
			query.disconnect();
			updateContent();
		}
		
		private void updateContent() {
			comboBoxOrganism.removeAllItems();
			comboBoxOrganism.setEnabled(false);
			
			txtEnterFrameid.setEnabled(false);
			txtServer.setEnabled(true);
			txtPort.setEnabled(true);
			lblNotConnected.setText("Not Connected");
			actionConnect.setEnabled(true);
			actionDisconnect.setEnabled(false);
			actionSubmit.setEnabled(false);
		}
	}
	private class ActionLoadTable extends AbstractAction {
		public ActionLoadTable() {
			putValue(NAME, "LoadTable");
			putValue(SHORT_DESCRIPTION, "Open an excel file into the Bulk Load tab.");
		}
		public void actionPerformed(ActionEvent e) {
			String excelFileName = txtFilePath.getText();
			File file = new File(excelFileName);   //excelFileName is the file you want to load.
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String text = null;
				
				String header = reader.readLine();
				ArrayList<String> dataRows = new ArrayList<String>();
				while ((text = reader.readLine()) != null) {
					dataRows.add(text);
				}
				
				String[] headings = header.split(",");
				Object[][] data = new Object[dataRows.size()][dataRows.get(0).split(",").length];
				for (int i=0; i<dataRows.size(); i++) {
					data[i] = dataRows.get(i).split(",");
				}
				
				JTable jTable = new JTable(data, headings);
				scrollPane_4.setViewportView(jTable);
				
			} catch (FileNotFoundException exception) {
				exception.printStackTrace();
			} catch (IOException exception) {
				exception.printStackTrace();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			finally {
				try {
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}
	}
	private class ActionTest extends AbstractAction {
		public ActionTest() {
			putValue(NAME, "Test Action");
			putValue(SHORT_DESCRIPTION, "Testing stuff");
		}
		public void actionPerformed(ActionEvent e) {
			query.specificLoadOfGoTerms(txtFilePath.getText());
		}
	}
	private class ActionRevertDB extends AbstractAction {
		public ActionRevertDB() {
			putValue(NAME, "Revert DB");
			putValue(SHORT_DESCRIPTION, "Revert currently connected database, losing all unsaved changes.");
		}
		public void actionPerformed(ActionEvent e) {
			query.revertDB();
		}
	}
}



/*
http://docs.oracle.com/javase/tutorial/uiswing/components/generaltext.html#undoableedits

//Declare these variables:
	protected UndoManager undo = new UndoManager();
	private UndoAction undoAction;
	private RedoAction redoAction;

//Include this code in the main initiation stack:
	undoAction = new UndoAction();
	mntmUndo_1.setAction(undoAction);

	redoAction = new RedoAction();
	mntmRedo_1.setAction(redoAction);
	
	installUndoRedoManager(txtPort);
	installUndoRedoManager(txtOrganism);

//Implement listener:
 	private void installUndoRedoManager(final JTextField component) {
		component.getDocument().addUndoableEditListener(new MyUndoableEditListener());
	}
	protected class MyUndoableEditListener implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			//Remember the edit and update the menus
			undo.addEdit(e.getEdit());
			undoAction.updateUndoState();
			redoAction.updateRedoState();
		}
	} 

//Create classes: (implemente *State() methods)
	private class UndoAction  extends AbstractAction {
		public UndoAction () {
			putValue(NAME, "Undo");
			putValue(SHORT_DESCRIPTION, "");
		}
		public void actionPerformed(ActionEvent e) {
		    try {
		        undo.undo();
		    } catch (CannotUndoException ex) {
		        System.out.println("Unable to undo: " + ex);
		        ex.printStackTrace();
		    }
		    updateUndoState();
		    redoAction.updateRedoState();
		}
		private void updateUndoState() {
			// TODO Auto-generated method stub
			
		}
	}
	private class RedoAction  extends AbstractAction {
		public RedoAction () {
			putValue(NAME, "Redo");
			putValue(SHORT_DESCRIPTION, "");
		}
		public void actionPerformed(ActionEvent e) {
		    try {
		        undo.redo();
		    } catch (CannotRedoException ex) {
		        System.out.println("Unable to redo: " + ex);
		        ex.printStackTrace();
		    }
		    updateRedoState();
		    undoAction.updateUndoState();
		}
		private void updateRedoState() {
			// TODO Auto-generated method stub
			
		}
	}
	
//Look further into javax.swing.undo.UndoableEditSupport class
*/
