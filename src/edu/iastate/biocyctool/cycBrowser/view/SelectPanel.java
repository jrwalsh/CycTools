package edu.iastate.biocyctool.cycBrowser.view;

import java.beans.PropertyChangeEvent;

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel.State;
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
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.Dimension;

public class SelectPanel extends AbstractViewPanel {
	BrowserController controller;
	private final Action actionSelectFrameBrowser = new ActionSelectFrameBrowser();
	private final Action actionSelectExportPanel = new ActionSelectExportPanel();
	private final Action actionSelectSearchPanel = new ActionSelectSearchPanel();
	private final Action actionExportStructurePanel = new ActionExportStructurePanel();

	/**
	 * Create the frame.
	 * @param controller 
	 */
	public SelectPanel(BrowserController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
    	JButton btnFrameBrowse = new JButton("Frame Browse");
    	btnFrameBrowse.setAction(actionSelectFrameBrowser);
		
		JButton btnExport = new JButton("Export");
		btnExport.setAction(actionSelectExportPanel);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setAction(actionSelectSearchPanel);
		
		JButton btnExportStructure = new JButton("Export Structure");
		btnExportStructure.setAction(actionExportStructurePanel);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(35)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnExportStructure, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnFrameBrowse, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnExport, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(195, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(35)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnExport, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnFrameBrowse, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnExportStructure, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(79, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
    
	private class ActionSelectFrameBrowser extends AbstractAction {
		public ActionSelectFrameBrowser() {
			putValue(NAME, "Frame Inspect Tool");
			putValue(SHORT_DESCRIPTION, "Open the Frame Browser screen.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.FRAMEBROWSE);
		}
	}
	
	private class ActionSelectExportPanel extends AbstractAction {
		public ActionSelectExportPanel() {
			putValue(NAME, "Export Tool");
			putValue(SHORT_DESCRIPTION, "Open the export screen.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.EXPORT);
		}
	}
	
	private class ActionSelectSearchPanel extends AbstractAction {
		public ActionSelectSearchPanel() {
			putValue(NAME, "Search Tool");
			putValue(SHORT_DESCRIPTION, "Open the search screen.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.SEARCH);
		}
	}
	
	private class ActionExportStructurePanel extends AbstractAction {
		public ActionExportStructurePanel() {
			putValue(NAME, "Export Structure Tool");
			putValue(SHORT_DESCRIPTION, "Export underlying PGDB Class and Instance structure.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.STRUCTURE_EXPORT);
		}
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
}
