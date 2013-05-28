package edu.iastate.biocyctool.cycBrowser.view;

import java.beans.PropertyChangeEvent;

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel.State;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;

import java.awt.CardLayout;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class MainCardPanel extends AbstractViewPanel {
	BrowserController controller;
	public final static String loginCard = "Login";
	public final static String selectCard = "Select";
	public final static String frameBrowseCard = "FrameBrowse";
	public final static String exportCard = "Export";
	public final static String searchCard = "Search";
	public final static String structureExportCard = "Structure Export";
	public final static String databaseCompareCard = "Database Compare";
	

	/**
	 * Create the frame.
	 * @param controller 
	 */
	public MainCardPanel(BrowserController controller) {
		setPreferredSize(new Dimension(800, 400));
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
    	this.setLayout(new CardLayout(0,0));
	}
    
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BrowserController.BROWSER_STATE_PROPERTY) && evt.getNewValue() != null) {
			if (evt.getNewValue() == State.NOT_CONNECTED) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.loginCard);
			}
			else if (evt.getNewValue() == State.MAIN_SCREEN) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.selectCard);
			}
			else if (evt.getNewValue() == State.FRAMEBROWSE) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.frameBrowseCard);
			}
			else if (evt.getNewValue() == State.EXPORT) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.exportCard);
			}
			else if (evt.getNewValue() == State.SEARCH) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.searchCard);
			}
			else if (evt.getNewValue() == State.STRUCTURE_EXPORT) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.structureExportCard);
			}
			else if (evt.getNewValue() == State.DATABASE_COMPARE) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.databaseCompareCard);
			}
		}
	}
}
