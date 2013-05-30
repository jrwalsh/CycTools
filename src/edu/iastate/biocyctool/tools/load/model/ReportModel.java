package edu.iastate.biocyctool.tools.load.model;

import edu.iastate.biocyctool.tools.load.controller.DefaultController;
import edu.iastate.biocyctool.util.model.AbstractModel;


public class ReportModel extends AbstractModel {
	static private int linesTotal;
	static private int linesProcessed;
	static private int linesFailedToProcess;
	static private int frameUpdatesTotal;
	static private int frameUpdatesDuplicates;
	static private int frameUpdatesConflicts;
    
    public ReportModel() {
    	initDefault();
    }
    
    public void initDefault() {
    	setLinesTotal(0);
    	setLinesProcessed(0);
    	setLinesFailedToProcess(0);
    	setFrameUpdatesTotal(0);
    	setFrameUpdatesDuplicates(0);
    	setFrameUpdatesConflicts(0);
    }

    // Accessors
	public int getLinesTotal() {
		return linesTotal;
	}

	public void setLinesTotal(int linesTotal) {
		int oldLinesTotal = ReportModel.linesTotal;
		ReportModel.linesTotal = linesTotal;
        firePropertyChange(DefaultController.DOCUMENT_TABLEMODEL_PROPERTY, oldLinesTotal, linesTotal);
	}
	
	static public void incrementLinesTotal() {
		ReportModel.linesTotal =+ 1;
	}

	public int getLinesProcessed() {
		return linesProcessed;
	}

	public void setLinesProcessed(int linesProcessed) {
		int oldLinesProcessed = ReportModel.linesProcessed;
		ReportModel.linesProcessed = linesProcessed;
        firePropertyChange(DefaultController.DOCUMENT_TABLEMODEL_PROPERTY, oldLinesProcessed, linesProcessed);
	}
	
	static public void incrementLinesProcessed() {
		ReportModel.linesProcessed =+ 1;
	}

	public int getLinesFailedToProcess() {
		return linesFailedToProcess;
	}

	public void setLinesFailedToProcess(int linesFailedToProcess) {
		int oldLinesFailedToProcess = ReportModel.linesFailedToProcess;
		ReportModel.linesFailedToProcess = linesFailedToProcess;
        firePropertyChange(DefaultController.DOCUMENT_TABLEMODEL_PROPERTY, oldLinesFailedToProcess, linesFailedToProcess);
	}
	
	static public void incrementLinesFailedToProcess() {
		ReportModel.linesFailedToProcess =+ 1;
	}

	public int getFrameUpdates() {
		return frameUpdatesTotal;
	}

	public void setFrameUpdatesTotal(int frameUpdates) {
		int oldFrameUpdatesTotal = ReportModel.frameUpdatesTotal;
		ReportModel.frameUpdatesTotal = frameUpdates;
        firePropertyChange(DefaultController.DOCUMENT_TABLEMODEL_PROPERTY, oldFrameUpdatesTotal, frameUpdatesTotal);
	}
	
	static public void incrementFrameUpdates() {
		ReportModel.frameUpdatesTotal =+ 1;
	}

	public int getFrameUpdatesDuplicates() {
		return frameUpdatesDuplicates;
	}

	public void setFrameUpdatesDuplicates(int frameUpdatesDuplicates) {
		int oldFrameUpdatesDuplicates = ReportModel.frameUpdatesDuplicates;
		ReportModel.frameUpdatesDuplicates = frameUpdatesDuplicates;
        firePropertyChange(DefaultController.DOCUMENT_TABLEMODEL_PROPERTY, oldFrameUpdatesDuplicates, frameUpdatesDuplicates);
	}
	
	static public void incrementFrameUpdatesDuplicates() {
		ReportModel.frameUpdatesDuplicates =+ 1;
	}

	public int getFrameUpdatesConflicts() {
		return frameUpdatesConflicts;
	}

	public void setFrameUpdatesConflicts(int frameUpdatesConflicts) {
		int oldFrameUpdatesConflicts = ReportModel.frameUpdatesConflicts;
		ReportModel.frameUpdatesConflicts = frameUpdatesConflicts;
        firePropertyChange(DefaultController.DOCUMENT_TABLEMODEL_PROPERTY, oldFrameUpdatesConflicts, frameUpdatesConflicts);
	}
	
	static public void incrementFrameUpdatesConflicts() {
		ReportModel.frameUpdatesConflicts =+ 1;
	}
}
    
