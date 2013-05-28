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
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class StatusPanel extends AbstractViewPanel {
	BrowserController controller;
	private JLabel lblStatus;
	
	/**
	 * Create the frame.
	 * @param controller 
	 */
	public StatusPanel(BrowserController controller) {
		this.controller = controller;
		
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
    	setPreferredSize(new Dimension(800, 30));
		
		lblStatus = new JLabel("Not Connected");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblStatus)
					.addContainerGap(718, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblStatus)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
    
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BrowserController.BROWSER_STATE_PROPERTY) && evt.getNewValue() != null) {
			if (evt.getNewValue() == State.NOT_CONNECTED) lblStatus.setText("Not Connected");
			else if (evt.getNewValue() == State.MAIN_SCREEN) lblStatus.setText("Connected");
		}
	}
}
