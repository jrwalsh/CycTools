package edu.iastate.cyctools.tools.load.util;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.cyctools.tools.load.model.SlotUpdate;

public class SimpleInterpreter implements Interpretable {

	public SimpleInterpreter() {
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
				
				frameUpdates.add(new SlotUpdate(frameID, slotLabel, values, true, true));
			}
		}
		return frameUpdates;
	}
}
