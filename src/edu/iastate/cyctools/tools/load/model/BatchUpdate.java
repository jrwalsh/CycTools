package edu.iastate.cyctools.tools.load.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.ProgressMonitor;
import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.externalSourceCode.AbstractModel;
import edu.iastate.cyctools.tools.load.threadedTasks.DownloadFramesTask;
import edu.iastate.cyctools.view.dialog.TranslucentGlassPane;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

// Contains a group of updates (AbstractFrameEdit).  Knows how to compare them to existing KB, commit them to KB, and report on the results of the commit.
// Save/revert of the KB must still be done separately, as save/revert of KB is not handled by this class.
public class BatchUpdate extends AbstractModel {
	private ProgressMonitor progressMonitor;
	private DownloadFramesTask task;
	private ArrayList<Frame> framesFromKB;
	
	private ArrayList<AbstractFrameEdit> frameEdits;
	private HashMap<String, ArrayList<AbstractFrameEdit>> frameEditsMap;  // Provides a way to index frameEdits which relate to a particular frame;
	private TreeSet<String> frameIDSet;
	private TreeSet<Integer> lines;
	private TreeSet<Integer> linesProcessed;
	private ArrayList<Event> eventLog;
    
    public BatchUpdate(ArrayList<AbstractFrameEdit> frameEditList) {
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
    
    public DefaultListModel<String> getFrameIDsModel() {
    	DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (String frameID : getFrameIDSet()) {
			listModel.addElement(frameID);
		}
		return listModel;
    }
    
//    public DefaultListModel<String> checkForNewData(JavacycConnection conn) {
//    	// We want to know which frame updates will actually result in new data in the database.
//    	// If data is not new (i.e. already in database), then this update is not adding new data
//    	// for that frame.
//    	
//    	DefaultListModel<String> listModel = new DefaultListModel<String>();
//    	TreeSet<String> frameIDSet = new TreeSet<String>();
//    	for (AbstractFrameEdit frameEdit : frameEdits) {
//    		try {
//    			boolean modifiesKB = frameEdit.modifiesKB(conn);
//				if (modifiesKB) {
//					frameIDSet.add(frameEdit.frameID);
//				}
//			} catch (PtoolsErrorException e) {
//				e.printStackTrace();
//			}
//    	}
//    	
//    	for (String id : frameIDSet) listModel.addElement(id);
//    	
//		return listModel;
//    }
    
//    private void updateComparison(String frameID) {
//		if (frameID.equalsIgnoreCase("")) return;
//		
//		String originalFrameString = controller.frameToString(frameID);
//		
//		if (originalFrameString == null || originalFrameString.equalsIgnoreCase("")) {
//			String message = "Frame " + frameID + " does not exist in database " + controller.getSelectedOrganism();
//			textAreaOld.setText(message);
//			textAreaNew.setText(message);
//			return;
//		}
//		
//		textAreaOld.setText(originalFrameString);
//		
//		// select the frame edits which relate to this frame
//		ArrayList<AbstractFrameEdit> frameEditArray = batchEdits.getFrameEditsMap().get(frameID);
//		
//		// apply frame edits to local copy of frame
//		Frame resultFrame = controller.updateLocalFrame(frameID, frameEditArray);
//		
//		// return print of the updated frame
//		String updatedFrameString = controller.frameToString(resultFrame);
//		textAreaNew.setText(updatedFrameString);
//		int currentPosition = 0;
//		for (String line : updatedFrameString.split("\n")) {
//			if (!originalFrameString.contains(line)) {
//				DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
//				try {
//					textAreaNew.getHighlighter().addHighlight(currentPosition, currentPosition + line.length(), highlightPainter);
//				} catch (BadLocationException e) {
//					e.printStackTrace();
//				}
//			}
//			currentPosition += line.length()+1;
//		}
//	}
    
    public void downloadFrames(JavacycConnection conn) {
    	progressMonitor = new ProgressMonitor(DefaultController.mainJFrame, "Analyzing Imports...", "", 0, 100);
    	task = new DownloadFramesTask(conn, progressMonitor, frameIDSet);
		
		task.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if ("progress".equals(e.getPropertyName())) {
                	progressMonitor.setProgress((Integer) e.getNewValue());
                	
                	if (progressMonitor.isCanceled()) {
                		task.cancel(true);
                	}
                	
                	if (task.isDone() && !task.isCancelled()) {
                		try {
							framesFromKB = task.get();
							progressMonitor.setProgress(0);
							progressMonitor.close();
							DefaultController.mainJFrame.getGlassPane().setVisible(false);
						} catch (InterruptedException e1) {
							framesFromKB = new ArrayList<Frame>();
						} catch (ExecutionException e1) {
							e1.printStackTrace();
						}
                	}
                	
                	if (task.isCancelled()) {
                		framesFromKB = new ArrayList<Frame>();
                		progressMonitor.setProgress(0);
						progressMonitor.close();
						DefaultController.mainJFrame.getGlassPane().setVisible(false);
                	}
                }
            }
        });
		DefaultController.mainJFrame.setGlassPane(new TranslucentGlassPane());
    	DefaultController.mainJFrame.getGlassPane().setVisible(true);
		task.execute();
    }
    
    public DefaultListModel<String> framesWhichModifyKB(JavacycConnection conn) {
    	DefaultListModel<String> listModel = new DefaultListModel<String>();
    	TreeSet<String> frameSet = new TreeSet<String>();
    	for (AbstractFrameEdit frameEdit : frameEdits) {
    		Frame frame = getFrameByID(frameEdit.getFrameID());
    		if (frame == null) frameSet.add(frameEdit.getFrameID());
    		else {
	    		boolean modifiesKB = true;
				try {
					modifiesKB = frameEdit.modifiesFrame(conn, frame);
				} catch (PtoolsErrorException e) {
					e.printStackTrace();
				}
	    		if (modifiesKB) {
	    			frameSet.add(frameEdit.getFrameID());
	    		}
    		}
    	}
    	
    	for (String frame : frameSet) listModel.addElement(frame);
    	return listModel;
    }
    
    public Frame getFrameByID(String frameID) {
    	for (Frame frame : framesFromKB) {
    		if (frame.getLocalID().equalsIgnoreCase(frameID)) return frame;
    	}
    	return null;
    }
    
    public Frame updateLocalFrame(Frame originalFrame) {
    	if (originalFrame == null) return null;
    	Frame frameToModify = originalFrame.copy(originalFrame.getLocalID());
    	for (AbstractFrameEdit frameEdit : frameEdits) {
        	if (frameEdit.getFrameID().equals(originalFrame.getLocalID())) {
        		try {
					frameEdit.commitLocal(frameToModify);
				} catch (PtoolsErrorException e) {
					e.printStackTrace();
					return null;
				}
        	}
    	}
    	return frameToModify;
	}
    
    public void commitToKB(JavacycConnection conn) {
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
    	SUCCESS, FAIL, NOCHANGE;
    }

}
    
