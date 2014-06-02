package edu.iastate.cyctools.tools.load.model;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

@SuppressWarnings("unchecked")
public class AnnotationUpdate extends AbstractFrameDataEdit {
	private String slotLabel;
	private String slotValue;
	private String annotationLabel;
	protected ArrayList<String> annotationValues;
	
	public AnnotationUpdate(String frameID, String slotLabel, String slotValue, String annotationLabel, ArrayList<String> annotationValues,  boolean append, boolean ignoreDuplicates, int[] associatedRows) {
		this.frameID = frameID;
		this.slotLabel = slotLabel;
		this.slotValue = slotValue;
		this.annotationLabel = annotationLabel;
		this.annotationValues = annotationValues;
		this.append = append;
		this.ignoreDuplicates = ignoreDuplicates;
		this.associatedRows = associatedRows;
	}

	protected String getSlotLabel() {
		return slotLabel;
	}
	
	protected String getSlotValue() {
		return slotValue;
	}

	protected String getAnnotationLabel() {
		return annotationLabel;
	}
	
	// Catches all "special" cases for annotation updates, such as PUBMED citations
	@Override
	public boolean commit(JavacycConnection conn) {
		Frame frame = null;
		try {
			if(!conn.frameExists(frameID)) {
				return false;
			}
			
			frame = this.getFrame(conn);
		} catch (PtoolsErrorException e1) {
			System.out.println("not sure how you would reach this");
			return false;
		}
		
		ArrayList<String> newValues = new ArrayList<String>();
		if (append) {
			try {
				newValues.addAll(frame.getAnnotations(slotLabel, slotValue, annotationLabel));
			} catch (PtoolsErrorException e) {
				System.out.println("not sure what to make of this.  The slot doesn't exist? bad data format?");
				return false;
			}
		}
		
		if (ignoreDuplicates) {
			for (String value : annotationValues) {
				if (!newValues.contains(value)) newValues.add(value);
			}
		} else newValues.addAll(annotationValues);
		
		frame.putLocalSlotValueAnnotations(slotLabel, slotValue, annotationLabel, newValues);
		
		try {
			frame.commit();
		} catch (PtoolsErrorException e) {
			System.out.println("error during commit, recommend a rollback");
			return false;
		}
		
		return true;
	}
	
	@Override
	public Frame commitLocal(Frame frame) throws PtoolsErrorException {
		ArrayList<String> newValues = new ArrayList<String>();
		if (append) {
			newValues.addAll(frame.getAnnotations(slotLabel, slotValue, annotationLabel));
		}
		
		if (ignoreDuplicates) {
			for (String value : annotationValues) {
				if (!newValues.contains(value)) newValues.add(value);
			}
		} else newValues.addAll(annotationValues);
		
		frame.putLocalSlotValueAnnotations(slotLabel, slotValue, annotationLabel, newValues);
		
		return frame;
	}
	
	@Override
	protected ArrayList<String> getValues() {
		return annotationValues;
	}

	@Override
	protected ArrayList<String> getRemoteValues(JavacycConnection conn) throws PtoolsErrorException {
		return (ArrayList<String>) conn.getValueAnnots(frameID, slotLabel, slotValue, annotationLabel);
	}

	@Override
	public boolean modifiesFrame(JavacycConnection conn, Frame aFrame) throws PtoolsErrorException {
		Frame frameToModify = aFrame.copy(aFrame.getLocalID());
		commitLocal(frameToModify);
		boolean isModified = !aFrame.equalBySlotValues(frameToModify); // If the frames are not equal, a change has been made to the database.
		return isModified;
	}

	@Override
	public String toString() {
		String annotValues = "";
		try {
			for (String value : annotationValues) {
			annotValues += value + ", ";
			}
			if (annotValues.length() > 0) annotValues = annotValues.substring(0, annotValues.length()-2);
		} catch (Exception e) {}

		return "updating annotation: " + annotationLabel + " for slot: " + slotLabel + " on frame: " + frameID + " with the values: " + annotValues;
	}
}