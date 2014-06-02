package edu.iastate.cyctools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.Network;
import edu.iastate.javacyco.Organism;
import edu.iastate.javacyco.Network.Edge;
import edu.iastate.javacyco.OrgStruct;
import edu.iastate.javacyco.PtoolsErrorException;

@SuppressWarnings({"rawtypes","unchecked"})
public class CycDataBaseAccess implements PropertyChangeListener {
	private ProgressMonitor progressMonitor;
	private SwingWorker task;
	
	public static JavacycConnection conn;
	private String host;
	private int port;
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
				// Select the first organism as a default
				ArrayList<OrgStruct> orgs = conn.allOrgs();
				if (orgs != null && orgs.size() > 0) {
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
	
	public JavacycConnection getConnection() {
		return conn;
	}
	
	public boolean testConnection() {
		if (conn == null) return false;
		try {
			ArrayList<OrgStruct> orgs = conn.allOrgs();
			if (orgs != null && orgs.size() > 0) return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setTimeOut (int miliseconds) {
		conn.setQueryTimeOutLength(miliseconds);
	}
	
	// Change selected organism
	public void selectOrganism(String organism) {
		ArrayList<String> organisms = new ArrayList<String>();
		for (OrgStruct org : getAvailableOrganisms()) organisms.add(org.getLocalID());
		conn.selectOrganism(organism);
	}
	
	public String getSelectedOrganism() {
		return conn.getOrganismID();
	}
	
	// Get all organisms available at the current connection
	public ArrayList<OrgStruct> getAvailableOrganisms() {
		try {
			return conn.allOrgs();
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return new ArrayList<OrgStruct>();
	}
	
	// Convert a frame into a printable string
	public String frameToString(String frameID) {
		try {
			Frame frame = Frame.load(conn, frameID);
			frame.update();
			return frameToString(frame);
		}
		catch(PtoolsErrorException e) {
			return null;
		}
	}
	
	// Convert a frame into a printable string.  Prints all values in local copy of the frame
	public String frameToString(Frame frame) {
		if (frame == null) return null;
		
		String printString = "";
		printString += "\n"+frame.getLocalID()+"\n";
		try {
			ArrayList slots = frame.getLocalSlotLabels();
			TreeSet<String> slotSet = new TreeSet<String>(slots);
			for (String slotName : slotSet) {
				ArrayList slotValues = frame.getSlotValues(slotName);
				printString += slotName+" ("+JavacycConnection.countLists(slotValues)+")\n";
				for(Object slotValue : slotValues) {
					if(slotValue instanceof String) {
						printString += "\t"+(String)slotValue+"\n";
						ArrayList<String> annots = frame.getAllAnnotLabels(slotName, (String)slotValue);
						for(String annotName : annots) {
							printString += "\t\t--"+annotName+"\t"+frame.getAnnotations(slotName, (String)slotValue, annotName)+"\n";
						}
					}
					else {
						printString += "\t"+JavacycConnection.ArrayList2LispList(slotValues)+"\n";
						break;
					}
				}
			}
			
			if (conn.frameExists(frame.getLocalID())) { // Can only print frame type information if the frame exists in the database
				if(!conn.getFrameType(frame.getLocalID()).toUpperCase().equals(":CLASS")) {
					printString += "~~THE FOLLOWING ARE NOT SLOTS~~\n~GFP SUPERCLASSES: \n";
					for(Object t : conn.getInstanceAllTypes(frame.getLocalID())) {
						printString += "\t"+(String)t+"\n";
					}
					printString += "~DIRECT GFP SUPERCLASSES: \n";
					for(Object t : conn.getInstanceDirectTypes(frame.getLocalID()))
					{
						printString += "\t"+(String)t+"\n";
					}
				}
			
				printString += "~CLASSIFIED AS\n\t"+Frame.load(conn, frame.getLocalID()).getClass().getName()+"\n";
				printString += "~LOADED FROM\n\t"+Frame.load(conn, frame.getLocalID()).getOrganism().getLocalID()+" "+Frame.load(conn, frame.getLocalID()).getOrganism().getSpecies()+"\n";
			}
			
			return printString;
		}
		catch(PtoolsErrorException e) {
			return null;
		}
	}

	public ArrayList<Frame> substringSearch(String text, String type) {
		try {
			return conn.search(text, type);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return new ArrayList<Frame>();
	}
	
	public ArrayList<String> getPGDBChildrenOfFrame(String classFrameID) {
		ArrayList<String> childFrameIDs = new ArrayList<String>();
		try {
			childFrameIDs = (ArrayList<String>)conn.getClassDirectSubs(classFrameID);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return childFrameIDs;
	}
	
	public ArrayList<Frame> getFramesOfType(String type) {
		ArrayList<Frame> frames = new ArrayList<Frame>();
		try{
			for (Frame f : conn.getAllGFPInstances(type)) {
				frames.add(f);
			}
		} catch (PtoolsErrorException e) {
			return frames;
		}
		return frames;
	}
	
	public String getOrganismCommonName(String organismID) {
		Organism org;
		String orgCommonName = "";
		try {
			org = conn.getOrganism();
			orgCommonName = org.getCommonName();
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return orgCommonName;
	} 
	
	/**
	 * Converts the frames instances of "type" to an XML format and writes to file at "path".
	 * 
	 * The format for XML is as follows.  Each frame is an element with an attribute of "ID" which contains the PGDB ID of that frame.
	 * Frames have Slot elements with the attribute of "Label".  If the slot is a string value slot (i.e. a string or an array of strings),
	 * then the slot will have zero or more text elements called SlotValue each of which will contain a single value for the slot and 
	 * zero or more Annotation elements.  Annotation elements have the attribute "AnnotationLabel".  Annotations will then have zero or 
	 * more Value text elements with the values of the Annotation.
	 * 
	 * A slot value can sometimes be a list of lists (i.e. String[][] rather than a string or array of strings), in which case the SlotValue
	 * element may contain more than one Value element.  Slots which contain array structures do not have annotations.
	 * 
	 * @param path
	 * @param type
	 * @return 0 for success, -1 for error;
	 * @throws PtoolsErrorException
	 */
	public int printFramesToXML(String path, String type) {
		ArrayList<Frame> frames = new ArrayList<Frame>(); //TODO build this into a worker thread, tends to run long
		
		try {
			for (Frame f : conn.getAllGFPInstances(type)) {
				frames.add(f);
			}
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Frames");
			doc.appendChild(rootElement);
			
			for (Frame frame : frames) {
				Element frameElement = doc.createElement("Frame");
				frameElement.setAttribute("ID", frame.getLocalID());
				
				for (String slotLabel : frame.getSlots().keySet()) {
					Element slotElement = doc.createElement("Slot");
					slotElement.setAttribute("Label", slotLabel);
					
					ArrayList<Object> slotValues = frame.getSlotValues(slotLabel); //Sometimes slot data is actually an arraylist, not a string
					for (Object slotValueObject : slotValues) {
						Element slotValueElement = doc.createElement("SlotValue");
						
						if (slotValueObject instanceof ArrayList) {
							// Handle when the slot value is actually an array of values
							ArrayList<String> valueArray = (ArrayList<String>)slotValueObject;
							for (String value : valueArray) {
								Element valueElement = doc.createElement("Value");
								valueElement.appendChild(doc.createTextNode(value));
								slotValueElement.appendChild(valueElement);
							}
						} else {
							// Handle when slot value is a normal string value
							String slotValue = (String) slotValueObject;
							
							Element valueElement = doc.createElement("Value");
							valueElement.appendChild(doc.createTextNode(slotValue));
							slotValueElement.appendChild(valueElement);
							for (String annotationLabel : frame.getAllAnnotLabels(slotLabel, slotValue)) {
								Element annotationElement = doc.createElement("Annotation");
								annotationElement.setAttribute("Label", annotationLabel);
								slotValueElement.appendChild(annotationElement);
								
								ArrayList<String> annotationValues = frame.getAnnotations(slotLabel, slotValue, annotationLabel);
								for (String annotationValue : annotationValues) {
									Element annotationValueElement = doc.createElement("Value");
									annotationValueElement.appendChild(doc.createTextNode(annotationValue));
									annotationElement.appendChild(annotationValueElement);
								}
							}
						}
						slotElement.appendChild(slotValueElement);
					}
					frameElement.appendChild(slotElement);
				}
				rootElement.appendChild(frameElement);
			}
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));
	 
			// StreamResult result = new StreamResult(System.out);
	 
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
	 
//			System.out.println("File saved!");
			
			return 0;
	  	} catch (PtoolsErrorException e) {
	  		e.printStackTrace();
	  	} catch (ParserConfigurationException pce) {
	  		pce.printStackTrace();
	  	} catch (TransformerException tfe) {
	  		tfe.printStackTrace();
	  	}
		return -1;
	}
	
	public int printFramesToCSV(String path, String type) {
		String slotDelimiter = "\t";
		String valueDelimiter = "$";
		
//		Frame frame = Frame.load(conn,  "G-14659");
		
		String printString = "";
		ArrayList<Frame> frames = new ArrayList<Frame>();
		try {
			frames.add(Frame.load(conn,  "G-14659"));
			frames.add(Frame.load(conn,  "G-14659"));
			frames.add(Frame.load(conn,  "G-14659"));
			frames.add(Frame.load(conn,  "G-14659"));
			
			TreeSet<String> slots = new TreeSet<String>();
			for (Frame frame : frames) {
				for (String slotLabel : frame.getSlots().keySet()) slots.add(slotLabel);
			}
			
			ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
			for (Frame frame : frames) {
				ArrayList<String> row = new ArrayList<String>();
				
				// Format to CSV
				row.add(frame.getLocalID());
				
				for (String slotLabel : slots) {
					ArrayList<Object> slotValueObjects = frame.getSlotValues(slotLabel);
					String slotValue = "";
					for (Object slotValueObject : slotValueObjects) {
						if (slotValueObject instanceof ArrayList) {
							ArrayList<String> valueArray = (ArrayList<String>)slotValueObject;
							String valueString = "(";
							for (String value : valueArray) {
								valueString += value + " ";
							}
							if (valueArray.size() > 0) valueString = valueString.substring(0, valueString.length()-1);
							valueString += ")";
							slotValue += valueString + valueDelimiter;
						} else {
							String value = (String) slotValueObject;
							slotValue += value + valueDelimiter;
						}	
					}
					if (slotValue.endsWith(valueDelimiter)) slotValue = slotValue.substring(0, slotValue.length()-1);
					row.add(slotValue);
				}
				rows.add(row);
			}
			
			printString += "FrameID\t";
			for (String slotLabel : slots) {
				printString += slotLabel + slotDelimiter;
			}
			printString += "\n";
			for (ArrayList<String> row : rows) {
				for (String string : row) {
					printString += string + slotDelimiter;
				}
				printString += "\n";
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
			return -1;
		}
		
		try {
			printString(path, printString);
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
//	public int printFramesToCSV(String path, String type) throws PtoolsErrorException {
//		ArrayList<Frame> frames = new ArrayList<Frame>();  //TODO build this into a worker thread and pull the data directly from the preview table, rather than reloading it.  Additionally, take this format and make it a separate option, maybe a custom extension.  This will be as "complete" an output of a frame in CSV as possible.
//		for (Frame f : conn.getAllGFPInstances(type)) {
//			frames.add(f);
//		}
//		
//		String printString = "";
//		String headerString = "";
//		String slotDelimiter = "\t";
//		String valueDelimiter = ":";
//		String valueArrayDelimiter = "::";
//		TreeSet<String> slots = new TreeSet<String>();
//		
//		// Get all possible slots for this group of frames
//		for (Frame frame : frames) {
//			for (String slotLabel : frame.getSlots().keySet()) slots.add(slotLabel);
//		}
//		
//		// Set up header string
//		headerString += "FrameID" + slotDelimiter;
//		for (String slot : slots) {
//			headerString += slot + slotDelimiter;
//		}
//		headerString += "\n";
//		
//		// Format to CSV
//		for (Frame frame : frames) {
//			printString += frame.getLocalID() + slotDelimiter;
//			
//			for (String slotLabel : slots) {
//				ArrayList<Object> slotValueObjects = frame.getSlotValues(slotLabel);
//				for (Object slotValueObject : slotValueObjects) {
//					if (slotValueObject instanceof ArrayList) {
//						ArrayList<String> valueArray = (ArrayList<String>)slotValueObject;
//						for (String value : valueArray) {
//							printString += value + valueArrayDelimiter;
//						}
//					} else {
//						String value = (String) slotValueObject;
//						printString += value + valueDelimiter;
//					}	
//				}
//				printString += slotDelimiter;
//			}
//			printString += "\n";
//		}
//		
//		try {
//			printString(path, headerString + printString);
//			return 0;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return -1;
//	}
		
	private void printString(String fileName, String printString) {
		PrintStream o = null;
		try {
			o = new PrintStream(new File(fileName));
			o.println(printString);
			o.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public DefaultTableModel getPGDBStructureTable(String rootGFPtype, boolean includeInstances, boolean directionForward) {
		Network network;
		try {
			network = conn.getClassHierarchy(rootGFPtype, includeInstances, directionForward);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
			return new DefaultTableModel();
		}
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
	
	public void commitFrameUpdates(ArrayList<AbstractFrameEdit> frameUpdates) {
		for (AbstractFrameEdit frameUpdate : frameUpdates) {
			try {
				boolean result = frameUpdate.commit(conn);
				if (!result) System.err.println("Commit failed!");
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Frame updateLocalFrame(String frameID, ArrayList<AbstractFrameEdit> frameUpdates) {
		Frame frame = null;
		try {
			frame = Frame.load(conn, frameID);
			frame.update();
			for (AbstractFrameEdit frameUpdate : frameUpdates) {
				frameUpdate.commitLocal(frame);
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return frame;
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
 				else slotValues.add(JavacycConnection.ArrayList2LispList((ArrayList) slotValue));
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
	
	public DefaultTableModel getSearchResultsTable(String text, String type) {
		progressMonitor = new ProgressMonitor(DefaultController.mainJFrame, "Running a Long Task", "", 0, 100);
		progressMonitor.setMillisToDecideToPopup(0);
		progressMonitor.setMinimum(0);
		progressMonitor.setProgress(0);
		task = new GetSearchResultsTableTask(text, type);
		task.addPropertyChangeListener(this);
		task.execute();
		
		try {
			if (task.get() != null) {
				return (DefaultTableModel)task.get();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return new DefaultTableModel();
	}
	
	private class GetSearchResultsTableTask extends SwingWorker<DefaultTableModel, Void> {
		private String text;
		private String type;
		
		public GetSearchResultsTableTask(String text, String type) {
			this.text = text;
			this.type = type;
		}
		
		@Override
		public DefaultTableModel doInBackground() {
			int progress = 0;
			setProgress(progress);
			try {
				// Parse text for search terms
				// Expect 1 term per line from the user
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
					
					progress = (int) ((i*100)/terms.length);
					setProgress(progress);
				}
				
				DefaultTableModel dtm = new DefaultTableModel(data, header);
				return dtm;
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public void done() {
			System.out.println("Done");
			progressMonitor.setProgress(0);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			
			if (progressMonitor.isCanceled() || task.isDone()) {
				if (progressMonitor.isCanceled()) {
					task.cancel(true);
				} else {
				}
			}
		}
	}

	public boolean isCurrentKBModified(String kb) {
		try {
			return conn.isModified(kb);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
			return false;
		}
	}
}
