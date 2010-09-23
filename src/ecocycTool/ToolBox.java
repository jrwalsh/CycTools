package ecocycTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javacyco.*;
import javacyco.Network.Edge;


public class ToolBox {
	static private String connectionString =  "jrwalsh.student.iastate.edu";
	//static private String connectionString =  "ecoserver.vrac.iastate.edu";
	//static private String connectionString =  "vitis.student.iastate.edu";
	static private String organismStringK12 =  "ECOLI"; //Built-in K12 model
	static private String organismStringABC =  "ABC"; //Edit-able copy of built-in K12 model on local machine
	static private String organismStringCBiRC =  "ECOTEST"; //Edit-able copy of built-in K12 model
	static private String organismString0157 =  "ECOO157"; //0157:H7 EDL933 strain
	static private String organismStringCFT073 =  "ECOL199310"; //CRT073 strain
	static private String organismStringARA =  "ARA"; //Aracyc model
	static private int port =  4444;
	
	// Functions for the GUI interface
 	public static HashMap<String,String> getAllPathways() {
 		HashMap<String,String> PathwayMap = new HashMap<String,String>();
 		
		// Get pathways from EcoCyc
		JavacycConnection conn = null;
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismStringABC);
		
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
		finally {
			conn.close();
		}
		
		return PathwayMap;
	}
 	
 	
 	// Export a selected group of pathways to a tab file using the print commands
 	public static void exportPathway(String pathwayID) {
 		//TODO Verify Pathway
 		
		// Print single pathway
 		JavacycConnection conn = null;
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismStringABC);
			
			Frame pway = Pathway.load(conn, pathwayID);
			Network net = ((Pathway)pway).getNetwork();
			net.loadNetworkPathwayInfo();
			net.printTab();
			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally
		{
			conn.close();
		}
 	}
 	
 	public static void exportPathways(String[] pathwayIDs) {
 		//TODO Verify Pathways
 		
 		// Print multiple pathways
 		JavacycConnection conn = null;
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismStringABC);
			
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
		finally {
			conn.close();
		}
 	}
 	
 	public static void exportAllPathways() {
 		// Print all pathways
 		JavacycConnection conn = null;
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismStringABC);
			
			Organism org = conn.getOrganism();
			org.printPathwayNetwork();
			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			conn.close();
		}
 	}

 	
 	// Push into Ecocyc
 	public static void pushNewRegulationFile (String fileName) {
 		JavacycConnection conn = null;
 		
 		// Read in file.
 		// For each row, identify TF and Gene pair.
 		// Push new regulates info into the TF and the Gene
 		
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismStringABC);
			
			updateFrameSlot("","","");
			conn.saveKB();
		} catch (PtoolsErrorException e) {
			System.out.println("Save was unsuccessful : " + e);
		}
 	}
 	
 	public static void updateFrameSlot(String frameID, String slot, String value) throws PtoolsErrorException {
 		JavacycConnection conn = null;
 		Frame frame = null;
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismStringABC);
			
			frame = Frame.load(conn, frameID);
			frame.putSlotValue(slot, value);
			
			frame.commit();
		} catch (PtoolsErrorException e) {
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally	{
			conn.close();
		}
 	}
 	
 	
 	//TODO Match up Al's new regulation information to ecocyc
 	public static void regulators() {
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
		JavacycConnection conn = null;
		HashMap<String, Frame> geneCommonNameMap = new HashMap<String, Frame>();
		HashMap<String, Frame> geneSynonymMap = new HashMap<String, Frame>();
		HashMap<String, Frame> proteinCommonNameMap = new HashMap<String, Frame>();
		HashMap<String, Frame> proteinSynonymMap = new HashMap<String, Frame>();
		
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismStringABC);
			
			
			
			//TEST>>
			Frame thePathway = Frame.load(conn, "GLYCOLYSIS-TCA-GLYOX-BYPASS");
			ArrayList<Frame> genesOfPathway = ((Pathway)thePathway).getGenes();
			ArrayList<String> genesOfPathwayNames = new ArrayList<String>();
			for (Frame f : genesOfPathway) {
				genesOfPathwayNames.add(f.getLocalID());
			}
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
				if (gene != null && genesOfPathwayNames.contains(gene.getLocalID())) {
					System.out.println(tfID + "\t" + "AL_PREDICTED_REGULATION" + "\t" + "1" + "\t" + "-" + "\t" + geneID + "\t" + "null");
				}
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
				if (reader != null) {
					conn.close();
				}
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
 	
 	
 	// Affymetrix Probe ID's Mapped to EcoCyc ID's
 	public static void createAffyProbeIDTranslationFile(String filePath, String writePath, boolean hasHeaders) {
 		// This method expects a tab delimited file at filePath containing the probeID, b-number, and gene common names delimited by '///'
 		// This method will write a tab delimited file at writePath with the probeID, b-number, and gene common names delimited by '///' found in the readIn file
 		// followed by the gene common name found in EcoCyc, b-number found in EcoCyc, and EcoCyc frameID
 		// This Method essentially checks all EcoCyc genes for the given b-number, and failing to find anything, will then try to match on a 
 		// gene common name
 		
 		File file = new File(filePath);
		BufferedReader reader = null;
		JavacycConnection conn = null;
		
		try {
			conn = new JavacycConnection(connectionString,port);

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
				if (reader != null) {
					conn.close();
				}
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
 	
 	private static Frame mapSearch(String bNum, ArrayList<String> commonNames, ArrayList<HashMap<String, Frame>> maps) {
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
	
 	private static ArrayList<HashMap<String, Frame>> getGeneMaps(JavacycConnection conn) throws PtoolsErrorException {
 		HashMap<String, Frame> bNumMap = new HashMap<String, Frame>();
 		HashMap<String, Frame> commonNameMap = new HashMap<String, Frame>();
 		HashMap<String, Frame> synonymMap = new HashMap<String, Frame>();
 		
 		// Get all Genes from EcoCyc
		System.out.print("Reading Genes....");
		ArrayList<Frame> allGenes = conn.getAllGFPInstances("|All-Genes|");
		//for(Frame f : conn.getAllGFPInstances("|Pseudo-Genes|")) allGenes.add(f);
		//for(Frame f : conn.getAllGFPInstances("Unclassified-Genes")) allGenes.add(f);
		//for(Frame f : conn.getAllGFPInstances("Unclassified-Genes")) allGenes.add(f);
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
 	public static void printGeneInfo () {
		JavacycConnection conn = null;
		
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismStringABC);
			
			// Get all Genes from EcoCyc
			ArrayList<Frame> allGenes = conn.getAllGFPInstances("|All-Genes|");
			
			// Print Headers
			System.out.println("ECOCYC-ID\tCOMMON-NAME\tCENTISOME-POSITION\tLEFT-END-POSITION\tRIGHT-END-POSITION\tTRANSCRIPTION-DIRECTION\tTYPE");
			
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
				
				System.out.println(id + "\t" + commonName + "\t" + cent + "\t" + left + "\t" + right + "\t" + dir + "\t" + type);
			}
			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			conn.close();
		}
 	}
 	
 	
 	// Print a stoich matrix
 	public static void printFlux(String[] pathwayIDs, String writePath) {
 		printStoichMatrix(pathwayIDs, writePath);
 		printPathwayReactionRegulation(pathwayIDs, writePath);
 	}
 	
 	private static void printStoichMatrix(String[] pathwayIDs, String writePath) {
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
 	
 	private static void printPathwayReactionRegulation(String[] pathwayIDs, String writePath) {
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
 		JavacycConnection conn = null;
 		ArrayList<String> regulatorNames = new ArrayList<String>();
 		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismStringABC);

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
		finally {
			conn.close();
		}
		
 	}
 	
 	private static HashMap<String, HashMap<String, String>> getStoichMatrix(String[] pathwayIDs) {
 		HashMap<String, HashMap<String, String>> reactionMap = new HashMap<String, HashMap<String, String>>();
 		JavacycConnection conn = null;
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismStringABC);
			
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
		finally {
			conn.close();
		}
		
		return reactionMap;
 	}
 	
 	
 	
 	// General print functions
 	public static void printFrameSlotsTab(ArrayList<String> frames) {
 		printFrameSlotsTab(frames, null);
 	}
 	
 	public static void printFrameSlotsTab(ArrayList<String> frames, ArrayList<String> slots) {
 		// Prints out all frames in tab delimited format.  Columns are printed in the order they appear in slots.
 		// If slots is null, then all slots are printed as the ordered union of the set of all slots of all frames
 		// If slots is empty, then no slots are printed
 		
 		String listSeparator = "::";
 		JavacycConnection conn = null;
		
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismStringABC);
			
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
		finally {
			conn.close();
		}
 	}
 	
 	//TODO Rewrite print file commands as needed for my purposes, as distinct from the standard print provided by JavaCycO
 	// Print File
	private static void printStructureTab(Network net) {
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
	
	private static void printNodeAttributesTab(Network net) {
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
 	
 	public static void printTabDelimitedNetwork(Network net) {
 		printStructureTab(net);
		printNodeAttributesTab(net);
 	}
 	
 	public static void printNetworkRegulators(Network net) {
 		
 	}
 	
}