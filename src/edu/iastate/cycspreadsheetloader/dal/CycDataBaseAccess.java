package edu.iastate.cycspreadsheetloader.dal;

import java.util.ArrayList;

import edu.iastate.cycspreadsheetloader.model.AbstractFrameUpdate;
import edu.iastate.cycspreadsheetloader.model.SlotUpdate;
import edu.iastate.cycspreadsheetloader.util.Report;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.OrgStruct;
import edu.iastate.javacyco.PtoolsErrorException;

public class CycDataBaseAccess {
	private static JavacycConnection conn;
	private static String host = "localhost";
	private static int port = 4444;
	private static String organism = "CORN";
	
	public CycDataBaseAccess() {
	}
	
	public void initDefault() {
		conn = null;
		connect(host,port,organism);
	}
	
	// Initiallize connection using first organism available
	private boolean connect(String server, int port) {
		try {
			conn = new JavacycConnection(server,port);
			if (conn.allOrgs() != null || conn.allOrgs().size() != 0) {
				conn.selectOrganism(conn.allOrgs().get(0).getLocalID());
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// Initiallize connection with specified organism
	private boolean connect(String server, int port, String organism) {
		try {
			conn = new JavacycConnection(server,port);
			conn.selectOrganism(organism);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// Disconnect from database
	private void disconnect() {
		conn.close();
		conn = null;
	}
	
	// Is connection established?
	public boolean isConnected() {
		return true;
		//TODO isConnected() does not exist
//		return conn.isConnection();
	}
	
	// Change selected organism
	private void selectOrganism(String organism) {
		if (isConnected()) conn.selectOrganism(organism);
	}
	
	// Get all organisms available at the current connection
	public ArrayList<String> getAvailableOrganisms() {
		ArrayList<String> orgIDs = new ArrayList<String>();
		try {
			for (OrgStruct org : conn.allOrgs()) {
				orgIDs.add(org.getLocalID());
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return orgIDs;
	}
	
	// Convert a frame into a printable string
	public String frameToString(String frameID) {
		String printString = "";
		
		printString += "\n"+frameID+"\n";
		
		try {
			ArrayList slots = conn.getFrameSlots(frameID);
			for(int k=0;k<slots.size();k++) {
				String slotName = (String)slots.get(k);
				ArrayList slotValues = conn.getSlotValues(frameID,slotName);
				printString += slotName+" ("+JavacycConnection.countLists(slotValues)+")\n";
				for(Object slotValue : slotValues) {
					if(slotValue instanceof String) {
						printString += "\t"+(String)slotValue+"\n";
						ArrayList<String> annots = conn.getAllAnnotLabels(frameID, slotName, (String)slotValue);
						for(String annotName : annots) {
							printString += "\t\t--"+annotName+"\t"+conn.getValueAnnots(frameID, slotName, (String)slotValue, annotName)+"\n";
						}
					}
					else {
						printString += "\t"+conn.ArrayList2LispList(slotValues)+"\n";
						break;
					}
				}
			}

			if(!conn.getFrameType(frameID).toUpperCase().equals(":CLASS"))
			{
				printString += "~~THE FOLLOWING ARE NOT SLOTS~~\n~GFP SUPERCLASSES: \n";
				for(Object t : conn.getInstanceAllTypes(frameID)) {
					printString += "\t"+(String)t+"\n";
				}
				printString += "~DIRECT GFP SUPERCLASSES: \n";
				for(Object t : conn.getInstanceDirectTypes(frameID))
				{
					printString += "\t"+(String)t+"\n";
				}
			}
		
			printString += "~CLASSIFIED AS\n\t"+Frame.load(conn, frameID).getClass().getName()+"\n";
			printString += "~LOADED FROM\n\t"+Frame.load(conn, frameID).getOrganism().getLocalID()+" "+Frame.load(conn, frameID).getOrganism().getSpecies()+"\n";
			
			return printString;
		}
		catch(PtoolsErrorException e) {
			e.printStackTrace();
		}
		
		return "failed";
	}

	
	// Queryies that change the database
	public void saveDataBase() {
		try {
			conn.saveKB();
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
	}
	
	public void revertDataBase() {
		try {
			conn.revertKB();
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
	}
	
	// Assumes one change per slot.... no slots can be changed to list values
	// Assumes one slot changes per frame... could be optimized to make all changes to a frame at once
	// Catches all "special" cases, such as GO-TERMS and PUBMED citations
	public void loadFrameUpdates(ArrayList<AbstractFrameUpdate> frameUpdates) throws PtoolsErrorException {
		for (AbstractFrameUpdate frameUpdate : frameUpdates) {
			Frame frame = frameUpdate.getFrame(conn);
			
			if (frameUpdate.getClass() == edu.iastate.cycspreadsheetloader.model.SlotUpdate.class) {
				SlotUpdate slotUpdate = (SlotUpdate) frameUpdate;
				if (slotUpdate.getSlotLabel().equalsIgnoreCase("GO-TERMS")) {
					conn.callFuncString("import-go-terms '" + slotUpdate.getValuesAsLispArray());
				}
				frame.putSlotValue(slotUpdate.getSlotLabel(), slotUpdate.getValuesAsLispArray());
				frame.commit();
			} else {
				
			}
		}
	}
	
//	public void compareFrameUpdates(ArrayList<AbstractFrameUpdate> frameUpdates, Report report) throws PtoolsErrorException {
//		for (AbstractFrameUpdate frameUpdate : frameUpdates) {
//			Frame frame = Frame.load(conn, frameUpdate.getFrameID());
//			
//			for (SlotUpdate slotUpdate : frameUpdate.getSlotUpdates()) {
//				if (slotUpdate.getSlotLabel().equalsIgnoreCase("GO-TERMS")) {
//					conn.callFuncString("import-go-terms '" + slotUpdate.getSlotValue());
//				}
//				frame.putSlotValue(slotUpdate.getSlotLabel(), slotUpdate.getSlotValue());
//				frame.commit();
//			}
//		}
//	}
}
