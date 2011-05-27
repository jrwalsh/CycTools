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
import edu.iastate.javacyco.Gene;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;
import edu.iastate.javacyco.Regulation;
import edu.iastate.javacyco.TranscriptionUnit;

/**
 * PGDBUpdater is a class that is designed to be used to modify the structure of a General Frame Protocol (GFP) complient
 * Pathway Genome Database (PGDB) and update the content within. Changes made to the PGDB are logged to a sql database. 
 * 
 * @author Jesse Walsh
 */
public class PGDBUpdater {
	// SQL static vars
//	static private String connectionURL = "jdbc:mysql://ecoserver.vrac.iastate.edu:3306/CBiRC";
//	static private String user = "changeLogUser";
//	static private String pass = "clPass";
	static private String connectionURL = "jdbc:mysql://localhost:3306/test";
	static private String user = "Jesse";
	static private String pass = "firebird";
	
	// Global Vars
	ToolBox tb = null;
	private Connection sqlConn = null;
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// Constructor
	/**
	 * Constructor which initializes a ToolBox object and a SQL connection object. 
	 */
	public PGDBUpdater() {
		tb = new ToolBox(ToolBox.connectionStringLocal, ToolBox.defaultPort, ToolBox.organismStringCBIRC);
		try {
			sqlConn = DriverManager.getConnection(connectionURL, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void finalize() throws Throwable {
	    try {
	    	if (sqlConn != null) sqlConn.close();
	    } finally {
	        super.finalize();
	    }
	}
	
	
	// Testing
	public void tester() throws PtoolsErrorException {
		updateFrameSlot("EG11025","NEWSLOT","HELLOWORLD");
		tb.getConn().saveKB();
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
	 * lab's regulation prediction analysis, we instead add a slot to the gene object which points to the gene of the transcription
	 * factor which regulates it.
	 * 
	 * @param fileName File which contains the columns transcriptionFactor, regulatedGene, regulationType
	 */
 	public void pushNewRegulationFile(String fileName) {
 		// Read in file.
 		// For each row, identify TF and Gene pair.
 		// Push new regulates info into the TF and the Gene
 		
		String regulationMechanism = ":OTHER";
		File tfLinks = new File(fileName);
		BufferedReader reader = null;
		TreeSet<String> geneSet = new TreeSet<String>();
		TreeSet<String> tfSet = new TreeSet<String>();
		ArrayList<NewRegulationLink> newLinks = new ArrayList<NewRegulationLink>();
		
		HashMap<String, Frame> geneSynonymToFrameMap = getGeneSynonymToFrameMap();
		HashMap<String, Frame> tfSynonymToFrameMap = getTFSynonymToFrameMap();
		
		try {
			reader = new BufferedReader(new FileReader(tfLinks));
			String text = null;
			
			// Headers
			reader.readLine();
			
			while ((text = reader.readLine()) != null) {
				String[] line = text.split("\t");
				String TF = line[0];
				String gene = line[1];
//				String mode = line[2];
				
				tfSet.add(TF);
				geneSet.add(gene);
				
				if (tfSynonymToFrameMap.containsKey(TF) && geneSynonymToFrameMap.containsKey(gene)) {
					// link to gene --> newLinks.add(new NewRegulationLink("", tfSynonymToFrameMap.get(TF), geneSynonymToFrameMap.get(gene), "", null));
					
					ArrayList<TranscriptionUnit> tus = ((Gene)geneSynonymToFrameMap.get(gene)).getTranscriptionUnits();
					for (TranscriptionUnit tu : tus) {
						if (tu.getPromoter() != null) {
							//TODO naming convention
							//TODO duplicate removal
							newLinks.add(new NewRegulationLink("", tfSynonymToFrameMap.get(TF), tu.getPromoter(), regulationMechanism, null));
//							System.out.println("" + " " + tfSynonymToFrameMap.get(TF).getLocalID() + " " + tu.getPromoter().getLocalID() + " " + regulationMechanism + " " + null);
						}
					}
				}
			}
			
//			for (String key : geneSynonymToFrameMap.keySet()) System.out.println(key);
//			for (String key : tfSynonymToFrameMap.keySet()) System.out.println(key);
			
//			for (String gene : genes) {
//				if (geneSynonymToFrameMap.containsKey(gene)) {
//					System.out.println(gene + "\t" + geneSynonymToFrameMap.get(gene).getLocalID());
//					Gene geneFrame = (Gene)Gene.load(conn, geneSynonymToFrameMap.get(gene).getLocalID());
//					ArrayList<TranscriptionUnit> tus = geneFrame.getTranscriptionUnits();
//					for (TranscriptionUnit tu : tus) {
//						if (tu.getPromoter() != null) System.out.print(tu.getPromoter().getLocalID() + " ");
//					}
//					System.out.println();
//				}
//				else System.out.println(gene + "\t");
//			}
//			for (String tf : tfs) {
//				if (tfSynonymToFrameMap.containsKey(tf)) System.out.println(tf + "\t" + tfSynonymToFrameMap.get(tf).getLocalID());
//				else System.out.println(tf + "\t");
//			}
			
//			for (String tf : tfs) {
//				if (tfSynonymToFrameMap.containsKey(tf)) {
//					Gene geneFrame = (Gene)Gene.load(conn, geneSynonymToFrameMap.get(gene).getLocalID());
//					ArrayList<TranscriptionUnit> tus = geneFrame.getTranscriptionUnits();
//					for (TranscriptionUnit tu : tus) {
//						if (tu.getPromoter() != null) System.out.print(tu.getPromoter().getLocalID() + " ");
//					}
//				}
//			}
			
			//TODO * Check if this regulation already exists in original ecocyc*
			sqlConn = DriverManager.getConnection(connectionURL, user, pass);
			sqlConn.setAutoCommit(false);
			Savepoint save = sqlConn.setSavepoint();
			boolean error = false;
			for (NewRegulationLink link : newLinks) {
				createRegulationFrame(link.frameID, link.regulator, link.regulatee, link.mechanism, link.mode);
				//TODO * Add commentary somewhere? such as on the gene being regulated? * updateFrameSlot(frameID, slot, value);
				// if () error = true;
			}
			if (!error) {
				sqlConn.commit();
				tb.getConn().saveKB();
			} else {
				sqlConn.rollback(save);
				tb.getConn().revertKB();
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
 	 * Create a new regulation frame in the pgdb. Does not save updates to pgdb.
 	 * 
 	 * @param frameID Must be a unique frame ID
 	 * @param regulator Entity which performs the regulation
 	 * @param regulatee Entity which is regulated
 	 * @param mechanism Regulation mechanism
 	 * @param mode Regulation mode expects either + or -
 	 * @throws PtoolsErrorException
 	 */
 	public void createRegulationFrame(String frameID, Frame regulator, Frame regulatee, String mechanism, String mode) throws PtoolsErrorException {
 		if (tb.getConn().frameExists(frameID)) {
 			System.err.println("Cannot create frame " + frameID + ". Frame already exists.");
 			return;
 		}
 		
 		Regulation reg = new Regulation(tb.getConn(), frameID);
 		int primaryKey = changeLogFrame(tb.getCurrentConnectionString(), tb.getCurrentOrganism(), frameID, true);
 		
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
			frame = Frame.load(tb.getConn(), frameID);
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
			ArrayList<Frame> allGenes = tb.getConn().getAllGFPInstances("|All-Genes|");
 			System.out.print(allGenes.size() + " found... ");
 			
 			for (Frame geneFrame : allGenes) {
 				if (geneFrame.getCommonName() != null && geneFrame.getCommonName().length() > 0) geneSynonymMap.put(geneFrame.getCommonName(), geneFrame);
 				if (geneFrame.hasSlot("Synonyms")) {
					for (String synonym : geneFrame.getSynonyms()) geneSynonymMap.put(synonym.replace("\"", ""), geneFrame);
				}
 				if (geneFrame.hasSlot("Accession-1") && geneFrame.getSlotValue("Accession-1") != null && geneFrame.getSlotValue("Accession-1").length() > 0) geneSynonymMap.put(geneFrame.getSlotValue("Accession-1").replace("\"", ""), geneFrame);
 				if (geneFrame.hasSlot("Accession-2") && geneFrame.getSlotValue("Accession-2") != null && geneFrame.getSlotValue("Accession-2").length() > 0) geneSynonymMap.put(geneFrame.getSlotValue("Accession-2").replace("\"", ""), geneFrame);
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
			ArrayList<Frame> allTFs = tb.getConn().getAllGFPInstances("|Proteins|");
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
