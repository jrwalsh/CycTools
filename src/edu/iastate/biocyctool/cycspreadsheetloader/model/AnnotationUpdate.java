package edu.iastate.biocyctool.cycspreadsheetloader.model;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
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
	public void commit(JavacycConnection conn) throws PtoolsErrorException {
		Frame frame = this.getFrame(conn);
		
		//TODO only if set to append
		ArrayList<String> values = new ArrayList<String>();
		values.addAll(frame.getAnnotations(slotLabel, slotValue, annotationLabel));
		values.addAll(annotationValues);
		
		frame.putLocalSlotValueAnnotations(slotLabel, slotValue, annotationLabel, values);
		frame.commit();
	}
	
//	@Override
//	protected boolean checkRemoteValueEmpty(JavacycConnection conn) throws PtoolsErrorException {
//		ArrayList<String> remoteAnnotationValues = (ArrayList<String>) conn.getValueAnnots(frameID, slotLabel, slotValue, annotationLabel);
//		if (remoteAnnotationValues == null || remoteAnnotationValues.isEmpty()) {
//			return true;
//		} else 
//			return false;
//	}
//
//	@Override
//	protected boolean checkRemoteValueDuplicate(JavacycConnection conn) throws PtoolsErrorException {
//		ArrayList<String> remoteAnnotationValues = (ArrayList<String>) conn.getValueAnnots(frameID, slotLabel, slotValue, annotationLabel);
//		if (remoteAnnotationValues.containsAll(annotationValues) && annotationValues.containsAll(remoteAnnotationValues)) {
//			return true;
//		} else 
//			return false;
//	}
	
	@Override
	protected ArrayList<String> getValues() {
		return annotationValues;
	}

	@Override
	protected ArrayList<String> getRemoteValues(JavacycConnection conn) throws PtoolsErrorException {
		return (ArrayList<String>) conn.getValueAnnots(frameID, slotLabel, slotValue, annotationLabel);
	}
}
