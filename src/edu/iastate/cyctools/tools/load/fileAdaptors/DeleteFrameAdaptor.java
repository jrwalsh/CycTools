package edu.iastate.cyctools.tools.load.fileAdaptors;

import java.util.ArrayList;
import javax.swing.table.TableModel;

import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.cyctools.tools.load.model.DeleteFrame;

// Format:
// Column 1: frameID or alternate identifier
public class DeleteFrameAdaptor extends AbstractFileAdaptor {

	@Override
	public ArrayList<AbstractFrameEdit> tableToFrameUpdates(TableModel tb) {
		ArrayList<AbstractFrameEdit> frameUpdates = new ArrayList<AbstractFrameEdit>();
		
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			String frameID = (String) tb.getValueAt(rowIndex, 0);
			frameUpdates.add(new DeleteFrame(frameID, new int[] {rowIndex + 1}));
		}
		return frameUpdates;
	}
}
