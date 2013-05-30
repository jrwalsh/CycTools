package edu.iastate.biocyctool.view;

import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import edu.iastate.biocyctool.controller.BrowserController;
import edu.iastate.biocyctool.tools.load.controller.DefaultController;
import edu.iastate.biocyctool.tools.load.model.DocumentModel;
import edu.iastate.biocyctool.tools.load.view.DataViewPanel;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;
import java.awt.FlowLayout;

public class LoadPanel extends AbstractViewPanel {
	BrowserController controller;
	
	/**
	 * Create the frame.
	 */
	public LoadPanel(BrowserController controller) {
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
		
		// Models
		DocumentModel document = new DocumentModel();
		document.initDefault();
		
		// Controllers
		DefaultController controller = new DefaultController();
		
		// Views
		DataViewPanel dataViewPanel = new DataViewPanel(controller); //TODO wrong controller, be consistent and merge the controllers?
		
		// Connect views, models, controllers, and data objects.
		controller.setDocumentModel(document);
		controller.addView(dataViewPanel);
		document.addPropertyChangeListener(controller);
		
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
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
