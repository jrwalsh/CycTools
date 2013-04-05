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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import javax.swing.JTree;
import javax.swing.JTable;

public class ExportPanel extends AbstractViewPanel {
	BrowserController controller;
	private JTable table;

	/**
	 * Create the frame.
	 */
	public ExportPanel(BrowserController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
    	JPanel panel1 = new JPanel();
    	
    	JPanel panel = new JPanel();
    	GroupLayout groupLayout = new GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addContainerGap()
    				.addComponent(panel1, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
    				.addGap(18)
    				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
    				.addContainerGap())
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
    				.addContainerGap()
    				.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
    					.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
    					.addComponent(panel1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
    				.addContainerGap())
    	);
    	
    	table = new JTable();
    	GroupLayout gl_panel = new GroupLayout(panel);
    	gl_panel.setHorizontalGroup(
    		gl_panel.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_panel.createSequentialGroup()
    				.addContainerGap()
    				.addComponent(table, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
    				.addContainerGap())
    	);
    	gl_panel.setVerticalGroup(
    		gl_panel.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_panel.createSequentialGroup()
    				.addGap(12)
    				.addComponent(table, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
    				.addContainerGap())
    	);
    	panel.setLayout(gl_panel);
    	
    	JComboBox comboBox = new JComboBox();
    	
    	JTree tree = new JTree();
    	GroupLayout gl_panel1 = new GroupLayout(panel1);
    	gl_panel1.setHorizontalGroup(
    		gl_panel1.createParallelGroup(Alignment.LEADING)
    			.addGroup(Alignment.TRAILING, gl_panel1.createSequentialGroup()
    				.addContainerGap()
    				.addGroup(gl_panel1.createParallelGroup(Alignment.TRAILING)
    					.addComponent(tree, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
    					.addComponent(comboBox, Alignment.LEADING, 0, 106, Short.MAX_VALUE))
    				.addContainerGap())
    	);
    	gl_panel1.setVerticalGroup(
    		gl_panel1.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_panel1.createSequentialGroup()
    				.addContainerGap()
    				.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    				.addPreferredGap(ComponentPlacement.RELATED)
    				.addComponent(tree, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
    				.addContainerGap())
    	);
    	panel1.setLayout(gl_panel1);
    	setLayout(groupLayout);
	}
    
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
}
