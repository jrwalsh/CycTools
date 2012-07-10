package edu.iastate.cycspreadsheetloader.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

import edu.iastate.cycspreadsheetloader.dal.DataAccess;
import edu.iastate.cycspreadsheetloader.model.DocumentModel;
import edu.iastate.cycspreadsheetloader.util.FrameUpdate;
import edu.iastate.cycspreadsheetloader.view.AbstractViewPanel;
import edu.iastate.javacyco.PtoolsErrorException;

public class DefaultController implements PropertyChangeListener {
	private DataAccess dataAccess;
	private ArrayList<AbstractViewPanel> registeredViews;
	private DocumentModel documentModel;
	
	public static String DOCUMENT_FILEPATH_PROPERTY = "FilePath";
	public static String DOCUMENT_TABLEMODEL_PROPERTY = "TableModel";
	public static String DOCUMENT_SAVED_PROPERTY = "DocumentSaved";
	
    public DefaultController() {
    	registeredViews = new ArrayList<AbstractViewPanel>();
    	documentModel = new DocumentModel();
    	dataAccess = new DataAccess();
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
    
    public void changeDocumentFile(File newFile) {
    	documentModel.setFile(newFile);
    }
    
    public void changeDocumentSaved(boolean isSaved) {
    	documentModel.setSaved(isSaved);
    }
    
    public void loadByTableHeader() {
    	ArrayList<FrameUpdate> frameUpdates = FrameUpdate.tableToFrameUpdates(documentModel.getTableModel());
		try {
			dataAccess.loadFrameUpdates(frameUpdates);
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