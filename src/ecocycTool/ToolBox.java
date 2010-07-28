package ecocycTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javacyco.*;


public class ToolBox {
	static private String connectionString =  "jrwalsh.student.iastate.edu";
	static private String organismString =  "ECOLI";
	static private int port =  4444;
	
 	public static HashMap<String,String> getAllPathways() {
 		HashMap<String,String> PathwayMap = new HashMap<String,String>();
 		
		// Get pathways from EcoCyc
		JavacycConnection conn = null;
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismString);
		
			ArrayList<Pathway> allPwys = null;
			try {
				allPwys = Pathway.all(conn);
			} catch (PtoolsErrorException e) {
				// TODO Auto-generated catch block
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
 	
 	public static void exportPathway(String pathwayID) {
 		//TODO Verify Pathway
 		
		// Print single pathway
 		JavacycConnection conn = null;
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismString);
			
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
			conn.selectOrganism(organismString);
			
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
			conn.selectOrganism(organismString);
		
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
 	
 	public static void createAffyProbeIDTranslationFile(String filePath, String writePath, boolean hasHeaders) {
 		// This method expects a tab delimited file at filePath containing the probeID, b-number, and gene common names delimited by '///'
 		// This method will write a tab delimited file at writePath with the probeID, b-number, and gene common names delimited by '///' found in the readIn file
 		// followed by the gene common name found in EcoCyc, b-number found in EcoCyc, and EcoCyc frameID
 		// This Method essentially checks all EcoCyc genes for the given b-number, and failing to find anything, will then try to match on a 
 		// gene common name
 		
 		File file = new File(filePath);
		BufferedReader reader = null;
		JavacycConnection conn = null;
		HashMap<String, Frame> bNumMap = new HashMap<String, Frame>();
		HashMap<String, Frame> commonNameMap = new HashMap<String, Frame>();
		HashMap<String, Frame> synonymMap = new HashMap<String, Frame>();
		
		try {
			conn = new JavacycConnection(connectionString,port);
			conn.selectOrganism(organismString);
			
			// Get all Genes from EcoCyc
			System.out.print("Reading Genes....");
			ArrayList<Frame> allGenes = conn.getAllGFPInstances(Gene.GFPtype);
			System.out.println("done");
			
			// Create HashMaps to facilitate fast searching
			System.out.print("Generating Maps....");
			for (Frame g : allGenes) {
				String accession = null;
				if (g.hasSlot("Accession-1")) accession = g.getSlotValue("Accession-1");
				if (accession != null && accession.length() > 0) bNumMap.put(accession.replace("\"", "").trim(), g);
				if (g.getCommonName() != null && g.getCommonName().length() > 0) commonNameMap.put(g.getCommonName(), g);
				if (g.hasSlot("Synonyms")) {
					for (Object synonym : g.getSlotValues("Synonyms")) synonymMap.put((String)synonym, g);
				}
			}
			System.out.println("done");
			
			//TODO synonyms map, never overwrites?
			
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			
			//TODO Headers
			String header = null;
			if (hasHeaders) {
				header = reader.readLine();
				header = header + "\tEcoCyc CommonName" + "\tEcoCyc accession" + "\tEcoCyc ID" + "\tFound?";
				System.out.println(header);
			}
			
			while ((text = reader.readLine()) != null) {
				ArrayList<String> commonNames = new ArrayList<String>();
				
				String[] tokens = text.split("\t");
				String probeID = tokens[0].trim();
				String bNum = tokens[2].trim();
				for (String commonName : tokens[3].split("///")) commonNames.add(commonName.replace("'", "").trim());
				
				Frame gene = mapSearch(bNum, commonNames, bNumMap, commonNameMap, synonymMap);
				
				if (gene != null) {
					String geneAccession = "";
					if (gene.hasSlot("Accession-1")) geneAccession = gene.getSlotValue("Accession-1");
					if (geneAccession != null) geneAccession = geneAccession.replace("\"", "").trim();
					else geneAccession = "";
					
					System.out.println(probeID + "\t" + bNum + "\t" + tokens[3] + "\t" + gene.getCommonName() + "\t" + geneAccession + "\t" + gene.getLocalID() + "\tyes");
				} else System.out.println(probeID + "\t" + bNum + "\t" + tokens[3] + "\t\t\t" + "\tno ");
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
 	
 	private static Frame mapSearch(String bNum, ArrayList<String> commonNames, HashMap<String, Frame> bNumMap, HashMap<String, Frame> commonNameMap, HashMap<String, Frame> synonymMap) {
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
 		
 		// Failed to find gene
 		return null;
 	}
 	
 	
 	public static void regulators() {
		// File Reader Code http://www.kodejava.org/examples/28.html
 		
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
			conn.selectOrganism(organismString);
			
			
			
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
	
//	private static String findRegulator(JavacycConnection conn, String ID) throws PtoolsErrorException {
////		Test test = new Test();
//		String returnString = "";
//		ArrayList<SearchResult> list = new ArrayList<SearchResult>();
//		for (Frame f : conn.search(ID, "|Proteins|")) {
//			//TODO list.add(new SearchResult(f.getLocalID(), f.getCommonName(), ((Protein)f).getSlotValue("Regulates"), ((Protein)f).getSlotValue("isComplex"), false));
//			
//			
//			System.out.println(f.getLocalID() + " : " + f.getCommonName());
//			System.out.println(((Protein)f).getSlotValue("Regulates"));
//		}
//		return returnString;
//	}
	
//	private static void regulators() throws PtoolsErrorException {
//		// File Reader Code http://www.kodejava.org/examples/28.html
//
////		File file = new File("/home/Jesse/Desktop/Predicted_Links.csv");
//		File file = new File("/home/Jesse/Desktop/test.txt");
//		StringBuffer contents = new StringBuffer();
//		BufferedReader reader = null;
//		
//		try {
//			reader = new BufferedReader(new FileReader(file));
//			String text = null;
//			
//			while ((text = reader.readLine()) != null) {
//				System.out.println(text);
//				findRegulator(conn, text);
//				System.out.println("");
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (reader != null) {
//					reader.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	private static String findRegulator(JavacycConnection conn, String ID) throws PtoolsErrorException {
////		Test test = new Test();
//		String returnString = "";
//		ArrayList<SearchResult> list = new ArrayList<SearchResult>();
//		for (Frame f : conn.search(ID, "|Proteins|")) {
//			//TODO list.add(new SearchResult(f.getLocalID(), f.getCommonName(), ((Protein)f).getSlotValue("Regulates"), ((Protein)f).getSlotValue("isComplex"), false));
//			
//			
//			System.out.println(f.getLocalID() + " : " + f.getCommonName());
//			System.out.println(((Protein)f).getSlotValue("Regulates"));
//		}
//		return returnString;
//	}
//	
//	public static class SearchResult {
//		public String ID;
//		public String CommonName;
//		public boolean hasRegulation;
//		public boolean isComplex;
//		public boolean isPredicted;
//		
//		public SearchResult(String ID, String CommonName, boolean hasRegulation, boolean isComplex, boolean isPredicted) {
//			this.ID = ID;
//			this.CommonName = CommonName;
//			this.hasRegulation = hasRegulation;
//			this.isComplex = isComplex;
//			this.isPredicted = isPredicted;
//		}
//	}
 	
}
