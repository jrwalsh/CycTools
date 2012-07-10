package edu.iastate.cycspreadsheetloader.dal;

import edu.iastate.cycspreadsheetloader.util.Report;
import edu.iastate.javacyco.Frame;

public class DataAccessUtil {
	public static void putSlotValue(Frame frame, String slotLabel, String slotValue, Report report) {
		frame.putSlotValue(slotLabel, slotValue);
	}
}
