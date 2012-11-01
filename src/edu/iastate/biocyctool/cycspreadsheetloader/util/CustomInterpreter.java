package edu.iastate.biocyctool.cycspreadsheetloader.util;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.biocyctool.cycspreadsheetloader.model.AbstractFrameUpdate;
import edu.iastate.biocyctool.cycspreadsheetloader.model.AnnotationUpdate;
import edu.iastate.biocyctool.cycspreadsheetloader.model.SlotUpdate;
import edu.iastate.javacyco.JavacycConnection;

// Designed for the MaizeGDB teams curation updates.  To be designed to handle their specifically formated input file.
public class CustomInterpreter implements Interpretable {

	public CustomInterpreter() {
	}
	
	// Assumes exact format for file with a defined column for each piece of info
	// Assumes one value per cell
	// Assumes headers are ignored, effectively skipping row one
	// Assumes frameID is in first column
	public ArrayList<AbstractFrameUpdate> tableToFrameUpdates(TableModel tb) {
		ArrayList<AbstractFrameUpdate> frameUpdates = new ArrayList<AbstractFrameUpdate>();
		
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			String frameID = (String) tb.getValueAt(rowIndex, 0);
//			frameUpdates.add(new SlotUpdate(frameID, "CurrentGeneModelName", (String) tb.getValueAt(rowIndex, 1)));
			String goTerm = (String) tb.getValueAt(rowIndex, 2);
			frameUpdates.add(new SlotUpdate(frameID, "GO-TERMS", goTerm, true, true));
//			frameUpdates.add(new SlotUpdate(frameID, "ReferenceID", (String) tb.getValueAt(rowIndex, 5)));
			
			ArrayList<String> annotValues = new ArrayList<String>();
			annotValues.add("\"" +(String) tb.getValueAt(rowIndex, 4) + ":" + (String) tb.getValueAt(rowIndex, 3) + ":3501259540:" + (String) tb.getValueAt(rowIndex, 6) + "\"");
			frameUpdates.add(new AnnotationUpdate(frameID, "GO-TERMS", goTerm, "CITATIONS", annotValues, true, true));
		}
		return frameUpdates;
	}
}
