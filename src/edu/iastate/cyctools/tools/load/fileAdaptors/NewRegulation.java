package edu.iastate.cyctools.tools.load.fileAdaptors;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.cyctools.tools.load.model.SlotUpdate;

//TODO finish this class and test
public class NewRegulation implements FileAdaptor {
	private boolean append;
	private boolean ignoreDuplicates;
	private String multipleValueDelimiter;
	
	public NewRegulation() {
		append = true;
		ignoreDuplicates = true;
		multipleValueDelimiter = "$";//TODO convert multiple value entries into arrays before insert
	}
	
	// FrameID of regulationFrame
	// FrameID of regulator
	// FrameID of regulatee
	// Mode of regulation, + for up regulate, - for down regulate
	// Mechanism of regulation
	public ArrayList<AbstractFrameEdit> tableToFrameUpdates(TableModel tb) {
		ArrayList<AbstractFrameEdit> frameUpdates = new ArrayList<AbstractFrameEdit>();
//		String mechanismSlot = "";
//		String mechanismValue = ":OTHER";
		String modeSlot = "";
		String regulationFrameType = "|Regulation-of-Transcription|";
		String regulatorFrameType = "|Protein|";
		String regulateeFrameType = "|All-Genes|";
		String creator = "|jrwalsh|";
//		String creationDate = "";
//		String associatedBindingSiteFrameID = "";
		
		// REGULATOR vs REGULATES
		// REGULATED-ENTITY vs REGULATED-BY
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			String regulationFrameID = (String) tb.getValueAt(rowIndex, 0);
			String regulatorFrameID = (String) tb.getValueAt(rowIndex, 1);
			String regulateeFrameID = (String) tb.getValueAt(rowIndex, 2);
			String modeValue = (String) tb.getValueAt(rowIndex, 3);
			frameUpdates.add(new SlotUpdate(regulationFrameID, "REGULATOR", regulatorFrameID, append, ignoreDuplicates, new int[] {rowIndex}));
			frameUpdates.add(new SlotUpdate(regulationFrameID, "REGULATEE", regulateeFrameID, append, ignoreDuplicates, new int[] {rowIndex}));
			frameUpdates.add(new SlotUpdate(regulationFrameID, "MODE", modeValue, append, ignoreDuplicates, new int[] {rowIndex}));
		}
		return frameUpdates;
	}
	
	@Override
	public void setAppend(boolean append) {
		this.append = append;
	}

	@Override
	public void setIgnoreDuplicates(boolean ignoreDuplicates) {
		this.ignoreDuplicates = ignoreDuplicates;
	}

	@Override
	public void setMultipleValueDelimiter(String multipleValueDelimiter) {
		this.multipleValueDelimiter = multipleValueDelimiter;
	}
}