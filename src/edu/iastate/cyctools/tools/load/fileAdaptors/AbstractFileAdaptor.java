package edu.iastate.cyctools.tools.load.fileAdaptors;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;

public abstract class AbstractFileAdaptor {
	boolean append = true;
	boolean ignoreDuplicates = true;
	String multipleValueDelimiter = "$";
	
	public void setAppend(boolean append) {
		this.append = append;
	}
	
	public void setIgnoreDuplicates(boolean ignoreDuplicates) {
		this.ignoreDuplicates = ignoreDuplicates;
	}
	
	public void setMultipleValueDelimiter(String multipleValueDelimiter) {
		this.multipleValueDelimiter = multipleValueDelimiter;
	}
	
	public abstract ArrayList<AbstractFrameEdit> tableToFrameUpdates(TableModel tb);
}
