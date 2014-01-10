package edu.iastate.cyctools.view;

import java.beans.PropertyChangeEvent;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.InternalStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;

import javax.swing.JLabel;

import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import net.miginfocom.swing.MigLayout;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class StatusPanel extends AbstractViewPanel {
	DefaultController controller;
	private JLabel lblStatus;
	
	/**
	 * Create the frame.
	 * @param controller 
	 */
	public StatusPanel(DefaultController controller) {
		setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		this.controller = controller;
		
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	//Add self as property change event listener of the controller
    	controller.addView(this);
    	controller.setStatusPanel(this);
    }
    
    private void initComponents() {
		setLayout(new MigLayout("", "[72px,grow]", "[14px]"));
		
		lblStatus = new JLabel("Not Connected");
		add(lblStatus, "cell 0 0,alignx left,aligny center");
	}
    
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DefaultController.BROWSER_STATE_PROPERTY) && evt.getNewValue() != null) {
			if (evt.getNewValue() == State.NOT_CONNECTED) lblStatus.setText("Not Connected");
			else if (evt.getNewValue() == State.SHOW_MAIN_SCREEN) lblStatus.setText("Connected");
		}
	}
}
