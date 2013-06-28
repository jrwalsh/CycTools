package edu.iastate.cyctools.tools.load.model;

import java.util.ArrayList;

import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

// This class is intended to represent any changes to an existing frame, but more specifically not used to create or remove frames.
public abstract class AbstractFrameDataEdit extends AbstractFrameEdit {
	protected boolean append;
	protected boolean ignoreDuplicates;
	
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

	protected abstract ArrayList<String> getValues();
	protected abstract ArrayList<String> getRemoteValues(JavacycConnection conn) throws PtoolsErrorException;
}
