package edu.iastate.cyctools.view;

import java.beans.PropertyChangeEvent;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.InternalStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class SelectPanel extends AbstractViewPanel {
	DefaultController controller;
	private final Action actionSelectFrameBrowser = new ActionSelectFrameBrowser();
	private final Action actionSelectExportPanel = new ActionSelectExportPanel();
	private final Action actionSelectSearchPanel = new ActionSelectSearchPanel();
	private final Action actionExportStructurePanel = new ActionExportStructurePanel();
	private final Action actionComparePanel = new ActionComparePanel();
	private final Action actionLoad = new ActionLoad();

	/**
	 * Create the frame.
	 * @param controller 
	 */
	public SelectPanel(DefaultController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	//Add self as property change event listener of the controller
    	controller.addView(this);
    	
    }
    
    private void initComponents() {
    	JButton btnFrameBrowse = new JButton("Frame Browse");
    	btnFrameBrowse.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent arg0) {
    		}
    	});
    	btnFrameBrowse.setMargin(new Insets(2, 2, 2, 2));
    	btnFrameBrowse.setAction(actionSelectFrameBrowser);
		
		JLabel lblFrameInspect = new JLabel("<html>View the contents of a data frame as text</html>");
		setLayout(new MigLayout("", "[grow][101px][25px][224px][grow]", "[grow][84px][25.00][84px][grow]"));
		add(btnFrameBrowse, "cell 1 1,grow");
		add(lblFrameInspect, "cell 3 1,grow");
		
		JButton btnLoadData = new JButton("Load Data");
		btnLoadData.setAction(actionLoad);
		btnLoadData.setMargin(new Insets(2, 2, 2, 2));
		add(btnLoadData, "cell 1 3,grow");
		
		JLabel lblloadASpreadsheet = new JLabel("<html>Load a data file into the database.</html>");
		add(lblloadASpreadsheet, "cell 3 3,grow");
	}
    
	private class ActionSelectFrameBrowser extends AbstractAction {
		public ActionSelectFrameBrowser() {
			putValue(NAME, "<html><center>Frame Viewer</center></html>");
			putValue(SHORT_DESCRIPTION, "Open the Frame Browser screen.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.SHOW_FRAMEBROWSE);
		}
	}
	
	private class ActionSelectExportPanel extends AbstractAction {
		public ActionSelectExportPanel() {
			putValue(NAME, "<html><center>Export Frames</center></html>");
			putValue(SHORT_DESCRIPTION, "Open the export screen.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.SHOW_EXPORT);
		}
	}
	
	private class ActionSelectSearchPanel extends AbstractAction {
		public ActionSelectSearchPanel() {
			putValue(NAME, "<html><center>Bulk Search</center></html>");
			putValue(SHORT_DESCRIPTION, "Open the search screen.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.SHOW_SEARCH);
		}
	}
	
	private class ActionExportStructurePanel extends AbstractAction {
		public ActionExportStructurePanel() {
			putValue(NAME, "<html><center>Export Structure</center></html>");
			putValue(SHORT_DESCRIPTION, "Export underlying PGDB Class and Instance structure.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.SHOW_STRUCTURE_EXPORT);
		}
	}
	
	private class ActionComparePanel extends AbstractAction {
		public ActionComparePanel() {
			putValue(NAME, "<html><center>Database Compare<br>Tool</center></html>");
			putValue(SHORT_DESCRIPTION, "Compare two PGDB databases frame by frame.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.SHOW_DATABASE_COMPARE);
		}
	}
	
	private class ActionLoad extends AbstractAction {
		public ActionLoad() {
			putValue(NAME, "<html><center>Import<br /> Assistant</center></html>");
			putValue(SHORT_DESCRIPTION, "Load spreadsheet file into database");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.SHOW_IMPORT);
		}
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
	
}
