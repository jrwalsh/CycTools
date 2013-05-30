package edu.iastate.cyctools.view;

import java.beans.PropertyChangeEvent;

import edu.iastate.cyctools.CycDataBaseAccess;
import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.DefaultStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.externalSourceCode.MenuPopupUtil;

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
import java.awt.Dimension;

public class LoginPanel extends AbstractViewPanel {
	DefaultController controller;
	private JTextField txtHost;
	private JTextField txtPort;
	private JTextField txtUser;
	private JPasswordField pwbox;
	private final Action actionConnect = new ActionConnect();

	/**
	 * Create the frame.
	 */
	public LoginPanel(DefaultController controller) {
		setPreferredSize(new Dimension(800, 400));
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	MenuPopupUtil.installContextMenu(txtHost);
    	MenuPopupUtil.installContextMenu(txtPort);
    	MenuPopupUtil.installContextMenu(txtUser);
    }
    
    private void initComponents() {
    	JPanel panel = new JPanel();
		
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
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPort, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblUser, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblHost, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
							.addComponent(txtUser, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
							.addComponent(pwbox, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
							.addComponent(txtPort, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
						.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
							.addComponent(btnConnect)
							.addComponent(txtHost, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE)))
					.addGap(418))
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
		
		JLabel lblWelcomeToCyctools = new JLabel("<html><h1>Welcome to CycTools</h1></html>");
		
		JLabel lblNewLabel = new JLabel("<html>Please enter connection information. If no username and password is required for your server, leave these fields blank.</html>");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(255)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblWelcomeToCyctools, GroupLayout.PREFERRED_SIZE, 306, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(lblNewLabel, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
							.addComponent(panel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 300, Short.MAX_VALUE)))
					.addContainerGap(239, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblWelcomeToCyctools)
					.addGap(11)
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(110, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
    
	private class ActionConnect extends AbstractAction {
		public ActionConnect() {
			putValue(NAME, "Connect");
			putValue(SHORT_DESCRIPTION, "Initiallizes the connection to a database using JavaCycO.");
		}
		public void actionPerformed(ActionEvent e) {
			try {
				if (txtUser.getText() != null && !txtUser.getText().isEmpty()) {
					String pw = "";
					if (pwbox.getPassword() != null) {
						pw = pwbox.getPassword().toString();
					}
					controller.connect(txtHost.getText(), Integer.parseInt(txtPort.getText()), txtUser.getText(), pw);
				} else {
					controller.connect(txtHost.getText(), Integer.parseInt(txtPort.getText()), null, null);
				}
			} catch (Exception exception) {
				
			}
		}
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
}
