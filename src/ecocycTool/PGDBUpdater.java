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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Date;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;
import edu.iastate.javacyco.Regulation;

/**
 * PGDBUpdater is a class that is designed to be used to modify the structure of a Generic Frame Protocol (GFP) complient
 * Pathway Genome Database (PGDB) and update the content within. Changes made to the PGDB are logged to a sql database. 
 * 
 * @author Jesse Walsh
 */
public class PGDBUpdater {
	// Global Vars
	private static Connection sqlConn = null;
	private static JavacycConnection conn = null;
	private static String connectionURL = "";
	private static String org = "";
	private static String ptoolsVersion = "";
	private static int port = -1;
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// Main
	/**
	 * Main method: initializes a JavaCycO connection object and a SQL connection object.
	 * 
	 * @param args FileName of new regulatory links
	 */
	public static void main(String[] args) {
		// Args
		if(args.length < 1) {
			System.out.println("Usage: PGDBUpdater FILENAME");
			System.exit(0);
		}
		String fileName = args[0];
		
		// Non-arg options
//		String sqlConnectionURL = "jdbc:mysql://ecoserver.vrac.iastate.edu:3306/CBiRC";
		String sqlConnectionURL = "jdbc:mysql://localhost:3306/CBiRC";
		String sqlUser = "cbircUser";
		String sqlPass = "cbircUserPass";
		connectionURL = ToolBox.connectionStringTHT;
		port = ToolBox.defaultPort;
		org = ToolBox.organismStringTEST;
		ptoolsVersion = "15.0";
		
		// Get SQL connection object
		try {
			sqlConn = DriverManager.getConnection(sqlConnectionURL, sqlUser, sqlPass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Get JavaCycO connection object
		conn = new JavacycConnection(connectionURL, port);
		conn.selectOrganism(org);
		try {
			pushNewRegulationFile(fileName);
	    } finally {
	    	try {
	    		if (sqlConn != null && !sqlConn.isClosed()) sqlConn.close();
	    	} catch (Exception e) {
	    		System.err.println(e.getStackTrace());
	    	}
	    }
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
	 * @param fileName Tab-delim file which contains the columns transcriptionFactor, regulatedGene, regulationType in this order. Header line is removed.
	 * @param connectionURL
	 * @param org
	 * @param ptoolsVersion
	 */
 	public static void pushNewRegulationFile(String fileName) {
 		// Options
 		String regulationMechanism = ":OTHER";
 		
 		// Declare Variables
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
			
			//TODO ***Check if this regulation already exists in original ecocyc***
			
			// Update PGDB and SQL databases
			sqlConn.setAutoCommit(false);
			Savepoint save = sqlConn.setSavepoint();
			boolean error = false;
			int pgdbKey = -1;
			try {
				//TODO if pgdb exists, get pgdbKey of existing entry
				pgdbKey = changeLogPGDBCreate(org, connectionURL, ptoolsVersion);
				if (pgdbKey < 0) throw new Exception("Could not find or create a pgdb entry in the sql database for this host/organism/version. PGDB key found is: " + pgdbKey);
			} catch (Exception e) {
				sqlConn.rollback(save);
				conn.revertKB();
				sqlConn.close();
				
				System.err.println(e.getMessage());
				System.err.println(e.getStackTrace());
				System.exit(-1);
			}
			
			// Insert all frames into pgdb and sql statements into the sql change log, but don't commit unless there are no errors
			for (NewRegulationLink link : newLinks) {
				try {
					createRegulationFrame(pgdbKey, link.frameID, link.regulator, link.regulatee, link.mechanism, link.mode);
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
		System.out.println("Success!");
 	}
 	
 	
 	// PGDB
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
 	public static void createRegulationFrame(int pgdbKey, String frameID, Frame regulator, Frame regulatee, String mechanism, String mode) throws PtoolsErrorException {
 		if (conn.frameExists(frameID)) {
 			System.err.println("Cannot create frame " + frameID + ". Frame already exists.");
 			return;
 		}
 		
 		Regulation reg = new Regulation(conn, frameID);
 		int primaryKey = changeLogFrameCreate(pgdbKey, frameID);
 		
 		if (primaryKey < 0) {
 			System.err.println("Cannot create frame " + frameID + ". SQL change log could not be updated.");
 			return;
 		}
 		
 		reg.commit();
 		
 		reg.setRegulator(regulator);
 		changeLogSlotUpdate(primaryKey, "REGULATOR", regulator.getLocalID());
 		
 		reg.setRegulatee(regulatee);
 		changeLogSlotUpdate(primaryKey, "REGULATED-ENTITY", regulatee.getLocalID());
 		
 		reg.setMechanism(mechanism);
 		changeLogSlotUpdate(primaryKey, "MECHANISM", mechanism);
 		
 		if (mode.equals("+")) {
 			reg.setMode(true);
 			changeLogSlotUpdate(primaryKey, "MODE", mode);
 		}
 		else if (mode.equals("-")) {
 			reg.setMode(false);
 			changeLogSlotUpdate(primaryKey, "MODE", mode);
 		}
 		
 		reg.commit();
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
 	
 	//TODO
 	private void deleteFrame() {}
 	private void deleteSlot() {}
 	private void createSlot() {}
 	
 	
 	// SQL
 	/**
 	 * Update the sql changelog to reflect changes to a pgdb frame.
 	 * 
 	 * @param organismID Name of pgdb (i.e., organism id of pgdb)
 	 * @param host Host of the pgdb server
 	 * @param ptoolsVersion version of ptools running when this update was made
 	 * @return primary key of insert
 	 */
 	private static int changeLogPGDBCreate(String organismID, String host, String ptoolsVersion) {
 		int primaryKeyOfInsert = -1;
 		try {
            Statement stmt = sqlConn.createStatement();
            
            String query = "INSERT INTO pgdb (organismID,host,ptoolsVersion) VALUES ";
            query += "('"+organismID+"','"+host+"',"+ptoolsVersion+")";
            
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
 	 * Update the sql changelog to reflect changes to a pgdb frame.
 	 * 
 	 * @param pgdbID Primary key of pgdb (i.e., organism id of pgdb)
 	 * @param frameID Frame ID
 	 * @return primary key of insert
 	 */
 	private static int changeLogFrameCreate(int pgdbKey, String frameID) {
 		int primaryKeyOfInsert = -1;
 		String createdNew = "1";
 		try {
            Statement stmt = sqlConn.createStatement();
            String query = "INSERT INTO frame (pgdb_id,frameID,createdNew,dateCreated) VALUES ";
            query += "('"+pgdbKey+"','"+frameID+"',"+createdNew+",'"+now()+"')";
            
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
 	 * @return returns primary key of inserted
 	 */
 	private static int changeLogSlotUpdate(int frameKey, String slotName, String value) {
 		int primaryKeyOfInsert = -1;
 		try {
            Statement stmt = sqlConn.createStatement();
            
            String query = "INSERT INTO slot (frame_id,slotName,value) VALUES ";
            query += "('"+frameKey+"','"+slotName+"','"+value+"')";

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
 	
 	//TODO
 	private void changeLogDeleteFrame() {}
 	private void changeLogDeleteSlot() {}
 	private void changeLogCreateSlot() {}
 	private void changeLogGetPGDB() {}
 	
 	// Helper Functions
 	/**
 	 * Create a hashmap in which each synonym of a gene points to a frame object for that gene. This helps
 	 * when reading in files of gene b#'s or short names and trying to match them up to their frame object.
 	 * 
 	 * @return A hashmap in which each synonym of a gene points to a frame object for that gene
 	 */
 	private static HashMap<String, Frame> getGeneSynonymToFrameMap() {
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
 	private HashMap<String, Frame> getProteinSynonymToFrameMap() {
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
 	
 	private static String now() {
 		Date today = new Date();
        Timestamp now = new Timestamp(today.getTime());
        return now.toString();
 	}
 	
 	// Internal Classes
 	/**
 	 * Internal class which holds all the information needed to create a new regulation object in a pgdb.
 	 */
 	public static class NewRegulationLink {
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
