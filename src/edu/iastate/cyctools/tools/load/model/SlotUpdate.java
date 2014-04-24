package edu.iastate.cyctools.tools.load.model;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

@SuppressWarnings("unchecked")
public class SlotUpdate extends AbstractFrameDataEdit {
	private String slotLabel;
	private ArrayList<String> slotValues;
	
	public SlotUpdate(String frameID, String slotLabel, String slotValue, boolean append, boolean ignoreDuplicates, int[] associatedRows) {
		ArrayList<String> slotValues = new ArrayList<String>();
		slotValues.add(slotValue);
		this.frameID = frameID;
		this.slotLabel = slotLabel;
		this.slotValues = slotValues;
		this.append = append;
		this.ignoreDuplicates = ignoreDuplicates;
		this.associatedRows = associatedRows;
	}
	
	public SlotUpdate(String frameID, String slotLabel, ArrayList<String> slotValues, boolean append, boolean ignoreDuplicates, int[] associatedRows) {
		this.frameID = frameID;
		this.slotLabel = slotLabel;
		this.slotValues = slotValues;
		this.append = append;
		this.ignoreDuplicates = ignoreDuplicates;
		this.associatedRows = associatedRows;
	}
	
	public String getSlotLabel() {
		return slotLabel;
	}
	
	@Override
	public boolean commit(JavacycConnection conn) {
		Frame frame = null;
		try {
			if(!conn.frameExists(frameID)) {
				return false;
			}
			
			frame = this.getFrame(conn);
		} catch (PtoolsErrorException e1) {
			System.out.println("not sure how you would reach this");
			return false;
		}
		
		// GO-TERMS are a special case, as pathway tools can automatically import information for them if told to do so.
		if (slotLabel.equalsIgnoreCase("GO-TERMS")) {
			try {
				conn.importGOTerms(getValues());
			} catch (PtoolsErrorException e) {
				System.out.println("here we have a problem importing a GO-term.  Perhaps this term doesn't exist?");
				return false;
			}
		}
		
		ArrayList<String> newValues = new ArrayList<String>();
		if (append) {
			try {
				newValues.addAll(frame.getSlotValues(slotLabel));
			} catch (PtoolsErrorException e) {
				System.out.println("not sure what to make of this.  The slot doesn't exist? bad data format?");
				return false;
			}
		}
		
		if (ignoreDuplicates) {
			for (String value : slotValues) {
				if (!newValues.contains(value)) newValues.add(value);
			}
		} else newValues.addAll(slotValues);
		
		frame.putSlotValues(slotLabel, newValues);
		
		try {
			frame.commit();
		} catch (PtoolsErrorException e) {
			System.out.println("error during commit, recommend a rollback");
			return false;
		}
		return true;
	}
	
	// Catches all "special" cases for slot updates, such as GO-TERMS
	@Override
	public Frame commitLocal(Frame frame) throws PtoolsErrorException {
//		// GO-TERMS are a special case, as pathway tools can automatically import information for them if told to do so.
//		if (slotLabel.equalsIgnoreCase("GO-TERMS")) {
//			conn.importGOTerms(getValues());
//		}
		
		ArrayList<String> newValues = new ArrayList<String>();
		if (append && frame.hasSlot(slotLabel)) {
			newValues.addAll(frame.getSlotValues(slotLabel));//TODO what if the slot value is actually an array?
		}
		
		if (ignoreDuplicates) {
			for (String value : slotValues) {
				if (!newValues.contains(value)) newValues.add(value);
			}
		} else newValues.addAll(slotValues);
		
		frame.putSlotValues(slotLabel, newValues);
		
		return frame;
	}
	
	@Override
	public ArrayList<String> getValues() {
		return slotValues;
	}

	@Override
	protected ArrayList<String> getRemoteValues(JavacycConnection conn) throws PtoolsErrorException {
		return (ArrayList<String>) conn.getSlotValues(frameID, slotLabel);
	}

	@Override
	public boolean modifiesFrame(JavacycConnection conn, Frame aFrame) throws PtoolsErrorException {
		Frame frameToModify = aFrame.copy(aFrame.getLocalID());
		commitLocal(frameToModify);
		boolean isModified = !aFrame.equalBySlotValues(frameToModify); // If the frames are not equal, a change has been made to the database.
		return isModified;
	}

	@Override
	public String toString() {
		return "updating slot: " + slotLabel + " on frame: " + frameID + " with the values: " + slotValues.toString();//TODO don't really expect this toString to work, right?
	}
	
//	@Override
//	public boolean modifiesKB(JavacycConnection conn) throws PtoolsErrorException {
//		Frame frame = Frame.load(conn, frameID);
//		frame.update();
//		
//		Frame frameToModify = frame.copy(frame.getLocalID());
//		commitLocal(frameToModify);
//		boolean isModified = !frame.equalBySlotValues(frameToModify); // If the frames are not equal, a change has been made to the database.
//		return isModified;
//	}
}