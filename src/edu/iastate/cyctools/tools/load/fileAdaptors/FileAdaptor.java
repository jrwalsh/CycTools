package edu.iastate.cyctools.tools.load.fileAdaptors;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;

public interface FileAdaptor {
	public ArrayList<AbstractFrameEdit> tableToFrameUpdates(TableModel tb);
	public void setAppend(boolean append);
	public void setIgnoreDuplicates(boolean ignoreDuplicates);
	public void setMultipleValueDelimiter(String multipleValueDelimiter);
}
