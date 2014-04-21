package edu.iastate.cyctools.tools.load.model;

import java.util.ArrayList;
import java.util.HashMap;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

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
	
	
	// Is this feature still requested, since we have conflicting suggestions to either keep this change or remove it.
	
	/*
	// As a particular special case request by Taner, we do not want to consider a GOTerm annotation changed unless the change occurs somewhere other than in the curator
	// piece of the annotation.  To do this, we need to make a custom equalBySlotValue check.
	@Override
	public boolean modifiesFrame(JavacycConnection conn, Frame aFrame) throws PtoolsErrorException {
		Frame frameToModify = aFrame.copy(aFrame.getLocalID());
		commitLocal(frameToModify);
		boolean isModified = !equalBySlotValues(aFrame, frameToModify); // If the frames are not equal, a change has been made to the database.
		return isModified;
	}
	
	private boolean equalBySlotValues(Frame aFrame, Frame anotherFrame) {
		try {
			if (!aFrame.getLocalSlots().equals(anotherFrame.getLocalSlots())) return false;
			if (!aFrame.slotValueAnnotations.equals(anotherFrame.slotValueAnnotations) && aFrame.slotValueAnnotations.size() == anotherFrame.slotValueAnnotations.size()) {
				// We know that something isn't equal but there isn't anything added or subtracted.
				// Now lets break down the annotation and see if the change is somewhere other than curator.
				// Assume that the change is only in a curator, and if we are found wrong (i.e. find one inequality not due to curator), then return false
				for (String slotName : aFrame.slotValueAnnotations.keySet()) {
					HashMap<String, HashMap<String, ArrayList>> aFrameMap1 = aFrame.slotValueAnnotations.get(slotName);
					HashMap<String, HashMap<String, ArrayList>> anotherFrameMap1 = anotherFrame.slotValueAnnotations.get(slotName);
					if (!aFrameMap1.equals(anotherFrameMap1) && aFrameMap1.size() == anotherFrameMap1.size()) {
						for (String slotValue : aFrameMap1.keySet()) {
							HashMap<String, ArrayList> aFrameMap2 = aFrameMap1.get(slotValue);
							HashMap<String, ArrayList> anotherFrameMap2 = anotherFrameMap1.get(slotValue);
							if (!aFrameMap2.equals(anotherFrameMap2) && aFrameMap2.size() == anotherFrameMap2.size()) {
								for (String annotationName : aFrameMap2.keySet()) {
									ArrayList aFrameValues = aFrameMap2.get(annotationName);
									ArrayList anotherFrameValues = anotherFrameMap2.get(annotationName);
									
									//TODO.....aFrame split into annotation parts and check.
									
								}
							} else return false;
						}
					} else return false;
				}
			} else return false;
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return true;
	}
	*/
}
