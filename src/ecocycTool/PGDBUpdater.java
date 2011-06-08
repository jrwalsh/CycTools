package ecocycTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;
import edu.iastate.javacyco.Regulation;

/**
 * PGDBUpdater is a class that is designed to be used to modify the structure of a General Frame Protocol (GFP) complient
 * Pathway Genome Database (PGDB) and update the content within. Changes made to the PGDB are logged to a sql database. 
 * 
 * @author Jesse Walsh
 */
public class PGDBUpdater {
	// Global Vars
	private Connection sqlConn = null;
	private JavacycConnection conn = null;
	private String CurrentConnectionString = "";
	private int CurrentPort = 0;
	private String CurrentOrganism = "";
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// Constructor
	/**
	 * Constructor which initializes a JavaCycO connection object and a SQL connection object.
	 */
	public PGDBUpdater() {
		String connectionURL = "jdbc:mysql://localhost:3306/test";
		String user = "Jesse";
		String pass = "firebird";
		try {
			sqlConn = DriverManager.getConnection(connectionURL, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		CurrentConnectionString = ToolBox.connectionStringLocal;
		CurrentPort = ToolBox.defaultPort;
		CurrentOrganism = ToolBox.organismStringCBIRC;
		conn = new JavacycConnection(CurrentConnectionString, CurrentPort);
		conn.selectOrganism(CurrentOrganism);
	}
	
	/**
	 * Constructor which initializes a JavaCycO connection object and a SQL connection object.
	 * 
	 * @param connectionString Server running the JavaCycO socket listener
	 * @param port Port JavaCycO socket listener is listening on
	 * @param organism Pathway Tools organism ID
	 */
	public PGDBUpdater(String connectionString, int port, String organism) {
//		String connectionURL = "jdbc:mysql://ecoserver.vrac.iastate.edu:3306/CBiRC";
//		String user = "changeLogUser";
//		String pass = "clPass";
		String connectionURL = "jdbc:mysql://localhost:3306/test";
		String user = "Jesse";
		String pass = "firebird";
		
		try {
			sqlConn = DriverManager.getConnection(connectionURL, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		CurrentConnectionString = connectionString;
		CurrentPort = port;
		CurrentOrganism = organism;
		conn = new JavacycConnection(CurrentConnectionString, CurrentPort);
		conn.selectOrganism(CurrentOrganism);
	}
	
	protected void finalize() throws Throwable {
	    try {
	    	if (sqlConn != null) sqlConn.close();
	    } finally {
	        super.finalize();
	    }
	}
	
	
	// Testing
	public void test() throws PtoolsErrorException {
		updateFrameSlot("EG11025","NEWSLOT","HELLOWORLD");
		conn.saveKB();
		
		getTFSynonymToFrameMap();
	}
	
	public void tester() throws PtoolsErrorException {
//		ArrayList<Frame> all = conn.getAllGFPInstances("|Regulation|");
//		for (Frame f : all) {
//			if (f.getLocalID().startsWith("PR_")) {
//				System.out.println(f.getLocalID());
//				System.out.println("\t"+f.getSlotValue("REGULATOR"));
//				System.out.println("\t"+f.getSlotValue("REGULATED-ENTITY"));
//				System.out.println("\t"+f.getSlotValue("MECHANISM"));
//				System.out.println("\t"+f.getSlotValue("MODE"));
//			}
//		}
		
		pushNewRegulationFile("");
	}
	
	
	// Push into Ecocyc
	/**
	 * Takes in a file of new transcription factor gene regulatory links and attempts to save them to the EcoCyc database
	 * pointed to by the ToolBox object. The file is expected to have the columns transcriptionFactor, regulatedGene, and
	 * regulationType. The first column must contain the common gene name which codes for the transcription factor.
	 * The second column must contain the common gene name of the gene affected by the transcription factor. The third column
	 * must contain the regulation type, either "-" for repression or "+" for activation.
	 * 
	 * Saves changes made to pgdb and updates sql database with information on the changes made to the pgdb.
	 * 
	 * Note: EcoCyc expects that transcription factors regulate promoter objects. Since this information is not given in our
	 * lab's regulation prediction analysis, we instead create a regulatory object which points directly to a gene object
	 * as the regulatee and another gene object as the regulator.
	 * 
	 * @param fileName File which contains the columns transcriptionFactor, regulatedGene, regulationType
	 */
 	public void pushNewRegulationFile(String fileName) {
 		String regulationMechanism = ":OTHER";
 		fileName = "/home/Jesse/Desktop/Push_Regulation_to_EcoCyc/New_Links_May_17_2011/NewLinks.txt";
 		
		File tfLinks = new File(fileName);
		BufferedReader reader = null;
		TreeSet<String> geneSet = new TreeSet<String>();
		TreeSet<String> tfSet = new TreeSet<String>();
		ArrayList<NewRegulationLink> newLinks = new ArrayList<NewRegulationLink>();
		
		HashMap<String, Frame> geneSynonymToFrameMap = getGeneSynonymToFrameMap();
		
		try {
			reader = new BufferedReader(new FileReader(tfLinks));
			String text = null;
			
			// Ignore Headers
			reader.readLine();
			
			while ((text = reader.readLine()) != null) {
				String[] line = text.split("\t");
				String TF = line[0].toLowerCase();
				String gene = line[1].toLowerCase();
				String mode = (line.length > 2) ? line[2] : "";
				
				tfSet.add(TF);
				geneSet.add(gene);
				
				if (geneSynonymToFrameMap.containsKey(TF) && geneSynonymToFrameMap.containsKey(gene)) {
					newLinks.add(new NewRegulationLink("PR_"+TF+"-"+gene, geneSynonymToFrameMap.get(TF), geneSynonymToFrameMap.get(gene), regulationMechanism, mode));
				} else {
					System.out.println("Gene or TF not found on line : " + text);
				}
			}
			
			//TODO * Check if this regulation already exists in original ecocyc *
			
			sqlConn.setAutoCommit(false);
			Savepoint save = sqlConn.setSavepoint();
			boolean error = false;
			for (NewRegulationLink link : newLinks) {
				try {
					createRegulationFrame(link.frameID, link.regulator, link.regulatee, link.mechanism, link.mode);
				} catch (Exception e) {
					error = true;
				}
			}
			if (!error) {
				sqlConn.commit();
				conn.saveKB();
			} else {
				sqlConn.rollback(save);
				conn.revertKB();
			}
			sqlConn.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 	}
 	
 	/**
 	 * Create a new regulation frame in the pgdb and updates the SQL change log to reflect the new regulation objects.
 	 * Does not save updates to pgdb or commit changes to SQL change log.
 	 * 
 	 * @param frameID Must be a unique frame ID
 	 * @param regulator Entity which performs the regulation
 	 * @param regulatee Entity which is regulated
 	 * @param mechanism Regulation mechanism
 	 * @param mode Regulation mode expects either + or -
 	 * @throws PtoolsErrorException
 	 */
 	public void createRegulationFrame(String frameID, Frame regulator, Frame regulatee, String mechanism, String mode) throws PtoolsErrorException {
 		if (conn.frameExists(frameID)) {
 			System.err.println("Cannot create frame " + frameID + ". Frame already exists.");
 			return;
 		}
 		
 		Regulation reg = new Regulation(conn, frameID);
 		int primaryKey = changeLogFrame(CurrentConnectionString, CurrentOrganism, frameID, true);
 		
 		if (primaryKey < 0) {
 			System.err.println("Cannot create frame " + frameID + ". SQL change log could not be updated.");
 			return;
 		}
 		
 		reg.commit();
 		
 		reg.setRegulator(regulator);
 		changeLogSlot(primaryKey, "REGULATOR", regulator.getLocalID(), false);
 		
 		reg.setRegulatee(regulatee);
 		changeLogSlot(primaryKey, "REGULATED-ENTITY", regulatee.getLocalID(), false);
 		
 		reg.setMechanism(mechanism);
 		changeLogSlot(primaryKey, "MECHANISM", mechanism, false);
 		
 		if (mode.equals("+")) {
 			reg.setMode(true);
 			changeLogSlot(primaryKey, "MODE", mode, false);
 		}
 		else if (mode.equals("-")) {
 			reg.setMode(false);
 			changeLogSlot(primaryKey, "MODE", mode, false);
 		}
 		
 		reg.commit();
 	}
 	
 	/**
 	 * Update the sql changelog to reflect changes to a pgdb frame.
 	 * 
 	 * @param pgdbName Name of pgdb (i.e., organism id of pgdb)
 	 * @param pgdbServer Host of the pgdb server
 	 * @param frameID Frame ID
 	 * @param isNewFrame Frame did not previously exist in this pgdb
 	 * @return
 	 */
 	private int changeLogFrame(String pgdbName, String pgdbServer, String frameID, boolean isNewFrame) {
 		int primaryKeyOfInsert = -1;
 		String createdNew = "0";
 		if (isNewFrame) createdNew = "1";
 		try {
            Statement stmt = sqlConn.createStatement();
            
            String query = "INSERT INTO frame (pgdbName,pgdbServer,frame,isNew) VALUES ";
            query += "('"+pgdbName+"','"+pgdbServer+"','"+frameID+"',"+createdNew+")";
            
            if (stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS) == 0) System.err.println("Row did not insert for query : " + query);
            else {
            	ResultSet rs = stmt.getGeneratedKeys();
            	rs.next();
            	primaryKeyOfInsert = rs.getInt(1);
            }

            stmt.close();
        } 
 		catch (Exception ex) {
 			System.err.println(ex.getMessage());
        }
 		return primaryKeyOfInsert;
 	}
 	
 	/**
 	 * Update the sql changelog to reflect changes to a pgdb frame's slot.
 	 * 
 	 * @param frameKey Primary key of the frame object that was modified
 	 * @param slotName Name of slot
 	 * @param value New value placed in this slot
 	 * @param isNewSlot Slot did not previously exist for this frame
 	 * @return
 	 */
 	private int changeLogSlot(int frameKey, String slotName, String value, boolean isNewSlot) {
 		int primaryKeyOfInsert = -1;
 		String createdNew = "0";
 		if (isNewSlot) createdNew = "1";
 		try {
            Statement stmt = sqlConn.createStatement();
            
            String query = "INSERT INTO slot (frame_id,slot,value,isNew) VALUES ";
            query += "('"+frameKey+"','"+slotName+"','"+value+"',"+createdNew+")";

            if (stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS) == 0) System.err.println("Row did not insert for query : " + query);
            else {
            	ResultSet rs = stmt.getGeneratedKeys();
            	rs.next();
            	primaryKeyOfInsert = rs.getInt(1);
            }

            stmt.close();
        } 
 		catch (Exception ex) {
 			System.err.println(ex.getMessage());
        }
 		return primaryKeyOfInsert;
 	}
 	
 	/**
 	 * Update a frames slot value in the pgdb.
 	 * 
 	 * @param frameID Frame ID
 	 * @param slot Name of slot
 	 * @param value New value placed in this slot
 	 * @throws PtoolsErrorException
 	 */
 	public void updateFrameSlot(String frameID, String slot, String value) throws PtoolsErrorException {
 		//TODO update changelog
 		Frame frame = null;
		try {
			frame = Frame.load(conn, frameID);
			frame.putSlotValue(slot, value);
			
			frame.commit();
		} catch (PtoolsErrorException e) {
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	/**
 	 * Create a hashmap in which each synonym of a gene points to a frame object for that gene. This helps
 	 * when reading in files of gene b#'s or short names and trying to match them up to their frame object.
 	 * 
 	 * @return A hashmap in which each synonym of a gene points to a frame object for that gene
 	 */
 	private HashMap<String, Frame> getGeneSynonymToFrameMap() {
		HashMap<String, Frame> geneSynonymMap = new HashMap<String, Frame>();
		
		try {
			// Get all genes from EcoCyc
			System.out.print("Reading Genes... ");
			ArrayList<Frame> allGenes = conn.getAllGFPInstances("|All-Genes|");
 			System.out.print(allGenes.size() + " found... ");
 			
 			for (Frame geneFrame : allGenes) {
 				if (geneFrame.getCommonName() != null && geneFrame.getCommonName().length() > 0) geneSynonymMap.put(geneFrame.getCommonName().toLowerCase(), geneFrame);
 				if (geneFrame.hasSlot("Synonyms")) {
					for (String synonym : geneFrame.getSynonyms()) geneSynonymMap.put(synonym.replace("\"", "").toLowerCase(), geneFrame);
				}
 				if (geneFrame.hasSlot("Accession-1") && geneFrame.getSlotValue("Accession-1") != null && geneFrame.getSlotValue("Accession-1").length() > 0) geneSynonymMap.put(geneFrame.getSlotValue("Accession-1").replace("\"", "").toLowerCase(), geneFrame);
 				if (geneFrame.hasSlot("Accession-2") && geneFrame.getSlotValue("Accession-2") != null && geneFrame.getSlotValue("Accession-2").length() > 0) geneSynonymMap.put(geneFrame.getSlotValue("Accession-2").replace("\"", "").toLowerCase(), geneFrame);
 			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("done");
		return geneSynonymMap;
	}
 	
 	/**
 	 * Create a hashmap in which each synonym of a protein points to a frame object for that protein.
 	 * 
 	 * @return A hashmap in which each synonym of a protein points to a frame object for that protein
 	 */
 	private HashMap<String, Frame> getTFSynonymToFrameMap() {
 		HashMap<String, Frame> tfSynonymMap = new HashMap<String, Frame>();
 		
		try {
			// Get all transcription factors from EcoCyc
			System.out.print("Reading Proteins... ");
			ArrayList<Frame> allTFs = conn.getAllGFPInstances("|Proteins|");
 			System.out.print(allTFs.size() + " found... ");
 			
 			for (Frame tfFrame : allTFs) {
 				if (tfFrame.getCommonName() != null && tfFrame.getCommonName().length() > 0) tfSynonymMap.put(tfFrame.getCommonName(), tfFrame);
 				if (tfFrame.hasSlot("Synonyms")) {
					for (String synonym : tfFrame.getSynonyms()) tfSynonymMap.put(synonym.replace("\"", ""), tfFrame);
				}
 			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("done");
		return tfSynonymMap;
	}
 	
 	
 	// Internal Classes
 	/**
 	 * Internal class which holds all the information needed to create a new regulation object in a pgdb.
 	 */
 	public class NewRegulationLink {
 		public String frameID;
		public Frame regulator;
		public Frame regulatee;
		public String mechanism;
		public String mode;
		
		public NewRegulationLink(String frameID, Frame regulator, Frame regulatee, String mechanism, String mode) {
			this.frameID = frameID;
			this.regulator = regulator;
			this.regulatee = regulatee;
			this.mechanism = mechanism;
			this.mode = mode;
		}
	}
}
