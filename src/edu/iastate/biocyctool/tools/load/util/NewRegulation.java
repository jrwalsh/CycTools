package edu.iastate.biocyctool.tools.load.util;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import edu.iastate.biocyctool.tools.load.model.AbstractFrameEdit;
import edu.iastate.biocyctool.tools.load.model.SlotUpdate;

public class NewRegulation implements Interpretable {

	public NewRegulation() {
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
			frameUpdates.add(new SlotUpdate(regulationFrameID, "REGULATOR", regulatorFrameID, true, true));
			frameUpdates.add(new SlotUpdate(regulationFrameID, "REGULATEE", regulateeFrameID, true, true));
			frameUpdates.add(new SlotUpdate(regulationFrameID, "MODE", modeValue, true, true));
		}
		return frameUpdates;
	}
}