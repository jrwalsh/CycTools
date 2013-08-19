package edu.iastate.cyctools.view;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.InternalStateModel.State;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {
	DefaultController controller;
	
	private static JMenu mnFile;
	private static JMenu mnAbout;
	private static JMenuItem mntmDisconnect;
	private static JMenuItem mntmExit;
	private static JMenu mnEdit;
	private final Action actionDisconnect = new ActionDisconnect();
	private final Action actionExit = new ActionExit();
	private JMenuItem mntmAbout;
	private final Action actionAbout = new ActionAbout();
	private JMenuItem mntmRevertKb;
	private final Action actionRevertKB = new ActionRevertKB();

	/**
	 * Create the frame.
	 */
	public MenuBar(DefaultController controller) {
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
		
		mntmRevertKb = new JMenuItem("Revert KB");
		mntmRevertKb.setAction(actionRevertKB);
		mnEdit.add(mntmRevertKb);
		
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
			JOptionPane.showMessageDialog(DefaultController.mainJFrame, "This program was written by Jesse Walsh", "About", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	private class ActionRevertKB extends AbstractAction {
		public ActionRevertKB() {
			putValue(NAME, "Revert KB");
			putValue(SHORT_DESCRIPTION, "Undo all changes to this KB since last save.");
		}
		public void actionPerformed(ActionEvent e) {
			int n = JOptionPane.showConfirmDialog(DefaultController.mainJFrame,
					"Reverting a database will undo all changes since the last save. This will undo all changes to KB\n" + 
					controller.getSelectedOrganism() + "\n\n" + 
					"Do you wish to continue?",
					"Revert KB",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (n == 0) {
				controller.revertDataBase();
				JOptionPane.showMessageDialog(DefaultController.mainJFrame, "Database reverted to previous version.", "Database revert performed", JOptionPane.INFORMATION_MESSAGE);
				controller.toolPanel.refreshOrganismList();
			}
		}
	}
}
