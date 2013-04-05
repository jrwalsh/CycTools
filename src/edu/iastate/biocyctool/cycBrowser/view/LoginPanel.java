package edu.iastate.biocyctool.cycBrowser.view;

import java.beans.PropertyChangeEvent;

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel.State;
import edu.iastate.biocyctool.cycspreadsheetloader.controller.DefaultController;
import edu.iastate.biocyctool.util.da.CycDataBaseAccess;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
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

public class LoginPanel extends AbstractViewPanel {
	BrowserController controller;
	private JTextField txtHost;
	private JTextField txtPort;
	private JTextField txtUser;
	private JPasswordField pwbox;
	private final Action actionConnect = new ActionConnect();

	/**
	 * Create the frame.
	 */
	public LoginPanel(BrowserController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
    	JPanel panel = new JPanel();
		add(panel);
		
		txtHost = new JTextField();
		txtHost.setText("jrwalsh.student.iastate.edu");
		txtHost.setColumns(10);
		
		JLabel lblPort = new JLabel("Port");
		
		txtPort = new JTextField();
		txtPort.setText("4444");
		txtPort.setColumns(10);
		
		JLabel lblUser = new JLabel("UserName");
		
		txtUser = new JTextField();
		txtUser.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		
		JLabel lblHost = new JLabel("Host");
		
		pwbox = new JPasswordField();
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.setAction(actionConnect);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(168)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnConnect)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPort, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblUser, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblHost, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(pwbox)
								.addComponent(txtHost, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
								.addComponent(txtPort)
								.addComponent(txtUser))))
					.addGap(169))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblHost, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPort, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblUser, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addComponent(pwbox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnConnect))
		);
		panel.setLayout(gl_panel);
	}
    
	private class ActionConnect extends AbstractAction {
		public ActionConnect() {
			putValue(NAME, "Connect");
			putValue(SHORT_DESCRIPTION, "Initiallizes the connection to a database using JavaCycO.");
		}
		public void actionPerformed(ActionEvent e) {
			try {
				controller.connect(txtHost.getText(), Integer.parseInt(txtPort.getText()), null, null);
			} catch (Exception exception) {
				
			}
			updateContent();
		}
		private void updateContent() {
			//TODO set warning if login failed
		}
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
}