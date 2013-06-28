package edu.iastate.cyctools.tools.load.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.DefaultListModel;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.externalSourceCode.AbstractModel;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;


public class BatchEditModel extends AbstractModel {
	private ArrayList<AbstractFrameEdit> frameEdits;
	private ArrayList<AbstractFrameEdit> successfulEdits;
	private ArrayList<AbstractFrameEdit> failedEdits;
	private HashMap<String, ArrayList<AbstractFrameEdit>> frameEditsMap;
	private TreeSet<String> frameIDSet;
	private TreeSet<Integer> lines;
	private TreeSet<Integer> linesProcessed;
	private int frameEditsProcessed;
	private int failed;
	private int succeeded;
	private String reportLog;
    
    public BatchEditModel(ArrayList<AbstractFrameEdit> frameEditList) {
    	initDefault();
    	this.frameEdits = frameEditList;
    	
    	frameEditsMap = new HashMap<String, ArrayList<AbstractFrameEdit>>();
		for (AbstractFrameEdit frameEdit : frameEdits) {
			for (int row : frameEdit.getAssociatedRows()) {
				lines.add(new Integer(row));
			}
			
			if (frameEditsMap.containsKey(frameEdit.getFrameID())) {
				ArrayList<AbstractFrameEdit> frameEditArray = frameEditsMap.get(frameEdit.getFrameID());
				frameEditArray.add(frameEdit);
				frameEditsMap.put(frameEdit.getFrameID(), frameEditArray);
			} else {
				ArrayList<AbstractFrameEdit> frameEditArray = new ArrayList<AbstractFrameEdit>();
				frameEditArray.add(frameEdit);
				frameEditsMap.put(frameEdit.getFrameID(), frameEditArray);
			}
		}
		
		frameIDSet = new TreeSet<String>();
		for (AbstractFrameEdit frameEdit : frameEdits) {
			frameIDSet.add(frameEdit.getFrameID());
		}
    }
    
    public void initDefault() {
    	successfulEdits = new ArrayList<AbstractFrameEdit>();
    	failedEdits = new ArrayList<AbstractFrameEdit>();
    	linesProcessed = new TreeSet<Integer>();
    	lines = new TreeSet<Integer>();
    	frameEditsProcessed = 0;
    	failed = 0;
    	succeeded = 0;
    	reportLog = "";
    }

    // Accessors
    public ArrayList<AbstractFrameEdit> getFrameEdits() {
    	return frameEdits;
    }
    
    public HashMap<String, ArrayList<AbstractFrameEdit>> getFrameEditsMap() {
    	return frameEditsMap;
    }
    
    public TreeSet<String> getFrameIDSet() {
    	return frameIDSet;
    }
    
    public int getLines() {
    	return lines.size();
    }
    
    public String getLog() {
    	return reportLog;
    }
    
    public void commitAll(JavacycConnection conn) {
    	reportLog += "Converted " + lines.size() + " lines of updates into " + frameEdits.size() + " individual updates.\n";
		reportLog += "Processing individual updates...\n";
		
    	for (AbstractFrameEdit frameEdit : frameEdits) {
    		int oldFrameEditsProcessed = frameEditsProcessed;
    		int oldSucceeded = succeeded;
        	int oldFailed = failed;
        	int oldLinesProcessed = linesProcessed.size();
    		boolean result = false;
    		
    		try {
    			frameEditsProcessed++;
    			for (int row : frameEdit.getAssociatedRows()) {
    				linesProcessed.add(new Integer(row));
    			}
				result = frameEdit.commit(conn);
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
			}
    		
    		if (result) {
    			succeeded++;
    			successfulEdits.add(frameEdit);
    			reportLog += "Successfully committed update to frame " + frameEdit.getFrameID() + ". Data from line(s) " + frameEdit.getAssociatedRowsString() + "\n";
    		} else {
    			failed++;
    			failedEdits.add(frameEdit);
    			reportLog += "Failed commit to frame " + frameEdit.getFrameID() + ". Data from line(s) " + frameEdit.getAssociatedRowsString() + "\n";
    		}
    		
        	firePropertyChange(DefaultController.REPORT_PROPERTY_FRAME_EDITS_PROCESSED, oldFrameEditsProcessed, frameEditsProcessed);
        	firePropertyChange(DefaultController.REPORT_PROPERTY_FRAME_EDITS_SUCCESS, oldSucceeded, succeeded);
        	firePropertyChange(DefaultController.REPORT_PROPERTY_FRAME_EDITS_FAIL, oldFailed, failed);
		}
    }
}
    
