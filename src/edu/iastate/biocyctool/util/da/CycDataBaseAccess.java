package edu.iastate.biocyctool.util.da;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import edu.iastate.biocyctool.cycspreadsheetloader.model.AbstractFrameEdit;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.Network;
import edu.iastate.javacyco.Network.Edge;
import edu.iastate.javacyco.OrgStruct;
import edu.iastate.javacyco.PtoolsErrorException;

public class CycDataBaseAccess {
	private JavacycConnection conn;
	private String host;
	private int port;
	private String organism;
	private String userName;
	private String password;
	
	public CycDataBaseAccess(String host, int port, String userName, String password) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		
		init();
	}
	
	private void init() {
		conn = null;
		if (connect()) {
			try {
				if (conn.allOrgs() != null || conn.allOrgs().size() != 0) {
					conn.selectOrganism(conn.allOrgs().get(0).getLocalID());
				}
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Initialize connection
	private boolean connect() {
		try {
			if (userName == null || userName.equalsIgnoreCase("") || password == null) conn = new JavacycConnection(host, port); 
			else conn = new JavacycConnection(host, port, userName, password);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// Change selected organism
	public void selectOrganism(String organism) {
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

	// Search
	public DefaultTableModel getSearchResultsTable(String text, String type) throws PtoolsErrorException {
		// Parse text for search terms
		// Expect 1 term per line
		String[] terms = text.split("\n");
		
		// Create dataTable of results
		Object[] header = new String[]{"Search Term", "Results"};
		Object[][] data = new Object[terms.length][2];
		for (int i=0; i<terms.length; i++) {
			String term = terms[i].trim();
			
			ArrayList<Frame> results = conn.search(term, type);
			String resultString = "";
			for (Frame result : results) resultString += result.getLocalID() + ",";
			if (resultString.length() > 0) resultString = resultString.substring(0, resultString.length()-1);
			
			data[i] = new String[]{term, resultString};
		}
		
		DefaultTableModel dtm = new DefaultTableModel(data, header);
		return dtm;
	}
	
	
	// Export Data
	public DefaultTableModel getPGDBStructureTable(String rootGFPtype, boolean includeInstances, boolean directionForward) throws PtoolsErrorException {
		Network network = conn.getClassHierarchy(rootGFPtype, includeInstances, directionForward);
		ArrayList<Edge> edges = network.getEdges();
		
		Object[] header = new String[]{"Source", "Interaction Type", "Target"};
		Object[][] data = new Object[network.getEdges().size()][3];
		for (int i=0; i<network.getEdges().size(); i++) {
			Edge edge = edges.get(i);
			data[i] = new String[]{edge.getSource().getLocalID(), edge.getInfo(), edge.getTarget().getLocalID()};
		}
		
		DefaultTableModel dtm = new DefaultTableModel(data, header);
		return dtm;
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
	
	public void commitFrameUpdates(ArrayList<AbstractFrameEdit> frameUpdates) throws PtoolsErrorException {
		for (AbstractFrameEdit frameUpdate : frameUpdates) {
			frameUpdate.commit(conn);
		}
	}
	
		
	public DefaultMutableTreeNode frameToTree(String frameID) {
 		Frame frame = null;
 		try {
			frame = Frame.load(conn, frameID);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
			return null;
		}
 		
 		DefaultMutableTreeNode root = null;
 		try {
 			root = createNodesFromFrame(new DefaultMutableTreeNode(frame.getLocalID()), frame);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
 		
 		return root;
 	}
 	
	private DefaultMutableTreeNode createNodesFromFrame(DefaultMutableTreeNode top, Frame frame) throws PtoolsErrorException {
 		// Get slot names
		HashMap<String, ArrayList> slots = frame.getSlots();
 		for (String slotLabel : slots.keySet()) {
			DefaultMutableTreeNode slotNode = new DefaultMutableTreeNode(slotLabel);
 			// Get slot values
			ArrayList<String> slotValues = new ArrayList<String>();
 			for (Object slotValue : conn.getSlotValues(frame.getLocalID(), slotLabel)) {
 				if (slotValue instanceof String) slotValues.add((String) slotValue);
 				else slotValues.add(conn.ArrayList2LispList((ArrayList) slotValue));
 			}
 			for (String slotValue : slotValues) {
 					DefaultMutableTreeNode slotValueNode = new DefaultMutableTreeNode(slotValue);
 	 				// Get annotation names
 	 				ArrayList<String> annotLabels = frame.getAllAnnotLabels(slotLabel, slotValue);
 	 				for (String annotLabel : annotLabels) {
 	 					DefaultMutableTreeNode annotLabelNode = new DefaultMutableTreeNode(annotLabel);
 	 					// Get annotation values
 	 					ArrayList<String> annotValues = (ArrayList<String>) frame.getAnnotations(slotLabel, slotValue, annotLabel);
 	 					for (String annotValue: annotValues) {
 	 						DefaultMutableTreeNode annotValueNode = new DefaultMutableTreeNode(annotValue);
 	 						annotLabelNode.add(annotValueNode);
 	 					}
 	 					slotValueNode.add(annotLabelNode);
 	 				}
 	 				slotNode.add(slotValueNode);
 			}
 			top.add(slotNode);
 		}
 		return top;
 	}

	
	
	// Queryies that change the database
	public void revertDB() {
		try {
			conn.revertKB();
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
	}
	
	public void specificLoadOfGoTerms(String excelFileName) {
		File file = new File(excelFileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			
			String header = reader.readLine();
			String[] headings = header.split(",");
			
			while ((text = reader.readLine()) != null) {
				String[] row = text.split(",");
				Frame frame = Frame.load(conn, row[0]);
				for (int i=1; i < row.length; i++) {
					frame.putSlotValue(headings[i], row[i]);
					if (headings[i].equalsIgnoreCase("GO-TERMS")) {
						//TODO remember, this is probably going to be an array of values, not a single
						ArrayList<String> goTerms = new ArrayList<String>();
						goTerms.add(row[i]);
						conn.importGOTerms(goTerms);//conn.callFuncString("import-go-terms '" + row[i]);
					}
				}
				frame.commit();
			}
//				conn.saveKB();
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		finally {
			try {
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

}
