package edu.iastate.cyctools.tools.load.threadedTasks;

import java.util.ArrayList;
import java.util.TreeSet;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;


// Simple task which takes a list of frames IDs and calls frame.update on each one in order to fully load all data on that frame from the KB to the
// local frame object.  This is a large upfront time cost, but greatly speeds up future operations depending on that frame data.
public class DownloadFramesTask extends SwingWorker<ArrayList<Frame>, Void> {
	private ProgressMonitor progressMonitor;
	private JavacycConnection conn;
	private TreeSet<String> frameIDs;
	
	public DownloadFramesTask(JavacycConnection conn, ProgressMonitor progressMonitor, TreeSet<String> frameIDs) {
		this.conn = conn;
		this.progressMonitor = progressMonitor;
		this.frameIDs = frameIDs;
		
		progressMonitor.setMinimum(0);
		progressMonitor.setProgress(0);
	}
	
	@Override
	public ArrayList<Frame> doInBackground() {
		ArrayList<Frame> frames = new ArrayList<Frame>();
    	progressMonitor.setNote("Importing frame data...");
    	
    	int progress = 0;
    	int processCount = 0;
    	int processTotal = frameIDs.size();
    	for (String frameID : frameIDs) {
    		processCount++;
    		try {
    			Frame frame = Frame.load(conn, frameID);
    			frame.update();
    			frames.add(frame);
			} catch (PtoolsErrorException e) {
				// Most common error is going to be that frame is not in this database.  Ignore
			}
    		
    		progress = (int) ((processCount*100)/processTotal);
    		progressMonitor.setNote("Importing frame data... " + processCount + " of " + processTotal);
			setProgress(progress);
			
			if (isCancelled()) return new ArrayList<Frame>();
    	}
    	
		return frames;
	}
}