package edu.iastate.cyctools.tools.load.model;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.externalSourceCode.AbstractModel;

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
    private String delimiter;
    
    public DocumentModel() {
    }
    
    public void initDefault() {
        setFile(null, ",");
        setTableModel(null);
    }
    
    // Accessors
    public File getFile() {
        return file;
    }
    
    public void setFile(File file, String delimiter) {
        File oldFile = this.file;
        this.file = file;
        this.delimiter = delimiter;
        
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

    // Utilities
    private void readFile() {
    	if (file == null || !file.exists() || !file.canRead()) {
    		file = null;
    		return;
    	}
    	
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
    
