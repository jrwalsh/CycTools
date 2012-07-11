package edu.iastate.cycspreadsheetloader.model;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public abstract class AbstractFrameUpdate {
	protected String frameID;
	protected boolean isConflict;
	protected boolean isDuplicate;
	protected boolean allowOverWrite;
	
	protected Frame getFrame(JavacycConnection conn) throws PtoolsErrorException {
		return Frame.load(conn, frameID);
	}
	
	public boolean isConflict() {
		return isConflict;
	}
	
	public boolean isDuplicate() {
		return isDuplicate;
	}
	
	public boolean allowOverWrite() {
		return allowOverWrite;
	}
	
	protected void compare(JavacycConnection conn) {
		isConflict = isRemoteValueEmpty(conn);
		isDuplicate = isRemoteValueDuplicate(conn);
	}
	
	protected abstract void commit(JavacycConnection conn);
	protected abstract boolean isRemoteValueEmpty(JavacycConnection conn);
	protected abstract boolean isRemoteValueDuplicate(JavacycConnection conn);
}
