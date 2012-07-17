package edu.iastate.cycspreadsheetloader.model;

import java.util.ArrayList;

import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public class SlotUpdate extends AbstractFrameUpdate {
	private String slotLabel;
	private ArrayList<String> slotValues;
	
	public SlotUpdate(String frameID, String slotLabel, ArrayList<String> slotValues) {
		this.frameID = frameID;
		this.slotLabel = slotLabel;
		this.slotValues = slotValues;
	}
	
	@Override
	protected void commit(JavacycConnection conn) {
		// TODO Auto-generated method stub
	}

	@Override
	protected boolean isRemoteValueEmpty(JavacycConnection conn) {
		ArrayList<String> remoteSlotValues;
		try {
			remoteSlotValues = (ArrayList<String>) conn.getSlotValues(frameID, slotLabel);
			if (remoteSlotValues == null || remoteSlotValues.isEmpty()) {
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
		ArrayList<String> remoteSlotValues;
		try {
			remoteSlotValues = (ArrayList<String>) conn.getSlotValues(frameID, slotLabel);
			if (remoteSlotValues.containsAll(slotValues) && slotValues.containsAll(remoteSlotValues)) {
				return true;
			} else 
				return false;
		} catch (PtoolsErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public String getSlotLabel() {
		return slotLabel;
	}
	
	@Override
	public ArrayList<String> getValues() {
		return slotValues;
	}
}