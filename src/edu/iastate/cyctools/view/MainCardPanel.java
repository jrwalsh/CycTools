package edu.iastate.cyctools.view;

import java.beans.PropertyChangeEvent;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.InternalStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;

import java.awt.CardLayout;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class MainCardPanel extends AbstractViewPanel {
	DefaultController controller;
	public final static String loginCard = "Login";
	public final static String selectCard = "Select";
	public final static String frameBrowseCard = "FrameBrowse";
	public final static String exportCard = "Export";
	public final static String searchCard = "Search";
	public final static String structureExportCard = "Structure Export";
	public final static String databaseCompareCard = "Database Compare";
	public final static String loadCard = "Load";
	

	/**
	 * Create the frame.
	 * @param controller 
	 */
	public MainCardPanel(DefaultController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	//Add self as property change event listener of the controller
    	controller.addView(this);
    }
    
    private void initComponents() {
    	this.setLayout(new CardLayout(0,0));
	}
    
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DefaultController.BROWSER_STATE_PROPERTY) && evt.getNewValue() != null) {
			if (evt.getNewValue() == State.NOT_CONNECTED) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.loginCard);
			}
			else if (evt.getNewValue() == State.SHOW_MAIN_SCREEN) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.selectCard);
			    controller.setDisconnectActionEnabled(true);
			}
			else if (evt.getNewValue() == State.SHOW_FRAMEBROWSE) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.frameBrowseCard);
			}
			else if (evt.getNewValue() == State.SHOW_EXPORT) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.exportCard);
			}
			else if (evt.getNewValue() == State.SHOW_SEARCH) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.searchCard);
			}
			else if (evt.getNewValue() == State.SHOW_STRUCTURE_EXPORT) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.structureExportCard);
			}
			else if (evt.getNewValue() == State.SHOW_DATABASE_COMPARE) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.databaseCompareCard);
			}
			else if (evt.getNewValue() == State.SHOW_IMPORT) {
				CardLayout cl = (CardLayout)(this.getLayout());
			    cl.show(this, MainCardPanel.loadCard);
			}
		}
	}
}
