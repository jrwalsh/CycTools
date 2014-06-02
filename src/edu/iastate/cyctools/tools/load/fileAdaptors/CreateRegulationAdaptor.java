package edu.iastate.cyctools.tools.load.fileAdaptors;

import java.util.ArrayList;
import javax.swing.table.TableModel;

import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.cyctools.tools.load.model.NewRegulation;

public class CreateRegulationAdaptor extends AbstractFileAdaptor {

	@Override
	public ArrayList<AbstractFrameEdit> tableToFrameUpdates(TableModel tb) {
		ArrayList<AbstractFrameEdit> frameUpdates = new ArrayList<AbstractFrameEdit>();
		
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			String regulator = (String) tb.getValueAt(rowIndex, 0);
			String regulatee = (String) tb.getValueAt(rowIndex, 1);
			String mode = (String) tb.getValueAt(rowIndex, 2);
			frameUpdates.add(new NewRegulation("PlaceholdeID_" + rowIndex, regulator, regulatee, mode, new int[] {rowIndex + 1}));
		}
		return frameUpdates;
	}
}
