package edu.iastate.cyctools.tools.load.model;

import java.util.ArrayList;

public class GOTermAnnotationUpdate extends AnnotationUpdate {

	// For most intents and purposes, a GOTermAnnotationUpdate is just an annotation update.  The primary difference is in the fact that Go term annotations are stored
	// as a list of delimited values within the single string entry.  We want to be able to differentiate different parts of this entry.
	
	public GOTermAnnotationUpdate(String frameID, String slotLabel, String slotValue, String annotationLabel, ArrayList<GOAnnotation> goAnnotationValues, boolean append, boolean ignoreDuplicates, int[] associatedRows) {
		super(frameID, slotLabel, slotValue, annotationLabel, new ArrayList<String>(), append, ignoreDuplicates, associatedRows);
		
		ArrayList<String> annotationValues = new ArrayList<String>();
		for (GOAnnotation goAnnot : goAnnotationValues) {
			annotationValues.add("\"" + goAnnot.getPubmedID() + ":" + goAnnot.getEvCode() + ":" + goAnnot.getTimeStampString() + ":" + goAnnot.getCurator() + "\"");
		}
		this.annotationValues = annotationValues;
	}
	
}
