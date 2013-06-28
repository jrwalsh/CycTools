package edu.iastate.cyctools.tools.load.fileAdaptors;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.cyctools.tools.load.model.SlotUpdate;

public class SimpleInterpreter implements FileAdaptor {
	private boolean append;
	private boolean ignoreDuplicates;
	private String multipleValueDelimiter;

	public SimpleInterpreter() {
		append = true;
		ignoreDuplicates = true;
		multipleValueDelimiter = "$";//TODO convert multiple value entries into arrays before insert
	}
	
	// Assumes one frame per row
	// Assumes one value per cell
	// Assumes only slot updates, no annotation updates
	// Assumes slot label is column header
	// Assumes frameID is in first column
	public ArrayList<AbstractFrameEdit> tableToFrameUpdates(TableModel tb) {
		ArrayList<AbstractFrameEdit> frameUpdates = new ArrayList<AbstractFrameEdit>();
		
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			String frameID = (String) tb.getValueAt(rowIndex, 0);
			for (int columnIndex = 1; columnIndex < tb.getColumnCount(); columnIndex++) {
				
				String slotLabel = (String) tb.getColumnName(columnIndex);
				ArrayList<String> values = new ArrayList<String>();
				values.add((String) tb.getValueAt(rowIndex, columnIndex));
				
				frameUpdates.add(new SlotUpdate(frameID, slotLabel, values, append, ignoreDuplicates, new int[] {rowIndex}));
			}
		}
		return frameUpdates;
	}
	
	@Override
	public void setAppend(boolean append) {
		this.append = append;
	}

	@Override
	public void setIgnoreDuplicates(boolean ignoreDuplicates) {
		this.ignoreDuplicates = ignoreDuplicates;
	}

	@Override
	public void setMultipleValueDelimiter(String multipleValueDelimiter) {
		this.multipleValueDelimiter = multipleValueDelimiter;
	}
}
