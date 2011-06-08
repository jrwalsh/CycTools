package ecocycTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.*;

import edu.iastate.javacyco.*;

public class ToolBox {
	// JavaCycO static vars
	static public String connectionStringLocal =  "jrwalsh.student.iastate.edu";
	static public String connectionStringEcoServer =  "ecoserver.vrac.iastate.edu";
	static public String connectionStringVitis =  "vitis.student.iastate.edu";
	static public String organismStringK12 =  "ECOLI"; //Built-in K12 model
	static public String organismStringABC =  "ABC"; //Edit-able copy of built-in K12 model on local machine
	static public String organismStringCBiRC =  "ECOTEST"; //Edit-able copy of built-in K12 model
	static public String organismString0157 =  "ECOO157"; //0157:H7 EDL933 strain
	static public String organismStringCFT073 =  "ECOL199310"; //CRT073 strain
	static public String organismStringARA =  "ARA"; //Aracyc model
	static public String organismStringCBIRC =  "CBIRC"; //CBiRC E. coli model
	static public int defaultPort =  4444;
	
	// Global Vars
	private JavacycConnection conn = null;
	private String CurrentConnectionString = connectionStringLocal;
	private int CurrentPort = defaultPort;
	private String CurrentOrganism = organismStringK12;
	private Connection sqlConn = null;
	
	
	// Constructor
	public ToolBox(String connectionString, int port, String organism) {
		CurrentConnectionString = connectionString;
		CurrentPort = port;
		CurrentOrganism = organism;

		conn = new JavacycConnection(CurrentConnectionString,CurrentPort);
		conn.selectOrganism(CurrentOrganism);
	}
	
	protected void finalize() throws Throwable {
	    try {
	    	if (conn != null) conn.close();
	    } finally {
	        super.finalize();
	    }
	}
	
	public void tester() throws PtoolsErrorException {
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
 		
 		
 		//--------------------------------------------------
//		try {
////			Frame glc = Frame.load(conn, "CHEBI");
////	 		glc.print();
//	 		
////			updateFrameSlot("GLC","DBLINKS","(CAS \"50-99-7\" |taltman| 3458663425 ) (CHEBI \"15903\" |taltman| 3458663425 ) (LIGAND-CPD \"C00221\" |taltman| 3458663425 ) (PUBCHEM \"64689\" |taltman| 3458663073 ) (MyDB \"identifier\" |tester| 3458663073 )");
////			conn.saveKB();
//		} catch (PtoolsErrorException e) {
//			System.out.println("Save was unsuccessful : " + e);
//		}
		
		
		
		
		
//		ArrayList<Frame> allGenes = conn.getAllGFPInstances("|Regulation-of-Transcription|");
//		System.out.println(allGenes.size());
//		for (Frame f : allGenes) f.print();
		
//		Frame.load(conn, "EG10226-MONOMER").print();
//		Frame.load(conn, "REG0-6610").print();
//		Frame.load(conn, "PM0-9327").print();
		
//		createRegulationFrame();
//		Frame.load(conn, "testFrame").print();
//		conn.saveKB();
//		updateFrameSlot("G6837", "NewRegulation", "EG10226-MONOMER");
		
//		Frame.load(conn, "|Regulation|").print();
//		pushNewRegulationFile("/home/Jesse/Desktop/Push_Regulation_to_EcoCyc/Regulation_Links_cut");

		
// 		String query = "INSERT INTO pgdbChangeLog (pgdbChangeLog_key,pgdbName,pgdbServer,frameID,slotName,oldSlotValue,newSlotValue,oldComment,newComment,createdNew,date) VALUES ()";
		
//		changeLogFrame(organismStringCBIRC, connectionStringEcoServer, "testFrame", true);
//		changeLogSlot(1,"NewRegulation","Created by CBiRC",true);
		
//		createRegulationFrame("newFrame", Frame.load(conn, "EG10226-MONOMER"), Frame.load(conn, "PM0-9327"), ":OTHER", "+");
		
		
		
//		for (OrgStruct o : conn.allOrgs()) System.out.println(o.getLocalID());
		
		
		
		
		
//		pathwaysOfGenes();
//		pathwayCommonNames();
//		geneCommonNames();
//		getRegulators();
//		Frame.load(conn, "PM125").print();
//		Frame.load(conn, "REG0-6079").print();
//		Frame.load(conn, "G6276-MONOMER").print();
//		Frame.load(conn, "BS0-6381").print();
//		ArrayList<Frame> allGenes = conn.getAllGFPInstances("|Transcription-Factors|");
		
		
		
		printAllPathwaysXGMML("/home/Jesse/Desktop/xgmml");
	}

	public void run(String[] args) {
		System.out.println("Reporting genomic structures at location points.");
		if (args.length == 2) {
			genomeStructureAtLocation(args[0], args[1]);
		} else {
			System.out.println("Please provide an input file and an output file.");
		}
	}
	
	public String reactionGeneRule(String reactionID) throws PtoolsErrorException {
		String orRule = "";
		for (Object enzyme : conn.enzymesOfReaction(reactionID)) {
			String andRule = "";
			for (Object gene : conn.genesOfProtein(enzyme.toString())) {
				andRule += gene.toString() + " and ";
			}
			if (andRule.length() > 0) {
				andRule = "(" + andRule.substring(0, andRule.length()-5) + ")";
				orRule += andRule + " or ";
			}
		}
		if (orRule.length() > 0) orRule = orRule.substring(0, orRule.length()-4);
		return orRule;
	}
	
	
	
