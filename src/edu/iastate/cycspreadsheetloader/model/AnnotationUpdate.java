package edu.iastate.cycspreadsheetloader.model;

import java.util.ArrayList;

import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public class AnnotationUpdate extends AbstractFrameUpdate {
	private String slotLabel;
	private String slotValue;
	private String annotationLabel;
	private ArrayList<String> annotationValues;
	
	public AnnotationUpdate(String frameID, String slotLabel, String slotValue, String annotationLabel, ArrayList<String> annotationValues) {
		this.frameID = frameID;
		this.slotLabel = slotLabel;
		this.slotValue = slotValue;
		this.annotationLabel = annotationLabel;
		this.annotationValues = annotationValues;
	}

	@Override
	protected void commit(JavacycConnection conn) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected boolean isRemoteValueEmpty(JavacycConnection conn) {
		ArrayList<String> remoteAnnotationValues;
		try {
			remoteAnnotationValues = (ArrayList<String>) conn.getValueAnnots(frameID, slotLabel, slotValue, annotationLabel);
			if (remoteAnnotationValues == null || remoteAnnotationValues.isEmpty()) {
				return true;
			} else 
				return false;
		} catch (PtoolsErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	protected boolean isRemoteValueDuplicate(JavacycConnection conn) {
		ArrayList<String> remoteAnnotationValues;
		try {
			remoteAnnotationValues = (ArrayList<String>) conn.getValueAnnots(frameID, slotLabel, slotValue, annotationLabel);
			if (remoteAnnotationValues.containsAll(annotationValues) && annotationValues.containsAll(remoteAnnotationValues)) {
				return true;
			} else 
				return false;
		} catch (PtoolsErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
