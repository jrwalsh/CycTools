package edu.iastate.biocyctool.cycBrowser.view;

import java.beans.PropertyChangeEvent;

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel.State;
import edu.iastate.biocyctool.cycspreadsheetloader.controller.DefaultController;
import edu.iastate.biocyctool.util.da.CycDataBaseAccess;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;

public class MenuBar extends JMenuBar {
	BrowserController controller;
	
	private static JMenu mnFile;
	private static JMenu mnAbout;
	private static JMenuItem mntmConnect;
	private static JMenuItem mntmDisconnect;
	private static JMenuItem mntmExit;
	private static JMenu mnEdit;
	private static JMenuItem mntmUndo;
	private static JMenuItem mntmRedo;
	private static JSeparator separator;
	private static JMenuItem mntmCut;
	private static JMenuItem mntmCopy;
	private static JMenuItem mntmPaste;
	private final Action actionDisconnect = new ActionDisconnect();

	/**
	 * Create the frame.
	 */
	public MenuBar(BrowserController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
    	mnFile = new JMenu("File");
		add(mnFile);
		
		mntmConnect = new JMenuItem("Connect");
		mnFile.add(mntmConnect);
		
		mntmDisconnect = new JMenuItem("Disconnect");
		mntmDisconnect.setAction(actionDisconnect);
		mnFile.add(mntmDisconnect);
		
		mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		mnEdit = new JMenu("Edit");
		add(mnEdit);
		
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
		add(mnAbout);
	}
    
	private class ActionDisconnect extends AbstractAction {
		public ActionDisconnect() {
			putValue(NAME, "Disconnect");
			putValue(SHORT_DESCRIPTION, "Disconnect from Pathway Tools server.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.NOT_CONNECTED);
		}
	}
}
