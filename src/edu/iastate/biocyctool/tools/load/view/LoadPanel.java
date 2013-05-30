package edu.iastate.biocyctool.tools.load.view;

import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import edu.iastate.biocyctool.DefaultController;
import edu.iastate.biocyctool.tools.load.model.DocumentModel;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;
import java.awt.FlowLayout;

public class LoadPanel extends AbstractViewPanel {
	DefaultController controller;
	
	/**
	 * Create the frame.
	 */
	public LoadPanel(DefaultController controller) {
		this.controller = controller;
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
    	setPreferredSize(new Dimension(800, 400));
		setMinimumSize(new Dimension(800, 400));
		setName("SimpleBrowser");
		
		DataViewPanel dataViewPanel = new DataViewPanel(controller);
		dataViewPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		this.add(dataViewPanel);
	}
    
    
	private class ActionSubmit extends AbstractAction {
		public ActionSubmit() {
			putValue(NAME, "Submit");
			putValue(SHORT_DESCRIPTION, "Submits query from text field and returns results in the primary tab.");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		
	}
	
}