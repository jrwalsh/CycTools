package edu.iastate.cycspreadsheetloader.util;

public class SlotUpdate {
	private String SlotLabel;
	private String SlotValue;
	
	public SlotUpdate(String slotLabel, String slotValue) {
		this.SlotLabel = slotLabel;
		this.SlotValue = slotValue;
	}

	public String getSlotLabel() {
		return SlotLabel;
	}

	public String getSlotValue() {
		return SlotValue;
	}
}
