package edu.iastate.biocyctool.tools.load.view;

import edu.iastate.biocyctool.DefaultController;
import edu.iastate.biocyctool.externalSourceCode.AbstractViewPanel;
import edu.iastate.biocyctool.tools.load.util.CustomInterpreter;
import edu.iastate.biocyctool.tools.load.util.SimpleInterpreter;

import java.awt.Component;
import java.beans.PropertyChangeEvent;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DataViewPanel extends AbstractViewPanel {
    // Controller used by this view
	private DefaultController controller;
    private JTable tableSpreadSheet;
    
    // Actions
    private final Action actionBrowse = new ActionBrowse();
    private final Action actionSubmit = new ActionSubmit();
    private final Action actionSave = new ActionSave();
    private final Action actionRevert = new ActionRevert();
    
    public DataViewPanel(DefaultController controller) {
        this.controller = controller;
        initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
    	JPanel fileOpenPanel = new JPanel();
        
        JButton btnBrowse = new JButton("Browse");
        btnBrowse.setAction(actionBrowse);
        
        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setAction(actionSubmit);
        
        JScrollPane SpreadsheetScrollPane = new JScrollPane();
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addComponent(fileOpenPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(SpreadsheetScrollPane, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))
        			.addContainerGap())
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(fileOpenPanel, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(SpreadsheetScrollPane, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
        			.addContainerGap())
        );
        
        tableSpreadSheet = new JTable();
        SpreadsheetScrollPane.setViewportView(tableSpreadSheet);
        
        JButton btnRevert = new JButton("Revert");
        btnRevert.setAction(actionRevert);
        
        JButton btnSave = new JButton("Save");
        btnSave.setAction(actionSave);
        GroupLayout gl_fileOpenPanel = new GroupLayout(fileOpenPanel);
        gl_fileOpenPanel.setHorizontalGroup(
        	gl_fileOpenPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_fileOpenPanel.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(btnBrowse)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(btnSubmit)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(btnRevert)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(btnSave)
        			.addGap(6))
        );
        gl_fileOpenPanel.setVerticalGroup(
        	gl_fileOpenPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_fileOpenPanel.createSequentialGroup()
        			.addGap(5)
        			.addGroup(gl_fileOpenPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(btnBrowse)
        				.addComponent(btnSubmit)
        				.addComponent(btnRevert)
        				.addComponent(btnSave)))
        );
        fileOpenPanel.setLayout(gl_fileOpenPanel);
        setLayout(groupLayout);
    }

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DefaultController.DOCUMENT_TABLEMODEL_PROPERTY) && evt.getNewValue() != null) {
			DefaultTableModel dtm = (DefaultTableModel)evt.getNewValue();
			tableSpreadSheet.setModel(dtm);
			revalidate();
			repaint();
		}
	}
	
	private class ActionBrowse extends AbstractAction {
		public ActionBrowse() {
			putValue(NAME, "Browse");
			putValue(SHORT_DESCRIPTION, "Browse local files");
		}
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showOpenDialog((Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
				controller.changeDocumentFile(fileChooser.getSelectedFile());
			}
		}
	}
	
	private class ActionSubmit extends AbstractAction {
		public ActionSubmit() {
			putValue(NAME, "Submit");
			putValue(SHORT_DESCRIPTION, "Submit data table to database");
		}
		public void actionPerformed(ActionEvent e) {
			controller.submitTable(new CustomInterpreter());//TODO How to make this select the right interpreter? Just hardcode it? Dropdown menu option?
		}
	}
	
	private class ActionSave extends AbstractAction {
		public ActionSave() {
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Save changes to database");
		}
		public void actionPerformed(ActionEvent e) {
			controller.saveDataBase();
		}
	}
	
	private class ActionRevert extends AbstractAction {
		public ActionRevert() {
			putValue(NAME, "Revert");
			putValue(SHORT_DESCRIPTION, "Revert changes to database");
		}
		public void actionPerformed(ActionEvent e) {
			controller.revertDataBase();
		}
	}
}