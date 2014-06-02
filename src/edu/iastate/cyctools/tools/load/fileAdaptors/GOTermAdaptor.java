package edu.iastate.cyctools.tools.load.fileAdaptors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.cyctools.tools.load.model.GOAnnotation;
import edu.iastate.cyctools.tools.load.model.GOTermAnnotationUpdate;
import edu.iastate.cyctools.tools.load.model.SlotUpdate;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

// Designed for the MaizeGDB teams curation updates.  To be designed to handle their specifically formated input file.  This generically imports GO-Term annotations.
@SuppressWarnings("unchecked")
public class GOTermAdaptor extends AbstractFileAdaptor {
	
	public GOTermAdaptor() {
		setAppend(true);
		setIgnoreDuplicates(true);
		setMultipleValueDelimiter("$"); // Unused
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
			
			JavacycConnection conn = new JavacycConnection("jrwalsh.student.iastate.edu", 4444);//TODO attach the default connection object here
			conn.selectOrganism("CORN");
			
			if (!goTerm.startsWith("|")) goTerm = "|" + goTerm;
			if (!goTerm.endsWith("|")) goTerm = goTerm + "|";
			frameUpdates.add(new SlotUpdate(frameID, "GO-TERMS", goTerm, append, ignoreDuplicates, new int[] {rowIndex}));
			
			ArrayList<GOAnnotation> goAnnotationValues = new ArrayList<GOAnnotation>();
			goAnnotationValues.add(new GOAnnotation(pubMedID, evCode, timeStampString, curator));
			frameUpdates.add(new GOTermAnnotationUpdate(frameID, "GO-TERMS", goTerm, "CITATIONS", goAnnotationValues, append, ignoreDuplicates, new int[] {rowIndex}));
		}
		return frameUpdates;
	}
}
