package edu.iastate.biocyctool.cycBrowser.view;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel.State;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;

import javax.swing.JLabel;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JProgressBar;
import javax.swing.JComboBox;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ToolPanel extends AbstractViewPanel {
	BrowserController controller;
	
	public final static String noOrganism = "None Available";
	
	private JComboBox comboBoxOrganism;
	private final Action actionSubmit = new ActionSetOrganism();
	
	/**
	 * Create the frame.
	 * @param controller 
	 */
	public ToolPanel(BrowserController controller) {
		this.controller = controller;
		
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	comboBoxOrganism.addItem("None Available");
		comboBoxOrganism.setSelectedItem("None Available");
		comboBoxOrganism.setEnabled(false);
    }
    
    private void initComponents() {
    	setPreferredSize(new Dimension(800, 35));
		
		comboBoxOrganism = new JComboBox();
		comboBoxOrganism.setAction(actionSubmit);
		
		JButton btnNewButton = new JButton("Back");
		
		JButton btnNewButton_1 = new JButton("Forward");
		
		JButton btnNewButton_2 = new JButton("Home");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_2)
					.addGap(418)
					.addComponent(comboBoxOrganism, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
					.addGap(41))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewButton_1)
						.addComponent(btnNewButton_2)
						.addComponent(btnNewButton)
						.addComponent(comboBoxOrganism, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
    
	private class ActionSetOrganism extends AbstractAction {
		public ActionSetOrganism() {
			putValue(NAME, "Set Organism");
			putValue(SHORT_DESCRIPTION, "Changes the organism used for all queries in the BioCyc database.");
		}
		public void actionPerformed(ActionEvent e) {
			if (comboBoxOrganism.getItemCount() > 0 && !comboBoxOrganism.getSelectedItem().toString().equalsIgnoreCase(noOrganism)){
				controller.selectOrganism(comboBoxOrganism.getSelectedItem().toString());
			}
		}
	}


	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BrowserController.BROWSER_STATE_PROPERTY) && evt.getNewValue() != null) {
			if (evt.getNewValue() == State.NOT_CONNECTED) {
				if (comboBoxOrganism.getItemCount() > 0) {
					comboBoxOrganism.removeAllItems();
				}
				comboBoxOrganism.addItem(noOrganism);
				comboBoxOrganism.setSelectedItem(noOrganism);
				comboBoxOrganism.setEnabled(false);
			}
			else if (evt.getNewValue() == State.CONNECTED) {
				if (comboBoxOrganism.getItemCount() > 0) {
					comboBoxOrganism.removeAllItems();
				}
				for (String org : controller.getAvailableOrganisms()) {
					comboBoxOrganism.addItem(org);
				}
				comboBoxOrganism.setEnabled(true);
			}
		}
	}
}
