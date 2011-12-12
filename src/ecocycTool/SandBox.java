package ecocycTool;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;

import edu.iastate.javacyco.Compound;
import edu.iastate.javacyco.EnzymeReaction;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.Gene;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.Network;
import edu.iastate.javacyco.Promoter;
import edu.iastate.javacyco.Protein;
import edu.iastate.javacyco.PtoolsErrorException;
import edu.iastate.javacyco.Reaction;
import edu.iastate.javacyco.Regulation;
import edu.iastate.javacyco.TranscriptionUnit;

public class SandBox {
	// JavaCycO static vars
	static public String connectionStringLocal =  "jrwalsh.student.iastate.edu";
	static public String connectionStringEcoServer =  "ecoserver.vrac.iastate.edu";
	static public String connectionStringVitis =  "vitis.student.iastate.edu";
	static public String organismStringK12 =  "ECOLI"; //Built-in K12 model
	static public String organismStringCBIRC =  "CBIRC"; //CBiRC E. coli model
	static public String organismStringARA =  "ARA"; //Aracyc model
	static public int defaultPort =  4444;
	
	// Global Vars
	private JavacycConnection conn = null;
	private String CurrentConnectionString = connectionStringLocal;
	private int CurrentPort = defaultPort;
	private String CurrentOrganism = organismStringK12;
	
	
	// Constructor
	public SandBox(String connectionString, int port, String organism) {
		CurrentConnectionString = connectionString;
		CurrentPort = port;
		CurrentOrganism = organism;

		conn = new JavacycConnection(CurrentConnectionString,CurrentPort);
		conn.selectOrganism(CurrentOrganism);
	}
	
	public void tester() throws PtoolsErrorException {
		ArrayList<Frame> terminators = conn.getAllGFPInstances("|Rho-Independent-Terminators|");
		for (Frame f : terminators) f.print();
//		Network test = conn.getClassHierarchy(false);
//		test.printStructureTab();
//		int unknown = 0;
//		for (Frame gene : conn.getAllGFPInstances("|Genes|")) {
//			if (gene.getCommonName().startsWith("y")) unknown++;
//			System.out.println(gene.getComment());
//		}
//		
//		System.out.println("All-Genes : " + conn.getAllGFPInstances("|All-Genes|").size());
//		System.out.println("Genes : " + conn.getAllGFPInstances("|Genes|").size());
//		System.out.println("Pseudo-Genes : " + conn.getAllGFPInstances("|Pseudo-Genes|").size());
//		System.out.println("Phantom-Genes : " + conn.getAllGFPInstances("|Phantom-Genes|").size());
//		System.out.println("Unclassified-Genes : " + conn.getAllGFPInstances("|Unclassified-Genes|").size());
//		System.out.println("Unknown : " + unknown);
			
//		for (Frame regulator : conn.getAllGFPInstances("|Regulation-of-Transcription|")) {
//			regulator.print();
//		}
		
		
		
//		for (String geneID : list) {
//			for (Frame regulatorFrame : conn.getAllGFPInstances("|Regulation|")) {
//				Regulation regulator = (Regulation) regulatorFrame;
//				System.out.println(regulator.getSlotValue("REGULATED-ENTITY"));
//				if (regulator.getRegulatee() != null && regulator.getRegulator() != null) {
//					if (list.contains(regulator.getRegulatee().getLocalID())) {
//						//System.out.println(regulator.getRegulatee().getCommonName() + "\t" + regulator.getRegulatee().getLocalID() + "\t" + regulator.getRegulator().getCommonName() + "\t" + regulator.getRegulator().getLocalID() + "\t" + regulator.getMode());
//					}
//				}
//			}
			
//			Gene gene = (Gene) Gene.load(conn, geneID);
//			TranscriptionUnit tu = gene;
//			conn.callFuncArray("transcription-unit-regulation-frames '" + tu.getLocalID());
//			reg = tu.getRegulations();
////			for (Frame f : gene.getRegulatingGenes()) {
////				System.out.println(gene.getCommonName() + "\t" + g + "\t" + f.getCommonName() + "\t" + f.getLocalID() + "\t" + f.getSlotValue(""));
////			}
//		}
		
		
//		for (Frame gene : conn.getAllGFPInstances("|All-Genes|")) {
//			gene.print();
//		}
		
//		genesToReactionProducts();
//		regulatorsOfTFs();
//		Frame ff = new Frame(conn, "EG12116-MONOMER");
//		for (Object o : conn.callFuncArray("all-forms-of-protein '" + ff.getLocalID())) {
//			System.out.println(o.toString());
//			for (Object act : conn.directActivators(o.toString())) {
//				System.out.print("Act=" + act.toString());
//			}
//			for (Object inhib : conn.directInhibitors(o.toString())) {
//				System.out.print("Act=" + inhib.toString());
//			}
//		}
//		geneSynonyms();
//		
//		ArrayList regs = conn.getClassAllInstances("|Regulation|");
//		for (Object reg : regs) {
//			Frame f = new Frame(conn, reg.toString());
//			if (f.getSlotValue("REGULATED-ENTITY") != null && !conn.getInstanceAllTypes(f.getSlotValue("REGULATED-ENTITY")).contains("|Enzymatic-Reactions|")) {
//				
//				System.out.println(f.getSlotValue("REGULATOR") + " : " + f.getSlotValue("REGULATED-ENTITY"));
//				
////				Frame d = Frame.load(conn, f.getSlotValue("REGULATED-ENTITY"));
////				for (Object s : conn.getInstanceAllTypes(d.getLocalID())) {
////					System.out.
////				}
////				for (Frame d : (Frame.load(conn, f.getSlotValue("REGULATED-ENTITY"))).getClass()) {
////					System.out.println(d.getLocalID());
////				}
//			}
//		}
		
		
//		ArrayList<Reaction> rlist = Reaction.all(conn);
//		for (Reaction r : rlist) {
//			System.out.println(r.getSlotValue("REACTION-DIRECTION") + "\t" + r.getLocalID() + "\t" + conn.getInstanceDirectTypes(r.getLocalID()));
//			System.out.println(r.getSlotValue("CANNOT-BALANCE?") + "\t" + r.getLocalID());
//		}
		
//		System.out.println(conn.callFuncString("create-named-og 'newGroup (list 'PGLUCISOM-RXN))"));
//		System.out.println(conn.callFuncString("get-og-by-name 'newGroup 'Jesse)"));
//		System.out.println(conn.callFuncArray("get-og-members :id '3)"));
//		System.out.println(conn.callFuncArray("transported-chemicals 'ABC-56-RXN)"));
//		System.out.println(conn.callFuncArray("specific-forms-of-rxn 'EXOPOLYPHOSPHATASE-RXN)"));
		
		
//		printString("/home/Jesse/Desktop/new.txt", "Hello World");
//		printGeneInfo(false);
//		chemicalSpeciesMatcher();
		
		
//		Frame f = Frame.load(conn, "RXN0-5195");
//		f.print();
//		for (Object r : ((Reaction)f).getSlotValues("LEFT")) {
//			System.out.println((Frame.load(conn, r.toString()).annotations.get("COMPARTMENT")));
//		}
//		Frame f2 = Frame.load(conn, "ABC-8-RXN");
//		f2.print();
//		for (Object p : ((Reaction)f2).getSlotValues("LEFT")) {
//			System.out.println((Frame.load(conn, p.toString()).annotations.get()));
//			System.out.println(conn.getValueAnnot("ABC-8-RXN", "LEFT", (String)p, "COMPARTMENT"));
//		}
//		System.out.println(conn.callFuncArray("compartments-of-reaction 'RXN0-5195)"));
//		System.out.println(conn.callFuncArray("compartments-of-reaction 'ABC-8-RXN)"));
		
//		printGeneInfoCircos();
		
		
//		System.out.println(isGeneralized("ACETOIN"));
//		Frame f = Frame.load(conn, "ACETOIN");
//		Frame f = loadFrame("Acceptor");
//		System.out.println(conn.frameExists("Acceptors"));
//		System.out.println(f.getSlots().keySet().size());
//		for (String s : f.getSlots().keySet()) System.out.println(s);
//		f.print();
//		System.out.println(f.getInChi());
//		for (Object o : f.getSlotValues("CHEMICAL-FORMULA")) System.out.println(o.toString());
		
//		if (f == null) System.out.println("LOL");
//		isGeneralized("blah");
//		System.out.println(f.getLocalID());
//		System.out.println(f.getCommonName());
//		System.out.println(f.getInChi());
//		System.out.println(conn.callFuncString("coerce-to-frame 'ACETOIN"));
//		System.out.println(conn.callFuncString("get-slot-value 'ACETOIN 'INCHI"));
//		String formula = conn.callFuncString("get-slot-value 'ACETOIN 'INCHI");
		//System.out.println(formula);
		
		
		
//		reactionGeneRule("SUCCCOASYN-RXN");
//		reactionGeneRule("FUMHYDR-RXN");
//		// Reactions of gene
//		ArrayList<Frame> genes = Gene.allFrames(conn);
//		for (Frame gene : genes) {
//			for (Object reaction : conn.reactionsOfGene(gene.getLocalID())) {
//				System.out.println(gene.getLocalID() + " : " + reaction.toString());	
//			}
//		}
		
//		Frame f = new Frame(conn, "NADH-P-OR-NOP");
//		System.out.println(f.getLocalID());
//		f.print();
//		conn.getInstanceAllTypes("NADH-P-OR-NOP");
		
//		writeGML();
		
		
		
//		ArrayList<Frame> fs = conn.getAllGFPInstances(CellComponent.GFPtype);
//		for (Frame f : fs) f.print();
//		Frame frame = Frame.load(conn, GOCellularComponent.GFPtype);
//		GOTerm go = (GOTerm)frame;
//		ArrayList<OntologyTerm> fs = go.getChildren();
//		for (Frame f : fs) f.print();
//		Frame f = Frame.load(conn, "CPD-763");
//		f.print();
		
		
		
		
		
//		pushNewRegulationFile();
		
		
//		genomeStructureAtLocation("/home/Jesse/Desktop/177data/L75", "/home/Jesse/Desktop/L75Results");
//		genomeStructureAtLocation("/home/Jesse/Desktop/177data/U167", "/home/Jesse/Desktop/U167Results");
	}

