package edu.iastate.biocyctool.cycspreadsheetloader.model;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.OrgStruct;
import edu.iastate.javacyco.PtoolsErrorException;

public class CycDataBaseAccess {
	private JavacycConnection conn;
	private String host;
	private int port;
	private String organism;
	
	public CycDataBaseAccess(String host, int port) {
		this.host = host;
		this.port = port;
		this.organism = null;
	}
	
	public CycDataBaseAccess(String host, int port, String organism) {
		this.host = host;
		this.port = port;
		this.organism = organism;
	}
	
	public void initDefault() {
		conn = null;
		if (organism == null || organism.isEmpty()) connect(host, port);
		else connect(host, port, organism);
	}
	
	// Initialize connection using first organism available
	private boolean connect(String server, int port) {
		try {
			conn = new JavacycConnection(server,port,"me","pass");//TODO
			if (conn.allOrgs() != null || conn.allOrgs().size() != 0) {
				conn.selectOrganism(conn.allOrgs().get(0).getLocalID());
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// Initialize connection with specified organism
	private boolean connect(String server, int port, String organism) {
		try {
			conn = new JavacycConnection(server, port, "me", "pass");//TODO
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
	//TODO isConnected() does not exist
	//	return conn.isConnection();
	public boolean isConnected() {
		try {
			if (conn != null && !conn.allOrgs().isEmpty()) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	// Change selected organism
	private void selectOrganism(String organism) {
		if (getAvailableOrganisms().contains(organism)) conn.selectOrganism(organism);
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

	
	// Save/revert/commit
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
	
	public void commitFrameUpdates(ArrayList<AbstractFrameUpdate> frameUpdates) throws PtoolsErrorException {
		for (AbstractFrameUpdate frameUpdate : frameUpdates) {
			frameUpdate.commit(conn);
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
