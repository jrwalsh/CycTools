package edu.iastate.cycspreadsheetloader.view;

import edu.iastate.cycspreadsheetloader.controller.DefaultController;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class DataViewPanel extends AbstractViewPanel {
    // The controller used by this view
    private DefaultController controller;
    private JTextField txtFilepath;
    private final Action actionBrowse = new ActionBrowse();
    private JTable tableSpreadSheet;
    private final Action actionSubmit = new ActionSubmit();
    
    public DataViewPanel(DefaultController controller) {
        this.controller = controller;
        initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	
    }
    
    private void initComponents() {
    	JPanel fileOpenPanel = new JPanel();
        
        txtFilepath = new JTextField();
        txtFilepath.setText("filePath");
        txtFilepath.setColumns(10);
        
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
        GroupLayout gl_fileOpenPanel = new GroupLayout(fileOpenPanel);
        gl_fileOpenPanel.setHorizontalGroup(
        	gl_fileOpenPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_fileOpenPanel.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(txtFilepath, GroupLayout.PREFERRED_SIZE, 231, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(btnBrowse)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(btnSubmit)
        			.addContainerGap())
        );
        gl_fileOpenPanel.setVerticalGroup(
        	gl_fileOpenPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_fileOpenPanel.createSequentialGroup()
        			.addGap(5)
        			.addGroup(gl_fileOpenPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(btnSubmit)
        				.addComponent(btnBrowse)
        				.addComponent(txtFilepath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
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
			controller.loadByTableHeader();
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