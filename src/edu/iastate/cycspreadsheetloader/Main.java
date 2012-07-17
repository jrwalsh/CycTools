package edu.iastate.cycspreadsheetloader;

import edu.iastate.cycspreadsheetloader.controller.DefaultController;
import edu.iastate.cycspreadsheetloader.dal.CycDataBaseAccess;
import edu.iastate.cycspreadsheetloader.model.DocumentModel;
import edu.iastate.cycspreadsheetloader.view.ConnectionViewPanel;
import edu.iastate.cycspreadsheetloader.view.ControlViewPanel;
import edu.iastate.cycspreadsheetloader.view.DataViewPanel;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;


public class Main {

	public static void main(String[] args) {
		// DataAccess
		CycDataBaseAccess da = new CycDataBaseAccess();
		da.initDefault();
		
		// Models
		DocumentModel document = new DocumentModel();
		
		// Controllers
		DefaultController controller = new DefaultController();
		
		// Views
		DataViewPanel dataViewPanel = new DataViewPanel(controller);
		dataViewPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		controller.setDocumentModel(document);
		controller.addView(dataViewPanel);
		document.addPropertyChangeListener(controller);
		
		document.initDefault();
		
        JFrame displayFrame = new JFrame("BioCyc SpreadSheet Uploader");
        GroupLayout groupLayout = new GroupLayout(displayFrame.getContentPane());
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addComponent(dataViewPanel, GroupLayout.PREFERRED_SIZE, 557, Short.MAX_VALUE)
        				.addGroup(groupLayout.createSequentialGroup()
//        					.addComponent(connectionViewPanel, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
//        					.addComponent(controlViewPanel, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
        					))
        			.addContainerGap())
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
//        				.addComponent(connectionViewPanel, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
//        				.addComponent(controlViewPanel, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
        					)
        			.addGap(18)
        			.addComponent(dataViewPanel, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
        			.addContainerGap())
        );
        displayFrame.getContentPane().setLayout(groupLayout);
        displayFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        displayFrame.pack();
        
        displayFrame.setVisible(true);
    }
}