package edu.iastate.cyctools.view;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.InternalStateModel.State;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.Protein;
import edu.iastate.javacyco.PtoolsErrorException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

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
	private JMenuItem mntmOptions;
	private final Action action = new ActionOptions();
	private JMenuItem mntmVerifyIdentifiers;
	private final Action action_1 = new ActionVerify();

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
		
		mntmVerifyIdentifiers = new JMenuItem("Verify Identifiers");
		mntmVerifyIdentifiers.setAction(action_1);
		mnEdit.add(mntmVerifyIdentifiers);
		
		mntmOptions = new JMenuItem("Options");
		mntmOptions.setAction(action);
		mnEdit.add(mntmOptions);
		
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
	private class ActionOptions extends AbstractAction {
		public ActionOptions() {
			putValue(NAME, "Options");
			putValue(SHORT_DESCRIPTION, "Edit Options");
		}
		public void actionPerformed(ActionEvent e) {
			JTextField queryTimeOut = new JTextField(5);
//			JTextField yField = new JTextField(5);
			JPanel myPanel = new JPanel();
			myPanel.add(new JLabel("Query TimeOut in Miliseconds"));
			myPanel.add(queryTimeOut);
//			myPanel.add(Box.createHorizontalStrut(15)); // a spacer
//			myPanel.add(new JLabel("y:"));
//			myPanel.add(yField);
			int result = JOptionPane.showConfirmDialog(null, myPanel, "Please enter new option values.", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				try {
					String queryTimeOutResult = queryTimeOut.getText();
					int querytimeOutValue = Integer.parseInt(queryTimeOutResult);
					controller.getConnection().setQueryTimeOutLength(querytimeOutValue);
					JOptionPane.showMessageDialog(DefaultController.mainJFrame, "Success.", "Valid Selection", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(DefaultController.mainJFrame, "Unable to change options.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
				}
//				System.out.println("y value: " + yField.getText());
			}
		      
//			JOptionPane.showInputDialog(parentComponent, message, title, messageType, icon, selectionValues, initialSelectionValue)(DefaultController.mainJFrame, "Database reverted to previous version.", "Database revert performed", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	private class ActionVerify extends AbstractAction {
		public ActionVerify() {
			putValue(NAME, "Verify Identifiers");
			putValue(SHORT_DESCRIPTION, "Make sure identifiers are valid frame ID's, else initiate a search to find valid frame ID's");
		}
		public void actionPerformed(ActionEvent e) {
			ArrayList<String> untestedIdentifiers = new ArrayList<String>();
			ArrayList<String> validIdentifiers = new ArrayList<String>();
			untestedIdentifiers.add("GDQC-104328-MONOMER");
			untestedIdentifiers.add("GDQC-104349-MONOMER");
			untestedIdentifiers.add("GDQC-104463-MONOMER");
			untestedIdentifiers.add("GDQC-104578-MONOMER");
			untestedIdentifiers.add("GDQC-104600-MONOMER");
			
			untestedIdentifiers.add("GRMZM2G070422_P01");
			untestedIdentifiers.add("GRMZM2G163809_P02");
			untestedIdentifiers.add("GRMZM2G068862_P01");
			untestedIdentifiers.add("GRMZM2G044237_P03");
			untestedIdentifiers.add("GRMZM2G097457_P01");
			
			JavacycConnection conn = controller.getConnection();
			for (String untestedIdentifier : untestedIdentifiers) {
				try {
					if (conn.frameExists(untestedIdentifier)) {
						System.out.println("Found frame " + untestedIdentifier + " :: " + Frame.load(conn, untestedIdentifier).getCommonName());
						validIdentifiers.add(untestedIdentifier);
					} else {
						ArrayList<Frame> matches = conn.search(untestedIdentifier, Protein.GFPtype);
						if (matches != null && matches.size() > 0) {
							for (Frame match : matches) System.out.println("Possible Match: " + match.getLocalID());
						} else {
							System.out.println("No Matchs found for: " + untestedIdentifier);
						}
					}
				} catch (PtoolsErrorException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
