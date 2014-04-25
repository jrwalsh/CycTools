package edu.iastate.cyctools.tools.load.model;

import java.util.ArrayList;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public class AuthorCreditUpdate extends AbstractFrameEdit {
	private String author;
	private String organization;
	private String lispTimeStamp;
	
	public AuthorCreditUpdate(String frameID, String author, String organization, String lispTimeStamp, int[] associatedRows) {
		this.frameID = frameID;
		this.author = author;
		this.organization = organization;
		this.lispTimeStamp = lispTimeStamp;
		this.associatedRows = associatedRows;
	}

	@Override
	public boolean modifiesFrame(JavacycConnection conn, Frame aFrame) throws PtoolsErrorException {
		Frame frameToModify = aFrame.copy(aFrame.getLocalID());
		commitLocal(frameToModify);
		boolean isModified = !aFrame.equalBySlotValues(frameToModify); // If the frames are not equal, a change has been made to the database.
		return isModified;
	}

	@Override
	public boolean commit(JavacycConnection conn) throws PtoolsErrorException {
		if(!conn.frameExists(frameID)) {
			return false;
		}
		
		conn.addCredit(frameID, author, organization);
		return true;
	}

	@Override
	public Frame commitLocal(Frame frame) throws PtoolsErrorException {
		// These are a special case of frame edits, since the logic for doing the import is largely in a built in lisp function already.  Since the import
		// is not innately obvious slot/annotation updates, we will translate them to slot/annottion updates here.  Note that future updates to the API could
		// break this commitLocal function even though the commit function still works.  This is a consequence of imagining all databases changes as either low level
		// slot or low level annotation updates, while this AuthorCreditUpdate is both a slot and annotation update at once, yet neither since it has its own high level
		// lisp function.
		// Also, this is currently specific to updating a "Revised" annotation, not a "Created" or "Reviewed" author credit.
		
		// Slot Component
		ArrayList<String> newSlotValues = new ArrayList<String>();
		String slotLabel = "CREDITS";
		
		newSlotValues.addAll(frame.getSlotValues(slotLabel));
		if (!newSlotValues.contains(author) && author.length() > 0) newSlotValues.add(author);
		if (!newSlotValues.contains(organization) && organization.length() > 0) newSlotValues.add(organization);
		frame.putSlotValues(slotLabel, newSlotValues);
		
		// Annotation Component
		ArrayList<String> newAnnotationValuesAuthor = new ArrayList<String>();
		ArrayList<String> newAnnotationValuesOrganization = new ArrayList<String>();
		String annotationLabel = "REVISED";
		
		newAnnotationValuesAuthor.addAll(frame.getAnnotations(slotLabel, author, annotationLabel));
		if (author.length() > 0) newAnnotationValuesAuthor.add(lispTimeStamp);
		newAnnotationValuesOrganization.addAll(frame.getAnnotations(slotLabel, organization, annotationLabel));
		if (organization.length() > 0) newAnnotationValuesOrganization.add(lispTimeStamp);
		
		frame.putLocalSlotValueAnnotations(slotLabel, author, annotationLabel, newAnnotationValuesAuthor);
		frame.putLocalSlotValueAnnotations(slotLabel, organization, annotationLabel, newAnnotationValuesOrganization);
		
		return frame;
	}

	@Override
	public String toString() {
		if (author.length() > 0 && organization.length() > 0) return "add author: " + author + " and organization: " + organization + " to credits on frame: " + frameID;
		else if (author.length() > 0) return "add author: " + author + " to credits on frame: " + frameID;
		else if (organization.length() > 0) return "add organization: " + organization + " to credits on frame: " + frameID;
		else return "attempted to add author or organization credits to frame: " + frameID + ", but no author or organization provided... skipped";
	}
}