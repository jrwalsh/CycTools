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
import java.awt.Insets;
import java.awt.event.ActionListener;

public class SelectPanel extends AbstractViewPanel {
	BrowserController controller;
	private final Action actionSelectFrameBrowser = new ActionSelectFrameBrowser();
	private final Action actionSelectExportPanel = new ActionSelectExportPanel();
	private final Action actionSelectSearchPanel = new ActionSelectSearchPanel();
	private final Action actionExportStructurePanel = new ActionExportStructurePanel();
	private final Action actionComparePanel = new ActionComparePanel();

	/**
	 * Create the frame.
	 * @param controller 
	 */
	public SelectPanel(BrowserController controller) {
		setPreferredSize(new Dimension(800, 400));
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
    	JButton btnFrameBrowse = new JButton("Frame Browse");
    	btnFrameBrowse.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent arg0) {
    		}
    	});
    	btnFrameBrowse.setMargin(new Insets(2, 2, 2, 2));
    	btnFrameBrowse.setAction(actionSelectFrameBrowser);
		
		JButton btnExport = new JButton("Export");
		btnExport.setMargin(new Insets(2, 2, 2, 2));
		btnExport.setAction(actionSelectExportPanel);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setMargin(new Insets(2, 2, 2, 2));
		btnSearch.setAction(actionSelectSearchPanel);
		
		JButton btnExportStructure = new JButton("Export Structure");
		btnExportStructure.setMargin(new Insets(2, 2, 2, 2));
		btnExportStructure.setAction(actionExportStructurePanel);
		
		JButton btnCompare = new JButton("Compare");
		btnCompare.setMargin(new Insets(2, 2, 2, 2));
		btnCompare.setAction(actionComparePanel);
		
		JLabel lblFrameInspect = new JLabel("<html>The frame inspect tool allows for a direct view of individual frame data.  This can be useful in understanding the structure of the PGDB data objects and where data is stored. </html>");
		
		JLabel lblSearch = new JLabel("<html>The search tool can be used to look search for multiple terms at once in the same way the substring search works in PathwayTools.</html>");
		
		JLabel lblExport = new JLabel("<html>The export tool is used to print out frame data to a file.  Multiple file formats are allowed.</html>");
		
		JLabel lblStructure = new JLabel("<html>The structure export tool is used to view the underlying GFP structure of the PGDB.</html>");
		
		JLabel lbltheCompareTool = new JLabel("<html>The compare tool allows a frame by frame comparison between two databases and reports on any frames that do not match.</html>");
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(55)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnCompare, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lbltheCompareTool, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(btnFrameBrowse, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblSearch, 0, 0, Short.MAX_VALUE)
								.addComponent(lblFrameInspect, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(btnExport, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnExportStructure, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblExport, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblStructure, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(56)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblExport, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
							.addComponent(lblFrameInspect, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnFrameBrowse, GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
						.addComponent(btnExport, GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblStructure, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnExportStructure, GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
							.addComponent(lblSearch, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnSearch, GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnCompare, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
						.addComponent(lbltheCompareTool, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
					.addGap(56))
		);
		setLayout(groupLayout);
	}
    
	private class ActionSelectFrameBrowser extends AbstractAction {
		public ActionSelectFrameBrowser() {
			putValue(NAME, "<html><center>Frame Viewer</center></html>");
			putValue(SHORT_DESCRIPTION, "Open the Frame Browser screen.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.FRAMEBROWSE);
		}
	}
	
	private class ActionSelectExportPanel extends AbstractAction {
		public ActionSelectExportPanel() {
			putValue(NAME, "<html><center>Export Frames</center></html>");
			putValue(SHORT_DESCRIPTION, "Open the export screen.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.EXPORT);
		}
	}
	
	private class ActionSelectSearchPanel extends AbstractAction {
		public ActionSelectSearchPanel() {
			putValue(NAME, "<html><center>Bulk Search</center></html>");
			putValue(SHORT_DESCRIPTION, "Open the search screen.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.SEARCH);
		}
	}
	
	private class ActionExportStructurePanel extends AbstractAction {
		public ActionExportStructurePanel() {
			putValue(NAME, "<html><center>Export Structure</center></html>");
			putValue(SHORT_DESCRIPTION, "Export underlying PGDB Class and Instance structure.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.STRUCTURE_EXPORT);
		}
	}
	
	private class ActionComparePanel extends AbstractAction {
		public ActionComparePanel() {
			putValue(NAME, "<html><center>Database Compare<br>Tool</center></html>");
			putValue(SHORT_DESCRIPTION, "Compare two PGDB databases frame by frame.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.setState(State.DATABASE_COMPARE);
		}
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
}
