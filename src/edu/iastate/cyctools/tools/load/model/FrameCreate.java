package edu.iastate.cyctools.tools.load.model;

import java.util.ArrayList;
import java.util.HashMap;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public class FrameCreate extends AbstractFrameEdit {
	private String frameType;
	private ArrayList<AbstractFrameDataEdit> dataEdits;
	
	public FrameCreate(String frameID, String frameType, ArrayList<AbstractFrameDataEdit> dataEdits) {
		this.frameID = frameID;
		this.frameType = frameType;
		this.dataEdits = dataEdits;
	}
	
	@Override
	public boolean commit(JavacycConnection conn) throws PtoolsErrorException {
		if (frameExistsInKB(conn)) {
			
		}
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Frame commitLocal(Frame frame) throws PtoolsErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean modifiesFrame(JavacycConnection conn, Frame aFrame)
			throws PtoolsErrorException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "creating frame: " + frameID;
	}

}