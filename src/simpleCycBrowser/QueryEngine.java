package simpleCycBrowser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.OrgStruct;
import edu.iastate.javacyco.PtoolsErrorException;

public class QueryEngine {
	private static JavacycConnection conn;
	
	public QueryEngine() {
		
	}
	
	// Initiallize connection using first organism available
	public boolean connect(String server, int port) {
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
	public boolean connect(String server, int port, String organism) {
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
	public void disconnect() {
		//TODO close() is broken
		conn.close();
	}
	
	// Is connection established?
	public boolean isConnected() {
		return true;
		//TODO isConnected() does not exist
//		return conn.isConnection();
	}
	
	// Change selected organism
	public void selectOrganism(String organism) {
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
				}
				frame.commit();
			}
//			conn.saveKB();
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
	
	public void bulkLoad (String[] frameIDs, String[][] data, ArrayList<ColumnMapping> columnMappings) {
		ArrayList<FrameUpdate> bulkUpdates = new ArrayList<FrameUpdate>();
		
		for (int i = 0; i < data.length; i++) {
			FrameUpdate update = new FrameUpdate();
			update.FrameID = frameIDs[i];
			for (int j = 0; j < data[i].length; j++) {
				ColumnMapping mapping = columnMappings.get(i);
				if (mapping != null) {
					if (mapping.isSlotMapping) {
//						update.SlotUpdates.add(new SlotUpdate(mapping.SlotLabel,data[i][j]));
					}
					else {
						//TODO figure out how to specify an annotation update
//						update.AnnotationUpdates.add(new AnnotationUpdate(mapping.SlotLabel,slotValue,mapping.AnnotationLabel,data[i][j]));
					}
				}
			}
			bulkUpdates.add(update);
		}
		
		loadUpdates(bulkUpdates);
	}
	private void loadUpdates (ArrayList<FrameUpdate> frameUpdates) {
		// frameUpdates should be in the format of key=frameID, value=ArrayList<FrameUpdate> which correspond to label/value pairs for what needs to be updated on that frame.
	}
	
	
	// Internal Classes
	private class ColumnMapping {
		private boolean isSlotMapping;
		private String delimiter;
		private String SlotLabel;
		private String AnnotationLabel;
	}
	private class FrameUpdate {
		private String FrameID;
		ArrayList<SlotUpdate> SlotUpdates;
		ArrayList<AnnotationUpdate> AnnotationUpdates;
	}
	private class SlotUpdate {
		private String SlotLabel;
		private String SlotValue;
	}
	private class AnnotationUpdate {
		private String SlotLabel;
		private String SlotValue;
		private String AnnotationLabel;
		private String AnnotationValue;
	}
}
