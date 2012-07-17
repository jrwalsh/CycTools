package edu.iastate.cycspreadsheetloader.util;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.cycspreadsheetloader.model.AbstractFrameUpdate;

public interface Interpretable {
	ArrayList<AbstractFrameUpdate> tableToFrameUpdates(TableModel tb);
}
