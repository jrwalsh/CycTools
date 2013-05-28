package edu.iastate.biocyctool.cycBrowser;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel;
import edu.iastate.biocyctool.cycBrowser.view.DatabaseComparePanel;
import edu.iastate.biocyctool.cycBrowser.view.ExportPGDBStructurePanel;
import edu.iastate.biocyctool.cycBrowser.view.ExportPanel;
import edu.iastate.biocyctool.cycBrowser.view.FrameInspectPanel;
import edu.iastate.biocyctool.cycBrowser.view.LoginPanel;
import edu.iastate.biocyctool.cycBrowser.view.MainCardPanel;
import edu.iastate.biocyctool.cycBrowser.view.MenuBar;
import edu.iastate.biocyctool.cycBrowser.view.SearchPanel;
import edu.iastate.biocyctool.cycBrowser.view.SelectPanel;
import edu.iastate.biocyctool.cycBrowser.view.StatusPanel;
import edu.iastate.biocyctool.cycBrowser.view.ToolPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;

public class Main {
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// DataAccess object initialized through the loginPanel
		
		// Model
		BrowserStateModel state = new BrowserStateModel();
		state.initDefault();
		
		// Controller
		BrowserController controller = new BrowserController(state);
		state.addPropertyChangeListener(controller);
		
		// Views
		ToolPanel toolPanel = new ToolPanel(controller);
		StatusPanel statusPanel = new StatusPanel(controller);
		LoginPanel loginPanel = new LoginPanel(controller);
		SelectPanel selectPanel = new SelectPanel(controller);
		FrameInspectPanel frameBrowsePanel = new FrameInspectPanel(controller);
		ExportPanel exportPanel = new ExportPanel(controller);
		SearchPanel searchPanel = new SearchPanel(controller);
		ExportPGDBStructurePanel exportStructurePanel = new ExportPGDBStructurePanel(controller);
		DatabaseComparePanel databaseComparePanel = new DatabaseComparePanel(controller);
		
		MainCardPanel cardPanel = new MainCardPanel(controller);
		cardPanel.add(loginPanel, MainCardPanel.loginCard);
		cardPanel.add(selectPanel, MainCardPanel.selectCard);
		cardPanel.add(frameBrowsePanel, MainCardPanel.frameBrowseCard);
		cardPanel.add(exportPanel, MainCardPanel.exportCard);
		cardPanel.add(searchPanel, MainCardPanel.searchCard);
		cardPanel.add(exportStructurePanel, MainCardPanel.structureExportCard);
		cardPanel.add(databaseComparePanel, MainCardPanel.databaseCompareCard);
		
		// Connect views, models, controllers, and data objects.
		controller.addView(toolPanel);
		controller.addView(statusPanel);
		controller.addView(cardPanel);
		controller.addView(loginPanel);
		controller.addView(selectPanel);
		controller.addView(frameBrowsePanel);
		controller.addView(exportPanel);
		controller.addView(searchPanel);
		controller.addView(exportStructurePanel);
		controller.addView(databaseComparePanel);
		
		JFrame displayFrame = new JFrame("CycBrowser");
		displayFrame.setResizable(false);
		displayFrame.setPreferredSize(new Dimension(835, 535));
		displayFrame.setJMenuBar(new MenuBar(controller));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{800, 0};
		gridBagLayout.rowHeights = new int[]{35, 400, 30, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		displayFrame.getContentPane().setLayout(gridBagLayout);
		
		GridBagConstraints gbc_toolPanel = new GridBagConstraints();
		gbc_toolPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_toolPanel.anchor = GridBagConstraints.NORTH;
		gbc_toolPanel.insets = new Insets(0, 0, 5, 0);
		gbc_toolPanel.gridx = 0;
		gbc_toolPanel.gridy = 0;
		displayFrame.getContentPane().add(toolPanel, gbc_toolPanel);
		
		
		GridBagConstraints gbc_cardPanel = new GridBagConstraints();
		gbc_cardPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_cardPanel.anchor = GridBagConstraints.NORTH;
		gbc_cardPanel.insets = new Insets(0, 0, 5, 0);
		gbc_cardPanel.gridx = 0;
		gbc_cardPanel.gridy = 1;
		displayFrame.getContentPane().add(cardPanel, gbc_cardPanel);
		
		GridBagConstraints gbc_statusPanel = new GridBagConstraints();
		gbc_statusPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_statusPanel.anchor = GridBagConstraints.NORTH;
		gbc_statusPanel.gridx = 0;
		gbc_statusPanel.gridy = 2;
		displayFrame.getContentPane().add(statusPanel, gbc_statusPanel);
		
        displayFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        displayFrame.pack();
        
        displayFrame.setLocationRelativeTo(null);
        controller.setMainJFrame(displayFrame);
        controller.setToolPanel(toolPanel);
        controller.setStatusPanel(statusPanel);
        toolPanel.setVisible(false);
        displayFrame.setVisible(true);
	}
}
