package edu.iastate.biocyctool.cycspreadsheetloader.util;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.biocyctool.cycspreadsheetloader.model.AbstractFrameEdit;
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
	public ArrayList<AbstractFrameEdit> tableToFrameUpdates(TableModel tb) {
		ArrayList<AbstractFrameEdit> frameUpdates = new ArrayList<AbstractFrameEdit>();
		
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			String frameID = (String) tb.getValueAt(rowIndex, 0);
			String goTerm = (String) tb.getValueAt(rowIndex, 1);
			String pubMedID = (String) tb.getValueAt(rowIndex, 2);
			String evCode = (String) tb.getValueAt(rowIndex, 3);
			String timeStampString = (String) tb.getValueAt(rowIndex, 4);
			String curator = (String) tb.getValueAt(rowIndex, 5);
			
			JavacycConnection conn = new JavacycConnection("jrwalsh.student.iastate.edu", 4444);
			conn.selectOrganism("CORN");
			String encodedTime = encodeTimeStampString(timeStampString, conn);
			
			frameUpdates.add(new SlotUpdate(frameID, "GO-TERMS", goTerm, true, true));
			
			ArrayList<String> annotValues = new ArrayList<String>();
			annotValues.add("\"" + pubMedID + ":" + evCode + ":" + encodedTime + ":" + curator + "\"");
			frameUpdates.add(new AnnotationUpdate(frameID, "GO-TERMS", goTerm, "CITATIONS", annotValues, true, true));
		}
		return frameUpdates;
	}
	
	// Agreed upon format is dd-mm-yyyy hh-mm-ss
	private String encodeTimeStampString(String timeStampString, JavacycConnection conn) {
		String encodedTime = "";
		
		if (timeStampString == null || timeStampString.length() == 0) {
			return "";
		}
		
		try {
			String date = timeStampString.split(" ")[0];
			String time = timeStampString.split(" ")[1];
			
			String day = date.split("-")[0];
			String month = date.split("-")[1];
			String year = date.split("-")[2];
			
			String hour = time.split("-")[0];
			String minute = time.split("-")[1];
			String second = time.split("-")[2];
			
			encodedTime = conn.encodeTimeStamp(second, minute, hour, day, month, year);
		} catch (Exception e) {
			System.err.println("Failed to encode timestamp " + timeStampString);
			e.printStackTrace();
		}
		
		return encodedTime;
	}
}
