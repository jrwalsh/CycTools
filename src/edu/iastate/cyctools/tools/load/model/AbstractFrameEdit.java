package edu.iastate.cyctools.tools.load.model;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

// Any change that can be committed to the database must be coded as an AbstractFrameEdit or a child class.
public abstract class AbstractFrameEdit {
	protected String frameID;
	protected int[] associatedRows;
	
	public String getFrameID() {
		return frameID;
	}
	
	public Frame getFrame(JavacycConnection conn) throws PtoolsErrorException {
		return Frame.load(conn, frameID);
	}
	
	public int[] getAssociatedRows() {
		return associatedRows;
	}
	
	public String getAssociatedRowsString() {
		String associatedRowsString = "";
		for (int i = 0; i < associatedRows.length; i++) {
			associatedRowsString += associatedRows[i] + ", ";
		}
		
		if (associatedRowsString.length() > 2) associatedRowsString = associatedRowsString.substring(0, associatedRowsString.length() - 2);
		return associatedRowsString;
	}
	
	protected boolean frameExistsInKB(JavacycConnection conn) throws PtoolsErrorException {
		return conn.frameExists(frameID);
	}
	
	public abstract boolean modifiesFrame(JavacycConnection conn, Frame aFrame) throws PtoolsErrorException;
	public abstract boolean commit(JavacycConnection conn) throws PtoolsErrorException;
	public abstract Frame commitLocal(Frame frame) throws PtoolsErrorException;
	public abstract String toString();
}
