package edu.iastate.cyctools.tools.load.model;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public class NewRegulation extends AbstractFrameEdit {
	private String regulator;
	private String regulatee;
	private String mode;

	public NewRegulation(String frameID, String regulator, String regulatee, String mode, int[] associatedRows) {
		this.frameID = frameID;
		this.regulator = regulator;
		this.regulatee = regulatee;
		this.mode = mode;
		this.associatedRows = associatedRows;
	}

	@Override
	public boolean modifiesFrame(JavacycConnection conn, Frame aFrame) throws PtoolsErrorException {
		//TODO check if this regulation event already exists?  I can guarantee that creating a new frame will always be a "change" so I return true
		return true;
	}

	@Override
	public boolean commit(JavacycConnection conn) throws PtoolsErrorException {
		frameID = conn.createInstanceWGeneratedId("|Regulation-of-Transcription|");
		Frame regulationFrame = Frame.load(conn, frameID);
		
		if(!conn.frameExists(regulator)) {
			return false;
		}
		if(!conn.frameExists(regulatee)) {
			return false;
		}
		
		if (mode == null) mode = "";
		if(!mode.equalsIgnoreCase("-") && !mode.equalsIgnoreCase("+") && !mode.equalsIgnoreCase("")) {
			return false;
		}
		
		regulationFrame.putSlotValue("regulator", regulator);
		regulationFrame.putSlotValue("regulated-entity", regulatee);
		regulationFrame.putSlotValue("mode", "\"+\"");
		
		regulationFrame.commit();

		return true;
	}

	@Override
	public Frame commitLocal(Frame frame) throws PtoolsErrorException {
		frame.putSlotValue("REGULATOR", regulator);
		frame.putSlotValue("REGULATED-ENTITY", regulatee);
		frame.putSlotValue("MODE", mode);
		return frame;
	}

	@Override
	public String toString() {
		return "creating regulation frame " + frameID + " with regulator: " + regulator + ", regulatee: " + regulatee + ", and regulation mode: " + mode;
	}
}
