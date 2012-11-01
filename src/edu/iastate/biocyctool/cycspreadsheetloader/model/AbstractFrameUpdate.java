package edu.iastate.biocyctool.cycspreadsheetloader.model;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public abstract class AbstractFrameUpdate {
	protected String frameID;
	protected boolean append;
	protected boolean ignoreDuplicates;
	
	public Frame getFrame(JavacycConnection conn) throws PtoolsErrorException {
		return Frame.load(conn, frameID);
	}
	
	public String getValuesAsLispArray() {
		ArrayList<String> values = this.getValues();
		if (values == null || values.size() == 0) return "";
		else if (values.size() == 1) return values.get(0);
		else return JavacycConnection.ArrayList2LispList(values);
	}
	
	protected boolean checkRemoteValueEmpty(JavacycConnection conn) throws PtoolsErrorException {
		ArrayList<String> remoteValues = this.getRemoteValues(conn);
		if (remoteValues == null || remoteValues.isEmpty()) {
			return true;
		} else 
			return false;
	}
	
	protected boolean checkRemoteValueDuplicate(JavacycConnection conn) throws PtoolsErrorException {
		ArrayList<String> remoteValues = this.getRemoteValues(conn);
		if (remoteValues.containsAll(this.getValues())) {
			return true;
		} else 
			return false;
	}
	
	public abstract void commit(JavacycConnection conn) throws PtoolsErrorException;
	protected abstract ArrayList<String> getValues();
	protected abstract ArrayList<String> getRemoteValues(JavacycConnection conn) throws PtoolsErrorException;
}
