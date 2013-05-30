package edu.iastate.biocyctool.tools.load.model;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public class SlotUpdate extends AbstractFrameDataEdit {
	private String slotLabel;
	private ArrayList<String> slotValues;
	
	public SlotUpdate(String frameID, String slotLabel, String slotValue, boolean append, boolean ignoreDuplicates) {
		ArrayList<String> slotValues = new ArrayList<String>();
		slotValues.add(slotValue);
		this.frameID = frameID;
		this.slotLabel = slotLabel;
		this.slotValues = slotValues;
		this.append = append;
		this.ignoreDuplicates = ignoreDuplicates;
	}
	
	public SlotUpdate(String frameID, String slotLabel, ArrayList<String> slotValues, boolean append, boolean ignoreDuplicates) {
		this.frameID = frameID;
		this.slotLabel = slotLabel;
		this.slotValues = slotValues;
		this.append = append;
		this.ignoreDuplicates = ignoreDuplicates;
	}
	
	public String getSlotLabel() {
		return slotLabel;
	}
	
	// Catches all "special" cases for slot updates, such as GO-TERMS
	@Override
	public void commit(JavacycConnection conn) throws PtoolsErrorException {
		Frame frame = this.getFrame(conn);
		
		// GO-TERMS are a special case, as pathway tools can automatically import information for them if told to do so.
		if (slotLabel.equalsIgnoreCase("GO-TERMS")) {
			conn.importGOTerms(getValues());
		}
		
		ArrayList<String> newValues = new ArrayList<String>();
		if (append) {
			newValues.addAll(frame.getSlotValues(slotLabel));
		}
		
		if (ignoreDuplicates) {
			for (String value : slotValues) {
				if (!newValues.contains(value)) newValues.add(value);
			}
		} else newValues.addAll(slotValues);
		
		frame.putSlotValues(slotLabel, newValues);
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

	@Override
	public void revert(JavacycConnection conn) throws PtoolsErrorException {
		// TODO Auto-generated method stub
		
	}
}