package edu.iastate.cycspreadsheetloader.model;

import edu.iastate.biocyctool.util.model.AbstractModel;
import edu.iastate.cycspreadsheetloader.controller.DefaultController;
import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class DocumentModel extends AbstractModel {
    private File file;
    private TableModel tableModel;
    private boolean saved; //TODO remove this???
    private String delimiter = ","; //TODO add file delimiter property set/get
    
    public DocumentModel() {   
    }
    
    public void initDefault() {
        setFile(null);
        setTableModel(null);
        setSaved(false);
    }
    
    // Accessors
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        File oldFile = this.file;
        this.file = file;
        
        readFile();
        
        firePropertyChange(DefaultController.DOCUMENT_FILEPATH_PROPERTY, oldFile, file);
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    private void setTableModel(TableModel tableModel) {
        TableModel oldTableModel = this.tableModel;
        this.tableModel = tableModel;
        firePropertyChange(DefaultController.DOCUMENT_TABLEMODEL_PROPERTY, oldTableModel, tableModel);
    }

    public boolean getSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        boolean oldSaved = this.saved;
        this.saved = saved;
        firePropertyChange(DefaultController.DOCUMENT_SAVED_PROPERTY, oldSaved, saved);
    }

    // Utilities
    private void readFile() {
    	if (file == null) return;
    	
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			
			String headerLine = reader.readLine();
			ArrayList<String> dataRows = new ArrayList<String>();
			while ((text = reader.readLine()) != null) {
				dataRows.add(text);
			}
			
			Object[] header = headerLine.split(delimiter);
			Object[][] data = new Object[dataRows.size()][dataRows.get(0).split(delimiter).length];
			for (int i=0; i<dataRows.size(); i++) {
				data[i] = dataRows.get(i).split(delimiter);
			}
			
			DefaultTableModel dtm = new DefaultTableModel(data, header);
			setTableModel(dtm);
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		finally {
			try {
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
    }
}
    
