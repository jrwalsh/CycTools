package edu.iastate.cyctools.tools.load.model;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public class AnnotationUpdate extends AbstractFrameDataEdit {
	private String slotLabel;
	private String slotValue;
	private String annotationLabel;
	private ArrayList<String> annotationValues;
	
	public AnnotationUpdate(String frameID, String slotLabel, String slotValue, String annotationLabel, ArrayList<String> annotationValues,  boolean append, boolean ignoreDuplicates) {
		this.frameID = frameID;
		this.slotLabel = slotLabel;
		this.slotValue = slotValue;
		this.annotationLabel = annotationLabel;
		this.annotationValues = annotationValues;
		this.append = append;
		this.ignoreDuplicates = ignoreDuplicates;
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
		frame.commit();
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
	public void revert(JavacycConnection conn) throws PtoolsErrorException {
		// TODO Auto-generated method stub
		
	}
}