	public void regulatorsOfGene() throws PtoolsErrorException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("EG10274");
		list.add("EG12606");
		list.add("EG11318");
		list.add("EG10273");
		list.add("EG11284");
		list.add("EG11528");
		list.add("EG50003");
		list.add("EG11317");
		list.add("G6105");
		list.add("G7212");
		list.add("EG10279");
		list.add("EG10024");
		list.add("EG10543");
		list.add("EG10025");
		list.add("EG50010");
		list.add("EG10031");
		list.add("G6775");
		list.add("EG10027");
		list.add("EG11172");
		list.add("EG11809");
		list.add("EG20173");
		list.add("G7288");
		list.add("EG10356");
		list.add("EG10358");
		list.add("EG10357");
		list.add("EG10756");
		
		System.out.println("Gene\tGeneID\tTF\tTFID\tTFGenes\tMode");
		
		for (String geneID : list) {
			Gene gene = (Gene) Gene.load(conn, geneID);
			for (TranscriptionUnit tu : gene.getTranscriptionUnits()) {
				try {
					for (Frame regulation : tu.getPromoter().getRegulations()) {
						Regulation regulator = (Regulation) regulation;
						String mode = regulator.getMode() ? "+" : "-";
						String geneNames = "";
						for (Frame regulatorGene : ((Protein)regulator.getRegulator()).getGenes()) {
							geneNames += regulatorGene.getCommonName() + ",";
						}
						if (geneNames.length() > 0) geneNames = geneNames.substring(0, geneNames.length()-1); 
						System.out.println(gene.getCommonName() + "\t" + gene.getLocalID() + "\t" + regulator.getRegulator().getCommonName() + "\t" + regulator.getRegulator().getLocalID() + "\t" + geneNames + "\t" + mode);
					}
				} catch (Exception e) {
					if (tu == null || tu.getPromoter() == null || tu.getPromoter().getRegulations().size() == 0) {
						// Do nothing
					} else {
						System.err.println("Unhandled exception while getting regulators of gene " + geneID);
					}
				}
			}
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
// 			stream = new PrintStream(new File("GML_GLY_TCA_GLYOX_BYPASS.gml"));
// 			
// 			Frame pway = Pathway.load(conn, "GLYCOLYSIS-TCA-GLYOX-BYPASS");
// 			Network net = ((Pathway)pway).getNetwork();
// 			
// 			((Pathway) pway).getNetwork().writeGML(stream);//, true, false, false);
 			
 			stream = new PrintStream(new File("ecocyc_v14.6.gml"));
 			Network net = conn.getNetwork();
 			
 			net.writeGML(stream, true, true, true, true, true, true);//, true, false, false);
 			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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
////		Comparator geneComparator = new Comparator() {  
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
//		// Get structures that contain the left:right range
//		try {
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
//	}




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


//Get pathways from EcoCyc
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









//private ArrayList<HashMap<String, String>> listCombinations(HashMap<String, ArrayList<String>> generalToInstancesMap) {
//if (generalToInstancesMap.keySet().size() > 1) {
//	String currentKey = generalToInstancesMap.keySet().toArray()[0].toString();
//	ArrayList<String> listOfInstances = generalToInstancesMap.remove(currentKey);
//	ArrayList<HashMap<String, String>> array = listCombinations(generalToInstancesMap);
//	ArrayList<HashMap<String, String>> returnArray = new ArrayList<HashMap<String, String>>();
//	
//	for (String instance : listOfInstances) {
//		for (HashMap<String, String> map : array) {
//			map.put(currentKey, instance);
//			return returnArray;
//		}
//	}
//	
//} else {
//	String finalKey = generalToInstancesMap.keySet().toArray()[0].toString();
//	ArrayList<String> listOfInstances = generalToInstancesMap.remove(finalKey);
//	
//	ArrayList<HashMap<String, String>> returnArray = new ArrayList<HashMap<String, String>>();
//	for (String instance : listOfInstances) {
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put(finalKey, instance);
//		returnArray.add(map);
//	}
//	return returnArray;
//}
//
//
//return new ArrayList<HashMap<String, String>>();
//}










//private boolean isReactionBalanced(ArrayList<String> reactantIDs, ArrayList<String> productIDs) {
//	// If a reactant or product has a stoichiometry greater than |1|, then it should appear in the list as many times as its stoich value
//	// This method does not interpret chemical shorthand (eg R-groups, etc).
//	// Returns true if successful, false if not.  Any errors or unreadable/missing formulas return false.
//	
//	Pattern matchElement = Pattern.compile("\\A[A-Z][a-z]?");
//	Pattern matchQuantity = Pattern.compile("\\A\\d+");
//	HashMap<String, Integer> reactantElements = new HashMap<String, Integer>();
//	HashMap<String, Integer> productElements = new HashMap<String, Integer>();
//	try {
//		for (String reactant : reactantIDs) {
//			// Special cases
//			if (reactant.equalsIgnoreCase("PROTON")) {
//				if (reactantElements.containsKey("H")) {
//					reactantElements.put("H", reactantElements.get("H") + 1);
//				} else {
//					reactantElements.put("H", 1);
//				}
//				continue;
//			}
//			
//			Compound reactantFrame = loadCompound(reactant);
//			String formula = reactantFrame.getInChi().split("/")[1];
//			
//			if (formula == null || formula.length() == 0) return false;
//			
//			while (formula.length() > 0) {
//				Matcher m = matchElement.matcher(formula);
//				String element = "";
//				Integer quantity = 1;
//				
//				//Get element
//				if (m.find()) {
//					element = formula.substring(0, m.end());
//					formula = formula.substring(m.end());
//				} else return false;
//				
//				//Get quantity
//				m = matchQuantity.matcher(formula);
//				if (m.find()) {
//					quantity = Integer.parseInt(formula.substring(0, m.end()));
//					formula = formula.substring(m.end());
//				} else quantity = 1;
//				
//				//Add to map
//				if (reactantElements.containsKey(element)) {
//					reactantElements.put(element, reactantElements.get(element) + quantity);
//				} else {
//					reactantElements.put(element, quantity);
//				}
////				System.out.println(element + " : " + quantity + " <==> " + element + " : " + reactantElements.get(element));
//			}
//		}
//		for (String product : productIDs) {
//			// Special cases
//			if (product.equalsIgnoreCase("PROTON")) {
//				if (productElements.containsKey("H")) {
//					productElements.put("H", productElements.get("H") + 1);
//				} else {
//					productElements.put("H", 1);
//				}
//				continue;
//			}
//			
//			Compound productFrame = loadCompound(product);
//			String formula = productFrame.getInChi().split("/")[1];
//			
//			if (formula == null || formula.length() == 0) return false;
//			
//			while (formula.length() > 0) {
//				Matcher m = matchElement.matcher(formula);
//				String element = "";
//				Integer quantity = 1;
//				
//				//Get element
//				if (m.find()) {
//					element = formula.substring(0, m.end());
//					formula = formula.substring(m.end());
//				} else return false;
//				
//				//Get quantity
//				m = matchQuantity.matcher(formula);
//				if (m.find()) {
//					quantity = Integer.parseInt(formula.substring(0, m.end()));
//					formula = formula.substring(m.end());
//				} else quantity = 1;
//				
//				//Add to map
//				if (productElements.containsKey(element)) {
//					productElements.put(element, productElements.get(element) + quantity);
//				} else {
//					productElements.put(element, quantity);
//				}
////				System.out.println(element + " : " + quantity + " <==> " + element + " : " + productElements.get(element));
//			}
//		}
//	} catch (PtoolsErrorException e) {
//		e.printStackTrace();
//		return false;
//	} catch (Exception e) {
//		return false;
//	}
//	
//	if (!reactantElements.keySet().containsAll(productElements.keySet()) || !productElements.keySet().containsAll(reactantElements.keySet())) return false;
//	for (String key : reactantElements.keySet()) {
//		if (reactantElements.get(key) != productElements.get(key)) return false;
//	}
//	
//	return true;
//}

















//
//
//
////EcoCyc SBML Model
//private void sbmlInteralFunctionTests(int mode) {
//	switch (mode) {
//		case 10: {
//			// Dry run of model read / modify code
//			sbml("/home/Jesse/Desktop/allReactions_with_directions.xml");
//		} break;
//		case 15: {
//			// Dry run on a small scale model
//			sbml("/home/Jesse/Desktop/glyc.xml");
//		} break;
//		case 20: {
//			// Dry run on a mini scale model
//			sbml("/home/Jesse/Desktop/glyc_test.xml");
//		} break;
//		case 30: {
//			// Check behavior of the instantiateGeneralizedReaction function
//			SBMLReader reader = new SBMLReader();
//			SBMLDocument doc  = reader.readSBML("/home/Jesse/Desktop/allReactions_with_directions.xml");
//			
//			Model model = doc.getModel();
//			org.sbml.libsbml.Reaction origReaction = model.getReaction("ABC__45__56__45__RXN");
////			org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN__45__11319");
////			org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN0__45__1842");
////			org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN0__45__3381");
////			org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN0__45__4261");
////			org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN0__45__4581");
////			org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN0__45__5128");
//			ArrayList<org.sbml.libsbml.Reaction> list = instantiateGeneralizedReaction(origReaction);
////			System.out.println(list.size());
//			for (org.sbml.libsbml.Reaction newReaction : list) {
//				System.out.println(newReaction.getId());
//				ListOfSpeciesReferences losr = newReaction.getListOfReactants();
//				for (int i = 0; i < losr.size(); i ++) {
//					System.out.println(losr.get(i).getSpecies());
//				}
//				ListOfSpeciesReferences losp = newReaction.getListOfProducts();
//				for (int i = 0; i < losp.size(); i ++) {
//					System.out.println(losp.get(i).getSpecies());
//				}
//				model.addReaction(newReaction);
////				System.out.println(newReaction.getName());
//			}
//			
//			
//			SBMLWriter writer = new SBMLWriter();
//			writer.writeSBML(model.getSBMLDocument(), "/home/Jesse/Desktop/written_SBML.xml");
//			
////			System.out.println("Done");
//		} break;
//		case 40: {
//			// Check behavior of the isReactionBalanced function
//			ArrayList<String> reacs = new ArrayList<String>();
//			ArrayList<String> prods =  new ArrayList<String>();
//			reacs.add("GLC");
//			reacs.add("GAP");
//			prods.add("GAP");
//			prods.add("GLC");
//			System.out.println(isReactionBalanced(reacs, prods));
//		} break;
//		case 50: {
//			// Check behavior of the generateInstantiatedReactions function
//			SBMLReader reader = new SBMLReader();
//			SBMLDocument doc  = reader.readSBML("/home/Jesse/Desktop/glyc_test.xml");
//			
//			Model model = doc.getModel();
//			org.sbml.libsbml.Reaction origReaction = model.getReaction("SUCCINATE__45__DEHYDROGENASE__45__UBIQUINONE__45__RXN");
//			
//			ArrayList<SpeciesReference> reactants = new ArrayList<SpeciesReference>();
//			ArrayList<SpeciesReference> products = new ArrayList<SpeciesReference>();
//			
//			SpeciesReference sr1 = new SpeciesReference(2, 1);
//			sr1.setSpecies("SUC_CCO__45__CYTOSOL");
//			sr1.setStoichiometry(5);
//			SpeciesReference sr2 = new SpeciesReference(2, 1);
//			sr2.setSpecies("CPD0__45__1464");
//			sr2.setStoichiometry(1);
//			SpeciesReference sr3 = new SpeciesReference(2, 1);
//			sr3.setSpecies("FUM_CCO__45__CYTOSOL");
//			sr3.setStoichiometry(1);
//			SpeciesReference sr4 = new SpeciesReference(2, 1);
//			sr4.setSpecies("CPD__45__9956");
//			sr4.setStoichiometry(1);
//			
//			reactants.add(sr1);
//			reactants.add(sr2);
//			products.add(sr3);
//			products.add(sr4);
//			
//			org.sbml.libsbml.Reaction newReaction = copyReaction(origReaction, origReaction.getId() + "_" + "renamed", origReaction.getName() + "_" + "renamed", reactants, products);
//			
//			ListOfSpeciesReferences losr = newReaction.getListOfReactants();
//			for (int i = 0; i < losr.size(); i ++) {
//				System.out.println(losr.get(i).getSpecies());
//			}
//			ListOfSpeciesReferences losp = newReaction.getListOfProducts();
//			for (int i = 0; i < losp.size(); i ++) {
//				System.out.println(losp.get(i).getSpecies());
//			}
//			
//			System.out.println(newReaction.getName());
//			
//			model.addReaction(newReaction);
//			ListOfReactions lor = model.getListOfReactions();
//			for (int i = 0; i < lor.size(); i ++) {
//				System.out.println(lor.get(i).getName());
//			}
//			// Write revised model.
//			SBMLWriter writer = new SBMLWriter();
//			writer.writeSBML(doc, "/home/Jesse/Desktop/written_SBML.xml");
//		} break;
//		case 60: {
//			// Check if a pathway contains a general term
//			isGeneralized("MALATE-DEHYDROGENASE-ACCEPTOR-RXN");
//			isGeneralized("SUCCINATE-DEHYDROGENASE-UBIQUINONE-RXN");
//		} break;
//		case 70: {
//			// Check behavior of the listCombinations function
//			ArrayList<ArrayList<String>> map = new ArrayList<ArrayList<String>>();
//			ArrayList<String> list1 = new ArrayList<String>();
//			list1.add("1");
//			list1.add("2");
//			list1.add("3");
//			ArrayList<String> list2 = new ArrayList<String>();
//			list2.add("4");
//			list2.add("5");
//			list2.add("6");
//			ArrayList<String> list3 = new ArrayList<String>();
//			list3.add("7");
//			list3.add("8");
//			list3.add("9");
//			map.add(list1);
//			map.add(list2);
//			map.add(list3);
//			
//			ArrayList<ArrayList<String>> result = listCombinations(map);
//			System.out.println(result.size());
//		} break;
//		case 80: {
//			// Look for conditions in comments
//			try {
//				ArrayList<Reaction> allRxns = Reaction.all(conn);
//				for (Reaction r : allRxns) {
//					if (r.getComment() != null && r.getComment().toLowerCase().contains("aerobic")) {
//						System.out.println(r.getLocalID());
//						System.out.println(r.getComment());
//					}
//				}
//			} catch (PtoolsErrorException e) {
//				e.printStackTrace();
//			}
//		} break;
//	}
//}
//
//public void sbml(String fileName) {
//	// Currently, we are removing all trna reactions and all reactions in which a protein is a reactant or substrate.
//	// Notably, Palsson includes tRNA synthesis, thioredoxin (protein), and linked disacharide murein units.
//	ArrayList<String> classToFilter = new ArrayList<String>();
//	classToFilter.add("|Polynucleotide-Reactions|");
//	classToFilter.add("|Protein-Reactions|");
//	
//	ArrayList<String> reactionsToFilter = new ArrayList<String>();
//	
//	SBMLReader reader = new SBMLReader();
//	SBMLDocument doc  = reader.readSBML(fileName);
//
//	if (doc.getNumErrors() > 0) {
//	    if (doc.getError(0).getErrorId() == libsbmlConstants.XMLFileUnreadable) System.out.println("XMLFileUnreadable error occured."); 
//	    else if (doc.getError(0).getErrorId() == libsbmlConstants.XMLFileOperationError) System.out.println("XMLFileOperationError error occured.");  
//	    else System.out.println("Error occured in document read or document contains errors.");
//	}
//	
//	Model model = doc.getModel();
//	
//	// Remove reactions that are not useful to this model
//	reactionFilter(model, classToFilter, reactionsToFilter);
//	
//	// Remove generic reactions. Include instantiated versions of the generic reactions if possible.
//	ArrayList<org.sbml.libsbml.Reaction> newReactions = removeAndInstantiateGenericReactions(model);
//	for (org.sbml.libsbml.Reaction newReaction : newReactions) {
//		model.addReaction(newReaction);
//	}
//	
//	// Write revised model.
//	SBMLWriter writer = new SBMLWriter();
//	writer.writeSBML(doc, "/home/Jesse/Desktop/written_SBML.xml");
//}
//
//public ArrayList<org.sbml.libsbml.Reaction> reactionFilter(Model model, ArrayList<String> classToFilter, ArrayList<String> reactionsToFilter) {
//	// The purpose of this function is to remove unwanted or unusable reactions from the model.
//	// Filter removes reactions by matching on the SMBL model reaction names. These names must be ecocyc frame IDs for this method to function properly.
//	// Returns reactions that were removed.
//	
//	ArrayList<String> filter = new ArrayList<String>();
//	ArrayList<String> sidsToRemove = new ArrayList<String>();
//	ArrayList<org.sbml.libsbml.Reaction> removedReactions = new ArrayList<org.sbml.libsbml.Reaction>();
//	
//	try {
//		if (classToFilter != null) {
//			for (String reactionClass : classToFilter) {
//				for (Object reaction : conn.getClassAllInstances(reactionClass)) filter.add(reaction.toString());
//			}
//		}
//		
//		if (reactionsToFilter != null) {
//			for (String reaction : reactionsToFilter) filter.add(reaction);
//		}
//	} catch (PtoolsErrorException e) {
//		e.printStackTrace();
//	}
//	
//	ListOfReactions lor = model.getListOfReactions();
//	for (int i = 0; i < lor.size() ; i++) {
//		if (filter.contains(lor.get(i).getName())) sidsToRemove.add(lor.get(i).getId());
//	}
//	for (String sid : sidsToRemove) removedReactions.add(model.removeReaction(sid));
//	
//	return removedReactions;
//}
//
//public ArrayList<org.sbml.libsbml.Reaction> removeAndInstantiateGenericReactions(Model model) {
//	ArrayList<org.sbml.libsbml.Reaction> instantiatedReactions = new ArrayList<org.sbml.libsbml.Reaction>();
//	ArrayList<String> reactionsToFilter = new ArrayList<String>();
//	
//	ListOfReactions lor = model.getListOfReactions();
//	for (int i = 0; i < lor.size(); i ++) {
//		org.sbml.libsbml.Reaction currentReaction = lor.get(i);
//		if (isGeneralized(currentReaction.getName())) {
//			for (org.sbml.libsbml.Reaction newReaction : instantiateGeneralizedReaction(currentReaction)) instantiatedReactions.add(newReaction);
//			reactionsToFilter.add(currentReaction.getName());
//		}
//	}
//	
//	reactionFilter(model, null, reactionsToFilter).size();
//	
////	System.out.println(reactionFilter(model, null, reactionsToFilter).size());
////	System.out.println(instantiatedReactions.size());
//	
//	return instantiatedReactions;
//}
//
//private ArrayList<org.sbml.libsbml.Reaction> instantiateGeneralizedReaction(org.sbml.libsbml.Reaction origReaction) {
//	// This function makes sure that the reaction has a possibility of being instantiated.  It then calls 
//	// createInstantiatedReactions to do the bulk of the work.
//	
//	ArrayList<org.sbml.libsbml.Reaction> instantiatedReactions = new ArrayList<org.sbml.libsbml.Reaction>();
//	ArrayList<String> allReactants = new ArrayList<String>();
//	ArrayList<String> allProducts = new ArrayList<String>();
//	ArrayList<String> generalizedReactants = new ArrayList<String>();
//	ArrayList<String> generalizedProducts = new ArrayList<String>();
//	ArrayList<String> reactants = new ArrayList<String>();
//	ArrayList<String> products = new ArrayList<String>();
//	
//	try {
//		// Load the original reaction
//		Reaction reaction = (Reaction)Reaction.load(conn, origReaction.getName());
//		
//		// If reaction has specific forms, then assume those forms are already in the model
//		if (conn.callFuncArray("specific-forms-of-rxn '" + origReaction.getName()).size() > 0) {
//			//TODO return these and check they are in the model, in case whole ecoli model was not the starting point
////			System.out.println("Specific Form Found");
//			return instantiatedReactions;
//		}
//		
//		// If reaction cannot be balanced then it cannot be instantiated
//		if (reaction.hasSlot("CANNOT-BALANCE?") && reaction.getSlotValue("CANNOT-BALANCE?") != null) {
//			return instantiatedReactions;
//		}
//		
//		// Get reactants and products.  Must account for direction of reaction.
//		if (reaction.getSlotValue("REACTION-DIRECTION") == null || !reaction.getSlotValue("REACTION-DIRECTION").equalsIgnoreCase("RIGHT-TO-LEFT")) {
//			allReactants = reaction.getSlotValues("LEFT");
//			allProducts = reaction.getSlotValues("RIGHT");
//		} else {
//			allProducts = reaction.getSlotValues("LEFT");
//			allReactants = reaction.getSlotValues("RIGHT");
//		}
//		
//		for (String reactant : allReactants) {
//			if (conn.getFrameType(reactant).toUpperCase().equals(":CLASS")) generalizedReactants.add(reactant);
//			else reactants.add(reactant);
//		}
//		for (String product : allProducts) {
//			if (conn.getFrameType(product).toUpperCase().equals(":CLASS")) generalizedProducts.add(product);
//			else products.add(product);
//		}
//		
//		// Make sure this reaction is a generalized reaction
//		if (generalizedReactants.size() == 0 && generalizedProducts.size() == 0) {
//			return instantiatedReactions;
//		}
//		
//		ArrayList<org.sbml.libsbml.Reaction> newReactions = generateInstantiatedReactions(origReaction, reactants, products, generalizedReactants, generalizedProducts);
//		
////		if (newReactions.size() == 0) System.err.println("Could not instantiate reaction : " + origReaction.getName());
//		
//		for (org.sbml.libsbml.Reaction newReaction : newReactions) instantiatedReactions.add(newReaction);
//	} catch (PtoolsErrorException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//	return instantiatedReactions;
//}
//
//private ArrayList<org.sbml.libsbml.Reaction> generateInstantiatedReactions(org.sbml.libsbml.Reaction origReaction, ArrayList<String> reactants, ArrayList<String> products, ArrayList<String> generalizedReactants, ArrayList<String> generalizedProducts) {
//	ArrayList<org.sbml.libsbml.Reaction> newReactions = new ArrayList<org.sbml.libsbml.Reaction>();
//	String defaultCompartment = "CCO-CYTOSOL";
//	
//	try {
//		
//		//TODO find a better way to guarantee correct reactant/product
//		Reaction reaction = (Reaction)Reaction.load(conn, origReaction.getName());
//		String reactantSlot = "";
//		String productSlot = "";
//		if (reaction.getSlotValue("REACTION-DIRECTION") == null || !reaction.getSlotValue("REACTION-DIRECTION").equalsIgnoreCase("RIGHT-TO-LEFT")) {
//			reactantSlot = "LEFT";
//			productSlot = "RIGHT";
//		} else {
//			productSlot = "LEFT";
//			reactantSlot = "RIGHT";
//		}
//		
//		//Possible compartment annotations
//		//CCO-PERI-BAC
//		//CCO-EXTRACELLULAR
//		//CCO-CYTOSOL
//		
//		// Collect unique set of general terms
//		ArrayList<String> generalizedTerms = new ArrayList<String>();
//		for (String term : generalizedReactants) {
//			if (!generalizedTerms.contains(term)) generalizedTerms.add(term);
//		}
//		for (String term : generalizedProducts) {
//			if (!generalizedTerms.contains(term)) generalizedTerms.add(term);
//		}
//		
//		// Collect instances of general terms
//		// Order of generalized terms is same as the order of term instances in the listOfTermLists
//		ArrayList<ArrayList<String>> listOfTermLists = new ArrayList<ArrayList<String>>();
//		for (String term : generalizedTerms) {
//			ArrayList<String> instancesOfGeneralTerm = new ArrayList<String>();
//			for (Object instance : conn.getClassAllInstances(term)) instancesOfGeneralTerm.add(instance.toString());
//			
//			if (instancesOfGeneralTerm.size() == 0) {
//				instancesOfGeneralTerm.add(term);
////				System.err.println("No instances of class : " + term);
////				return new ArrayList<org.sbml.libsbml.Reaction>();
//			}
//			
//			listOfTermLists.add(instancesOfGeneralTerm);
//		}
//		
//		// Generate all possible combinations of instances for the general terms
//		ArrayList<ArrayList<String>> termCombinations = listCombinations(listOfTermLists);
//		
//		// For each combination, create a new reaction for it if the reaction is elementally balanced
//		for (ArrayList<String> combinationSet : termCombinations) {
//			ArrayList<SpeciesReference> reactantReferences = new ArrayList<SpeciesReference>();
//			ArrayList<SpeciesReference> productReferences = new ArrayList<SpeciesReference>();
//			ArrayList<String> reactantBalance = new ArrayList<String>();
//			ArrayList<String> productBalance = new ArrayList<String>();
//			
//			for (String reactant : reactants) {
//				// New reactant ref
//				SpeciesReference oldReactant = new SpeciesReference(2, 1);
//				
//				String species = convertToSBMLSafe(reactant);
//				String compartment = conn.getValueAnnot(reaction.getLocalID(), reactantSlot, reactant, "COMPARTMENT");
//				if (compartment.equalsIgnoreCase("NIL")) species += "_" + convertToSBMLSafe(defaultCompartment);
//				else species += "_" + convertToSBMLSafe(compartment);
//				
////				System.out.println("Reaction=" + reaction.getLocalID() + " Species=" + species);
//				oldReactant.setSpecies(species);
//				
//				int coeficient = 1;
//				try {
//					coeficient = Integer.parseInt(conn.getValueAnnot(reaction.getLocalID(), reactantSlot, reactant, "COEFFICIENT"));
//				} catch (Exception e) {
//					coeficient = 1;
//				}
//				
//				oldReactant.setStoichiometry(coeficient);
//				reactantReferences.add(oldReactant);
//				
//				while (coeficient > 0) {
//					reactantBalance.add(reactant);
//					coeficient--;
//				}
//			}
//			for (String product :products) {
//				// New product ref
//				SpeciesReference oldProduct = new SpeciesReference(2, 1);
//				
//				String species = convertToSBMLSafe(product);
//				String compartment = conn.getValueAnnot(reaction.getLocalID(), productSlot, product, "COMPARTMENT");
//				if (compartment.equalsIgnoreCase("NIL")) species += "_" + convertToSBMLSafe(defaultCompartment);
//				else species += "_" + convertToSBMLSafe(compartment);
//				
////				System.out.println("Reaction=" + reaction.getLocalID() + " Species=" + species);
//				oldProduct.setSpecies(species);
//				
//				int coeficient = 1;
//				try {
//					coeficient = Integer.parseInt(conn.getValueAnnot(reaction.getLocalID(), reactantSlot, product, "COEFFICIENT"));
//				} catch (Exception e) {
//					coeficient = 1;
//				}
//				
//				oldProduct.setStoichiometry(coeficient);
//				productReferences.add(oldProduct);
//				
//				while (coeficient > 0) {
//					productBalance.add(product);
//					coeficient--;
//				}
//			}
//			for (String term : generalizedReactants) {
//				// New reactant ref
//				SpeciesReference newReactant = new SpeciesReference(2, 1);
//				
//				String species = convertToSBMLSafe(combinationSet.get(generalizedTerms.indexOf(term)));
//				String compartment = conn.getValueAnnot(reaction.getLocalID(), reactantSlot, term, "COMPARTMENT");
//				if (compartment.equalsIgnoreCase("NIL")) species += "_" + convertToSBMLSafe(defaultCompartment);
//				else species += "_" + convertToSBMLSafe(compartment);
//				
////				System.out.println("Reaction=" + reaction.getLocalID() + " Species=" + species);
//				newReactant.setSpecies(species);
//				
//				int coeficient = 1;
//				try {
//					coeficient = Integer.parseInt(conn.getValueAnnot(reaction.getLocalID(), reactantSlot, term, "COEFFICIENT"));
//				} catch (Exception e) {
//					coeficient = 1;
//				}
//				
//				newReactant.setStoichiometry(coeficient);
//				reactantReferences.add(newReactant);
//				
//				while (coeficient > 0) {
//					reactantBalance.add(combinationSet.get(generalizedTerms.indexOf(term)));
//					coeficient--;
//				}
//			}
//			for (String term :generalizedProducts) {
//				// New product ref
//				SpeciesReference newProduct = new SpeciesReference(2, 1);
//				
//				String species = convertToSBMLSafe(combinationSet.get(generalizedTerms.indexOf(term)));
//				String compartment = conn.getValueAnnot(reaction.getLocalID(), productSlot, term, "COMPARTMENT");
//				if (compartment.equalsIgnoreCase("NIL")) species += "_" + convertToSBMLSafe(defaultCompartment);
//				else species += "_" + convertToSBMLSafe(compartment);
//				
////				System.out.println("Reaction=" + reaction.getLocalID() + " Species=" + species);
//				newProduct.setSpecies(species);
//				
//				int coeficient = 1;
//				try {
//					coeficient = Integer.parseInt(conn.getValueAnnot(reaction.getLocalID(), reactantSlot, term, "COEFFICIENT"));
//				} catch (Exception e) {
//					coeficient = 1;
//				}
//				
//				newProduct.setStoichiometry(coeficient);
//				productReferences.add(newProduct);
//				
//				while (coeficient > 0) {
//					productBalance.add(combinationSet.get(generalizedTerms.indexOf(term)));
//					coeficient--;
//				}
//			}
//			
//			if (origReaction.getName().equals("THTOREDUCT-RXN")){
////				System.out.println("Halt");
//			}
//			
//			String nameModifier = "";
//			for (String term : combinationSet) nameModifier += term + "_";
//			if (nameModifier.endsWith("_")) nameModifier = nameModifier.substring(0, nameModifier.length()-1);
//			
//			String newID = origReaction.getId() + "_" + convertToSBMLSafe(nameModifier);
//			String newName = origReaction.getName() + "_" + nameModifier;
//			if (isReactionBalanced(reactantBalance, productBalance)) newReactions.add(copyReaction(origReaction, newID, newName, reactantReferences, productReferences));
//		}
//		
//		// TODO Special reaction types
//		// Case 1: Change of compartments occurs
//		// Case 2: Change in (n) annotation occurs
//	} catch (PtoolsErrorException e) {
//		e.printStackTrace();
//	}
//	return newReactions;
//}
//
//private org.sbml.libsbml.Reaction copyReaction(org.sbml.libsbml.Reaction origReaction, String newID, String newName, ArrayList<SpeciesReference> reactants, ArrayList<SpeciesReference> products) {
//	org.sbml.libsbml.Reaction newReaction = new org.sbml.libsbml.Reaction(origReaction);
//	newReaction.setId(newID);
//	newReaction.setName(newName);
//	newReaction.getListOfReactants().clear();
//	newReaction.getListOfProducts().clear();
//	
//	for (SpeciesReference reactant : reactants) {
//		if (newReaction.addReactant(reactant) != 0) {
//			System.err.println("Failed to add reactant to reaction.  Reaction=" + origReaction.getName() + ", Reactant=" + reactant.getSpecies());
//			return null;
//		}
//	}
//	for (SpeciesReference product : products) {
//		if (newReaction.addProduct(product) != 0) {
//			System.err.println("Failed to add product to reaction.  Reaction=" + origReaction.getName() + ", Product=" + product.getSpecies());
//			return null;
//		}
//	}
//	
//	return newReaction;
//}
//
//private ArrayList<ArrayList<String>> listCombinations(ArrayList<ArrayList<String>> generalToInstancesArray) {
//	// This function takes in a list of lists and returns every possible combination of 1 item from each sublist.
//	// Thus, if the lists [1,2,3], [4,5,6], and [7,8,9] were input, then the output would be
//	// [1,4,7], [1,4,8], [1,4,8], [1,5,7], [1,5,8], [1,5,9] ...
//	// This method was written as a way to instantiate general terms in a reaction. Each general term in a reaction has a list of possible
//	// values, and every possible combination of terms is needed.
//	// The position of the term in the output subarray is the same as the term's value array in the inputs main array.
//	if (generalToInstancesArray == null || generalToInstancesArray.size() < 1) return new ArrayList<ArrayList<String>>();
//	if (generalToInstancesArray.size() > 1) {
//		ArrayList<ArrayList<String>> resultArray = new ArrayList<ArrayList<String>>();
//		ArrayList<String> list = generalToInstancesArray.remove(0);
//		for (ArrayList<String> combinations : listCombinations(generalToInstancesArray)) {
//			for (String item : list) {
//				ArrayList<String> combinationsCopy = (ArrayList<String>)combinations.clone();
//				combinationsCopy.add(0, item);
//				resultArray.add(combinationsCopy);
//			}
//		}
//		return resultArray;
//	}
//	
//	for (String item : generalToInstancesArray.remove(0)) {
//		ArrayList<String> list = new ArrayList<String>();
//		list.add(item);
//		generalToInstancesArray.add(list);
//	}
//	return generalToInstancesArray;
//}
//
//private boolean isGeneralized(String reactionName) {
//	boolean result = false;
//	try {
//		Reaction reaction = loadReaction(reactionName);
//		ArrayList<String> reactants = reaction.getSlotValues("LEFT");
//		ArrayList<String> products = reaction.getSlotValues("RIGHT");
//		
//		for (String reactant : reactants) {
//			if (conn.getFrameType(reactant).toUpperCase().equals(":CLASS")) return true;
//		}
//		
//		for (String product : products) {
//			if (conn.getFrameType(product).toUpperCase().equals(":CLASS")) return true;
//		}
//	} catch (PtoolsErrorException e) {
//		e.printStackTrace();
//	}
//	return result;
//}
//
//private boolean isReactionBalanced(ArrayList<String> reactantIDs, ArrayList<String> productIDs) {
//	// If a reactant or product has a stoichiometry greater than |1|, then it should appear in the list as many times as its stoich value
//	// This method does not interpret chemical shorthand (eg R-groups, etc).
//	// Returns true if successful, false if not.  Any errors or unreadable/missing formulas return false.
//	
//	HashMap<String, Integer> reactantElements = new HashMap<String, Integer>();
//	HashMap<String, Integer> productElements = new HashMap<String, Integer>();
//	try {
//		for (String reactant : reactantIDs) {
//			// Special Case
//			int specialCases = 0;
//			if (reactant.equalsIgnoreCase("|Acceptor|")) specialCases = 1;
//			else if (reactant.equalsIgnoreCase("|Donor-H2|")) specialCases = 2;
//			switch (specialCases) {
//				case 1: {
//					if (reactantElements.containsKey("A")) {
//						reactantElements.put("A", reactantElements.get("A") + 1);
//					} else {
//						reactantElements.put("A", 1);
//					}
//				} break;
//				case 2: {
//					if (reactantElements.containsKey("A")) {
//						reactantElements.put("A", reactantElements.get("A") + 1);
//					} else {
//						reactantElements.put("A", 1);
//					}
//					if (reactantElements.containsKey("H")) {
//						reactantElements.put("H", reactantElements.get("H") + 2);
//					} else {
//						reactantElements.put("H", 2);
//					}
//				} break;
//			}
//			if (specialCases != 0) {
////				System.out.println("Special Case handled");
//				continue;
//			}
//			
//			// Regular Case
//			Compound reactantFrame = loadCompound(reactant);
//			
//			for (Object o : reactantFrame.getSlotValues("CHEMICAL-FORMULA")) {
//				String chemicalFormulaElement = o.toString().substring(1, o.toString().length()-1).replace(" ", "");
//				String element = chemicalFormulaElement.split(",")[0];
//				Integer quantity = Integer.parseInt(chemicalFormulaElement.split(",")[1]);
//				
//				//Add to map
//				if (reactantElements.containsKey(element)) {
//					reactantElements.put(element, reactantElements.get(element) + quantity);
//				} else {
//					reactantElements.put(element, quantity);
//				}
//			}
//		}
//		for (String product : productIDs) {
//			// Special Case
//			int specialCases = 0;
//			if (product.equalsIgnoreCase("|Acceptor|")) specialCases = 1;
//			else if (product.equalsIgnoreCase("|Donor-H2|")) specialCases = 2;
//			switch (specialCases) {
//				case 1: {
//					if (productElements.containsKey("A")) {
//						productElements.put("A", productElements.get("A") + 1);
//					} else {
//						productElements.put("A", 1);
//					}
//				} break;
//				case 2: {
//					if (productElements.containsKey("A")) {
//						productElements.put("A", productElements.get("A") + 1);
//					} else {
//						productElements.put("A", 1);
//					}
//					if (productElements.containsKey("H")) {
//						productElements.put("H", productElements.get("H") + 2);
//					} else {
//						productElements.put("H", 2);
//					}
//				} break;
//			}
//			if (specialCases != 0) {
////				System.out.println("Special Case handled");
//				continue;
//			}
//			
//			// Regular Case
//			Compound productFrame = loadCompound(product);
//			
//			for (Object o : productFrame.getSlotValues("CHEMICAL-FORMULA")) {
//				String chemicalFormulaElement = o.toString().substring(1, o.toString().length()-1).replace(" ", "");
//				String element = chemicalFormulaElement.split(",")[0];
//				Integer quantity = Integer.parseInt(chemicalFormulaElement.split(",")[1]);
//				
//				//Add to map
//				if (productElements.containsKey(element)) {
//					productElements.put(element, productElements.get(element) + quantity);
//				} else {
//					productElements.put(element, quantity);
//				}
//			}
//		}
//	} catch (PtoolsErrorException e) {
//		e.printStackTrace();
//		return false;
//	} catch (Exception e) {
//		return false;
//	}
//	
//	if (!reactantElements.keySet().containsAll(productElements.keySet()) || !productElements.keySet().containsAll(reactantElements.keySet())) return false;
//	for (String key : reactantElements.keySet()) {
////		if (key.equalsIgnoreCase("H")) {
////			if (reactantElements.get(key) - productElements.get(key) == 1 || reactantElements.get(key) - productElements.get(key) == -1) {
////				System.out.println("Save reaction with a proton.");
////			}
////		}
//		if (reactantElements.get(key) != productElements.get(key)) return false;
//	}
//	
//	return true;
//}
//
//private String convertToSBMLSafe(String input) {
//	String output = input;
//	output = output.replace("-", "__45__");
//	output = output.replace("+", "__43__");
//	output = output.replace("|", "");
//	try {
//		Integer.parseInt(output.substring(0,1));
//		output = "_" + output;
//	} catch(NumberFormatException nfe) {
//		// Do nothing
//	}
////	output = output + "_CCO__45__CYTOSOL";
//	return output;
//}
//
//private String convertFromSBMLSafe(String input) {
//	String output = input;
////	output = output.replace("__45__", "-");
////	output = output.replace("__43__", "+");
////	output = output.replace("_CCO__45__CYTOSOL", "");
//	return output;
//}
//
//private Frame loadFrame(String id) throws PtoolsErrorException {
//	Frame f = new Frame(conn, id);
//	if (f.inKB()) return f;
//	else return null;
//}
//
//private Compound loadCompound(String id) throws PtoolsErrorException {
//	Compound f = new Compound(conn, id);
//	if (f.inKB()) return f;
//	else return null;
//}
//
//private Reaction loadReaction(String id) throws PtoolsErrorException {
//	Reaction f = new Reaction(conn, id);
//	if (f.inKB()) return f;
//	else return null;
//}
