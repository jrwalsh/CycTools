package edu.iastate.cyctools;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.iastate.cyctools.tools.compare.DatabaseComparePanel;
import edu.iastate.cyctools.tools.exportFrame.ExportFramePanel;
import edu.iastate.cyctools.tools.exportPGDBStructure.ExportPGDBStructurePanel;
import edu.iastate.cyctools.tools.frameView.FrameViewPanel;
import edu.iastate.cyctools.tools.load.view.LoadPanel;
import edu.iastate.cyctools.tools.search.SearchPanel;
import edu.iastate.cyctools.view.LoginPanel;
import edu.iastate.cyctools.view.MainCardPanel;
import edu.iastate.cyctools.view.MenuBar;
import edu.iastate.cyctools.view.SelectPanel;
import edu.iastate.cyctools.view.StatusPanel;
import edu.iastate.cyctools.view.ToolPanel;

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
		DefaultStateModel state = new DefaultStateModel();
		state.initDefault();
		
		// Controller
		DefaultController controller = new DefaultController(state);
		state.addPropertyChangeListener(controller);
		
		// Views
		ToolPanel toolPanel = new ToolPanel(controller);
		StatusPanel statusPanel = new StatusPanel(controller);
		
		MainCardPanel cardPanel = new MainCardPanel(controller);
		cardPanel.add(new LoginPanel(controller), MainCardPanel.loginCard);
		cardPanel.add(new SelectPanel(controller), MainCardPanel.selectCard);
		cardPanel.add(new FrameViewPanel(controller), MainCardPanel.frameBrowseCard);
		cardPanel.add(new ExportFramePanel(controller), MainCardPanel.exportCard);
		cardPanel.add(new SearchPanel(controller), MainCardPanel.searchCard);
		cardPanel.add(new ExportPGDBStructurePanel(controller), MainCardPanel.structureExportCard);
		cardPanel.add(new DatabaseComparePanel(controller), MainCardPanel.databaseCompareCard);
		cardPanel.add(new LoadPanel(controller), MainCardPanel.loadCard);
		
		JFrame displayFrame = new JFrame("CycBrowser");
		controller.setMainJFrame(displayFrame);
		
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
        displayFrame.setVisible(true);
	}
}
