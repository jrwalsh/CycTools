package edu.iastate.biocyctool.cycspreadsheetloader;

import edu.iastate.biocyctool.cycspreadsheetloader.controller.DefaultController;
import edu.iastate.biocyctool.cycspreadsheetloader.model.CycDataBaseAccess;
import edu.iastate.biocyctool.cycspreadsheetloader.model.DocumentModel;
import edu.iastate.biocyctool.cycspreadsheetloader.view.DataViewPanel;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class Main {
	private static String host = "jrwalsh.student.iastate.edu";
	private static int port = 4444;
	private static String organism = "CORN";
	
	public static void main(String[] args) {
		// DataAccess
		CycDataBaseAccess da = new CycDataBaseAccess(host, port, organism);
		da.initDefault();
		
		// Models
		DocumentModel document = new DocumentModel();
		document.initDefault();
		
		// Controllers
		DefaultController controller = new DefaultController();
		
		// Views
		DataViewPanel dataViewPanel = new DataViewPanel(controller);
		dataViewPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		// Connect views, models, controllers, and data objects.
		controller.setDocumentModel(document);
		controller.addView(dataViewPanel);
		controller.setDataAccess(da);
		document.addPropertyChangeListener(controller);
		
        JFrame displayFrame = new JFrame("BioCyc SpreadSheet Uploader");
        GroupLayout groupLayout = new GroupLayout(displayFrame.getContentPane());
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addComponent(dataViewPanel, GroupLayout.PREFERRED_SIZE, 557, Short.MAX_VALUE)
        				.addGroup(groupLayout.createSequentialGroup()
        					.addPreferredGap(ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
        					))
        			.addContainerGap())
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
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