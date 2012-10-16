package edu.iastate.biocyctool.cycspreadsheetloader.model;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public class SlotUpdate extends AbstractFrameUpdate {
	private String slotLabel;
	private ArrayList<String> slotValues;
	
	public SlotUpdate(String frameID, String slotLabel, String slotValue) {
		ArrayList<String> slotValues = new ArrayList<String>();
		slotValues.add(slotValue);
		this.frameID = frameID;
		this.slotLabel = slotLabel;
		this.slotValues = slotValues;
	}
	
	public SlotUpdate(String frameID, String slotLabel, ArrayList<String> slotValues) {
		this.frameID = frameID;
		this.slotLabel = slotLabel;
		this.slotValues = slotValues;
	}
	
	public String getSlotLabel() {
		return slotLabel;
	}
	
	// Catches all "special" cases for slot updates, such as GO-TERMS
	@Override
	public void commit(JavacycConnection conn) throws PtoolsErrorException {
		Frame frame = this.getFrame(conn);
		
		if (slotLabel.equalsIgnoreCase("GO-TERMS")) {
			conn.importGOTerms(getValues());//("import-go-terms '" + this.getValuesAsLispArray());
		}
		
		//TODO only if set to append
		ArrayList<String> values = new ArrayList<String>();
		values.addAll(frame.getSlotValues(slotLabel));
		values.addAll(slotValues);
		
		frame.putSlotValues(slotLabel, values);
		frame.commit();
	}

//	@Override
//	protected boolean checkRemoteValueEmpty(JavacycConnection conn) throws PtoolsErrorException {
//		ArrayList<String> remoteSlotValues = (ArrayList<String>) conn.getSlotValues(frameID, slotLabel);
//		if (remoteSlotValues == null || remoteSlotValues.isEmpty()) {
//			return true;
//		} else 
//			return false;
//	}
//
//	@Override
//	protected boolean checkRemoteValueDuplicate(JavacycConnection conn) throws PtoolsErrorException {
//		ArrayList<String> remoteSlotValues = (ArrayList<String>) conn.getSlotValues(frameID, slotLabel);
//		if (remoteSlotValues.containsAll(slotValues) && slotValues.containsAll(remoteSlotValues)) {
//			return true;
//		} else 
//			return false;
//	}
	
	@Override
	public ArrayList<String> getValues() {
		return slotValues;
	}

	
	@Override
	protected ArrayList<String> getRemoteValues(JavacycConnection conn) throws PtoolsErrorException {
		return (ArrayList<String>) conn.getSlotValues(frameID, slotLabel);
	}
}