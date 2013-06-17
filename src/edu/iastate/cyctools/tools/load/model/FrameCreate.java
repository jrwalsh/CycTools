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
	public void commit(JavacycConnection conn) throws PtoolsErrorException {
		if (frameExistsInKB(conn)) {
			
		}
		// TODO Auto-generated method stub
	}

	@Override
	public void revert(JavacycConnection conn) throws PtoolsErrorException {
		// TODO Auto-generated method stub
	}

	@Override
	public Frame modifyLocalFrame(Frame frame, JavacycConnection conn)
			throws PtoolsErrorException {
		// TODO Auto-generated method stub
		return null;
	}
}