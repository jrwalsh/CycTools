package edu.iastate.biocyctool.cycBrowser.view;

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel.State;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {
	BrowserController controller;
	
	private static JMenu mnFile;
	private static JMenu mnAbout;
	private static JMenuItem mntmDisconnect;
	private static JMenuItem mntmExit;
	private static JMenu mnEdit;
	private final Action actionDisconnect = new ActionDisconnect();
	private final Action actionExit = new ActionExit();
	private JMenuItem mntmAbout;
	private final Action actionAbout = new ActionAbout();

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
		
		mntmDisconnect = new JMenuItem("Disconnect");
		mntmDisconnect.setAction(actionDisconnect);
		mnFile.add(mntmDisconnect);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.setAction(actionExit);
		mnFile.add(mntmExit);
		
		mnEdit = new JMenu("Edit");
		add(mnEdit);
		
		mnAbout = new JMenu("About");
		add(mnAbout);
		
		mntmAbout = new JMenuItem("About");
		mntmAbout.setAction(actionAbout);
		mnAbout.add(mntmAbout);
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
	
	private class ActionExit extends AbstractAction {
		public ActionExit() {
			putValue(NAME, "Exit");
			putValue(SHORT_DESCRIPTION, "Exit program.");
		}
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	private class ActionAbout extends AbstractAction {
		public ActionAbout() {
			putValue(NAME, "About");
			putValue(SHORT_DESCRIPTION, "About this program.");
		}
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(BrowserController.mainJFrame, "This program was written by Jesse Walsh", "About", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
