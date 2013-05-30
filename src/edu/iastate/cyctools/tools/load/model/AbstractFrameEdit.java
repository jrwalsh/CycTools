package edu.iastate.cyctools.tools.load.model;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public abstract class AbstractFrameEdit {
	protected String frameID;
	protected int[] associatedRows;
	
	public Frame getFrame(JavacycConnection conn) throws PtoolsErrorException {
		return Frame.load(conn, frameID);
	}
	
	protected boolean frameExistsInKB(JavacycConnection conn) throws PtoolsErrorException {
		return conn.frameExists(frameID);
	}
	
	public abstract void commit(JavacycConnection conn) throws PtoolsErrorException;
	public abstract void revert(JavacycConnection conn) throws PtoolsErrorException;
}
