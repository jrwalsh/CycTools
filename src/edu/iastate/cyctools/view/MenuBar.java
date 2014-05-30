package edu.iastate.cyctools.view;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.InternalStateModel.State;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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

	/**
	 * Create the frame.
	 */
	public MenuBar(DefaultController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	setDisconnectActionEnabled(false);
    	controller.setMenuPanel(this);
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
    
    public void setDisconnectActionEnabled(boolean enabled) {
    	actionDisconnect.setEnabled(enabled);
    }
    
	private class ActionDisconnect extends AbstractAction {
		public ActionDisconnect() {
			putValue(NAME, "Disconnect");
			putValue(SHORT_DESCRIPTION, "Disconnect from Pathway Tools server.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.NOT_CONNECTED);
			setDisconnectActionEnabled(false);
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
			try {
				final URI uri = new URI("https://github.com/jrwalsh/CycTools/wiki");
				class OpenUrlAction implements ActionListener {
					@Override public void actionPerformed(ActionEvent e) {
						open(uri);
					}
				}
				
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
				JLabel label1 = new JLabel("<html><bold>CycTools: Software for Maintaining and Migrating Functional Annotations in BioCyc Model Organism Databases.</bold></html>");
				label1.setFont(new Font("serif", Font.BOLD, 14));
				label1.setAlignmentX(CENTER_ALIGNMENT);
				panel.add(label1);
				JLabel label2 = new JLabel("Beta Version: 0.1");
				label2.setAlignmentX(CENTER_ALIGNMENT);
				panel.add(label2);
				JLabel label3 = new JLabel("Licensed under GNU GPL.");
				label3.setAlignmentX(CENTER_ALIGNMENT);
				panel.add(label3);
				panel.add(Box.createVerticalStrut(10));
				
				JButton button = new JButton();
			    button.setText("<HTML>Visit <FONT color=\"#000099\"><U>" + uri + "</U></FONT></HTML>");
			    button.setHorizontalAlignment(SwingConstants.LEFT);
			    button.setBorderPainted(false);
			    button.setOpaque(false);
			    button.setBackground(Color.WHITE);
			    button.setToolTipText(uri.toString());
			    button.addActionListener(new OpenUrlAction());
			    button.setAlignmentX(Component.CENTER_ALIGNMENT);
			    button.setMaximumSize(new Dimension(290,25));
			    panel.add(button);
			    panel.add(Box.createVerticalStrut(20));
			    
				JOptionPane.showMessageDialog(DefaultController.mainJFrame, panel, "About", JOptionPane.INFORMATION_MESSAGE);
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private static void open(URI uri) {
	    if (Desktop.isDesktopSupported()) {
	      try {
	        Desktop.getDesktop().browse(uri);
	      } catch (IOException e) {
	    	  //error handling
	      }
	    } else { //error handling
	    	
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
			JPanel myPanel = new JPanel();
			myPanel.add(new JLabel("Query TimeOut in Miliseconds (enter 0 to disable query timeout):"));
			myPanel.add(queryTimeOut);
			int result = JOptionPane.showConfirmDialog(null, myPanel, "Change Settings.", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				try {
					String queryTimeOutResult = queryTimeOut.getText();
					int querytimeOutValue = Integer.parseInt(queryTimeOutResult);
					controller.getConnection().setQueryTimeOutLength(querytimeOutValue);
					JOptionPane.showMessageDialog(DefaultController.mainJFrame, "Success.", "Valid Selection", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(DefaultController.mainJFrame, "Unable to change options.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}
}