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

public class StatusPanel extends AbstractViewPanel {
	BrowserController controller;
	
	private JComboBox comboBoxOrganism;
	private JLabel lblStatus;
	private final Action actionSubmit = new ActionSetOrganism();
	
	/**
	 * Create the frame.
	 * @param controller 
	 */
	public StatusPanel(BrowserController controller) {
		setPreferredSize(new Dimension(800, 30));
		this.controller = controller;
		
		lblStatus = new JLabel("Not Connected");
		add(lblStatus);
		
		JProgressBar progressBar = new JProgressBar();
		add(progressBar);
		
		comboBoxOrganism = new JComboBox();
		comboBoxOrganism.setAction(actionSubmit);
		add(comboBoxOrganism);
		
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
	}
    
	private class ActionSetOrganism extends AbstractAction {
		public ActionSetOrganism() {
			putValue(NAME, "Set Organism");
			putValue(SHORT_DESCRIPTION, "Changes the organism used for all queries in the BioCyc database.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.selectOrganism(comboBoxOrganism.getSelectedItem().toString());
		}
	}
    
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BrowserController.BROWSER_STATE_PROPERTY) && evt.getNewValue() != null) {
			if (evt.getNewValue() == State.NOT_CONNECTED) {
				comboBoxOrganism.removeAllItems();
				lblStatus.setText("Not Connected");
				this.setEnabled(false);
			}
			else if (evt.getNewValue() == State.CONNECTED) {
				for (String org : controller.getAvailableOrganisms()) {
					comboBoxOrganism.addItem(org);
				}
				lblStatus.setText("Connected");
				this.setEnabled(true);
			}
		}
	}
}
