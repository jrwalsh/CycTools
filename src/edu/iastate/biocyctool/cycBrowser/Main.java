package edu.iastate.biocyctool.cycBrowser;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import java.awt.Dimension;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.EtchedBorder;

public class Main {
	private static JMenuBar menuBar;
	
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
		
		MainCardPanel cardPanel = new MainCardPanel(controller);
		cardPanel.add(loginPanel, MainCardPanel.loginCard);
		cardPanel.add(selectPanel, MainCardPanel.selectCard);
		cardPanel.add(frameBrowsePanel, MainCardPanel.frameBrowseCard);
		cardPanel.add(exportPanel, MainCardPanel.exportCard);
		cardPanel.add(searchPanel, MainCardPanel.searchCard);
		cardPanel.add(exportStructurePanel, MainCardPanel.structureExportCard);
		
		// Connect views, models, controllers, and data objects.
		controller.addView(cardPanel);
		controller.addView(toolPanel);
		controller.addView(statusPanel);
		controller.addView(loginPanel);
		controller.addView(selectPanel);
		controller.addView(frameBrowsePanel);
		controller.addView(exportPanel);
		controller.addView(searchPanel);
		controller.addView(exportStructurePanel);
		
		JFrame displayFrame = new JFrame("CycBrowser");
		displayFrame.setResizable(false);
		displayFrame.setPreferredSize(new Dimension(835, 535));
		displayFrame.setJMenuBar(new MenuBar(controller));
		displayFrame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		displayFrame.getContentPane().add(toolPanel);
		displayFrame.getContentPane().add(cardPanel);
		displayFrame.getContentPane().add(statusPanel);
        displayFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        displayFrame.pack();
        
        controller.setMainJFrame(displayFrame);
        
        displayFrame.setVisible(true);
	}
}
