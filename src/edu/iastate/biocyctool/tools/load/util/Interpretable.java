package edu.iastate.biocyctool.tools.load.util;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.biocyctool.tools.load.model.AbstractFrameEdit;

public interface Interpretable {
	ArrayList<AbstractFrameEdit> tableToFrameUpdates(TableModel tb);
}
