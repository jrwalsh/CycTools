package edu.iastate.cycspreadsheetloader.util;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.javacyco.Frame;

public class FrameUpdate {
	private String FrameID;
	private ArrayList<SlotUpdate> SlotUpdates;
	private ArrayList<AnnotationUpdate> AnnotationUpdates;
	
	// Assumes one frame per row
	// Assumes one value per cell
	// Assumes only slot updates, no annotation updates
	// Assumes slot label is column header
	// Assumes frameID is in first column
	public static ArrayList<FrameUpdate> tableToFrameUpdates(TableModel tb) {
		ArrayList<FrameUpdate> frameUpdates = new ArrayList<FrameUpdate>();
		
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			FrameUpdate frameUpdate = new FrameUpdate();
			
			for (int columnIndex = 0; columnIndex < tb.getColumnCount(); columnIndex++) {
				if (columnIndex == 0) frameUpdate.FrameID = (String) tb.getValueAt(rowIndex, columnIndex);
				else {
					frameUpdate.addSlotUpdate(new SlotUpdate(tb.getColumnName(columnIndex), (String) tb.getValueAt(rowIndex, columnIndex)));
				}
			}
			frameUpdates.add(frameUpdate);
		}
		
		return frameUpdates;
	}
	
	private FrameUpdate() {
		SlotUpdates = new ArrayList<SlotUpdate>();
		AnnotationUpdates = new ArrayList<AnnotationUpdate>();
	}
	
	public String getFrameID() {
		return FrameID;
	}
	
	private void addSlotUpdate(SlotUpdate slotUpdate) {
		SlotUpdates.add(slotUpdate);
	}

	public ArrayList<SlotUpdate> getSlotUpdates() {
		return SlotUpdates;
	}
}
