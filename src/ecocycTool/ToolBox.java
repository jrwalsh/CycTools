package ecocycTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import javacyco.*;


public class ToolBox {
	// Static Vars
	static public String connectionStringLocal =  "jrwalsh.student.iastate.edu";
	static public String connectionStringEcoServer =  "ecoserver.vrac.iastate.edu";
	static public String connectionStringVitis =  "vitis.student.iastate.edu";
	static public String organismStringK12 =  "ECOLI"; //Built-in K12 model
	static public String organismStringABC =  "ABC"; //Edit-able copy of built-in K12 model on local machine
	static public String organismStringCBiRC =  "ECOTEST"; //Edit-able copy of built-in K12 model
	static public String organismString0157 =  "ECOO157"; //0157:H7 EDL933 strain
	static public String organismStringCFT073 =  "ECOL199310"; //CRT073 strain
	static public String organismStringARA =  "ARA"; //Aracyc model
	static public int defaultPort =  4444;
	
	
	// Global Vars
	private JavacycConnection conn = null;
	private String CurrentConnectionString = connectionStringLocal;
	private int CurrentPort = defaultPort;
	private String CurrentOrganism = organismStringK12;
	
	
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
//		ArrayList<Reaction> rlist = Reaction.all(conn);
//		for (Reaction r : rlist) {
//			System.out.println(r.getSlotValue("REACTION-DIRECTION") + "\t" + r.getLocalID() + "\t" + conn.getInstanceDirectTypes(r.getLocalID()));
//		}
		
//		System.out.println(conn.callFuncString("create-named-og 'newGroup (list 'PGLUCISOM-RXN))"));
//		System.out.println(conn.callFuncString("get-og-by-name 'newGroup 'Jesse)"));
		System.out.println(conn.callFuncArray("get-og-members :id '3)"));
		
		
//		printString("/home/Jesse/Desktop/new.txt", "Hello World");
//		printGeneInfo(false);
//		chemicalSpeciesMatcher();
	}
	
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
			printFluxTopology(net, pathwayID, fileName);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	
 	// Push into Ecocyc
 	public void pushNewRegulationFile (String fileName) {
 		// Read in file.
 		// For each row, identify TF and Gene pair.
 		// Push new regulates info into the TF and the Gene
 		
		try {
			updateFrameSlot("","","");
			conn.saveKB();
		} catch (PtoolsErrorException e) {
			System.out.println("Save was unsuccessful : " + e);
		}
 	}
 	
 	public void updateFrameSlot(String frameID, String slot, String value) throws PtoolsErrorException {
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
 	
 	
 	//TODO Match up Al's new regulation information to ecocyc
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
 	public void printGeneInfo (boolean getProteinSequenceFeatures) {
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
 	
 	public void genomeStructureAtLocation (int point) {
 		genomeStructureAtLocation(new int[] {point});
 	}
 	
 	public void genomeStructureAtLocation (int[] pointList) {
 		ArrayList<GenomicStructure> genomicStructures = new ArrayList<GenomicStructure>();
 		try {
			// Process genomic elements for location on genome
 			System.out.println("Loading ecocyc frames:");
 			ArrayList<Frame> genes = conn.getAllGFPInstances("|All-Genes|");
 			System.out.println("Genes: " + genes.size());
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
			ArrayList<Frame> terminators = conn.getAllGFPInstances("|Rho-Independent-Terminators|");
			System.out.println("Terminators: " + terminators.size());
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
		System.out.println("POINT\tCOMMON-NAME\tECOCYC-ID\tLEFT-END-POSITION\tRIGHT-END-POSITION");
		
		for (int point : pointList) {
			System.out.print(point);
			for (GenomicStructure genomicStructure : genomicStructures) {
				if (genomicStructure.leftEnd <= point && point <= genomicStructure.rightEnd) {
					System.out.println("\t" + genomicStructure.commonName + "\t" + genomicStructure.localID + "\t" + genomicStructure.leftEnd + "\t" + genomicStructure.rightEnd);
				}
			}
			System.out.println();
		}
 	}
 	
 	public void genomeStructureAtLocation (String fileName) {
 		File pointListFile = new File(fileName);
		BufferedReader reader = null;
		ArrayList<Integer> pointList = new ArrayList<Integer>();
		
		try {
			reader = new BufferedReader(new FileReader(pointListFile));
			String text = null;
			
			while ((text = reader.readLine()) != null) {
				pointList.add(Integer.parseInt(text));
			}
			
			int[] pointListArray = new int[pointList.size()];
			for (int i = 0; i < pointList.size(); i++) {
				pointListArray[i] = pointList.get(i);
			}
			
			genomeStructureAtLocation(pointListArray);
			
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
 	
 	
 	//TODO Rewrite print file commands as needed for my purposes, as distinct from the standard print provided by JavaCycO
 	// Print File
	public void printFluxTopology(Network net, String pathwayID, String fileName) {
		//TODO Broken...
		FileWriter file = null;
		try	{
			file = new FileWriter(fileName, true);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		try {
			//Print Header
			//file.write("Pathway Name\tReaction Name\tReactant 1\tReactant 2\tProduct 1\tProduct 2\tEnzyme\tGene\n");

			for(Frame node : net.getNodes()) {
				if (node.inKB()) {
					if (node.isGFPClass(Reaction.GFPtype)) {
						String name = "";
						String reactant1 = "";
						String reactant2 = "";
						String product1 = "";
						String product2 = "";
						String enzyme1 = "";
						String enzyme2 = "";
						String gene1 = "";
						String gene2 = "";
						
						name = node.getCommonName();
						
						if (node.isGFPClass(EnzymeReaction.GFPtype)) {
							ArrayList<Protein> enzymes = ((EnzymeReaction)node).getEnzymes();
							if (enzymes.size() > 0) {
								enzyme1 = enzymes.get(0).getCommonName();
								ArrayList<Gene> genes = enzymes.get(0).getGenes();
								if (genes.size() > 0) {
									gene1 = genes.get(0).getCommonName();
								}
							}
						}
						
						ArrayList<Frame> reactants = ((Reaction)node).getReactants();
						if (reactants.size() > 2) {
							//TODO pick 2 most important ones
							compoundPicker(reactants, (Reaction)node);
						}
						else if (reactants.size() > 1) {
							reactant1 = reactants.get(0).getCommonName();
							reactant2 = reactants.get(1).getCommonName();
						} else if (reactants.size() == 1) {
							reactant1 = reactants.get(0).getCommonName();
						}
						
						ArrayList<Frame> products = ((Reaction)node).getProducts();
						if (products.size() > 2) {
							//TODO pick 2 most important ones
						}
						else if (products.size() > 1) {
							product1 = products.get(0).getCommonName();
							product2 = products.get(1).getCommonName();
						} else if (products.size() == 1) {
							product1 = products.get(0).getCommonName();
						}
						
						file.write(pathwayID+"\t"+name+"\t"+reactant1+"\t"+reactant2+"\t"+product1+"\t"+product2+"\t"+enzyme1+"\n");
						
//						if (node.isGFPClass(EnzymeReaction.GFPtype)) {
//							for (Protein enzyme : ((EnzymeReaction)node).getEnzymes()) {
//								o.print("Enzyme=" + enzyme.getCommonName() + "\t");
//							}
//						}
//						for (Frame reactant : ((Reaction)node).getReactants()) {
//							o.print("Reactant=" + reactant.getCommonName() + "\t");
//						}
//						for (Frame product : ((Reaction)node).getProducts()) {
//							o.print("Product=" + product.getCommonName() + "\t");
//						}
					}
				}
			}
			file.close();
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 	
 	private void compoundPicker(ArrayList<Frame> compounds, Reaction reaction) {
 		ArrayList<Compound> compoundList = new ArrayList<Compound>(); 
 		try {
 			for (Frame compound : compounds) {
				if (compound.isGFPClass("|Compound|")) compoundList.add((Compound)compound);
 			}
 			
 			//Define a Main Substrate as one in which the product of the reaction is the reactant of another reaction.
 			ArrayList<Frame> pathways = reaction.getPathways();
 			
 			
 			//Main Substrates cannot be commonly excluded substrates, such as water, ATP, O2
 			
 			//Main Substrates
 			
 			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
 	}
 	
 	private void getCommonMetabolites() {
 		
 	}
 	
	private void printStructureTab(Network net) {
//		PrintStream o = null;
//		try {
//			o = new PrintStream(new File(net.getName()+"_structure.tab"));
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
//		
//		//Print Headers
//		o.println("Source"+"\t"+"InteractionType"+"\t"+"Stoichiometry"+"\t"+"Compartment"+"\t"+"Target"+"\t"+"pathway");
//		
//		for(Edge eg : net.getEdges()) {
//			String inPathways = "";
//			String superPathways = "";
//			
//			for (String pway : eg.pathways) {
//				inPathways = inPathways + pway + "::";
//			}
//			
//			if (inPathways.isEmpty()) inPathways = null;
//			
//			o.println(eg.getSource().getLocalID()+"\t"+eg.getInfo()+"\t"+eg.getTarget().getLocalID()+"\t"+inPathways);
//		}
	}
	
	private void printNodeAttributesTab(Network net) {
//		PrintStream o = null;
//		try {
//			o = new PrintStream(new File(name+"_node_atts.tab"));
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
//		
//		//Print Headers
//		o.println("EcoCycID"+"\t"+"CommonName"+"\t"+"Comment"+"\t"+"Type"+"\t"+"pathway"+"\t"+"SuperPathways");
//		
//		for(Frame node : nodes)
//		{
//			//Get the pathways this element can be found in
//			
////			ArrayList<Frame> pways = new ArrayList<Frame>();
////			for (Frame pway : node.getPathways()) {
////				//Add each pathway to the list
////				if (!pways.contains(pway)) pways.add(pway);
////				//Add superpathways to the list
////				for (Frame sPway : pway.getPathways()) if (!pways.contains(sPway)) pways.add(sPway);
////			}
//			
////			String inPathways = "";
////			String superPathways = "";
//			
////			for (Frame pway : pways) {
////				
////				if (!(pway instanceof Pathway)) {
////					System.out.println(pway.ID);
////					break;
////				}
////				
////				if (((Pathway)pway).isSuperPathway()) {
////					superPathways = superPathways + pway.ID + "::";
////				} else {
////					inPathways = inPathways + pway.ID + "::";
////				}
////			}
//			
//			///////
//			String inPathways = "";
//			String superPathways = "";
//			
//			for (Frame pway : node.pathways) {
//				//Check for odd non-pathway frames
//				if (!(pway instanceof Pathway)) {
//					System.out.println(pway.ID);
//					break;
//				}
//				
//				if (((Pathway)pway).isSuperPathway()) {
//					superPathways = superPathways + pway.ID + "::";
//				} else {
//					inPathways = inPathways + pway.ID + "::";
//				}
//			}
//			///////
//			
//			if (inPathways.isEmpty()) inPathways = null;
//			//else inPathways = inPathways.substring(0, inPathways.length()-2);
//			
//			if (superPathways.isEmpty()) superPathways = null;
//			//else superPathways = superPathways.substring(0, superPathways.length()-2);
//			
//			o.println(node.getLocalID()+"\t"+node.getCommonName()+"\t"+node.getComment()+"\t"+node.getClass().getName().replace("javacyc.", "")+"\t"+inPathways+"\t"+superPathways);
//		}
	}
 	
 	public void printTabDelimitedNetwork(Network net) {
 		printStructureTab(net);
		printNodeAttributesTab(net);
 	}
 	
 	public void printNetworkRegulators(Network net) {
 		
 	}
 	
 	public void writeGML() {
 		PrintStream stream = null;
 		try {
 			stream = new PrintStream(new File("GML_GLY_TCA_GLYOX_BYPASS.gml"));
 			
 			Frame pway = Pathway.load(conn, "GLYCOLYSIS-TCA-GLYOX-BYPASS");
 			Network net = ((Pathway)pway).getNetwork();
 			
 			((Pathway) pway).getNetwork().writeGML(stream);//, true, false, false);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
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
}

//public static void main(String[] args)
//{
////	JavacycConnection conn = new JavacycConnection("jrwalsh.student.iastate.edu",4444);//"vitis.student.iastate.edu",4444);
//	JavacycConnection conn = new JavacycConnection("ecoserver.vrac.iastate.edu",4444);
//	conn.selectOrganism("ECOLI");
//	try
//	{
//		Frame.cache = true;
//		
//		//Network net = new Network("all_pathways_net");
//		conn.selectOrganism("ECOLI");
//		
//		//transcription-units-of-promoter
//		//Junk
//		//System.out.println(conn.getAllGFPInstances("|Promoters|").size());//GOOD
//		//System.out.println(Promoter.allFrames(conn).size());//BAD
//		
////		Frame f = Frame.load(conn, "RPOD-MONOMER");
////		for (String s : f.getSlots().keySet()) {
////			System.out.println(s + " : " + f.getSlots().get(s));
////		}
////		for (Gene g : ((Protein)f).genesRegulatedBySigmaFactor()) System.out.println(g.getLocalID());
////		if(f.isGFPClass("|Sigma-Factors|")) {
////			System.out.println(true);
////			
////			for (Frame promoter : conn.getAllGFPInstances("|Promoters|")) {
////				if (((Promoter)promoter).getSigmaFactor() != null && f.getLocalID().equals(((Promoter)promoter).getSigmaFactor().getLocalID())) {
////					for (String tu : (ArrayList<String>)((Promoter)promoter).transcriptionUnitsOfPromoter()) {
////						for (Gene g : ((TranscriptionUnit)TranscriptionUnit.load(conn, tu)).getGenes()) {
////							System.out.println(g.ID);
////						}
////					}
////				}
////			}
////			
////		} else {
////			System.out.println(false);
////		}
////		for (String s : (ArrayList<String>)f.getSlotValues("RECOGNIZED-PROMOTERS")) {
////			for (String tu : (ArrayList<String>)conn.transcriptionUnitsOfPromoter(s)) {
////				for (String g : (ArrayList<String>)conn.transcriptionUnitGenes(tu)) {
////					System.out.println(Gene.load(conn, g).getCommonName());
////				}
////			}
////		}
//		
//		
//		
//		//Frame frame = Complex.load(conn, "EG10672");
//		//System.out.println(frame.ID.length());
////		ArrayList<Frame> pways = frame.getPathways();
////		Frame f = Frame.load(conn, "phospho-narl");
////		for (String s : (ArrayList<String>)f.getSlotValues("Regulates")) {
////			Frame f2 = Frame.load(conn, s);
////			for (String s2 : (ArrayList<String>)f2.getSlotValues("REGULATED-ENTITY")) {
////				//System.out.println(f.getCommonName() + " regulates " + s + " = " + s2);
////			}
////		}
////		
////		char c1 = '-';
////		char c2 = '‑';
////		int charVal1 = c1;
////		int charVal2 = c2;
////		System.out.println(charVal1 + " : " + charVal2);
////		System.out.println("-".equals("‑"));
////		Frame f3 = Frame.load(conn, "RPOD-MONOMER");
////		
////		HashMap<String, ArrayList> ne = f3.getSlots();
////		for (String s : ne.keySet()) System.out.println(s + " : " + ne.get(s));
////		for (Gene g : ((Protein)f3).genesRegulatedByProtein()) System.out.println(g.getCommonName());
//		
//		//Test single frame
////		Frame frame = Complex.load(conn, "EG10672");
////		//Frame frame = Reaction.load(conn, "RXN0-5245");
////		ArrayList<Frame> pways = frame.getPathways();
////		System.out.println(pways.size());
//		
//		
//		
//		
//		
//		
//		//Print single pathway		
////		Frame pway = Pathway.load(conn, "GLUTDEG-PWY");
////		Network net = ((Pathway)pway).getNetwork();
////		net.loadNetworkPathwayInfo();
////		net.printTab();
//		
//		
//		//Print three pathways
////		Frame pway = Pathway.load(conn, "TRESYN-PWY");
////		Frame pway2 = Pathway.load(conn, "GLUTDEG-PWY");
////		Frame pway3 = Pathway.load(conn, "PWY0-1264");
////		Network net = ((Pathway)pway).getNetwork();
////		net.importNetwork(((Pathway)pway2).getNetwork());
////		net.importNetwork(((Pathway)pway3).getNetwork());
////		net.printTab();
//		
//		
//		//Print whole organism
//		Organism org = conn.getOrganism();
//		org.printPathwayNetwork();
//
//	}
//	catch(Exception e)
//	{
//		e.printStackTrace();
//		System.out.println("Caught a "+e.getClass().getName()+". Shutting down...");
//	}
//	finally
//	{
//		conn.close();
//	}
//	
//}

//	public void genomeStructureAtLocation (int[] pointList) {
//// 		Comparator geneComparator = new Comparator() {  
////			public int compare (Object a, Object b) {
////				try {
////					int aLeft = Integer.parseInt(((Gene)a).getSlotValue("LEFT-END-POSITION"));
////					int bLeft = Integer.parseInt(((Gene)b).getSlotValue("LEFT-END-POSITION"));
////					return compare(aLeft, bLeft);
////				} catch (Exception e) {
////					System.out.println
////				}
////			}  
////		};
// 		
// 		// Get structures that contain the left:right range
// 		try {
//			// Get all Genes from EcoCyc
//			ArrayList<Frame> allGenes = conn.getAllGFPInstances("|All-Genes|");
//			ArrayList<Frame> genes = new ArrayList<Frame>();
////			TreeSet<Frame> genes = new TreeSet<Frame>(new Comparator() {
////															public int compare (Object a, Object b) {
////																try {
////																	int aLeft = Integer.parseInt(((Gene)a).getSlotValue("LEFT-END-POSITION"));
////																	int bLeft = Integer.parseInt(((Gene)b).getSlotValue("LEFT-END-POSITION"));
////																	return compare(aLeft, bLeft);
////																} catch (Exception e) {
////																	throw new ClassCastException("");
////																}
////															}
////														});
//			
//			for (Frame gene : allGenes) {
//				try {
//					// Check if the gene has a valid right and left position, will throw exception if it doesn't
////					int geneLeft = Integer.parseInt(gene.getSlotValue("LEFT-END-POSITION"));
////					int geneRight = Integer.parseInt(gene.getSlotValue("RIGHT-END-POSITION"));
//					
//					if (Integer.parseInt(gene.getSlotValue("LEFT-END-POSITION")) >= 0 && Integer.parseInt(gene.getSlotValue("RIGHT-END-POSITION")) >= 0) {
//						genes.add(gene);
//					}
//					
////					if ((geneLeft <= left && right <= geneRight)) {
////						System.out.println("Winner!   " + gene.getCommonName() + " = " + geneLeft + ":" + geneRight);
////					}
////					if ((geneLeft <= left && right <= geneRight)) {
////						//print
////					}
//				} catch (Exception e) {
//					// Ignore gene if left or right do not contain int values.
//				}
//			}
//			
//			
//			
//		} catch (PtoolsErrorException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
// 	}




//File file = new File("/home/Jesse/Desktop/file/all");
//StringBuffer contents = new StringBuffer();
//BufferedReader reader = null;
//ArrayList<Integer> pointList = new ArrayList<Integer>();
//try {
//	reader = new BufferedReader(new FileReader(file));
//	String text = null;
//	while ((text = reader.readLine()) != null) {
//		pointList.add(Integer.parseInt(text));
//	}
//} catch (Exception e) {
//	System.out.println(e);
//}
//int[] points = new int[pointList.size()];
//for (int i = 0; i < pointList.size(); i++) {
//	points[i] = pointList.get(i);
//}
//
//genomeStructureAtLocation(points);

//
//try {
//	ArrayList<Frame> all1 = conn.getAllGFPInstances("|DNA-Binding-Sites|");
////	ArrayList<Frame> all2 = conn.getAllGFPInstances("|Rho-Independent-Terminators|");
////	ArrayList<Frame> all3 = conn.getAllGFPInstances("|Regulation|");
////	
//	all1.get(1).print();
//	all1.get(100).print();
//	all1.get(150).print();
//	all1.get(200).print();
////	Frame reg = Frame.load(conn, "TERM0-1057");
////	Frame reg2 = Frame.load(conn, "REG0-9084");
////	Frame reg3 = Frame.load(conn, "BS00102");
////	Frame reg4 = Frame.load(conn, "Abs-Center-Pos");
//////	System.out.println(all1.size());
//////	System.out.println(all2.size());
////	reg.print();
////	reg2.print();
////	reg3.print();
////	reg4.print();
//	
////	all3.get(1).print();
////	all3.get(2).print();
////	all3.get(3).print();
////	all3.get(4).print();
//} catch (PtoolsErrorException e) {
//	e.printStackTrace();
//} catch(Exception e) {
//	e.printStackTrace();
//}


// Get pathways from EcoCyc
//JavacycConnection conn2 = new JavacycConnection("ecoserver.vrac.iastate.edu",4444);
//conn2.selectOrganism("ECOTEST");
//
//try {
//	Frame f = Frame.load(conn2, "GLC-6-P");
//	f.print();
//	
//	
//} catch (PtoolsErrorException e) {
//	e.printStackTrace();
//} catch(Exception e) {
//	e.printStackTrace();
//}

//Frame newFrame = Frame.load(conn, "PC00061");
//for (Gene g : ((Complex)newFrame).genesRegulatedByProtein()) System.out.println(g.getLocalID());

//Frame test = Frame.load(conn, "CCO-PM-BAC-NEG");
//test.print();

//Frame newFrame2 = Frame.load(conn, "EG10699");
//for (Frame g : ((Gene)newFrame2).getRegulatingGenes()) System.out.println(g.getLocalID());




//String test = "(2S,3S)-2-methylcitrate";
//for (String s : test.split("[_\\-\\,\\(\\)]+")) {
//	System.out.println(s);
//}

//Frame bass = Gene.load(conn, "EG11614");
//bass.print();
//conn.getClassAllInstances("|Misc‑Features|");
//Frame fff = Frame.load(conn, "|Misc‑Features|");
//Frame f = Frame.load(conn, "|Promoters|");
//f.print();
//for (String s : conn.getKbClasses()) System.out.println(s);
//conn.getClassAllInstances("|Promoters|");
//conn.getAllGFPInstances("|Misc‑Features|");
//for (Frame ff : conn.getAllGFPInstances("|Promoters|")) System.out.println(ff.getLocalID());
//ArrayList<String> ids = conn.getClassAllInstances("OCELOT-GFP::FRAMES");
//for (String s : ids) System.out.println(s);
//for(String id : ids)
//{
//	ArrayList<String> directClasses = conn.getInstanceDirectTypes(id);
//	for(String c : directClasses)
//	{
//		System.out.println(c);
//	}
//}
//System.out.println(conn.getGeneSequence("EG11614"));
//ArrayList<String> ids = conn.getClassAllInstances("|Transmembrane-Regions|");
//for (String s : ids) {
////	System.out.println(s);
//	Frame f = Frame.load(conn, s);
//	f.print();
////	System.out.println(f.getSlotValue("FEATURE-OF"));
//	if (f.getSlotValue("FEATURE-OF").startsWith("BASS")) f.print();
//}
//Frame f = Frame.load(conn, "G0-10506");
//f.print();
//Network n = conn.getClassHierarchy(true);
//for (Frame node : n.getNodes()) node.print();
//n.printNodeAttributesTab();

//TreeSet<String> set = new TreeSet<String>();
////ArrayList<String> ids = conn.getClassAllInstances("|Protein-Segments|");
////ArrayList<String> ids = conn.getClassAllInstances("|Phosphorylation-Modifications|");
//ArrayList<String> ids = conn.getClassAllInstances("|Protein-Features|");
//for (String s : ids) {
//	int i = 0;
//	for(Object t : conn.getInstanceDirectTypes(s)) {
//		set.add(t.toString());
////		System.out.println("\t"+(String)t);
//	}
//	Frame.load(conn, s).print();
//}
//for (String s : set) System.out.println(s);


//ArrayList<String> ids = new ArrayList<String>();
//for (Object o : conn.getClassAllInstances("|Active-Peptides|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Ca-Binding-Regions|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Catalytic-Domains|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Chains|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Conserved-Regions|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|DNA-Binding-Regions|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Extrinsic-Sequence-Variants|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Intrinsic-Sequence-Variants|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Mutagenesis-Variants|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Nucleotide-Phosphate-Binding-Regions|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Propeptides|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Protein-Binding-Regions|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Repeats|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Sequence-Conflicts|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Signal-Sequences|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Transmembrane-Regions|")) ids.add(o.toString());
//for (Object o : conn.getClassAllInstances("|Zn-Finger-Regions|")) ids.add(o.toString());

//Frame f = Frame.load(conn, "EG11063");
//ArrayList products = conn.allProductsOfGene(f.getLocalID());
//if (products == null) products = new ArrayList();
//for(Object product : products) {
//	Frame p = Frame.load(conn, product.toString());
//	p.print();
//}