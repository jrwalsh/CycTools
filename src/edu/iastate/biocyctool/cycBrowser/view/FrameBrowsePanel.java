package edu.iastate.biocyctool.cycBrowser.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import java.awt.Component;
import javax.swing.JSeparator;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.EtchedBorder;
import javax.swing.text.DefaultEditorKit;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JTabbedPane;
import javax.swing.JProgressBar;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JTree;
import java.awt.Dimension;
import javax.swing.JComboBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
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

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel.State;
import edu.iastate.biocyctool.util.da.CycDataBaseAccess;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;

public class FrameBrowsePanel extends AbstractViewPanel {
	BrowserController controller;

	private JPanel panel;
	private JPanel textPanel;
	private final Action actionSubmit = new ActionSubmit();
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private final Action actionDisconnect = new ActionDisconnect();
	private JPanel panel_2;
	private JTextField txtEnterFrameid;
	private JButton button;
	
	/**
	 * Create the frame.
	 */
	public FrameBrowsePanel(BrowserController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
		setMinimumSize(new Dimension(800, 525));
		setName("SimpleBrowser");
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		add(panel, BorderLayout.NORTH);
		
		panel_2 = new JPanel();
		panel.add(panel_2);
		
		txtEnterFrameid = new JTextField();
		txtEnterFrameid.setColumns(10);
		panel_2.add(txtEnterFrameid);
		
		button = new JButton("Search");
		button.setAction(actionSubmit);
		panel_2.add(button);
		
		textPanel = new JPanel();
		add(textPanel, BorderLayout.CENTER);
		GridBagLayout gbl_textPanel = new GridBagLayout();
		gbl_textPanel.columnWidths = new int[]{503, 0};
		gbl_textPanel.rowHeights = new int[]{132, 0};
		gbl_textPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_textPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		textPanel.setLayout(gbl_textPanel);
		
		scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(10, 10));
		
		textArea = new JTextArea();
		textArea.setPreferredSize(new Dimension(4, 50));
		textArea.setMinimumSize(new Dimension(10, 10));
		textArea.setWrapStyleWord(true);
		textArea.setRows(7);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setColumns(70);
		scrollPane.setViewportView(textArea);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		textPanel.add(scrollPane, gbc_scrollPane);
	}
	
	
	
	//http://www.coderanch.com/t/346220/GUI/java/Copy-paste-popup-menu
    private void installContextMenu(final JTextField component) {
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
			textArea.setText(controller.frameToString(txtEnterFrameid.getText()));
		}
	}
	
	private class ActionDisconnect extends AbstractAction {
		public ActionDisconnect() {
			putValue(NAME, "Disconnect");
			putValue(SHORT_DESCRIPTION, "Disconnect from database.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.disconnect();
//			da = null;
//			updateContent();
		}
		
		private void updateContent() {
//			comboBoxOrganism.removeAllItems();
//			comboBoxOrganism.setEnabled(false);
//			
//			txtEnterFrameid.setEnabled(false);
//			txtServer.setEnabled(true);
//			txtPort.setEnabled(true);
//			lblNotConnected.setText("Not Connected");
//			actionDisconnect.setEnabled(false);
//			actionSubmit.setEnabled(false);
		}
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		
	}
	
}
