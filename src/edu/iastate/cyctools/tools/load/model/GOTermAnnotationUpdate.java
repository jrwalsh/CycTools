package edu.iastate.cyctools.tools.load.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.iastate.cyctools.CycDataBaseAccess;
import edu.iastate.javacyco.JavacycConnection;

public class GOTermAnnotationUpdate extends AnnotationUpdate {

	// For most intents and purposes, a GOTermAnnotationUpdate is just an annotation update.  The primary difference is in the fact that Go term annotations are stored
	// as a list of delimited values within the single string entry.  We want to be able to differentiate different parts of this entry.
	
	public GOTermAnnotationUpdate(String frameID, String slotLabel, String slotValue, String annotationLabel, ArrayList<GOAnnotation> goAnnotationValues, boolean append, boolean ignoreDuplicates, int[] associatedRows) {
		super(frameID, slotLabel, slotValue, annotationLabel, new ArrayList<String>(), append, ignoreDuplicates, associatedRows);
		
		ArrayList<String> annotationValues = new ArrayList<String>();
		for (GOAnnotation goAnnot : goAnnotationValues) {
			String encodedTime = encodeTimeStampString(goAnnot.getTimeStampString(), CycDataBaseAccess.conn);
			annotationValues.add("\"" + goAnnot.getPubmedID() + ":" + goAnnot.getEvCode() + ":" + encodedTime + ":" + goAnnot.getCurator() + "\"");
		}
		this.annotationValues = annotationValues;
	}
	
	private String encodeTimeStampString(String timeStampString, JavacycConnection conn) {
		// Agreed upon format of timeStampString is mm-dd-yyyy hh-mm-ss
		String encodedTime = "";
		
		if (timeStampString == null || timeStampString.length() == 0) {
			return "";
		}
		
		try {
			String date = timeStampString.split(" ")[0];
			String time = timeStampString.split(" ")[1];
			
			String month = date.split("-")[0];
			String day = date.split("-")[1];
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
