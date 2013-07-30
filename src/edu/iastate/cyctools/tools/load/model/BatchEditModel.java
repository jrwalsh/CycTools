package edu.iastate.cyctools.tools.load.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import edu.iastate.cyctools.externalSourceCode.AbstractModel;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;


public class BatchEditModel extends AbstractModel {
	private ArrayList<AbstractFrameEdit> frameEdits;
	private HashMap<String, ArrayList<AbstractFrameEdit>> frameEditsMap;  // Provides a way to index frameEdits which relate to a particular frame;
	private TreeSet<String> frameIDSet;
	private TreeSet<Integer> lines;
	private TreeSet<Integer> linesProcessed;
	private ArrayList<Event> eventLog;
    
    public BatchEditModel(ArrayList<AbstractFrameEdit> frameEditList) {
    	initDefault();
    	this.frameEdits = frameEditList;
    	
    	frameEditsMap = new HashMap<String, ArrayList<AbstractFrameEdit>>();
    	frameIDSet = new TreeSet<String>();
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
			frameIDSet.add(frameEdit.getFrameID());
		}
    }
    
    public void initDefault() {
    	linesProcessed = new TreeSet<Integer>();
    	lines = new TreeSet<Integer>();
    	eventLog = new ArrayList<Event>();
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
    
    public ArrayList<Event> getEventLog() {
    	return eventLog;
    }
    
    public void commitAll(JavacycConnection conn) {
    	System.out.println("Converted " + lines.size() + " lines of updates into " + frameEdits.size() + " individual updates spanning across " + frameIDSet.size() + " frames.\n");
    	System.out.println("Processing individual updates...\n");
		
    	for (AbstractFrameEdit frameEdit : frameEdits) {
        	boolean result = false;
    		
    		try {
    			for (int row : frameEdit.getAssociatedRows()) {
    				linesProcessed.add(new Integer(row));
    			}
				result = frameEdit.commit(conn);
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
			}
    		
    		if (result) {
    			Event event = new Event(new Date(), Status.SUCCESS, "Successfully committed update to frame " + frameEdit.getFrameID() + ". Data from line(s) " + frameEdit.getAssociatedRowsString());
    			eventLog.add(event);
    		} else {
    			Event event = new Event(new Date(), Status.FAIL, "Failed commit to frame " + frameEdit.getFrameID() + ". Data from line(s) " + frameEdit.getAssociatedRowsString());
    			eventLog.add(event);
    		}
		}
    }
    
    public class Event {
    	private Date timestamp;
    	private Status status;
    	private String event;
    	
    	public Event(Date timestamp, Status status, String event) {
    		this.timestamp = timestamp;
    		this.status = status;
    		this.event = event;
    	}
    	
		public Date getTimeStamp() {
    		return timestamp;
    	}
    	
    	public Status getStatus() {
    		return status;
    	}
    	
    	public String getEvent() {
    		return event;
    	}
    	
    	@Override
    	public String toString() {
    		return timestamp.toString() + "\t" + status.toString() + "\t" + event; 
    	}
    }
    
    public enum Status {
    	SUCCESS, FAIL;
    }
}
    
