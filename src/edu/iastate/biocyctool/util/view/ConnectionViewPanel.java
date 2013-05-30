package edu.iastate.biocyctool.util.view;

import edu.iastate.biocyctool.tools.load.controller.DefaultController;

import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class ConnectionViewPanel extends AbstractViewPanel {
    // The controller used by this view
    private DefaultController controller;
    private JTextField txtHost;
    private JTextField txtPort;
//    private final Action actionConnect = new ActionConnect();
    
    public ConnectionViewPanel(DefaultController controller) {
    	this.controller = controller;
    	initComponents();
    	localInitialization();
    }

    public void localInitialization() {

    }
    
    private void initComponents() {
    	JPanel displayPanel = new JPanel();
    	
    	JLabel labelTitle = new JLabel("Status:");
    	
    	txtHost = new JTextField();
    	txtHost.setText("jrwalsh.student.iastate.edu");
    	txtHost.setColumns(10);
    	
    	txtPort = new JTextField();
    	txtPort.setText("4444");
    	txtPort.setColumns(10);
    	
    	JComboBox comboOrganism = new JComboBox();
    	comboOrganism.setEnabled(false);
    	GroupLayout groupLayout = new GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addContainerGap()
    				.addComponent(displayPanel, GroupLayout.PREFERRED_SIZE, 262, GroupLayout.PREFERRED_SIZE)
    				.addGap(82))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addContainerGap()
    				.addComponent(displayPanel, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
    				.addContainerGap(7, Short.MAX_VALUE))
    	);
    	
    	JButton btnConnect = new JButton("Connect");
//    	btnConnect.setAction(actionConnect);
    	
    	JLabel labelStatus = new JLabel("not connected");
    	
    	JButton btnDisconnect = new JButton("Disconnect");
    	
    	JLabel lblHost = new JLabel("Host");
    	
    	JLabel lblPort = new JLabel("Port");
    	
    	JLabel lblOrg = new JLabel("Org");
    	GroupLayout gl_displayPanel = new GroupLayout(displayPanel);
    	gl_displayPanel.setHorizontalGroup(
    		gl_displayPanel.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_displayPanel.createSequentialGroup()
    				.addContainerGap()
    				.addGroup(gl_displayPanel.createParallelGroup(Alignment.LEADING)
    					.addComponent(lblHost)
    					.addComponent(lblPort)
    					.addComponent(lblOrg))
    				.addPreferredGap(ComponentPlacement.RELATED)
    				.addGroup(gl_displayPanel.createParallelGroup(Alignment.LEADING)
    					.addComponent(txtHost, GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
    					.addGroup(gl_displayPanel.createSequentialGroup()
    						.addComponent(labelTitle)
    						.addGap(18)
    						.addComponent(labelStatus))
    					.addGroup(gl_displayPanel.createSequentialGroup()
    						.addGroup(gl_displayPanel.createParallelGroup(Alignment.LEADING)
    							.addComponent(txtPort, GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
    							.addComponent(comboOrganism, Alignment.TRAILING, 0, 80, Short.MAX_VALUE))
    						.addPreferredGap(ComponentPlacement.RELATED)
    						.addGroup(gl_displayPanel.createParallelGroup(Alignment.TRAILING)
    							.addComponent(btnConnect)
    							.addComponent(btnDisconnect))))
    				.addContainerGap())
    	);
    	gl_displayPanel.setVerticalGroup(
    		gl_displayPanel.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_displayPanel.createSequentialGroup()
    				.addGroup(gl_displayPanel.createParallelGroup(Alignment.BASELINE)
    					.addComponent(labelTitle)
    					.addComponent(labelStatus))
    				.addPreferredGap(ComponentPlacement.RELATED)
    				.addGroup(gl_displayPanel.createParallelGroup(Alignment.BASELINE)
    					.addComponent(txtHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    					.addComponent(lblHost))
    				.addPreferredGap(ComponentPlacement.RELATED)
    				.addGroup(gl_displayPanel.createParallelGroup(Alignment.BASELINE)
    					.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    					.addComponent(btnConnect)
    					.addComponent(lblPort))
    				.addPreferredGap(ComponentPlacement.RELATED)
    				.addGroup(gl_displayPanel.createParallelGroup(Alignment.BASELINE)
    					.addComponent(comboOrganism, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    					.addComponent(btnDisconnect)
    					.addComponent(lblOrg))
    				.addGap(3))
    	);
    	displayPanel.setLayout(gl_displayPanel);
    	setLayout(groupLayout);
    }

    @Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
    
//    private class ActionConnect extends AbstractAction {
//		public ActionConnect() {
//			putValue(NAME, "Connect");
//			putValue(SHORT_DESCRIPTION, "Initiallizes the connection to a database using JavaCycO.");
//		}
//		public void actionPerformed(ActionEvent e) {
//			try {
//				controller.connect(txtHost.getText(), Integer.parseInt(txtPort.getText()));
//			} catch (Exception exception) {
//				JDialog jd = new JDialog();
//				jd.add(new JLabel("There was an error connecting to the database."));
//			}
//			updateContent();
//		}
//		private void updateContent() {
//			//TODO
//		}
//	}
}