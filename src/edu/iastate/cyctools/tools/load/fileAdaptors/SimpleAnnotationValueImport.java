package edu.iastate.cyctools.tools.load.fileAdaptors;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.table.TableModel;

import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.cyctools.tools.load.model.AnnotationUpdate;

public class SimpleAnnotationValueImport extends AbstractFileAdaptor {

	public SimpleAnnotationValueImport() {
		setAppend(true);
		setIgnoreDuplicates(true);
		setMultipleValueDelimiter("$");
	}
	
	// Assumes one frame, one annotation per row
	// Assumes frameID is in 1st column
	// Assumes slot label is 2nd column header, slot value is 2nd column values
	// Assumes annotation label is 3rd column header, annotation values is 3rd column values 
	public ArrayList<AbstractFrameEdit> tableToFrameUpdates(TableModel tb) {
		ArrayList<AbstractFrameEdit> frameUpdates = new ArrayList<AbstractFrameEdit>();
		
		String slotLabel = (String) tb.getColumnName(1);
		String annotationLabel = (String) tb.getColumnName(2);
		
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			String frameID = (String) tb.getValueAt(rowIndex, 0);
			String slotValue = (String) tb.getValueAt(rowIndex, 1);

			ArrayList<String> values = new ArrayList<String>();
			String columnValue = (String) tb.getValueAt(rowIndex, 2);
			if (columnValue == null || columnValue.length() <= 0) {
				// do nothing
			} else if (columnValue.contains(multipleValueDelimiter)) {
				String delimiter = Pattern.quote(multipleValueDelimiter);
				String[] multipleValueArray = columnValue.split(delimiter);
				for (String value : multipleValueArray) {
					values.add(value);
				}
			} else values.add(columnValue);
			
			frameUpdates.add(new AnnotationUpdate(frameID, slotLabel, slotValue, annotationLabel, values, append, ignoreDuplicates, new int[] {rowIndex+1}));
		}
		return frameUpdates;
	}
}
