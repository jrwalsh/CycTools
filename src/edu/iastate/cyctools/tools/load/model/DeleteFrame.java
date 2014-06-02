package edu.iastate.cyctools.tools.load.model;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.Gene;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.Protein;
import edu.iastate.javacyco.PtoolsErrorException;

public class DeleteFrame extends AbstractFrameEdit {

	public DeleteFrame(String frameID, int[] associatedRows) {
		this.frameID = frameID;
		this.associatedRows = associatedRows;
	}
	
	@Override
	public boolean commit(JavacycConnection conn) throws PtoolsErrorException {
		Frame frame = null;
		try {
			if(!conn.frameExists(frameID)) {
				return false;
			}
			
			frame = this.getFrame(conn);
			
			if (frame.isGFPClass(Gene.GFPtype)) {
				Gene gene = (Gene) frame;
				for (Protein enzyme : gene.getEnzymes()) {
					for (Object catalyzedReaction : enzyme.getSlotValues("CATALYZES")) {
						conn.deleteFrameAndDependents(catalyzedReaction.toString());  // First delete any enzymeReactions and dependents
					}
				}
				for (Protein product: gene.getProducts()) {
					if (conn.frameExists(product.getLocalID())) conn.deleteFrameAndDependents(product.getLocalID());  // Second delete any products and dependents
					//else System.out.println("Frame already deleted: " + product.getLocalID());
				}
			}
			
			conn.deleteFrameAndDependents(frame.getLocalID()); // Finally, delete the frame and dependents
		} catch (PtoolsErrorException e) {
			System.out.println("error during commit, recommend a rollback");
			return false;
		}
		
		return true;
	}

	@Override
	public Frame commitLocal(Frame frame) throws PtoolsErrorException {
		frame = null;
		return frame;
	}
	
	@Override
	public boolean modifiesFrame(JavacycConnection conn, Frame aFrame) throws PtoolsErrorException {
		if(!conn.frameExists(frameID)) return false;
		else return true;
	}

	@Override
	public String toString() {
		return "deleting frame " + frameID + " and all dependent frames";
	}
}
