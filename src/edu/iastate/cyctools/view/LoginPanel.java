package edu.iastate.cyctools.view;

import java.beans.PropertyChangeEvent;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.externalSourceCode.MenuPopupUtil;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.Dimension;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
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
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	//Add self as property change event listener of the controller
    	controller.addView(this);
    	
    	MenuPopupUtil.installContextMenu(txtHost);
    	MenuPopupUtil.installContextMenu(txtPort);
    	MenuPopupUtil.installContextMenu(txtUser);
    }
    
    private void initComponents() {
		setLayout(new MigLayout("", "[25%,grow][][][][25%,grow]", "[49px][53px][][][][][][grow]"));
		
		JLabel lblWelcomeToCyctools = new JLabel("<html><h1>Welcome to CycTools</h1></html>");
		add(lblWelcomeToCyctools, "cell 0 0 5 1,alignx center,aligny center");
		
		JLabel lblNewLabel = new JLabel("<html>Please enter connection information. If no username and password is required for your server, leave these fields blank.</html>");
		add(lblNewLabel, "cell 0 1 5 1,alignx center,aligny center");
		
		JLabel lblHost = new JLabel("Host");
		add(lblHost, "cell 1 2,alignx right,aligny center");
		
		txtHost = new JTextField();
		add(txtHost, "cell 2 2,alignx left,aligny center");
		txtHost.setText("");
		txtHost.setColumns(20);
		
		JLabel lblPort = new JLabel("Port");
		add(lblPort, "cell 1 3,alignx right,aligny center");
		
		txtPort = new JTextField();
		add(txtPort, "cell 2 3,alignx left,aligny center");
		txtPort.setText("4444");
		txtPort.setColumns(20);
		
		JLabel lblUser = new JLabel("UserName");
		add(lblUser, "cell 1 4,alignx right,aligny center");
		
		txtUser = new JTextField();
		add(txtUser, "cell 2 4,alignx left,aligny center");
		txtUser.setColumns(20);
		
		JLabel lblPassword = new JLabel("Password");
		add(lblPassword, "cell 1 5,alignx right,aligny center");
		
		pwbox = new JPasswordField();
		pwbox.setColumns(20);
		add(pwbox, "cell 2 5,alignx left,aligny center");
		
		JButton btnConnect = new JButton("Connect");
		add(btnConnect, "cell 2 6,alignx right,aligny center");
		btnConnect.setAction(actionConnect);
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
