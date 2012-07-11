package edu.iastate.biocyctool.util.model;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public abstract class AbstractFrameModel extends AbstractModel {
	private Frame thisFrame;
	
	public void create(JavacycConnection conn, String type, String id) {
		try {
			thisFrame = Frame.create(conn, type, id);
		} catch (PtoolsErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void read(JavacycConnection conn, String id) {
		try {
			thisFrame = Frame.load(conn, id);
		} catch (PtoolsErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateSlotValues(JavacycConnection conn, String slotLabel, ArrayList<String> slotValues) {
		try {
			conn.putSlotValues(thisFrame.getLocalID(), slotLabel, JavacycConnection.ArrayList2LispList(slotValues));
		} catch (PtoolsErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateAnnotationValues(JavacycConnection conn, String slotLabel, String slotValue, String annotationLabel, ArrayList<String> annotationValues) {
		try {
			conn.putAnnotations(thisFrame.getLocalID(), slotLabel, slotValue, annotationLabel, annotationValues);
		} catch (PtoolsErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void delete() {
		try {
			thisFrame.deleteFromKB();
		} catch (PtoolsErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
