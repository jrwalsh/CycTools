package edu.iastate.biocyctool.cycspreadsheetloader.util;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.biocyctool.cycspreadsheetloader.model.AbstractFrameEdit;

public interface Interpretable {
	ArrayList<AbstractFrameEdit> tableToFrameUpdates(TableModel tb);
}
