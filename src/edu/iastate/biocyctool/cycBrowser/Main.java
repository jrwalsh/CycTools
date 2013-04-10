package edu.iastate.biocyctool.cycBrowser;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel;
import edu.iastate.biocyctool.cycBrowser.view.ExportPGDBStructurePanel;
import edu.iastate.biocyctool.cycBrowser.view.ExportPanel;
import edu.iastate.biocyctool.cycBrowser.view.FrameBrowsePanel;
import edu.iastate.biocyctool.cycBrowser.view.LoginPanel;
import edu.iastate.biocyctool.cycBrowser.view.MainCardPanel;
import edu.iastate.biocyctool.cycBrowser.view.MenuBar;
import edu.iastate.biocyctool.cycBrowser.view.SearchPanel;
import edu.iastate.biocyctool.cycBrowser.view.SelectPanel;
import edu.iastate.biocyctool.cycBrowser.view.StatusPanel;
import edu.iastate.biocyctool.util.da.CycDataBaseAccess;

import java.awt.FlowLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import java.awt.Insets;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.Dimension;

public class Main {
	private static JMenuBar menuBar;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// DataAccess
		CycDataBaseAccess da = null;
		
		// Model
		BrowserStateModel state = new BrowserStateModel();
		state.initDefault();
		
		// Controller
		BrowserController controller = new BrowserController(state);
		state.addPropertyChangeListener(controller);
		
		// Views
		StatusPanel statusPanel = new StatusPanel(controller);
		LoginPanel loginPanel = new LoginPanel(controller);
		SelectPanel selectPanel = new SelectPanel(controller);
		FrameBrowsePanel frameBrowsePanel = new FrameBrowsePanel(controller);
		ExportPanel exportPanel = new ExportPanel(controller);
		SearchPanel searchPanel = new SearchPanel(controller);
		ExportPGDBStructurePanel exportStructurePanel = new ExportPGDBStructurePanel(controller);
		
		MainCardPanel cardPanel = new MainCardPanel(controller);
		cardPanel.add(loginPanel, MainCardPanel.loginCard);
		cardPanel.add(selectPanel, MainCardPanel.selectCard);
		cardPanel.add(frameBrowsePanel, MainCardPanel.frameBrowseCard);
		cardPanel.add(exportPanel, MainCardPanel.exportCard);
		cardPanel.add(searchPanel, MainCardPanel.searchCard);
		cardPanel.add(exportStructurePanel, MainCardPanel.structureExportCard);
		
		// Connect views, models, controllers, and data objects.
		controller.addView(cardPanel);
		controller.addView(statusPanel);
		controller.addView(loginPanel);
		controller.addView(selectPanel);
		controller.addView(frameBrowsePanel);
		controller.addView(exportPanel);
		controller.addView(searchPanel);
		controller.addView(exportStructurePanel);
		
		JFrame displayFrame = new JFrame("CycBrowser");
		displayFrame.setResizable(false);
		displayFrame.setPreferredSize(new Dimension(800, 500));
		displayFrame.setJMenuBar(new MenuBar(controller));
		displayFrame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		displayFrame.getContentPane().add(cardPanel);
		displayFrame.getContentPane().add(statusPanel);
        displayFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        displayFrame.pack();
        
        displayFrame.setVisible(true);
	}
}