	// Compare Palsson model to EcoCyc model -- based on outdated model
	public void chemicalSpeciesMatcher() {
		//1: Load ecocyc list
		//2: Create map from chemical formula to id, keeping duplicates
		//2.5: Create map from id to name+formula
		//3: Load Palsson list
		//4: For each Palsson species, look its formula up in the map to get the matching EcoCyc ids
		//5: For each match, print a line 'P_id', 'P_name', 'P_formula', 'E_id', 'E_name', 'E_formula'
		
		//TODO
		/**
		 * Redo process to be sure there are no bugs
		 * Anything uniquely identified should be in one section
		 * Anything not identified in another
		 * EcoCyc compounds not in Palsson probably can't be identified as of yet
		 * Find a way to trim out the compartment duplicates from the Palson model
		 */
		
		File ecocycFile = new File("/home/Jesse/Desktop/CBiRC_FBA/Code/MatchingSpecies/EcoCyc_species_list");
		File palssonFile = new File("/home/Jesse/Desktop/CBiRC_FBA/Code/MatchingSpecies/Palsson_species_list");
		BufferedReader reader = null;
		
		HashMap<String, ArrayList<String>> formulaToIdMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, String[]> idToInfoMap = new HashMap<String, String[]>();
		
		String nonParseable = "";
		String noFormulaMatches = "";
		String noSynonymMatches = "";
		String matches = "";
		
		try {
			reader = new BufferedReader(new FileReader(ecocycFile));
			String text = null;
			
			// Read ecocyc file and generate maps from chemical formula to Id and from Id to formula and name
			while ((text = reader.readLine()) != null) {
				String[] line = text.split("\t");
				
				if (line.length == 3) {
					idToInfoMap.put(line[0], new String[] {line[1],line[2]});
					if (formulaToIdMap.containsKey(line[2])) {
						formulaToIdMap.get(line[2]).add(line[0]);
					} else {
						ArrayList<String> id = new ArrayList<String>();
						id.add(line[0]);
						formulaToIdMap.put(line[2], id);
					}
				} else {
					// Without expected information, we cannot automatically match, but right now we only care about finding
					// EcoCyc correlates to the species in the Palsson model, not the other way around.
				}
			}
			
			// Read palsson file, attempt to match each compound in the palsson model to a frame in EcoCyc
			reader = new BufferedReader(new FileReader(palssonFile));
			text = "";
			
			while ((text = reader.readLine()) != null) {
				String[] line = text.split("\t");
				
				if (line.length == 3) {
					String synMatch = "";
					String nonSynMatch = "";
					
					if (formulaToIdMap.containsKey(line[2])) {
						for (String id : formulaToIdMap.get(line[2])) {
							
							// Formula Only Matching
//							String[] info = idToInfoMap.get(id);
//							String palssonInfo = line[0] + "\t" + line[1] + "\t" + line[2];
//							String ecocycInfo = id + "\t" + info[0] + "\t" + info[1];
//							System.out.println(palssonInfo + "\t" + ecocycInfo);
							
							
							// SmartMatch heuristic matching

							// Get all ecocyc synonyms for the proposed match
							ArrayList<String> synonyms = null;
							ArrayList<String> modSynonyms = null;
							try {
								Frame compound = Frame.load(conn, id);
								synonyms = compound.getSynonyms();
								synonyms.add(compound.getCommonName());
								synonyms.add(compound.getLocalID());
								modSynonyms = new ArrayList<String>();
							} catch (PtoolsErrorException e) {
								// If you can't load the frame, then treat this as a formula only match
								System.out.println("Cannot look up : " + id);
								String[] info = idToInfoMap.get(id);
								String palssonInfo = line[0] + "\t" + line[1] + "\t" + line[2];
								String ecocycInfo = id + "\t" + info[0] + "\t" + info[1];
								nonSynMatch += palssonInfo + "\t" + ecocycInfo + "\n";
								break;
							}
							
							// Break each synonym into primary parts by spliting on [_-,() ] and concatenating pieces with a length > 2
							for (String synonym : synonyms) {
								String modSynonym = "";
								String[] synonymParts = synonym.split("[_\\-\\,\\(\\) ]+");
								for (String synonymPart : synonymParts) {
									if (synonymPart.toLowerCase().replace("\"", "").length() > 2) modSynonym += synonymPart.toLowerCase().replace("\"", "");
								}
								
								modSynonyms.add(modSynonym);
							}
							
							// Break the palsson name into primary parts by spliting on [_-,() ] and concatenating pieces with a length > 2
							String modPalssonName = "";
							String[] palssonNameParts = line[1].split("[_\\-\\,\\(\\) ]+");
							for (String palssonNamePart : palssonNameParts) {
								if (palssonNamePart.length() > 2) modPalssonName += palssonNamePart;
							}
							
							// If the modified palsson name exactly matches a modified synonym, call this a match
							if (modSynonyms.contains(modPalssonName.toLowerCase())) {
								String[] info = idToInfoMap.get(id);
								String palssonInfo = line[0] + "\t" + line[1] + "\t" + line[2];
								String ecocycInfo = id + "\t" + info[0] + "\t" + info[1];
								synMatch += palssonInfo + "\t" + ecocycInfo + "\n";
							} else {
								String[] info = idToInfoMap.get(id);
								String palssonInfo = line[0] + "\t" + line[1] + "\t" + line[2];
								String ecocycInfo = id + "\t" + info[0] + "\t" + info[1];
								nonSynMatch += palssonInfo + "\t" + ecocycInfo + "\n";
							}
						}
					} else {
						// No chemical formula match was found
						String palssonInfo = line[0] + "\t" + line[1] + "\t" + line[2];
						noFormulaMatches += palssonInfo + "\n";
					}
					
					// If any proposed matches had matching synonyms, then keep all direct matches.
					if (synMatch.length() > 0) {
						matches += synMatch;
					} else {
						// If no proposed matches had exact matching synonyms, then keep all proposed matches.  These
						// will probably need to be reviewed manually.
						noSynonymMatches += nonSynMatch;
					}
				} else {
					// Without expected information, we cannot automatically match
					//TODO catch unmatched terms :: unmatchables += "";
				}
			}
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
		
		// Print output
		System.out.println("Direct Synonym Matches");
		System.out.println(matches);
		
		System.out.println("Formula Only Matches");
		System.out.println(noSynonymMatches);
		
		System.out.println("No Matches");
		System.out.println(noFormulaMatches);
		
		System.out.println("Incomplete Palsson Species");
		System.out.println(nonParseable);
	}
	
	
	
	// Functions for the GUI interface
 	public HashMap<String,String> getAllPathways() {
 		HashMap<String,String> PathwayMap = new HashMap<String,String>();
 		
		// Get pathways from EcoCyc
		try {
			ArrayList<Pathway> allPwys = null;
			try {
				allPwys = Pathway.all(conn);
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
			}
			for (Pathway pwy : allPwys) {
				PathwayMap.put(pwy.getCommonName(), pwy.getLocalID());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return PathwayMap;
	}
 	
 	
 	// Export a selected group of pathways to a tab file using the print commands
 	public void exportPathway(String pathwayID) {
 		//TODO Verify Pathway
 		
		// Print single pathway
		try {
			Frame pway = Pathway.load(conn, pathwayID);
			Network net = ((Pathway)pway).getNetwork();
			net.loadNetworkPathwayInfo();
			net.printTab();
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	public void exportPathways(String[] pathwayIDs) {
 		//TODO Verify Pathways
 		
 		// Print multiple pathways
		try {
			if (pathwayIDs.length == 1) exportPathway(pathwayIDs[0]);
			else {
				Network net = null;
				for (String id : pathwayIDs) {
					Frame pway = Pathway.load(conn, id);
					
					if (net == null) net = ((Pathway)pway).getNetwork();
					else net.importNetwork(((Pathway)pway).getNetwork());
					
					net.loadNetworkPathwayInfo();
					net.printTab();
				}
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	public void exportAllPathways() {
 		// Print all pathways
		try {
			Organism org = conn.getOrganism();
			org.printPathwayNetwork();
			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	public void exportPathwaysByOntology(String ontology) {
 		//TODO Broken...
 		// Print all pathways
		try {
//			if (!conn.instanceAllInstanceOfP("|Pathways|", ontology)) {
//				System.out.println("NOT A PATHWAY CLASS");
//				return;
//			}
			
			ArrayList<Frame> pathways = conn.getAllGFPInstances(ontology);
			int i = 1;
			for (Frame p : pathways) {
				exportPathwayFlux(p.getLocalID(), ontology);
				System.out.println(i + "/" + pathways.size());i++;
			}
			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	public void exportPathwayFlux(String pathwayID, String fileName) {
 		//TODO Broken...
 		//TODO Verify Pathway
 		
		// Print single pathway
		try {
			Frame pway = Pathway.load(conn, pathwayID);
			Network net = ((Pathway)pway).getNetwork();
//			printFluxTopology(net, pathwayID, fileName);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	
// 	// Push into Ecocyc
// 	public void pushNewRegulationFile(String fileName) {
// 		// Read in file.
// 		// For each row, identify TF and Gene pair.
// 		// Push new regulates info into the TF and the Gene
// 		
//		String regulationMechanism = ":OTHER";
//		File tfLinks = new File(fileName);
//		BufferedReader reader = null;
//		TreeSet<String> geneSet = new TreeSet<String>();
//		TreeSet<String> tfSet = new TreeSet<String>();
//		ArrayList<NewRegulationLink> newLinks = new ArrayList<NewRegulationLink>();
//		
//		HashMap<String, Frame> geneSynonymToFrameMap = getGeneSynonymToFrameMap();
//		HashMap<String, Frame> tfSynonymToFrameMap = getTFSynonymToFrameMap();
//		
//		try {
//			reader = new BufferedReader(new FileReader(tfLinks));
//			String text = null;
//			
//			// Headers
//			reader.readLine();
//			
//			while ((text = reader.readLine()) != null) {
//				String[] line = text.split("\t");
//				String TF = line[0];
//				String gene = line[1];
////				String mode = line[2];
//				
//				tfSet.add(TF);
//				geneSet.add(gene);
//				
//				if (tfSynonymToFrameMap.containsKey(TF) && geneSynonymToFrameMap.containsKey(gene)) {
//					// link to gene --> newLinks.add(new NewRegulationLink("", tfSynonymToFrameMap.get(TF), geneSynonymToFrameMap.get(gene), "", null));
//					
//					ArrayList<TranscriptionUnit> tus = ((Gene)geneSynonymToFrameMap.get(gene)).getTranscriptionUnits();
//					for (TranscriptionUnit tu : tus) {
//						if (tu.getPromoter() != null) {
//							//TODO naming convention
//							//TODO duplicate removal
//							newLinks.add(new NewRegulationLink("", tfSynonymToFrameMap.get(TF), tu.getPromoter(), regulationMechanism, null));
////							System.out.println("" + " " + tfSynonymToFrameMap.get(TF).getLocalID() + " " + tu.getPromoter().getLocalID() + " " + regulationMechanism + " " + null);
//						}
//					}
//				}
//			}
//			
////			for (String key : geneSynonymToFrameMap.keySet()) System.out.println(key);
////			for (String key : tfSynonymToFrameMap.keySet()) System.out.println(key);
//			
////			for (String gene : genes) {
////				if (geneSynonymToFrameMap.containsKey(gene)) {
////					System.out.println(gene + "\t" + geneSynonymToFrameMap.get(gene).getLocalID());
////					Gene geneFrame = (Gene)Gene.load(conn, geneSynonymToFrameMap.get(gene).getLocalID());
////					ArrayList<TranscriptionUnit> tus = geneFrame.getTranscriptionUnits();
////					for (TranscriptionUnit tu : tus) {
////						if (tu.getPromoter() != null) System.out.print(tu.getPromoter().getLocalID() + " ");
////					}
////					System.out.println();
////				}
////				else System.out.println(gene + "\t");
////			}
////			for (String tf : tfs) {
////				if (tfSynonymToFrameMap.containsKey(tf)) System.out.println(tf + "\t" + tfSynonymToFrameMap.get(tf).getLocalID());
////				else System.out.println(tf + "\t");
////			}
//			
////			for (String tf : tfs) {
////				if (tfSynonymToFrameMap.containsKey(tf)) {
////					Gene geneFrame = (Gene)Gene.load(conn, geneSynonymToFrameMap.get(gene).getLocalID());
////					ArrayList<TranscriptionUnit> tus = geneFrame.getTranscriptionUnits();
////					for (TranscriptionUnit tu : tus) {
////						if (tu.getPromoter() != null) System.out.print(tu.getPromoter().getLocalID() + " ");
////					}
////				}
////			}
//			
//			//TODO * Check if this regulation already exists in original ecocyc*
//			sqlConn = DriverManager.getConnection(connectionURL, user, pass);
//			sqlConn.setAutoCommit(false);
//			Savepoint save = sqlConn.setSavepoint();
//			boolean error = false;
//			for (NewRegulationLink link : newLinks) {
//				createRegulationFrame(link.frameID, link.regulator, link.regulatee, link.mechanism, link.mode);
//				//TODO * Add commentary somewhere? such as on the gene being regulated? * updateFrameSlot(frameID, slot, value);
//				// if () error = true;
//			}
//			if (!error) {
//				sqlConn.commit();
//				conn.saveKB();
//			} else {
//				sqlConn.rollback(save);
//				conn.revertKB();
//			}
//			sqlConn.close();
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		finally {
//			try {
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			try {
//				if (reader != null) {
//					reader.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
// 	}
// 	
// 	public void createRegulationFrame(String frameID, Frame regulator, Frame regulatee, String mechanism, String mode) throws PtoolsErrorException {
// 		if (conn.frameExists(frameID)) {
// 			System.err.println("Cannot create frame " + frameID + ". Frame already exists.");
// 			return;
// 		}
// 		
// 		Regulation reg = new Regulation(conn, frameID);
// 		int primaryKey = changeLogFrame(CurrentConnectionString, CurrentOrganism, frameID, true);
// 		
// 		if (primaryKey < 0) {
// 			System.err.println("Cannot create frame " + frameID + ". SQL change log could not be updated.");
// 			return;
// 		}
// 		
// 		reg.commit();
// 		
// 		reg.setRegulator(regulator);
// 		changeLogSlot(primaryKey, "REGULATOR", regulator.getLocalID(), false);
// 		
// 		reg.setRegulatee(regulatee);
// 		changeLogSlot(primaryKey, "REGULATED-ENTITY", regulatee.getLocalID(), false);
// 		
// 		reg.setMechanism(mechanism);
// 		changeLogSlot(primaryKey, "MECHANISM", mechanism, false);
// 		
// 		if (mode.equals("+")) {
// 			reg.setMode(true);
// 			changeLogSlot(primaryKey, "MODE", mode, false);
// 		}
// 		else if (mode.equals("-")) {
// 			reg.setMode(false);
// 			changeLogSlot(primaryKey, "MODE", mode, false);
// 		}
// 		
// 		reg.commit();
// 	}
// 	
// 	private int changeLogFrame(String pgdbName, String pgdbServer, String frameID, boolean isNewFrame) {
// 		int primaryKeyOfInsert = -1;
// 		String createdNew = "0";
// 		if (isNewFrame) createdNew = "1";
// 		try {
//            Statement stmt = sqlConn.createStatement();
//            
//            String query = "INSERT INTO frame (pgdbName,pgdbServer,frame,isNew) VALUES ";
//            query += "('"+pgdbName+"','"+pgdbServer+"','"+frameID+"',"+createdNew+")";
//            
//            if (stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS) == 0) System.err.println("Row did not insert for query : " + query);
//            else {
//            	ResultSet rs = stmt.getGeneratedKeys();
//            	rs.next();
//            	primaryKeyOfInsert = rs.getInt(1);
//            }
//
//            stmt.close();
//        } 
// 		catch (Exception ex) {
// 			System.err.println(ex.getMessage());
//        }
// 		return primaryKeyOfInsert;
// 	}
// 	
// 	private int changeLogSlot(int frameID, String slotName, String value, boolean isNewSlot) {
// 		int primaryKeyOfInsert = -1;
// 		String createdNew = "0";
// 		if (isNewSlot) createdNew = "1";
// 		try {
//            Statement stmt = sqlConn.createStatement();
//            
//            String query = "INSERT INTO slot (frame_id,slot,value,isNew) VALUES ";
//            query += "('"+frameID+"','"+slotName+"','"+value+"',"+createdNew+")";
//
//            if (stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS) == 0) System.err.println("Row did not insert for query : " + query);
//            else {
//            	ResultSet rs = stmt.getGeneratedKeys();
//            	rs.next();
//            	primaryKeyOfInsert = rs.getInt(1);
//            }
//
//            stmt.close();
//        } 
// 		catch (Exception ex) {
// 			System.err.println(ex.getMessage());
//        }
// 		return primaryKeyOfInsert;
// 	}
// 	
// 	public void updateFrameSlot(String frameID, String slot, String value) throws PtoolsErrorException {
// 		//TODO update changelog
// 		Frame frame = null;
//		try {
//			frame = Frame.load(conn, frameID);
//			frame.putSlotValue(slot, value);
//			
//			frame.commit();
//		} catch (PtoolsErrorException e) {
//			throw e;
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
// 	}
// 	
// 	private HashMap<String, Frame> getGeneSynonymToFrameMap() {
//		HashMap<String, Frame> geneSynonymMap = new HashMap<String, Frame>();
//		
//		try {
//			// Get all genes from EcoCyc
//			System.out.print("Reading Genes... ");
//			ArrayList<Frame> allGenes = conn.getAllGFPInstances("|All-Genes|");
// 			System.out.print(allGenes.size() + " found... ");
// 			
// 			for (Frame geneFrame : allGenes) {
// 				if (geneFrame.getCommonName() != null && geneFrame.getCommonName().length() > 0) geneSynonymMap.put(geneFrame.getCommonName(), geneFrame);
// 				if (geneFrame.hasSlot("Synonyms")) {
//					for (String synonym : geneFrame.getSynonyms()) geneSynonymMap.put(synonym.replace("\"", ""), geneFrame);
//				}
// 				if (geneFrame.hasSlot("Accession-1") && geneFrame.getSlotValue("Accession-1") != null && geneFrame.getSlotValue("Accession-1").length() > 0) geneSynonymMap.put(geneFrame.getSlotValue("Accession-1").replace("\"", ""), geneFrame);
// 				if (geneFrame.hasSlot("Accession-2") && geneFrame.getSlotValue("Accession-2") != null && geneFrame.getSlotValue("Accession-2").length() > 0) geneSynonymMap.put(geneFrame.getSlotValue("Accession-2").replace("\"", ""), geneFrame);
// 			}
//		} catch (PtoolsErrorException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println("done");
//		return geneSynonymMap;
//	}
// 	
// 	private HashMap<String, Frame> getTFSynonymToFrameMap() {
// 		HashMap<String, Frame> tfSynonymMap = new HashMap<String, Frame>();
// 		
//		try {
//			// Get all transcription factors from EcoCyc
//			System.out.print("Reading Proteins... ");
//			ArrayList<Frame> allTFs = conn.getAllGFPInstances("|Proteins|");
// 			System.out.print(allTFs.size() + " found... ");
// 			
// 			for (Frame tfFrame : allTFs) {
// 				if (tfFrame.getCommonName() != null && tfFrame.getCommonName().length() > 0) tfSynonymMap.put(tfFrame.getCommonName(), tfFrame);
// 				if (tfFrame.hasSlot("Synonyms")) {
//					for (String synonym : tfFrame.getSynonyms()) tfSynonymMap.put(synonym.replace("\"", ""), tfFrame);
//				}
// 			}
//		} catch (PtoolsErrorException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println("done");
//		return tfSynonymMap;
//	}
// 	
 	
 	//TODO Match up Al's new regulation information to ecocyc
 	public void pathwayCommonNames() {
 		String output = "";
 		String fileName = "/home/Jesse/Desktop/pathwayCommonNames.txt";
 		try {
			ArrayList<Frame> allGenes = conn.getAllGFPInstances("|Pathways|");
			for (Frame f : allGenes) {
				output += f.getLocalID() + "\t" + f.getCommonName() + "\n";
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		
		printString(fileName, output);
 	}
 	
 	public void geneCommonNames() {
 		String output = "";
 		String fileName = "/home/Jesse/Desktop/geneCommonNames.txt";
 		try {
			ArrayList<Frame> allGenes = conn.getAllGFPInstances("|All-Genes|");
			for (Frame f : allGenes) {
				output += f.getLocalID() + "\t" + f.getCommonName() + "\n";
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		
		printString(fileName, output);
 	}
 	
// 	public void getRegulators() {
// 		String output = "";
//// 		String fileName = "/home/Jesse/Desktop/geneCommonNames.txt";
// 		try {
//			ArrayList<Frame> allRegulators = conn.getAllGFPInstances("|Regulation-of-Transcription|");
//			System.out.println(allRegulators.size());
//			for (Frame f : allRegulators) {
//				String synonyms = "";
//				for (String synonym : Frame.load(conn, f.getSlotValue("REGULATOR")).getSynonyms()) synonyms += synonym + "::";
//				System.out.println(f.getLocalID() + "\t" + synonyms + "\t" + f.getSlotValue("REGULATOR") + "\t" + f.getSlotValue("REGULATED-ENTITY"));
////				output += f.getLocalID() + "\t" + f.getCommonName() + "\n";
//			}
//		} catch (PtoolsErrorException e) {
//			e.printStackTrace();
//		}
//		
////		printString(fileName, output);
// 	}
 	
 	public void pathwaysOfGenes() {
 		String output = "";
 		String fileName = "/home/Jesse/Desktop/pathwaysOfGenes.txt";
 		try {
			ArrayList<Frame> allGenes = conn.getAllGFPInstances("|All-Genes|");
			
			for (Frame f : allGenes) {
				Gene gene = (Gene)f;
				ArrayList<Frame> pathways = gene.getPathways();
				
				output += gene.getLocalID();
				for (Frame pathway : pathways) {
					output += "\t" + pathway.getLocalID();
				}
				output += "\n";
			}
			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		
		printString(fileName, output);
 	}
 	
 	public void regulators() {
		// File Reader Code http://www.kodejava.org/examples/28.html
 		
 		//>> The Strategy  >> \\
 		/*
 		 * 1) Read in line
 		 * 2) Is transcriptID MG1655, EDL933, SAKAI, CFT073
 		 * 3) Check appropriate strain for the bnumber/ID/CommonName/Synonym
 		 * 4) Print original file, then print which organism was matched and match's IDs
 		 */
 		
		File file = new File("/home/Jesse/Desktop/Predicted_Links.csv");
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;
		HashMap<String, Frame> geneCommonNameMap = new HashMap<String, Frame>();
		HashMap<String, Frame> geneSynonymMap = new HashMap<String, Frame>();
		HashMap<String, Frame> proteinCommonNameMap = new HashMap<String, Frame>();
		HashMap<String, Frame> proteinSynonymMap = new HashMap<String, Frame>();
		
		try {
			//TEST>>
//			Frame thePathway = Frame.load(conn, "GLYCOLYSIS-TCA-GLYOX-BYPASS");
//			ArrayList<Frame> genesOfPathway = ((Pathway)thePathway).getGenes();
//			ArrayList<String> genesOfPathwayNames = new ArrayList<String>();
//			for (Frame f : genesOfPathway) {
//				genesOfPathwayNames.add(f.getLocalID());
//			}
			//<<TEST
			
			// Get all Genes from EcoCyc
			System.out.print("Reading Genes....");
			ArrayList<Frame> allGenes = conn.getAllGFPInstances(Gene.GFPtype);
			System.out.println("done");
			
			// Create HashMaps to facilitate fast searching
			System.out.print("Generating Gene Maps....");
			for (Frame g : allGenes) {
				if (g.getCommonName() != null && g.getCommonName().length() > 0) geneCommonNameMap.put(g.getCommonName(), g);
				if (g.hasSlot("Synonyms")) {
					for (Object synonym : g.getSlotValues("Synonyms")) geneSynonymMap.put((String)synonym, g);
				}
			}
			System.out.println("done");
			
			// Get all Proteins from EcoCyc
			System.out.print("Reading Proteins....");
			ArrayList<Frame> allProteins = conn.getAllGFPInstances(Protein.GFPtype);
			System.out.println("done");
			
			// Create HashMaps to facilitate fast searching
			System.out.print("Generating Protein Maps....");
			for (Frame p : allProteins) {
				if (p.getCommonName() != null && p.getCommonName().length() > 0) proteinCommonNameMap.put(p.getCommonName(), p);
				if (p.hasSlot("Synonyms")) {
					for (Object synonym : p.getSlotValues("Synonyms")) proteinSynonymMap.put(((String)synonym).replace("'", "").replace("\"", ""), p);
				}
			}
			System.out.println("done");

			reader = new BufferedReader(new FileReader(file));
			String text = null;
			
			//TODO Headers
			reader.readLine();
			
			while ((text = reader.readLine()) != null) {
				String[] line = text.split("\t");
				
				//Get frame of TF
				Frame tf = proteinCommonNameMap.get(line[0]);
				if (tf == null) tf = proteinSynonymMap.get(line[0]);
				
				//Get frame of gene
				Frame gene = geneCommonNameMap.get(line[1]);
				if (gene == null) gene = geneSynonymMap.get(line[1]);
				
				String tfName = "";
				String geneName = "";
				String tfID = "";
				String geneID = "";
				String pathways = "";
				String superPathways = "";
				if (tf != null) {
					tfName = tf.getCommonName();
					tfID = tf.getLocalID();
				}
				if (gene != null) {
					geneName = gene.getCommonName();
					geneID = gene.getLocalID();
					
					ArrayList<Frame> pways = ((Gene)gene).getPathways();
					for (Frame pway : pways) {
						pathways = pathways + "::" + pway.getLocalID();
					}
					for (Frame pway : pways) {
						if (((Pathway)pway).hasSlot("In-Pathway") && pway.getSlotValue("In-Pathway") != null) {
							superPathways = superPathways + "::" + pway.getSlotValue("In-Pathway");
						}
						pathways = pathways + "::" + pway.getLocalID();
					}
				}
				
				//TEST>>
//				if (gene != null && genesOfPathwayNames.contains(gene.getLocalID())) {
//					System.out.println(tfID + "\t" + "AL_PREDICTED_REGULATION" + "\t" + "1" + "\t" + "-" + "\t" + geneID + "\t" + "null");
//				}
				//<<TEST
				
				//System.out.println(tfName + "\t" + geneName + "\t" + tfID + "\t" + geneID + "\t" + pathways + "\t" + superPathways);
				//System.out.println(line[0] + "=" + tfName + " || " + line[1] + "=" + geneName);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PtoolsErrorException e) {
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
		
		//TODO Print results to file
	}
 	
 	public void pathwaysOfRegulators(String[] regulatorIDs) {
 		try {
 			// Headers
 			System.out.println("TF Common Name\tPathway Common Names");
 			
 			// Load the regulators specified in the regulatorIDs array.  Print a warning if a requested ID does not perform regulation
	 		for (String regulatorID : regulatorIDs) {
	 			TreeSet<String> regulatorPathways = new TreeSet<String>();
	 			Frame regulator = Frame.load(conn, regulatorID);
	 			
//	 			if (!regulator.hasSlot("Regulates")) System.out.println(regulator.getLocalID() + " is not a regulator");

//	 			// Load the regulation objects this regulator is involved in
//	 			ArrayList<String> regulationIDs = regulator.getSlotValues("Regulates");
//	 			for (String regulationID : regulationIDs) {
//	 				Frame regulation = Frame.load(conn, regulationID);
//	 				
//	 				// Load the promoters this regulator regulates
//	 				ArrayList<String> regulatedEntityIDs = regulation.getSlotValues("Regulated-Entity");
//	 				for (String regulatedEntityID : regulatedEntityIDs) {
//	 					Frame regulatedEntity = Frame.load(conn, regulatedEntityID);
//	 					
//	 					// Get the TUs of the promoters, the genes of the TUs, and the pathways of the genes
//	 					ArrayList<String> TuIDs = regulatedEntity.getSlotValues("Component-Of");
//	 					for (String TuID : TuIDs) {
//	 						Frame TU = Frame.load(conn, TuID);
//	 						
//	 						ArrayList<Gene> genes = ((TranscriptionUnit)TU).getGenes();
//	 						for (Gene gene : genes) {
//	 							
//	 							ArrayList<Frame> pathways = gene.getPathways();
//	 							for (Frame pathway : pathways) {
//	 								regulatorPathways.add(pathway.getLocalID());
//	 							}
//	 						}
//	 					}
//	 					
//	 				}
//	 			}
//	 			System.out.println(regulator.getLocalID());
//	 			for (String pathway : regulatorPathways) {
//	 				System.out.println("\t" + pathway);
//	 			}
//	 			System.out.println(regulator.getLocalID());
	 			
	 			// Get genes regulated by this regulator
	 			for (Gene gene : ((Protein)regulator).genesRegulatedByProtein()) {
	 				ArrayList<Frame> pathways = gene.getPathways();
						for (Frame pathway : pathways) {
							regulatorPathways.add(pathway.getLocalID());
						}
	 			}
	 			
	 			// Print pathways of genes this regulator regulates
	 			System.out.print(regulator.getCommonName());
	 			for (String pathwayID : regulatorPathways) {
	 				Frame pathway = Frame.load(conn, pathwayID);
	 				System.out.print("\t" + pathway.getCommonName());
	 			}
	 			System.out.println();
	 		}
 		} catch (PtoolsErrorException e) {
 			e.printStackTrace();
		}
 	}
 	
 	public void genesToReactionProducts() {
 		// Note that the getProducts() function ignores class objects
 		String output = "";
		try {
			ArrayList<Reaction> rxns = Reaction.all(conn);
		
			for (Reaction rxn : rxns) {
				output += reactionGeneRule(rxn.getLocalID()) + "\t";
				output += rxn.getLocalID() + "\t";
				String productString = "";
				for (Frame product : rxn.getProducts()) {
					productString += product.getLocalID() + ",";
				}
				if (productString.length() > 0) output += productString.substring(0, productString.length()-1);
				output += "\n";
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		printString("/home/Jesse/Desktop/al_gene_to_metabolite", output);
 	}
 	
 	public void regulatorsOfTFs() {
 		try {
			for (Object tf : conn.allTranscriptionFactors()) {
				System.out.print(tf.toString() + " : ");
				for (Object act : conn.directActivators(tf.toString())) {
					System.out.print("Act=" + act.toString());
				}
				for (Object inhib : conn.directInhibitors(tf.toString())) {
					System.out.print("Inh=" + inhib.toString());
				}
				System.out.println();
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
 	}
 	
 	public void geneSynonyms() {
 		// Print out gene ID and all synonyms
 		try {
			// Get all Genes from EcoCyc
 		// Get all Genes from EcoCyc
			ArrayList<Frame> allGenes = conn.getAllGFPInstances("|All-Genes|");
			
			// Print Headers
			String headers = "ECOCYC-ID\tCOMMON-NAME\tACCESSION-1\tACCESSION-1\tSYNONYMS";
			System.out.println(headers);
			
			for (Frame gene : allGenes) {
				String id = gene.getLocalID();
				String commonName = gene.getCommonName();
				String geneAccession1 = "";
				if (gene.hasSlot("Accession-1")) geneAccession1 = gene.getSlotValue("Accession-1");
				if (geneAccession1 != null) geneAccession1 = geneAccession1.replace("\"", "").trim();
				String geneAccession2 = "";
				if (gene.hasSlot("Accession-2")) geneAccession2 = gene.getSlotValue("Accession-2");
				if (geneAccession2 != null) geneAccession2 = geneAccession2.replace("\"", "").trim();
				
				String synonyms = "";
				for (String synonym : gene.getSynonyms()) synonyms += synonym + ",";
				if (synonyms.length() > 0) {
					synonyms = "(" + synonyms.substring(0, synonyms.length() - 1) + ")";
					synonyms = synonyms.replace("\"", "").trim();
				}
				
				System.out.println(id + "\t" + commonName + "\t" + geneAccession1 + "\t" + geneAccession2 + "\t" + synonyms);
			}
 		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	
 	// Affymetrix Probe ID's Mapped to EcoCyc ID's
 	public void createAffyProbeIDTranslationFile(String filePath, String writePath, boolean hasHeaders) {
 		// This method expects a tab delimited file at filePath containing the probeID, b-number, and gene common names delimited by '///'
 		// This method will write a tab delimited file at writePath with the probeID, b-number, and gene common names delimited by '///' found in the readIn file
 		// followed by the gene common name found in EcoCyc, b-number found in EcoCyc, and EcoCyc frameID
 		// This Method essentially checks all EcoCyc genes for the given b-number, and failing to find anything, will then try to match on a 
 		// gene common name
 		
 		File file = new File(filePath);
		BufferedReader reader = null;
		
		try {
			//K12 info
			conn.selectOrganism(organismStringABC);
			ArrayList<HashMap<String, Frame>> maps = getGeneMaps(conn);
			
			//0157:H7 info
			conn.selectOrganism(organismString0157);
			ArrayList<HashMap<String, Frame>> maps0157 = getGeneMaps(conn);
			
			//CFT073 info
			conn.selectOrganism(organismStringCFT073);
			ArrayList<HashMap<String, Frame>> mapsCFT073 = getGeneMaps(conn);
			
			//TODO synonyms map, never overwrites?
			
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			
			//TODO Headers
			String header = null;
			if (hasHeaders) {
				header = reader.readLine();
				header = header + "\tEcoCyc CommonName" + "\tEcoCyc accession" + "\tEcoCyc ID" + "\tFound?" + "\tSpecies";
				System.out.println(header);
			}
			
			while ((text = reader.readLine()) != null) {
				ArrayList<String> commonNames = new ArrayList<String>();
				
				String[] tokens = text.split("\t");
				String probeID = tokens[0].trim();
				String bNum = tokens[2].trim();
				for (String commonName : tokens[3].split("///")) commonNames.add(commonName.replace("'", "").trim());
				for (int i = 1; i < tokens[1].split("_").length; i++) commonNames.add((tokens[1].split("_")[i]).replace("'", "").trim());
				//for (String commonName : tokens[1].split("_")) commonNames.add(commonName.replace("'", "").trim());
				
				Frame gene = null;
				String species = "";
				if (tokens[1].trim() != null && tokens[1].trim().length() > 0 && tokens[1].trim().startsWith("EDL933")) {
					conn.selectOrganism(organismString0157);
					gene = mapSearch(bNum, commonNames, maps0157);
					species = "EDL933";
				} else if (tokens[1].trim() != null && tokens[1].trim().length() > 0 && tokens[1].trim().startsWith("CFT073")) {
					conn.selectOrganism(organismStringCFT073);
					gene = mapSearch(bNum, commonNames, mapsCFT073);
					species = "CFT073";
				} else if (tokens[1].trim() != null && tokens[1].trim().length() > 0 && tokens[1].trim().startsWith("MG1655")) {
					conn.selectOrganism(organismStringABC);
					gene = mapSearch(bNum, commonNames, maps);
					species = "MG1655";
				} else {
					conn.selectOrganism(organismStringABC);
					gene = mapSearch(bNum, commonNames, maps);
					if (gene != null) {
						species = "MG1655";
					} else {
						species = "undetermined";
					}
				}
				
				if (gene != null) {
					String geneAccession = "";
					if (gene.hasSlot("Accession-1")) geneAccession = gene.getSlotValue("Accession-1");
					if (geneAccession != null) {
						geneAccession = geneAccession.replace("\"", "").trim();
					}
					else {
						geneAccession = "";
					}
					
					System.out.println(probeID + "\t" + tokens[1] + "\t" + bNum + "\t" + tokens[3] + "\t" + gene.getCommonName() + "\t" + geneAccession + "\t" + gene.getLocalID() + "\tyes" + "\t" + species);
				} else System.out.println(probeID + "\t" + tokens[1] + "\t" + bNum + "\t" + tokens[3] + "\t\t\t" + "\tno" + "\t" + species);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PtoolsErrorException e) {
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
		
		//TODO Print results to file
 	}
 	
 	private Frame mapSearch(String bNum, ArrayList<String> commonNames, ArrayList<HashMap<String, Frame>> maps) {
		HashMap<String, Frame> bNumMap = maps.get(0);
		HashMap<String, Frame> commonNameMap = maps.get(1);
		HashMap<String, Frame> synonymMap = maps.get(2);
		
 		//TODO never multi-match on different search criteria?
 		
 		// Search bNumMap for an exact match on bNum
 		if (bNum != null && bNum.length() > 0) {
 			Frame gene = bNumMap.get(bNum);
 			if (gene != null) return gene;
 		}
 		
 		// Search commonNameMap for an exact match on one of the common names.
 		if (commonNames != null && !commonNames.isEmpty()) {
 			for (String commonName : commonNames) {
 				Frame gene = commonNameMap.get(commonName);
 				if (gene != null) return gene;
 			}
 		}
 		
 		// Search synonymMap for an exact match on common names
 		if (commonNames != null && !commonNames.isEmpty()) {
 			for (String commonName : commonNames) {
 				Frame gene = synonymMap.get(commonName);
 				if (gene != null) return gene;
 			}
 		}
 		
 		// Search for a bNum in the synonymMap (Common in pseudoGenes)
 		if (bNum != null && bNum.length() > 0) {
 			Frame gene = synonymMap.get(bNum);
 			if (gene != null) return gene;
 		}
 		
 		// Search for a bNum in the commonNameMap (Common in Phantom Genes)
 		if (bNum != null && bNum.length() > 0) {
 			Frame gene = commonNameMap.get(bNum);
 			if (gene != null) return gene;
 		}
 		
 		// Failed to find gene
 		return null;
 	}
	
 	private ArrayList<HashMap<String, Frame>> getGeneMaps(JavacycConnection conn) throws PtoolsErrorException {
 		HashMap<String, Frame> bNumMap = new HashMap<String, Frame>();
 		HashMap<String, Frame> commonNameMap = new HashMap<String, Frame>();
 		HashMap<String, Frame> synonymMap = new HashMap<String, Frame>();
 		
 		// Get all Genes from EcoCyc
		System.out.print("Reading Genes....");
		ArrayList<Frame> allGenes = conn.getAllGFPInstances("|All-Genes|");
		System.out.println("done");
		
		// Create HashMaps to facilitate fast searching
		System.out.print("Generating Maps....");
		for (Frame g : allGenes) {
			// Get "bNumber"
			String accession = null;
			if (g.hasSlot("Accession-1")) accession = g.getSlotValue("Accession-1");
			if (accession != null && accession.length() > 0) bNumMap.put(accession.replace("\"", "").trim(), g);
			
			// Get Common names
			if (g.getCommonName() != null && g.getCommonName().length() > 0) commonNameMap.put(g.getCommonName(), g);
			
			// Get ID
			commonNameMap.put(g.getLocalID(), g);
			
			// Get synonyms
			if (g.hasSlot("Synonyms")) {
				for (Object synonym : g.getSlotValues("Synonyms")) synonymMap.put((String)synonym, g);
			}
		}
		System.out.println("done");
		
		ArrayList<HashMap<String, Frame>> maps = new ArrayList<HashMap<String, Frame>>();
		maps.add(bNumMap);
		maps.add(commonNameMap);
		maps.add(synonymMap);
		
		return maps;
 	}
 	
 	
 	// Print all genes and locations for Erin
 	public void printGeneInfo(boolean getProteinSequenceFeatures) {
 		//TODO only get features if getProteinSequenceFeatures is true
 		//TODO print to file
		try {
			// Get all Genes from EcoCyc
			ArrayList<Frame> allGenes = conn.getAllGFPInstances("|All-Genes|");
			
			// Get all protein features
			HashMap<String, ArrayList<Frame>> proteinToFeatures = new HashMap<String, ArrayList<Frame>>();
			if (getProteinSequenceFeatures) proteinToFeatures = proteinFeatures();

			// Print Headers
			String headers = "ECOCYC-ID\tCOMMON-NAME\tCENTISOME-POSITION\tLEFT-END-POSITION\tRIGHT-END-POSITION\tTRANSCRIPTION-DIRECTION\tTYPE\tUNIFICATION-LINK\tDNA-SEQUENCE";
			System.out.println(headers);
			
			for (Frame gene : allGenes) {
				String id = gene.getLocalID();
				String commonName = gene.getCommonName();
				String cent = gene.getSlotValue("CENTISOME-POSITION");
				String left = gene.getSlotValue("LEFT-END-POSITION");
				String right = gene.getSlotValue("RIGHT-END-POSITION");
				String dir = gene.getSlotValue("TRANSCRIPTION-DIRECTION");
				String type = "";
				if (Gene.isGFPClass(conn, gene.getLocalID(), "|Pseudo-Genes|")) type = "|Pseudo-Genes|";
				else if (Gene.isGFPClass(conn, gene.getLocalID(), "|Phantom-Genes|")) type = "|Phantom-Genes|";
				else if (Gene.isGFPClass(conn, gene.getLocalID(), "|Unclassified-Genes|")) type = "|Unclassified-Genes|";
				else type = "|Genes|";
				
				String outLine = id + "\t" + commonName + "\t" + cent + "\t" + left + "\t" + right + "\t" + dir + "\t" + type;
				String outLineFeatures = "";
				
				// Parse unification links to requested databases
				ArrayList products = conn.allProductsOfGene(gene.getLocalID());
				if (products == null) products = new ArrayList();
				String unificationLink = "";
				for(Object product : products) {
					try{
						Frame productFrame = Frame.load(conn, product.toString());
					
						ArrayList dblinks = null;
						if (productFrame.hasSlot("DBLINKS")) dblinks = productFrame.getSlotValues("DBLINKS");
						for (Object dblink: dblinks) {
							ArrayList<String> dbLinkArray = ((ArrayList<String>)dblink); 
							if (dbLinkArray.get(0).contains("UNIPROT")) {
								unificationLink += dbLinkArray.get(1).replace("\"", "") + ",";
							}
						}
					} catch (PtoolsErrorException e) {
						// If product does not resolve or does not have dblinks, simply ignore this product
					}
				}
				if (unificationLink.length() > 0) outLine += "\t" + unificationLink.substring(0, unificationLink.length()-1);
				else outLine += "\t";
				
				// Format proteinSequenceFeatures
				if (getProteinSequenceFeatures) {
					products = conn.allProductsOfGene(gene.getLocalID());
					if (products == null) products = new ArrayList();
					for(Object product : products) {
//						if (products.size() > 1) System.err.println("Gene has more than 1 product: " + gene.getLocalID());
						ArrayList<Frame> features = proteinToFeatures.get(product.toString());
						if (features == null) features = new ArrayList();
						for (Frame feature : features) {
							outLineFeatures += "Product = " + product.toString() + "; Feature = " + conn.getInstanceDirectTypes(feature.getLocalID()) + "; ";
							if (feature.hasSlot("RESIDUE-NUMBER")) {
								outLineFeatures += "Residue = ";
								for (Object residue : feature.getSlotValues("RESIDUE-NUMBER")) {
									outLineFeatures += residue + ", ";
								}
								outLineFeatures = outLineFeatures.substring(0, outLineFeatures.length()-2) + "; ";
							} else {
								outLineFeatures += "Position = " + feature.getSlotValue("LEFT-END-POSITION") + "->" + feature.getSlotValue("RIGHT-END-POSITION") + "; ";
							}
							outLineFeatures += "Comment = " + feature.getComment() + "\n";
						}
					}
				}
				
				outLine += "\t" + conn.getGeneSequence(gene.getLocalID());
				System.out.println(outLine);
				if (getProteinSequenceFeatures) System.out.print(outLineFeatures);
			}
			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	public void printGeneInfoCircos() {
 		String locationData = "";
		String lableData = "";
		try {
			// Get all Genes from EcoCyc
			ArrayList<Frame> allGenes = conn.getAllGFPInstances("|All-Genes|");
			for (Frame gene : allGenes) {
				String id = gene.getLocalID();
				String commonName = gene.getCommonName();
				String cent = gene.getSlotValue("CENTISOME-POSITION");
				String left = gene.getSlotValue("LEFT-END-POSITION");
				String right = gene.getSlotValue("RIGHT-END-POSITION");
				String dir = gene.getSlotValue("TRANSCRIPTION-DIRECTION");
				String type = "";
				if (Gene.isGFPClass(conn, gene.getLocalID(), "|Pseudo-Genes|")) type = "|Pseudo-Genes|";
				else if (Gene.isGFPClass(conn, gene.getLocalID(), "|Phantom-Genes|")) type = "|Phantom-Genes|";
				else if (Gene.isGFPClass(conn, gene.getLocalID(), "|Unclassified-Genes|")) type = "|Unclassified-Genes|";
				else type = "|Genes|";
				
				if (right.equals("null") || left.equals("null")) {
					//skip gene, no info
				} else {
					locationData += "chr1 " + left + " " + right + " fill_color=red\n";
					lableData += "chr1 " + left + " " + right + " " + commonName + "\n";
				}
			}
			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(locationData);
		System.out.println();
		System.out.println("Break");
		System.out.println();
		System.out.println(lableData);
 	}
 	
 	private HashMap<String, ArrayList<Frame>> proteinFeatures() {
 		HashMap<String, ArrayList<Frame>> proteinToFeatures = new HashMap<String, ArrayList<Frame>>();
 		try {
 			ArrayList<String> ids = conn.getClassAllInstances("|Protein-Features|");
 			for (String id : ids) {
 				Frame feature = Frame.load(conn, id);
 				String protein = feature.getSlotValue("FEATURE-OF");
 				if (proteinToFeatures.containsKey(protein)) {
 					proteinToFeatures.get(protein).add(feature);
 				} else {
 					ArrayList<Frame> features = new ArrayList<Frame>();
 					features.add(feature);
 					proteinToFeatures.put(protein, features);
 				}
 			}
 		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
 		return proteinToFeatures;
 	}
 	
 	public void genomeStructureAtLocation(int point) {
 		genomeStructureAtLocation("", new int[] {point});
 	}
 	
 	public void genomeStructureAtLocation(String fileName, int[] pointList) {
 		PrintStream stream = null;
 		try {
			stream = new PrintStream(new File(fileName));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
 		
 		ArrayList<GenomicStructure> genomicStructures = new ArrayList<GenomicStructure>();
 		try {
			// Process genomic elements for location on genome
 			System.out.println("Loading ecocyc frames:");
 			ArrayList<Frame> genes = conn.getAllGFPInstances("|All-Genes|");
 			System.out.println("All-Genes: " + genes.size());
 			int count = 0;
			for (Frame f : genes) {
				try {
					Gene gene = (Gene)f;
					int leftEnd = Integer.parseInt(gene.getSlotValue("LEFT-END-POSITION"));
					int rightEnd = Integer.parseInt(gene.getSlotValue("RIGHT-END-POSITION"));
					if (leftEnd > rightEnd) System.out.println("Warning, leftend is greater than rightend");
					genomicStructures.add(new GenomicStructure(f.getCommonName(), f.getLocalID(), leftEnd, rightEnd, gene));
				} catch (Exception e){
					// Ignore elements that do not have integer left and right end positions
				}
				count++;
				if (count > genes.size()*.1) {
					System.out.print(".");
					count = 0;
				}
			}
			System.out.println("done");
			
			count = 0;
			ArrayList<Frame> terminators = conn.getAllGFPInstances("|Rho-Independent-Terminators|");
			System.out.println("Rho-Independent-Terminators: " + terminators.size());
			for (Frame element : terminators) {
				try {
					int leftEnd = Integer.parseInt(element.getSlotValue("LEFT-END-POSITION"));
					int rightEnd = Integer.parseInt(element.getSlotValue("RIGHT-END-POSITION"));
					if (leftEnd > rightEnd) System.out.println("Warning, leftend is greater than rightend");
					genomicStructures.add(new GenomicStructure(element.getCommonName(), element.getLocalID(), leftEnd, rightEnd, element));
				} catch (Exception e){
					// Ignore elements that do not have integer left and right end positions
				}
				count++;
				if (count > terminators.size()*.1) {
					System.out.print(".");
					count = 0;
				}
			}
			System.out.println("done");
//			ArrayList<Frame> bindingSites = conn.getAllGFPInstances("|DNA-Binding-Sites|");
//			for (Frame element : bindingSites) {
//				try {
//					int leftEnd = Integer.parseInt(element.getSlotValue("LEFT-END-POSITION"));
//					int rightEnd = Integer.parseInt(element.getSlotValue("RIGHT-END-POSITION"));
//					genomicStructures.add(new GenomicStructure(element.getCommonName(), element.getLocalID(), leftEnd, rightEnd, element));
//				} catch (Exception e){
//					// Ignore elements that do not have integer left and right end positions
//				}
//			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Print Headers
		System.out.println("Processing points: " + pointList.length);
		stream.println("POINT\tCOMMON-NAME\tECOCYC-ID\tLEFT-END-POSITION\tRIGHT-END-POSITION");
		
		int count = 0;
		for (int point : pointList) {
			stream.print(point);
			boolean pointFound = false;
			for (GenomicStructure genomicStructure : genomicStructures) {
				if (genomicStructure.leftEnd <= point && point <= genomicStructure.rightEnd) {
					stream.println("\t" + genomicStructure.commonName + "\t" + genomicStructure.localID + "\t" + genomicStructure.leftEnd + "\t" + genomicStructure.rightEnd);
					pointFound = true;
				}
			}
			if (!pointFound) stream.println("\t\t\t\t");
			count++;
			if (count > pointList.length*.1) {
				System.out.print(".");
				count = 0;
			}
		}
		System.out.println("done");
 	}
 	
 	public void genomeStructureAtLocation(String inFileName, String outFileName) {
 		File pointListFile = new File(inFileName);
		BufferedReader reader = null;
		ArrayList<Integer> pointList = new ArrayList<Integer>();
		
		try {
			reader = new BufferedReader(new FileReader(pointListFile));
			String text = null;
			
			while ((text = reader.readLine()) != null) {
				pointList.add(Integer.parseInt(text.split("\t")[0].trim()));
			}
			
			int[] pointListArray = new int[pointList.size()];
			for (int i = 0; i < pointList.size(); i++) {
				pointListArray[i] = pointList.get(i);
			}
			
			genomeStructureAtLocation(outFileName, pointListArray);
			
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
 	
 	
 	// Print a stoich matrix
 	public void printFlux(String[] pathwayIDs, String writePath) {
 		printStoichMatrix(pathwayIDs, writePath);
 		printPathwayReactionRegulation(pathwayIDs, writePath);
 	}
 	
 	private void printStoichMatrix(String[] pathwayIDs, String writePath) {
		//TODO write to writePath
// 		PrintStream o = null;
//		try {
//			o = new PrintStream(new File("pathway_stoich.tab"));
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
		
 		HashMap<String, HashMap<String, String>> reactionMap = getStoichMatrix(pathwayIDs);
 		
 		// Get a sorted set of all metabolite names
 		TreeSet<String> metaboliteSet = new TreeSet<String>();
 		for (String reaction : reactionMap.keySet()) metaboliteSet.addAll(reactionMap.get(reaction).keySet());

		// Print Headers
		for (String metabolite : metaboliteSet) System.out.print("\t" + metabolite);
		System.out.println();
		
 		// Print matrix
 		for (String reaction : reactionMap.keySet()) {
			System.out.print(reaction);
			
			for (String metabolite : metaboliteSet) {
				String coeff = reactionMap.get(reaction).get(metabolite);
				if (coeff != null && coeff.length() > 0) {
					System.out.print("\t" + coeff);
				} else {
					System.out.print("\t0");
				}
			}
			System.out.println();
		}
 	}
 	
 	private void printPathwayReactionRegulation(String[] pathwayIDs, String writePath) {
		//TODO write to writePath
// 		PrintStream o = null;
//		try {
//			o = new PrintStream(new File("pathway_stoich.tab"));
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
		
 		// Print the list of enzymes that participate in each reaction
 		ArrayList<String> regulatorNames = new ArrayList<String>();
 		try {
			// Print Headers
			System.out.println("Reactions" + "\tEnzymes");
			
	 		for (String pathwayID : pathwayIDs) {
	 			Frame pway = Pathway.load(conn, pathwayID);
	 			
	 			for (Reaction rxn : ((Pathway)pway).getReactions()) {
	 				System.out.print(rxn.getLocalID());
	 				
	 				if(rxn instanceof EnzymeReaction) {
	 					for(Frame f : ((EnzymeReaction)rxn).getCatalysis()) {
	 						Catalysis c = (Catalysis)f;
	 						Protein p = c.getEnzyme();
	 						regulatorNames.add(p.getLocalID());
	 						
	 						System.out.print("\t" + p.getLocalID());
	 					}
	 				} else {
	 					System.out.print("\tN/A");
	 				}
	 				
	 				System.out.println();
	 			}
	 		}
	 		
	 		System.out.println();
	 		System.out.println();
	 		
	 		ArrayList<String> slotsRequested = new ArrayList<String>();
	 		slotsRequested.add(":CREATOR");
	 		slotsRequested.add("COMMON-NAME");
	 		slotsRequested.add("OVERVIEW-NODE-SHAPE");
	 		printFrameSlotsTab(regulatorNames);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	private HashMap<String, HashMap<String, String>> getStoichMatrix(String[] pathwayIDs) {
 		HashMap<String, HashMap<String, String>> reactionMap = new HashMap<String, HashMap<String, String>>();
		try {
			for (String pathwayID : pathwayIDs) {
				Frame pway = Pathway.load(conn, pathwayID);
				
				for (Reaction rxn : ((Pathway)pway).getReactions()) {
					HashMap<String, String> map = new HashMap<String, String>();
					
					for (Frame reactant : rxn.getReactants()) {
						String coeff = reactant.annotations.get("COEFFICIENT");
						if (coeff == null) coeff = "1";
						map.put(reactant.getLocalID(), "-" + coeff.replace("(", "").replace(")", "").trim());
					}
					for (Frame product : rxn.getProducts()) {
						String coeff = product.annotations.get("COEFFICIENT");
						if (coeff == null) coeff = "1";
						map.put(product.getLocalID(), coeff.replace("(", "").replace(")", "").trim());
					}
					
					reactionMap.put(rxn.getLocalID(), map);
				}
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return reactionMap;
 	}
 	
 	
 	// General print functions
 	public void printFrameSlotsTab(ArrayList<String> frames) {
 		printFrameSlotsTab(frames, null);
 	}
 	
 	public void printFrameSlotsTab(ArrayList<String> frames, ArrayList<String> slots) {
 		// Prints out all frames in tab delimited format.  Columns are printed in the order they appear in slots.
 		// If slots is null, then all slots are printed as the ordered union of the set of all slots of all frames
 		// If slots is empty, then no slots are printed
 		
 		String listSeparator = "::";
		try {
	 		HashMap<String, HashMap<String, ArrayList<String>>> regulatorInfoMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
	 		TreeSet<String> slotNameSet = new TreeSet<String>();
	 		
	 		for (String frame : frames) {
	 			Frame regulator = Frame.load(conn, frame);
	 			HashMap<String, ArrayList<String>> infoMap = new HashMap<String, ArrayList<String>>();
	 			
	 			for (String slotName : regulator.getSlots().keySet()) {
	 				ArrayList<String> info = new ArrayList<String>();
	 				slotNameSet.add(slotName);
	 				ArrayList slotValues = regulator.getSlotValues(slotName);
	 				if (slotValues != null && slotValues.size() > 0) {
	 					for (Object slotValue : regulator.getSlotValues(slotName)) info.add(slotValue.toString());
	 				}
	 				infoMap.put(slotName, info);
	 			}
	 			regulatorInfoMap.put(frame, infoMap);
	 		}
	 		
	 		// If slots is not null, only print the requested slots.  Else, print all slots.
	 		if (slots != null) {
	 			slotNameSet.clear();
	 			slotNameSet.addAll(slots);
	 		}
	 		
	 		// Print Headers
	 		System.out.print("ECOCYC-ID\t");
	 		for (String slotName : slotNameSet) {
	 			System.out.print(slotName);
	 			if (slotName != slotNameSet.last()) System.out.print("\t");
	 		}
	 		System.out.println();
	 		
	 		// Print regulator info
	 		for (String regulator : regulatorInfoMap.keySet()) {
	 			System.out.print(regulator + "\t");
	 			for (String slotName : slotNameSet) {
	 				ArrayList<String> values = regulatorInfoMap.get(regulator).get(slotName);
	 				if (values != null && values.size() > 0) {
		 				for (int i = 0; i < values.size(); i++) {
		 					if (i < values.size()-1) System.out.print(values.get(i) + listSeparator);
		 					else System.out.print(values.get(i));
		 				}
	 				}
	 				System.out.print("\t");
	 			}
	 			System.out.println();
	 		}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	public void printAllPathwaysXGMML(String path) {
 		try {
			ArrayList<String> allPathways = conn.allPathways();
			for (String pathwayID :allPathways) {
				PrintStream o = null;
				try {
					o = new PrintStream(new File(path + "/" + pathwayID + ".xgmml"));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				((Pathway)Pathway.load(conn, pathwayID)).getNetwork().writeXGMML(o, true, false, true, true, false, false);
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
 	}
 	
 	// Helper Functions
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
 	
 	
 	// Getters and Setters
 	public JavacycConnection getConn() {
 		return conn;
 	}
 	
 	public String getCurrentConnectionString() {
 		return CurrentConnectionString;
 	}
 	
 	public String getCurrentOrganism() {
 		return CurrentOrganism;
 	}
 	
 	
 	// Internal Classes
 	public class GenomicStructure {
		public String commonName;
		public String localID;
		public int leftEnd;
		public int rightEnd;
		private Frame f;
		
		public GenomicStructure(String commonName, String localID, int leftEnd, int rightEnd, Frame f) {
			this.commonName = commonName;
			this.localID = localID;
			this.leftEnd = leftEnd;
			this.rightEnd = rightEnd;
			this.f = f;
		}
	}
 	
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