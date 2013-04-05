package edu.iastate.biocyctool.cycspreadsheetloader.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

import edu.iastate.biocyctool.cycspreadsheetloader.model.AbstractFrameEdit;
import edu.iastate.biocyctool.cycspreadsheetloader.model.DocumentModel;
import edu.iastate.biocyctool.cycspreadsheetloader.model.ReportModel;
import edu.iastate.biocyctool.cycspreadsheetloader.util.Interpretable;
import edu.iastate.biocyctool.util.da.CycDataBaseAccess;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;
import edu.iastate.javacyco.PtoolsErrorException;

public class DefaultController implements PropertyChangeListener {
	private CycDataBaseAccess dataAccess;
	private ArrayList<AbstractViewPanel> registeredViews;
	private DocumentModel documentModel;
	
	public static String DOCUMENT_FILEPATH_PROPERTY = "FilePath";
	public static String DOCUMENT_TABLEMODEL_PROPERTY = "TableModel";
	
    public DefaultController() {
    	registeredViews = new ArrayList<AbstractViewPanel>();
    	documentModel = null;
    	dataAccess = null;
    }
    
    public void addView(AbstractViewPanel view) {
        registeredViews.add(view);
    }

    public void removeView(AbstractViewPanel view) {
        registeredViews.remove(view);
    }
    
    public void setDocumentModel(DocumentModel documentModel) {
    	this.documentModel = documentModel;
    }
    
    public void setDataAccess(CycDataBaseAccess dataAccess) {
    	this.dataAccess = dataAccess;
    }
    
    public void changeDocumentFile(File newFile) {
    	documentModel.setFile(newFile);
    }
    
    public void submitTable(Interpretable interpreter) {
    	ArrayList<AbstractFrameEdit> frameUpdates = interpreter.tableToFrameUpdates(documentModel.getTableModel());
		try {
			dataAccess.commitFrameUpdates(frameUpdates);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
	}
    
	public void revertDataBase() {
		dataAccess.revertDataBase();
	}
	
	public void saveDataBase() {
		dataAccess.saveDataBase();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		for (AbstractViewPanel view: registeredViews) {
            view.modelPropertyChange(evt);
        }
	}
}