package ecocycTool;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.sbml.libsbml.*;

import edu.iastate.javacyco.Compound;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;
import edu.iastate.javacyco.Reaction;

/**
 * CycModeler is a class that is designed to generate a stoichiometric model in SBML output from a BioCyc database.
 * This class is built around the JavaCycO class created by John Van Hemert.
 * 
 * Current operations planned for two modes.  Under development is the whole scale model mode.  In this mode, the entire
 * EcoCyc reaction network is ported to SBML, at which point it is read in by this class and manipulated into a feasible
 * stoichiometric model.  This is specific to ecocyc's current output, therefore it is assumed that the boundary reactions
 * desired will be all metabolites in the extracellular region. 
 * 
 * Future development will include
 * 
 * 
 * @author Jesse Walsh
 *
 */
public class CycModeler {
	// Global variables
	private JavacycConnection conn = null;
	private String OutputDirectory = "";
	private Model model = null;
	private String defaultCompartment = "CCO-CYTOSOL";
	private int DefaultSBMLLevel = 2;
	private int DefaultSBMLVersion = 1;
	private HashMap<String, String> compartmentAbrevs = new HashMap<String, String>();
	private String speciesPrefix = "M";
	private String reactionPrefix = "R";
	
	// Constructor
	public CycModeler (String connectionString, int port, String organism) {
		String CurrentConnectionString = connectionString;
		int CurrentPort = port;
		String CurrentOrganism = organism;

		conn = new JavacycConnection(CurrentConnectionString,CurrentPort);
		conn.selectOrganism(CurrentOrganism);
		
		setDefaults();
	}
	
	public CycModeler (JavacycConnection connection) {
		conn = connection;
		setDefaults();
	}
	
	
	// Testing
	public void sbmlInteralFunctionTests(int mode) {
		switch (mode) {
			case 10: {
				// Dry run of model read / modify code
				runSBMLFixModelScript("/home/Jesse/Desktop/ecocyc.xml");
//				runSBMLFixModelScript("/home/Jesse/Desktop/allReactions_with_directions.xml");
			} break;
			case 15: {
				// Dry run on a small scale model
				runSBMLFixModelScript("/home/Jesse/Desktop/glyc.xml");
			} break;
			case 20: {
				// Dry run on a mini scale model
				runSBMLFixModelScript("/home/Jesse/Desktop/glyc_test.xml");
			} break;
			case 30: {
				// Check behavior of the instantiateGeneralizedReaction function
				SBMLReader reader = new SBMLReader();
				SBMLDocument doc  = reader.readSBML("/home/Jesse/Desktop/allReactions_with_directions.xml");
				
				Model model = doc.getModel();
				org.sbml.libsbml.Reaction origReaction = model.getReaction("ABC__45__56__45__RXN");
//				org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN__45__11319");
//				org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN0__45__1842");
//				org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN0__45__3381");
//				org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN0__45__4261");
//				org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN0__45__4581");
//				org.sbml.libsbml.Reaction origReaction = model.getReaction("RXN0__45__5128");
				ArrayList<org.sbml.libsbml.Reaction> list = instantiateGeneralizedReaction(origReaction).get(0);
//				System.out.println(list.size());
				for (org.sbml.libsbml.Reaction newReaction : list) {
					System.out.println(newReaction.getId());
					ListOfSpeciesReferences losr = newReaction.getListOfReactants();
					for (int i = 0; i < losr.size(); i ++) {
						System.out.println(losr.get(i).getSpecies());
					}
					ListOfSpeciesReferences losp = newReaction.getListOfProducts();
					for (int i = 0; i < losp.size(); i ++) {
						System.out.println(losp.get(i).getSpecies());
					}
					model.addReaction(newReaction);
//					System.out.println(newReaction.getName());
				}
				
				
				SBMLWriter writer = new SBMLWriter();
				writer.writeSBML(model.getSBMLDocument(), "/home/Jesse/Desktop/written_SBML.xml");
				
//				System.out.println("Done");
			} break;
			case 40: {
				// Check behavior of the isReactionBalanced function
				ArrayList<String> reacs = new ArrayList<String>();
				ArrayList<String> prods =  new ArrayList<String>();
				reacs.add("GLC");
				reacs.add("GAP");
				prods.add("GAP");
				prods.add("GLC");
				System.out.println(isReactionBalanced(reacs, prods));
			} break;
			case 50: {
				// Check behavior of the generateInstantiatedReactions function
				SBMLReader reader = new SBMLReader();
				SBMLDocument doc  = reader.readSBML("/home/Jesse/Desktop/glyc_test.xml");
				
				Model model = doc.getModel();
				org.sbml.libsbml.Reaction origReaction = model.getReaction("SUCCINATE__45__DEHYDROGENASE__45__UBIQUINONE__45__RXN");
				
				ArrayList<SpeciesReference> reactants = new ArrayList<SpeciesReference>();
				ArrayList<SpeciesReference> products = new ArrayList<SpeciesReference>();
				
				SpeciesReference sr1 = new SpeciesReference(2, 1);
				sr1.setSpecies("SUC_CCO__45__CYTOSOL");
				sr1.setStoichiometry(5);
				SpeciesReference sr2 = new SpeciesReference(2, 1);
				sr2.setSpecies("CPD0__45__1464");
				sr2.setStoichiometry(1);
				SpeciesReference sr3 = new SpeciesReference(2, 1);
				sr3.setSpecies("FUM_CCO__45__CYTOSOL");
				sr3.setStoichiometry(1);
				SpeciesReference sr4 = new SpeciesReference(2, 1);
				sr4.setSpecies("CPD__45__9956");
				sr4.setStoichiometry(1);
				
				reactants.add(sr1);
				reactants.add(sr2);
				products.add(sr3);
				products.add(sr4);
				
				org.sbml.libsbml.Reaction newReaction = copyReaction(origReaction, origReaction.getId() + "_" + "renamed", origReaction.getName() + "_" + "renamed", reactants, products);
				
				ListOfSpeciesReferences losr = newReaction.getListOfReactants();
				for (int i = 0; i < losr.size(); i ++) {
					System.out.println(losr.get(i).getSpecies());
				}
				ListOfSpeciesReferences losp = newReaction.getListOfProducts();
				for (int i = 0; i < losp.size(); i ++) {
					System.out.println(losp.get(i).getSpecies());
				}
				
				System.out.println(newReaction.getName());
				
				model.addReaction(newReaction);
				ListOfReactions lor = model.getListOfReactions();
				for (int i = 0; i < lor.size(); i ++) {
					System.out.println(lor.get(i).getName());
				}
				// Write revised model.
				SBMLWriter writer = new SBMLWriter();
				writer.writeSBML(doc, "/home/Jesse/Desktop/written_SBML.xml");
			} break;
			case 60: {
				// Check if a pathway contains a general term
				isGeneralizedReaction("MALATE-DEHYDROGENASE-ACCEPTOR-RXN");
				isGeneralizedReaction("SUCCINATE-DEHYDROGENASE-UBIQUINONE-RXN");
			} break;
			case 70: {
				// Check behavior of the listCombinations function
				ArrayList<ArrayList<String>> map = new ArrayList<ArrayList<String>>();
				ArrayList<String> list1 = new ArrayList<String>();
				list1.add("1");
				list1.add("2");
				list1.add("3");
				ArrayList<String> list2 = new ArrayList<String>();
				list2.add("4");
				list2.add("5");
				list2.add("6");
				ArrayList<String> list3 = new ArrayList<String>();
				list3.add("7");
				list3.add("8");
				list3.add("9");
				map.add(list1);
				map.add(list2);
				map.add(list3);
				
				ArrayList<ArrayList<String>> result = listCombinations(map);
				System.out.println(result.size());
			} break;
			case 80: {
				// Look for conditions in comments
				try {
					ArrayList<Reaction> allRxns = Reaction.all(conn);
					for (Reaction r : allRxns) {
						if (r.getComment() != null && r.getComment().toLowerCase().contains("aerobic")) {
							System.out.println(r.getLocalID());
							System.out.println(r.getComment());
						}
					}
				} catch (PtoolsErrorException e) {
					e.printStackTrace();
				}
			} break;
			case 100: {
				// Try to load all entities in the model from ecocyc
				SBMLDocument doc = readSBML("/home/Jesse/Desktop/ecocyc.xml");
				Model model = doc.getModel();
				loadMetabolites(model);
				loadReactions(model);
			} break;
			case 200: {
				createGenomeScaleModelFromEcoCyc();
			} break;
			case 210: {
				try {
					System.out.println(instantiateGenericReaction(loadReaction("ABC-56-RXN")).size());
//					System.out.println(instantiateGenericReaction(loadReaction("RXN-11319")).size());
//					System.out.println(instantiateGenericReaction(loadReaction("RXN0-1842")).size());
//					System.out.println(instantiateGenericReaction(loadReaction("RXN0-3381")).size());
//					System.out.println(instantiateGenericReaction(loadReaction("RXN0-4261")).size());
//					System.out.println(instantiateGenericReaction(loadReaction("RXN0-4581")).size());
//					System.out.println(instantiateGenericReaction(loadReaction("RXN0-5128")).size());
				} catch (PtoolsErrorException e) {
					e.printStackTrace();
				}
			} break;
			case 220: {
				try {
					ArrayList<ReactionInstance> rxns = new ArrayList<ReactionInstance>();
					ReactionInstance rxn = new ReactionInstance(null, loadReaction("PGLUCISOM-RXN"), "NamedReaction", false, new ArrayList<Metabolite>(), new ArrayList<Metabolite>());
					rxn.reactants.add(new Metabolite(loadFrame("GLC-6-P"), defaultCompartment, 1));
					rxn.products.add(new Metabolite(loadFrame("FRUCTOSE-6P"), defaultCompartment, 1));
					rxns.add(rxn);
					SBMLDocument doc = createBlankSBMLDocument("Testing", 2, 1);
					doc = generateSBMLModel(doc, rxns);
					SBMLWriter writer = new SBMLWriter();
					writer.writeSBML(doc, OutputDirectory + "testing_SBML.xml");
				} catch (PtoolsErrorException e) {
					e.printStackTrace();
				}
			} break;
		}
	}
	
	
	// Methods
	public void runSBMLFixModelScript(String fileName) {
		// Currently, we are removing all trna reactions and all reactions in which a protein is a reactant or substrate.
		// Notably, Palsson includes tRNA synthesis, thioredoxin (protein), and linked disacharide murein units.
		ArrayList<String> classToFilter = new ArrayList<String>();
		classToFilter.add("|Polynucleotide-Reactions|");
		classToFilter.add("|Protein-Reactions|");
		
		ArrayList<String> reactionsToFilter = new ArrayList<String>();
		
		System.out.println("Reading File...");
		SBMLDocument doc = readSBML(fileName);
		
		// Remove reactions that are not useful to this model
		System.out.println("Filtering Reactions...");
		ArrayList<org.sbml.libsbml.Reaction> removedReactions = reactionFilter(classToFilter, reactionsToFilter);
		
		// Remove generic reactions. Include instantiated versions of the generic reactions if possible.
		System.out.println("Instantiating Generic Reactions...");
		instantiateGenericSpecies();
		System.out.println("Instantiating Generic Reactions...");
		ArrayList<ArrayList<org.sbml.libsbml.Reaction>> resultSet = removeAndInstantiateGenericReactions();
		for (org.sbml.libsbml.Reaction newReaction : resultSet.get(0)) model.addReaction(newReaction);
		
		// Clean metabolites list
		//TODO
		// Normalize boundaries
		//TODO convert existing into cytosol, periplasm, and extracellular
		// Add boundary reactions
		addBoundaryReactions("CCO-EXTRACELLULAR", "boundary");
		// Check duplicate reactions
		//TODO
		// Enrich model
		//TODO
		
		// Write revised model.
		System.out.println("Writing Output...");
		SBMLWriter writer = new SBMLWriter();
		writer.writeSBML(doc, OutputDirectory + "written_SBML.xml");
		
		printListOfReactions("removedreactions.txt", removedReactions);
		printListOfReactions("instantiatedReactions.txt", resultSet.get(0));
		printListOfReactions("failedInstantiationReactions", resultSet.get(1));
		printListOfReactions("genericReactions.txt", resultSet.get(2));
		
		System.out.println("Done!");
	}
	
	public SBMLDocument readSBML(String fileName) {
		SBMLReader reader = new SBMLReader();
		SBMLDocument doc  = reader.readSBML(fileName);

		if (doc.getNumErrors() > 0) {
		    if (doc.getError(0).getErrorId() == libsbmlConstants.XMLFileUnreadable) System.out.println("XMLFileUnreadable error occured."); 
		    else if (doc.getError(0).getErrorId() == libsbmlConstants.XMLFileOperationError) System.out.println("XMLFileOperationError error occured.");  
		    else System.out.println("Error occured in document read or document contains errors.");
		}
		
		model = doc.getModel();
		
		return doc;
	}
	
	public void createSBMLModel() {
		//TODO
	}
	
	public ArrayList<org.sbml.libsbml.Reaction> reactionFilter(ArrayList<String> classToFilter, ArrayList<String> reactionsToFilter) {
		//TODO model is null check
		
		// The purpose of this function is to remove unwanted or unusable reactions from the model.
		// Filter removes reactions by matching on the SMBL model reaction names. These names must be ecocyc frame IDs for this method to function properly.
		// Returns reactions that were removed.
		
		ArrayList<String> filter = new ArrayList<String>();
		ArrayList<String> sidsToRemove = new ArrayList<String>();
		ArrayList<org.sbml.libsbml.Reaction> removedReactions = new ArrayList<org.sbml.libsbml.Reaction>();
		
		try {
			if (classToFilter != null) {
				for (String reactionClass : classToFilter) {
					for (Object reaction : conn.getClassAllInstances(reactionClass)) filter.add(reaction.toString());
				}
			}
			
			if (reactionsToFilter != null) {
				for (String reaction : reactionsToFilter) filter.add(reaction);
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		
		ListOfReactions lor = model.getListOfReactions();
		for (int i = 0; i < lor.size() ; i++) {
			if (filter.contains(lor.get(i).getName())) sidsToRemove.add(lor.get(i).getId());
		}
		for (String sid : sidsToRemove) removedReactions.add(model.removeReaction(sid));
		
		return removedReactions;
	}
	
	/**
	 * 
	 * @return
	 * List of 3 lists:
	 * 1) instantiatedReactions
	 * 2) failedInstantiationReactions
	 * 3) removedGenericReactions
	 */
	public ArrayList<ArrayList<org.sbml.libsbml.Reaction>> removeAndInstantiateGenericReactions() {
		//TODO model is null check
		ArrayList<org.sbml.libsbml.Reaction> instantiatedReactions = new ArrayList<org.sbml.libsbml.Reaction>();
		ArrayList<org.sbml.libsbml.Reaction> failedInstantiationReactions = new ArrayList<org.sbml.libsbml.Reaction>();
		ArrayList<org.sbml.libsbml.Reaction> removedGenericReactions = new ArrayList<org.sbml.libsbml.Reaction>();
		ArrayList<String> reactionsToFilter = new ArrayList<String>();
		
		ListOfReactions lor = model.getListOfReactions();
		for (int i = 0; i < lor.size(); i ++) {
			org.sbml.libsbml.Reaction currentReaction = lor.get(i);
			if (isGeneralizedReaction(currentReaction.getName())) {
				ArrayList<ArrayList<org.sbml.libsbml.Reaction>> resultSet = instantiateGeneralizedReaction(currentReaction);
				for (org.sbml.libsbml.Reaction newReaction : resultSet.get(0)) instantiatedReactions.add(newReaction);
				for (org.sbml.libsbml.Reaction newReaction : resultSet.get(1)) failedInstantiationReactions.add(newReaction);
				removedGenericReactions.add(currentReaction);
				reactionsToFilter.add(currentReaction.getName());
			}
		}
		
		reactionFilter(null, reactionsToFilter);
		
		ArrayList<ArrayList<org.sbml.libsbml.Reaction>> resultSet = new ArrayList<ArrayList<org.sbml.libsbml.Reaction>>();
		resultSet.add(instantiatedReactions);
		resultSet.add(failedInstantiationReactions);
		resultSet.add(removedGenericReactions);
		return resultSet;
	}
	
	/**
	 * 
	 * @param origReaction
	 * @return
	 * List of 2 lists:
	 * 1) instantiatedReactions
	 * 2) failedInstantiationReactions
	 */
	private ArrayList<ArrayList<org.sbml.libsbml.Reaction>> instantiateGeneralizedReaction(org.sbml.libsbml.Reaction origReaction) {
		// This function makes sure that the reaction has a possibility of being instantiated.  It then calls 
		// createInstantiatedReactions to do the bulk of the work.
		
		ArrayList<org.sbml.libsbml.Reaction> instantiatedReactions = new ArrayList<org.sbml.libsbml.Reaction>();
		ArrayList<org.sbml.libsbml.Reaction> failedInstantiationReactions = new ArrayList<org.sbml.libsbml.Reaction>();
		ArrayList<String> allReactants = new ArrayList<String>();
		ArrayList<String> allProducts = new ArrayList<String>();
		ArrayList<String> generalizedReactants = new ArrayList<String>();
		ArrayList<String> generalizedProducts = new ArrayList<String>();
		ArrayList<String> reactants = new ArrayList<String>();
		ArrayList<String> products = new ArrayList<String>();
		
		try {
			// Load the original reaction
			Reaction reaction = (Reaction)Reaction.load(conn, origReaction.getName());
			
			// If reaction has specific forms, then assume those forms are already in the model
			if (conn.specificFormsOfReaction(origReaction.getName()).size() > 0) {
				//TODO return these and check they are in the model, in case whole ecoli model was not the starting point
//				System.out.println("Specific Form Found");
				ArrayList<ArrayList<org.sbml.libsbml.Reaction>> resultSet = new ArrayList<ArrayList<org.sbml.libsbml.Reaction>>();
				resultSet.add(instantiatedReactions);
				resultSet.add(failedInstantiationReactions);
				return resultSet;
			}
			
			// If reaction cannot be balanced then it cannot be instantiated
			if (reaction.hasSlot("CANNOT-BALANCE?") && reaction.getSlotValue("CANNOT-BALANCE?") != null) {
				ArrayList<ArrayList<org.sbml.libsbml.Reaction>> resultSet = new ArrayList<ArrayList<org.sbml.libsbml.Reaction>>();
				resultSet.add(instantiatedReactions);
				resultSet.add(failedInstantiationReactions);
				return resultSet;
			}
			
			// Get reactants and products.  Must account for direction of reaction.
			if (reaction.getSlotValue("REACTION-DIRECTION") == null || !reaction.getSlotValue("REACTION-DIRECTION").equalsIgnoreCase("RIGHT-TO-LEFT")) {
				allReactants = reaction.getSlotValues("LEFT");
				allProducts = reaction.getSlotValues("RIGHT");
			} else {
				allProducts = reaction.getSlotValues("LEFT");
				allReactants = reaction.getSlotValues("RIGHT");
			}
			
			for (String reactant : allReactants) {
				if (conn.getFrameType(reactant).toUpperCase().equals(":CLASS")) generalizedReactants.add(reactant);
				else reactants.add(reactant);
			}
			for (String product : allProducts) {
				if (conn.getFrameType(product).toUpperCase().equals(":CLASS")) generalizedProducts.add(product);
				else products.add(product);
			}
			
			// Make sure this reaction is a generalized reaction
			if (generalizedReactants.size() == 0 && generalizedProducts.size() == 0) {
				ArrayList<ArrayList<org.sbml.libsbml.Reaction>> resultSet = new ArrayList<ArrayList<org.sbml.libsbml.Reaction>>();
				resultSet.add(instantiatedReactions);
				resultSet.add(failedInstantiationReactions);
				return resultSet;
			}
			
			ArrayList<org.sbml.libsbml.Reaction> newReactions = generateInstantiatedReactions(origReaction, reactants, products, generalizedReactants, generalizedProducts);
			
			if (newReactions.size() == 0) failedInstantiationReactions.add(origReaction);
			for (org.sbml.libsbml.Reaction newReaction : newReactions) instantiatedReactions.add(newReaction);
		} catch (PtoolsErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<ArrayList<org.sbml.libsbml.Reaction>> resultSet = new ArrayList<ArrayList<org.sbml.libsbml.Reaction>>();
		resultSet.add(instantiatedReactions);
		resultSet.add(failedInstantiationReactions);
		return resultSet;
	}
	
	private ArrayList<org.sbml.libsbml.Reaction> generateInstantiatedReactions(org.sbml.libsbml.Reaction origReaction, ArrayList<String> reactants, ArrayList<String> products, ArrayList<String> generalizedReactants, ArrayList<String> generalizedProducts) {
		ArrayList<org.sbml.libsbml.Reaction> newReactions = new ArrayList<org.sbml.libsbml.Reaction>();
		
		try {
			
			//TODO find a better way to guarantee correct reactant/product
			Reaction reaction = (Reaction)Reaction.load(conn, origReaction.getName());
			String reactantSlot = "";
			String productSlot = "";
			if (reaction.getSlotValue("REACTION-DIRECTION") == null || !reaction.getSlotValue("REACTION-DIRECTION").equalsIgnoreCase("RIGHT-TO-LEFT")) {
				reactantSlot = "LEFT";
				productSlot = "RIGHT";
			} else {
				productSlot = "LEFT";
				reactantSlot = "RIGHT";
			}
			
			//Possible compartment annotations
			//CCO-PERI-BAC
			//CCO-EXTRACELLULAR
			//CCO-CYTOSOL
			
			// Collect unique set of general terms
			ArrayList<String> generalizedTerms = new ArrayList<String>();
			for (String term : generalizedReactants) {
				if (!generalizedTerms.contains(term)) generalizedTerms.add(term);
			}
			for (String term : generalizedProducts) {
				if (!generalizedTerms.contains(term)) generalizedTerms.add(term);
			}
			
			// Collect instances of general terms
			// Order of generalized terms is same as the order of term instances in the listOfTermLists
			ArrayList<ArrayList<String>> listOfTermLists = new ArrayList<ArrayList<String>>();
			for (String term : generalizedTerms) {
				ArrayList<String> instancesOfGeneralTerm = new ArrayList<String>();
				for (Object instance : conn.getClassAllInstances(term)) instancesOfGeneralTerm.add(instance.toString());
				
				if (instancesOfGeneralTerm.size() == 0) {
					instancesOfGeneralTerm.add(term);
//					System.err.println("No instances of class : " + term);
//					return new ArrayList<org.sbml.libsbml.Reaction>();
				}
				
				listOfTermLists.add(instancesOfGeneralTerm);
			}
			
			// Generate all possible combinations of instances for the general terms
			ArrayList<ArrayList<String>> termCombinations = listCombinations(listOfTermLists);
			
			// For each combination, create a new reaction for it if the reaction is elementally balanced
			for (ArrayList<String> combinationSet : termCombinations) {
				ArrayList<SpeciesReference> reactantReferences = new ArrayList<SpeciesReference>();
				ArrayList<SpeciesReference> productReferences = new ArrayList<SpeciesReference>();
				ArrayList<String> reactantBalance = new ArrayList<String>();
				ArrayList<String> productBalance = new ArrayList<String>();
				
				for (String reactant : reactants) {
					// New reactant ref
					SpeciesReference oldReactant = new SpeciesReference(2, 1);
					
					String species = convertToSBMLSafe(reactant);
					String compartment = conn.getValueAnnot(reaction.getLocalID(), reactantSlot, reactant, "COMPARTMENT");
					if (compartment.equalsIgnoreCase("NIL")) species += "_" + convertToSBMLSafe(defaultCompartment);
					else species += "_" + convertToSBMLSafe(compartment);
					
//					System.out.println("Reaction=" + reaction.getLocalID() + " Species=" + species);
					oldReactant.setSpecies(species);
					
					int coeficient = 1;
					try {
						coeficient = Integer.parseInt(conn.getValueAnnot(reaction.getLocalID(), reactantSlot, reactant, "COEFFICIENT"));
					} catch (Exception e) {
						coeficient = 1;
					}
					
					oldReactant.setStoichiometry(coeficient);
					reactantReferences.add(oldReactant);
					
					while (coeficient > 0) {
						reactantBalance.add(reactant);
						coeficient--;
					}
				}
				for (String product :products) {
					// New product ref
					SpeciesReference oldProduct = new SpeciesReference(2, 1);
					
					String species = convertToSBMLSafe(product);
					String compartment = conn.getValueAnnot(reaction.getLocalID(), productSlot, product, "COMPARTMENT");
					if (compartment.equalsIgnoreCase("NIL")) species += "_" + convertToSBMLSafe(defaultCompartment);
					else species += "_" + convertToSBMLSafe(compartment);
					
//					System.out.println("Reaction=" + reaction.getLocalID() + " Species=" + species);
					oldProduct.setSpecies(species);
					
					int coeficient = 1;
					try {
						coeficient = Integer.parseInt(conn.getValueAnnot(reaction.getLocalID(), reactantSlot, product, "COEFFICIENT"));
					} catch (Exception e) {
						coeficient = 1;
					}
					
					oldProduct.setStoichiometry(coeficient);
					productReferences.add(oldProduct);
					
					while (coeficient > 0) {
						productBalance.add(product);
						coeficient--;
					}
				}
				for (String term : generalizedReactants) {
					// New reactant ref
					SpeciesReference newReactant = new SpeciesReference(2, 1);
					
					String species = convertToSBMLSafe(combinationSet.get(generalizedTerms.indexOf(term)));
					String compartment = conn.getValueAnnot(reaction.getLocalID(), reactantSlot, term, "COMPARTMENT");
					if (compartment.equalsIgnoreCase("NIL")) species += "_" + convertToSBMLSafe(defaultCompartment);
					else species += "_" + convertToSBMLSafe(compartment);
					
//					System.out.println("Reaction=" + reaction.getLocalID() + " Species=" + species);
					newReactant.setSpecies(species);
					
					int coeficient = 1;
					try {
						coeficient = Integer.parseInt(conn.getValueAnnot(reaction.getLocalID(), reactantSlot, term, "COEFFICIENT"));
					} catch (Exception e) {
						coeficient = 1;
					}
					
					newReactant.setStoichiometry(coeficient);
					reactantReferences.add(newReactant);
					
					while (coeficient > 0) {
						reactantBalance.add(combinationSet.get(generalizedTerms.indexOf(term)));
						coeficient--;
					}
				}
				for (String term :generalizedProducts) {
					// New product ref
					SpeciesReference newProduct = new SpeciesReference(2, 1);
					
					String species = convertToSBMLSafe(combinationSet.get(generalizedTerms.indexOf(term)));
					String compartment = conn.getValueAnnot(reaction.getLocalID(), productSlot, term, "COMPARTMENT");
					if (compartment.equalsIgnoreCase("NIL")) species += "_" + convertToSBMLSafe(defaultCompartment);
					else species += "_" + convertToSBMLSafe(compartment);
					
//					System.out.println("Reaction=" + reaction.getLocalID() + " Species=" + species);
					newProduct.setSpecies(species);
					
					int coeficient = 1;
					try {
						coeficient = Integer.parseInt(conn.getValueAnnot(reaction.getLocalID(), reactantSlot, term, "COEFFICIENT"));
					} catch (Exception e) {
						coeficient = 1;
					}
					
					newProduct.setStoichiometry(coeficient);
					productReferences.add(newProduct);
					
					while (coeficient > 0) {
						productBalance.add(combinationSet.get(generalizedTerms.indexOf(term)));
						coeficient--;
					}
				}
				
				String nameModifier = "";
				for (String term : combinationSet) nameModifier += term + "_";
				if (nameModifier.endsWith("_")) nameModifier = nameModifier.substring(0, nameModifier.length()-1);
				
				String newID = origReaction.getId() + "_" + convertToSBMLSafe(nameModifier);
				String newName = origReaction.getName() + "_" + nameModifier;
				if (isReactionBalanced(reactantBalance, productBalance)) newReactions.add(copyReaction(origReaction, newID, newName, reactantReferences, productReferences));
			}
			
			// TODO Special reaction types
			// Case 1: Change of compartments occurs
			// Case 2: Change in (n) annotation occurs
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return newReactions;
	}
	
	private org.sbml.libsbml.Reaction copyReaction(org.sbml.libsbml.Reaction origReaction, String newID, String newName, ArrayList<SpeciesReference> reactants, ArrayList<SpeciesReference> products) {
		org.sbml.libsbml.Reaction newReaction = new org.sbml.libsbml.Reaction(origReaction);
		newReaction.setId(newID);
		newReaction.setName(newName);
		newReaction.getListOfReactants().clear();
		newReaction.getListOfProducts().clear();
		
		for (SpeciesReference reactant : reactants) {
			if (newReaction.addReactant(reactant) != 0) {
				System.err.println("Failed to add reactant to reaction.  Reaction=" + origReaction.getName() + ", Reactant=" + reactant.getSpecies());
				return null;
			}
		}
		for (SpeciesReference product : products) {
			if (newReaction.addProduct(product) != 0) {
				System.err.println("Failed to add product to reaction.  Reaction=" + origReaction.getName() + ", Product=" + product.getSpecies());
				return null;
			}
		}
		
		return newReaction;
	}
	
	private ArrayList<ArrayList<String>> listCombinations(ArrayList<ArrayList<String>> generalToInstancesArray) {
		// This function takes in a list of lists and returns every possible combination of 1 item from each sublist.
		// Thus, if the lists [1,2,3], [4,5,6], and [7,8,9] were input, then the output would be
		// [1,4,7], [1,4,8], [1,4,8], [1,5,7], [1,5,8], [1,5,9] ...
		// This method was written as a way to instantiate general terms in a reaction. Each general term in a reaction has a list of possible
		// values, and every possible combination of terms is needed.
		// The position of the term in the output subarray is the same as the term's value array in the inputs main array.
		if (generalToInstancesArray == null || generalToInstancesArray.size() < 1) return new ArrayList<ArrayList<String>>();
		if (generalToInstancesArray.size() > 1) {
			ArrayList<ArrayList<String>> resultArray = new ArrayList<ArrayList<String>>();
			ArrayList<String> list = generalToInstancesArray.remove(0);
			for (ArrayList<String> combinations : listCombinations(generalToInstancesArray)) {
				for (String item : list) {
					ArrayList<String> combinationsCopy = (ArrayList<String>)combinations.clone();
					combinationsCopy.add(0, item);
					resultArray.add(combinationsCopy);
				}
			}
			return resultArray;
		}
		
		for (String item : generalToInstancesArray.remove(0)) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(item);
			generalToInstancesArray.add(list);
		}
		return generalToInstancesArray;
	}
	
	private boolean isGeneralizedReaction(String reactionName) {
		boolean result = false;
		try {
			Reaction reaction = loadReaction(reactionName);
			ArrayList<String> leftMetabolites = reaction.getSlotValues("LEFT");
			ArrayList<String> rightMetabolites = reaction.getSlotValues("RIGHT");
			
			for (String left : leftMetabolites) {
				if (conn.getFrameType(left).toUpperCase().equals(":CLASS")) return true;
			}
			
			for (String right : rightMetabolites) {
				if (conn.getFrameType(right).toUpperCase().equals(":CLASS")) return true;
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private boolean isGeneralizedReaction(Reaction reaction) {
		boolean result = false;
		try {
			ArrayList<String> leftMetabolites = reaction.getSlotValues("LEFT");
			ArrayList<String> rightMetabolites = reaction.getSlotValues("RIGHT");
			
			for (String left : leftMetabolites) {
				if (conn.getFrameType(left).toUpperCase().equals(":CLASS")) return true;
			}
			
			for (String right : rightMetabolites) {
				if (conn.getFrameType(right).toUpperCase().equals(":CLASS")) return true;
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private boolean isGeneralizedMetabolite(String metaboliteName) {
		boolean result = false;
		try {
			if (conn.getFrameType(metaboliteName).toUpperCase().equals(":CLASS")) result = true;
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private boolean isReactionBalanced(ArrayList<String> reactantIDs, ArrayList<String> productIDs) {
		// If a reactant or product has a stoichiometry greater than |1|, then it should appear in the list as many times as its stoich value
		// This method does not interpret chemical shorthand (eg R-groups, etc).
		// Returns true if successful, false if not.  Any errors or unreadable/missing formulas return false.
		
		HashMap<String, Integer> reactantElements = new HashMap<String, Integer>();
		HashMap<String, Integer> productElements = new HashMap<String, Integer>();
		try {
			for (String reactant : reactantIDs) {
				// Special Case
				int specialCases = 0;
				if (reactant.equalsIgnoreCase("|Acceptor|")) specialCases = 1;
				else if (reactant.equalsIgnoreCase("|Donor-H2|")) specialCases = 2;
				switch (specialCases) {
					case 1: {
						if (reactantElements.containsKey("A")) {
							reactantElements.put("A", reactantElements.get("A") + 1);
						} else {
							reactantElements.put("A", 1);
						}
					} break;
					case 2: {
						if (reactantElements.containsKey("A")) {
							reactantElements.put("A", reactantElements.get("A") + 1);
						} else {
							reactantElements.put("A", 1);
						}
						if (reactantElements.containsKey("H")) {
							reactantElements.put("H", reactantElements.get("H") + 2);
						} else {
							reactantElements.put("H", 2);
						}
					} break;
				}
				if (specialCases != 0) {
//					System.out.println("Special Case handled");
					continue;
				}
				
				// Regular Case
				Compound reactantFrame = loadCompound(reactant);
				
				for (Object o : reactantFrame.getSlotValues("CHEMICAL-FORMULA")) {
					String chemicalFormulaElement = o.toString().substring(1, o.toString().length()-1).replace(" ", "");
					String element = chemicalFormulaElement.split(",")[0];
					Integer quantity = Integer.parseInt(chemicalFormulaElement.split(",")[1]);
					
					//Add to map
					if (reactantElements.containsKey(element)) {
						reactantElements.put(element, reactantElements.get(element) + quantity);
					} else {
						reactantElements.put(element, quantity);
					}
				}
			}
			for (String product : productIDs) {
				// Special Case
				int specialCases = 0;
				if (product.equalsIgnoreCase("|Acceptor|")) specialCases = 1;
				else if (product.equalsIgnoreCase("|Donor-H2|")) specialCases = 2;
				switch (specialCases) {
					case 1: {
						if (productElements.containsKey("A")) {
							productElements.put("A", productElements.get("A") + 1);
						} else {
							productElements.put("A", 1);
						}
					} break;
					case 2: {
						if (productElements.containsKey("A")) {
							productElements.put("A", productElements.get("A") + 1);
						} else {
							productElements.put("A", 1);
						}
						if (productElements.containsKey("H")) {
							productElements.put("H", productElements.get("H") + 2);
						} else {
							productElements.put("H", 2);
						}
					} break;
				}
				if (specialCases != 0) {
//					System.out.println("Special Case handled");
					continue;
				}
				
				// Regular Case
				Compound productFrame = loadCompound(product);
				
				for (Object o : productFrame.getSlotValues("CHEMICAL-FORMULA")) {
					String chemicalFormulaElement = o.toString().substring(1, o.toString().length()-1).replace(" ", "");
					String element = chemicalFormulaElement.split(",")[0];
					Integer quantity = Integer.parseInt(chemicalFormulaElement.split(",")[1]);
					
					//Add to map
					if (productElements.containsKey(element)) {
						productElements.put(element, productElements.get(element) + quantity);
					} else {
						productElements.put(element, quantity);
					}
				}
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			return false;
		}
		
		if (!reactantElements.keySet().containsAll(productElements.keySet()) || !productElements.keySet().containsAll(reactantElements.keySet())) return false;
		for (String key : reactantElements.keySet()) {
//			if (key.equalsIgnoreCase("H")) {
//				if (reactantElements.get(key) - productElements.get(key) == 1 || reactantElements.get(key) - productElements.get(key) == -1) {
//					System.out.println("Save reaction with a proton.");
//				}
//			}
			if (reactantElements.get(key) != productElements.get(key)) return false;
		}
		
		return true;
	}
	
	private String convertToSBMLSafe(String input) {
		String output = input;
		output = output.replace("-", "__45__");
		output = output.replace("+", "__43__");
		output = output.replace(" ", "__32__");
		output = output.replace("(", "__40__");
		output = output.replace(")", "__41__");
		output = output.replace(".", "__46__");
		
		output = output.replace("|", "");
		try {
			Integer.parseInt(output.substring(0,1));
			output = "_" + output;
		} catch(NumberFormatException nfe) {
			// Do nothing
		}
//		output = output + "_CCO__45__CYTOSOL";
		return output;
	}
	
	private String convertFromSBMLSafe(String input) {
		String output = input;
		output = output.replace("__45__", "-");
		output = output.replace("__43__", "+");
		output = output.replace("__32__", " ");
		output = output.replace("__40__", "(");
		output = output.replace("__41__", ")");
		output = output.replace("__46__", ".");
		if (output.substring(0,1).equals("_")) output = output.substring(1, output.length());
		
		output = output.replace("_CCO-UNKNOWN-SPACE", "");
		output = output.replace("_CCO-CYTOPLASM", "");
		output = output.replace("_CCO-EXTRACELLULAR", "");
		output = output.replace("_CCO-PERIPLASM", "");
		output = output.replace("_CCO-PERI-BAC", "");
		output = output.replace("_CCO-PM-BAC-NEG", "");
		output = output.replace("_CCO-CYTOSOL", "");
		
		return output;
	}
	
	private Frame loadFrame(String id) throws PtoolsErrorException {
		Frame instanceFrame = new Frame(conn, id);
		if (instanceFrame.inKB()) return instanceFrame;
		else {
			Frame classFrame = new Frame(conn, "|"+id+"|");
			if (classFrame.inKB()) return classFrame;
		}
		return null;
	}
	
	private Compound loadCompound(String id) throws PtoolsErrorException {
		Compound f = new Compound(conn, id);
		if (f.inKB()) return f;
		else return null;
	}
	
	private Reaction loadReaction(String id) throws PtoolsErrorException {
		Reaction f = new Reaction(conn, id);
		if (f.inKB()) return f;
		else return null;
	}
	
	private String reactionGeneRule(String reactionID) throws PtoolsErrorException {
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

	public void addBoundaryReactions(String oldBoundaryName, String newBoundaryName) {
		//TODO Model is null check
		ListOfCompartments loc = model.getListOfCompartments();
		Compartment currentBoundary = null;
		Compartment newBoundary = new Compartment(2,1);
		newBoundary.setName(newBoundaryName);
		for (int i = 0; i < loc.size() ; i++) {
			if (loc.get(i).getName().equals(oldBoundaryName)) {
				currentBoundary = loc.get(i);
				break;
			}
		}
		
		ListOfSpecies los = model.getListOfSpecies();
		for (int i = 0; i < los.size() ; i++) {
			if (los.get(i).getCompartment().equals(currentBoundary.getId())) {
				//createNewBoundaryReaction
				org.sbml.libsbml.Reaction newBoundaryRxn = new org.sbml.libsbml.Reaction(2,1);
				
				newBoundaryRxn.setId(los.get(i).getId() + "_e");
				newBoundaryRxn.setName(los.get(i).getName() + "_exchange");
				Parameter p = new Parameter(2,1);
				KineticLaw kl = new KineticLaw(2,1);
				kl.addParameter(p);
				newBoundaryRxn.setKineticLaw(kl);
				
				SpeciesReference newReactant = new SpeciesReference(2, 1);
				newReactant.setSpecies(los.get(i).getId());
				newReactant.setStoichiometry(1);
				if (newBoundaryRxn.addReactant(newReactant) != 0) {
					System.err.println("Failed to add reactant to boundary reaction.  Reaction=" + newBoundaryRxn.getName() + ", Reactant=" + newReactant.getSpecies());
				}
				
				SpeciesReference newPoduct = new SpeciesReference(2, 1);
				newPoduct.setSpecies(los.get(i).getId());
				newPoduct.setStoichiometry(1);
				if (newBoundaryRxn.addProduct(newPoduct) != 0) {
					System.err.println("Failed to add product to boundary reaction.  Reaction=" + newBoundaryRxn.getName() + ", Product=" + newPoduct.getSpecies());
				}
				
				model.addReaction(newBoundaryRxn);
			}
		}
		
	}
	
	public void cleanModel() {
		// Remove any duplicated reactions
		//TODO
		
		// Check for any species reference that has no associated species
		//TODO
		
		// Remove any species in the models list of species that does not appear in at least one reaction
		ArrayList<String> usedSpeciesIDs = new ArrayList<String>();
		ArrayList<String> unusedSpeciesIDs = new ArrayList<String>();
		ListOfReactions lor = model.getListOfReactions();
		for (int i = 0; i < lor.size() ; i++) {
			ListOfSpeciesReferences reactants = lor.get(i).getListOfReactants();
			for (int j = 0; j < reactants.size() ; j++) usedSpeciesIDs.add(reactants.get(j).getId());
			
			ListOfSpeciesReferences products = lor.get(i).getListOfProducts();
			for (int j = 0; j < products.size() ; j++) usedSpeciesIDs.add(products.get(j).getId());
		}
		
		ListOfSpecies los = model.getListOfSpecies();
		for (int i = 0; i < los.size() ; i++) {
			if (!usedSpeciesIDs.contains(los.get(i).getId())) unusedSpeciesIDs.add(los.get(i).getId());
		}
		
		for (String unusedSpeciesID : unusedSpeciesIDs) model.removeSpecies(unusedSpeciesID);
		
		// Remove any compartments that contains no species
		ArrayList<String> usedCompartmentIDs = new ArrayList<String>();
		ArrayList<String> unusedCompartmentIDs = new ArrayList<String>();
		los = model.getListOfSpecies();
		for (int i = 0; i < los.size() ; i++) usedCompartmentIDs.add(los.get(i).getCompartment());
		
		ListOfCompartments loc = model.getListOfCompartments();
		for (int i = 0; i < loc.size() ; i++) {
			if (!usedCompartmentIDs.contains(loc.get(i).getId())) unusedCompartmentIDs.add(loc.get(i).getId());
		}
		
		for (String unusedCompartmentID : unusedCompartmentIDs) model.removeCompartment(unusedCompartmentID);
	}
	
	public void convertToPalssonAnnotations() {
		ListOfSpecies los = model.getListOfSpecies();
		for (int i = 0; i < los.size() ; i++) {
			
		}
		
		ListOfReactions lor = model.getListOfReactions();
		for (int i = 0; i < lor.size() ; i++) {
			
		}
		
		ListOfCompartments loc = model.getListOfCompartments();
		for (int i = 0; i < loc.size() ; i++) {
			
		}
	}
	
	private void instantiateGenericSpecies() {
		ListOfSpecies los = model.getListOfSpecies();
		ArrayList<String> speciesInList = new ArrayList<String>();
		ArrayList<Species> speciesToAdd = new ArrayList<Species>();
		for (int i = 0; i < los.size() ; i++) speciesInList.add(los.get(i).getId());
		for (int i = 0; i < los.size() ; i++) {
			if (isGeneralizedMetabolite(convertFromSBMLSafe(los.get(i).getId()))) {
				try {
					for (Object instance : conn.getClassAllInstances(convertFromSBMLSafe(los.get(i).getId()))) {
						if (!speciesInList.contains(instance.toString())) {
							Species newSpecies = new Species(2,1);
							newSpecies.setId(convertToSBMLSafe(instance.toString()));
							newSpecies.setName(instance.toString());
							speciesToAdd.add(newSpecies);
						}
					}
				} catch (PtoolsErrorException e) {
					e.printStackTrace();
				}
			}
		}
		
		for (Species s : speciesToAdd) {
			model.addSpecies(s);
		}
	}
	
	private void loadMetabolites(Model thisModel) {
		HashMap<String, Frame> metabolites = new HashMap<String, Frame>();
		
		ListOfSpecies los = thisModel.getListOfSpecies();
		ArrayList<String> sids = new ArrayList<String>();
		for (int i = 0; i < los.size() ; i++) {
			sids.add(los.get(i).getId());
		}
		
		for (String sid : sids) {
			String ecocycID = convertFromSBMLSafe(sid);
			try {
				Frame metabolite = loadFrame(ecocycID);
				if (metabolite != null) metabolites.put(sid, metabolite);
				else System.out.println("Failed to load metabolite : " + ecocycID);
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadReactions(Model thisModel) {
		HashMap<String, Frame> reactions = new HashMap<String, Frame>();
		
		ListOfReactions lor = thisModel.getListOfReactions();
		ArrayList<String> rids = new ArrayList<String>();
		for (int i = 0; i < lor.size() ; i++) {
			rids.add(lor.get(i).getId());
		}
		
		for (String rid : rids) {
			String ecocycID = convertFromSBMLSafe(rid);
			try {
				Frame reaction = loadReaction(ecocycID);
				if (reaction != null) reactions.put(rid, reaction);
				else System.out.println("Failed to load reaction : " + ecocycID);
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// Helper functions
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
 	
	private void setOutputDirectory(String directory) {
		OutputDirectory = directory;
	}

	private void printListOfReactions(String fileName, ArrayList<org.sbml.libsbml.Reaction> rxnList) {
		String printString = "";
		for (org.sbml.libsbml.Reaction rxn : rxnList) {
			printString += rxn.getName() + "\n";
		}
		printString(OutputDirectory + fileName, printString);
	}

	
	
	
	
	
	
	public void setDefaults() {
		//TODO read from a config file?
		OutputDirectory = "/home/Jesse/Desktop/output/";
		defaultCompartment = "CCO-CYTOSOL";
		DefaultSBMLLevel = 2;
		DefaultSBMLVersion = 1;
		
		
		speciesPrefix = "M";
		reactionPrefix = "R";
		
		compartmentAbrevs.put("CCO-CYTOSOL", "c");
		compartmentAbrevs.put("CCO-PERI-BAC", "periBac");
		compartmentAbrevs.put("CCO-PERIPLASM", "p");
		compartmentAbrevs.put("CCO-EXTRACELLULAR", "e");
		compartmentAbrevs.put("CCO-CYTOPLASM", "cp");
		compartmentAbrevs.put("CCO-UNKNOWN-SPACE", "unk");
		compartmentAbrevs.put("Boundary", "b");
	}
	
	public void createGenomeScaleModelFromEcoCyc() {
		Long start = System.currentTimeMillis();
		try {
			// 1) Create blank model
			System.out.println("Generating blank model ...");
			SBMLDocument doc = createBlankSBMLDocument("CBiRC", DefaultSBMLLevel, DefaultSBMLVersion);
			
			// 2) Load all reactions
			System.out.println("Loading all reactions ...");
			ArrayList<Reaction> allReactions = Reaction.all(conn);
			
			// 3) Filter unwanted reactions
			System.out.println("Filtering unwanted reactions ...");
			ArrayList<String> classToFilter = new ArrayList<String>();
			classToFilter.add("|Polynucleotide-Reactions|");
			classToFilter.add("|Protein-Reactions|");
			FilterResults filterResults = filterReactions(allReactions, classToFilter, null);
			
			// 4) Find and instantiate generics
			System.out.println("Instantiating generic reactions ...");
			InstantiationResults instantiationResults = generateSpecificReactionsFromGenericReactions(filterResults.keepList);
			ArrayList<String> reactionsToFilter = new ArrayList<String>();
			for (Frame reaction : instantiationResults.genericReactionsFound) reactionsToFilter.add(reaction.getLocalID());
			FilterResults genericReactionFilterResults = filterReactions(filterResults.keepList, null, reactionsToFilter);
			
			// 5) Add boundaries
			System.out.println("Adding boundary reactions ...");
			ArrayList<ReactionInstance> boundaryResults = addBoundaryReactionsByCompartment("CCO-EXTRACELLULAR", genericReactionFilterResults.keepList, instantiationResults.instantiatedReactions);
			
			// 6) Generate SBML model
			System.out.println("Generating SBML model ...");
			ArrayList<ReactionInstance> reactions = reactionListToReactionInstances(genericReactionFilterResults.keepList);
			reactions.addAll(instantiationResults.instantiatedReactions);
			reactions.addAll(boundaryResults);
			generateSBMLModel(doc, reactions);
			
			// 7) Write revised model.
			System.out.println("Writing output ...");
			
			SBMLWriter writer = new SBMLWriter();
			writer.writeSBML(doc, OutputDirectory + "written_SBML.xml");
			
//			printListOfReactions("removedreactions.txt", removedReactions);
//			printListOfReactions("instantiatedReactions.txt", resultSet.get(0));
//			printListOfReactions("failedInstantiationReactions", resultSet.get(1));
//			printListOfReactions("genericReactions.txt", resultSet.get(2));
			
			// Print statistics
			System.out.println("Writing statistics ...");
			System.out.println("All reactions : " + allReactions.size());
			System.out.println("Filtered reactions keeplist : " + filterResults.keepList.size());
			System.out.println("Filtered reactions tosslist : : " + filterResults.removedList.size());
			System.out.println("Generic reactions found : " + instantiationResults.genericReactionsFound.size());
			System.out.println("Generic reactions failed to instantiate : " + instantiationResults.genericReactionsFailedToInstantiate.size());
			System.out.println("New reactions from generic reaction instantiations : " + instantiationResults.instantiatedReactions.size());
			System.out.println("Generic keeplist : " + genericReactionFilterResults.keepList.size());
			System.out.println("Generic tosslist : " + genericReactionFilterResults.removedList.size());
			System.out.println("Boundary reactions added : " + boundaryResults.size());
			int grandTotal = instantiationResults.instantiatedReactions.size() + genericReactionFilterResults.keepList.size() + boundaryResults.size();
			System.out.println("Grand total : " + grandTotal);
			
			System.out.println("Done!");
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		Long stop = System.currentTimeMillis();
		Long runtime = (stop - start) / 1000;
		System.out.println("Runtime is " + runtime + " seconds.");
	}
	
	public SBMLDocument createBlankSBMLDocument(String modelID, int SBMLLevel, int SBMLVersion) {
		SBMLDocument doc = new SBMLDocument(SBMLLevel, SBMLVersion);
		Model model = doc.createModel(modelID);
		model.setName("Generated from BioCyc Pathway/Genome Database");
		
		UnitDefinition UD = model.createUnitDefinition();
		UD.setId("mmol_per_gDW_per_hr");
		Unit mole = UD.createUnit();
		mole.setKind(libsbmlConstants.UNIT_KIND_MOLE);
		mole.setScale(-3);
		mole.setMultiplier(1);
		mole.setOffset(0);
		
		Unit gram = UD.createUnit();
		gram.setKind(libsbmlConstants.UNIT_KIND_GRAM);
		gram.setExponent(-1);
		gram.setMultiplier(1);
		gram.setOffset(0);
		
		Unit second = UD.createUnit();
		second.setKind(libsbmlConstants.UNIT_KIND_SECOND);
		second.setExponent(-1);
		second.setMultiplier(0.00027777);
		second.setOffset(0);
		
		return doc;
	}
	
	public FilterResults filterReactions(ArrayList<Reaction> reactions, ArrayList<String> classToFilter, ArrayList<String> reactionsToFilter) {
		ArrayList<String> filter = new ArrayList<String>();
		ArrayList<Reaction> removedList = new ArrayList<Reaction>();
		ArrayList<Reaction> keepList = new ArrayList<Reaction>();
		
		try {
			if (classToFilter != null) {
				for (String reactionClass : classToFilter) {
					for (Object reaction : conn.getClassAllInstances(reactionClass)) filter.add(reaction.toString());
				}
			}
			
			if (reactionsToFilter != null) {
				for (String reaction : reactionsToFilter) filter.add(reaction);
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		
		for (Reaction reaction : reactions) {
			if (filter.contains(reaction.getLocalID())) removedList.add(reaction);
			else keepList.add(reaction);
		}
		
		return new FilterResults(keepList, removedList);
	}
	
	public InstantiationResults generateSpecificReactionsFromGenericReactions(ArrayList<Reaction> reactions) {
		InstantiationResults instantiationResults = new InstantiationResults(new ArrayList<ReactionInstance>(), new ArrayList<Frame>(), new ArrayList<Frame>());
		
		for (Reaction reaction : reactions) {
			if (isGeneralizedReaction(reaction)) {
				instantiationResults.genericReactionsFound.add(reaction);
				ArrayList<ReactionInstance> instantiatedReactions = instantiateGenericReaction(reaction);
				if (instantiatedReactions != null && instantiatedReactions.size() > 0) {
					instantiationResults.instantiatedReactions.addAll(instantiatedReactions);
				} else {
					instantiationResults.genericReactionsFailedToInstantiate.add(reaction);
				}
			}
		}
		
		return instantiationResults;
	}
	
	private ArrayList<ReactionInstance> instantiateGenericReaction(Reaction reaction) {
		// This function makes sure that the reaction has a possibility of being instantiated.  It then calls 
		// createInstantiatedReactions to do the bulk of the work.
		ArrayList<String> allReactantIDs = new ArrayList<String>();
		ArrayList<String> allProductIDs = new ArrayList<String>();
		ArrayList<Frame> generalizedReactants = new ArrayList<Frame>();
		ArrayList<Frame> generalizedProducts = new ArrayList<Frame>();
		ArrayList<Frame> reactants = new ArrayList<Frame>();
		ArrayList<Frame> products = new ArrayList<Frame>();
		
		try {
			//TODO return these and check they are in the model, in case whole ecoli model was not the starting point
			// If reaction has specific forms, then assume those forms are already in the model
			if (conn.specificFormsOfReaction(reaction.getLocalID()).size() > 0) return null;
			
			// If reaction cannot be balanced then it cannot be instantiated
			if (reaction.hasSlot("CANNOT-BALANCE?") && reaction.getSlotValue("CANNOT-BALANCE?") != null) return null;
			
			// Get reactants and products.  Must account for direction of reaction.
			String reactantSlot = "";
			String productSlot = "";
			if (reaction.getSlotValue("REACTION-DIRECTION") == null || !reaction.getSlotValue("REACTION-DIRECTION").equalsIgnoreCase("RIGHT-TO-LEFT")) {
				reactantSlot = "LEFT";
				productSlot = "RIGHT";
			} else {
				reactantSlot = "RIGHT";
				productSlot = "LEFT";
			}
			
			allReactantIDs = reaction.getSlotValues(reactantSlot);
			allProductIDs = reaction.getSlotValues(productSlot);
			
			for (String reactantID : allReactantIDs) {
				Frame reactant = loadFrame(reactantID);
				if (conn.getFrameType(reactantID).toUpperCase().equals(":CLASS")) generalizedReactants.add(reactant);
				else reactants.add(reactant);
			}
			for (String productID : allProductIDs) {
				Frame product = loadFrame(productID);
				if (conn.getFrameType(productID).toUpperCase().equals(":CLASS")) generalizedProducts.add(product);
				else products.add(product);
			}
			
			// Make sure this reaction is a generalized reaction
			if (generalizedReactants.size() == 0 && generalizedProducts.size() == 0) return null;
			
			return generateInstantiatedReactions(reaction, reactants, products, generalizedReactants, generalizedProducts, reactantSlot, productSlot);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private ArrayList<ReactionInstance> generateInstantiatedReactions(Reaction origReaction, ArrayList<Frame> reactants, ArrayList<Frame> products, ArrayList<Frame> genericReactants, ArrayList<Frame> genericProducts, String reactantSlot, String productSlot) {
		ArrayList<ReactionInstance> newReactions = new ArrayList<ReactionInstance>();
		
		try {
			// Generate all possible combinations of instances for the general terms
			ArrayList<NamedList> listSet = new ArrayList<NamedList>();
			for (Frame genericTerm : genericReactants) {
				ArrayList<String> instancesOfGeneralTerm = new ArrayList<String>();
				for (Object instance : conn.getClassAllInstances(genericTerm.getLocalID())) instancesOfGeneralTerm.add(instance.toString());
				if (instancesOfGeneralTerm.size() == 0) instancesOfGeneralTerm.add(genericTerm.getLocalID());
				NamedList namedList = new NamedList(genericTerm.getLocalID(), instancesOfGeneralTerm);
				if (!listSet.contains(namedList)) listSet.add(namedList);
			}
			
			for (Frame genericTerm : genericProducts) {
				ArrayList<String> instancesOfGeneralTerm = new ArrayList<String>();
				for (Object instance : conn.getClassAllInstances(genericTerm.getLocalID())) instancesOfGeneralTerm.add(instance.toString());
				if (instancesOfGeneralTerm.size() == 0) instancesOfGeneralTerm.add(genericTerm.getLocalID());
				NamedList namedList = new NamedList(genericTerm.getLocalID(), instancesOfGeneralTerm);
				if (!listSet.contains(namedList)) listSet.add(namedList);
			}
			
			ListCombinationResults termCombinations = listCombinations2(listSet);
			
			// Generate the Metabolite objects for reactants and products, which will be static accross all new reactions
			ArrayList<Metabolite> reactantMetabolites = new ArrayList<Metabolite>();
			ArrayList<Metabolite> productMetabolites = new ArrayList<Metabolite>();
			for (Frame reactant : reactants) reactantMetabolites.add(generateMetabolite(origReaction, reactantSlot, reactant, reactant));
			for (Frame product : products) productMetabolites.add(generateMetabolite(origReaction, productSlot, product, product));
			
			// For each combination, create a new reaction for it if the reaction is elementally balanced
			for (ArrayList<String> combinationSet : termCombinations.listOfTuples) {
				ReactionInstance newReaction = new ReactionInstance(origReaction, null, "", origReaction.isReversible(), new ArrayList<Metabolite>(), new ArrayList<Metabolite>());
				ArrayList<String> reactantBalance = new ArrayList<String>();
				ArrayList<String> productBalance = new ArrayList<String>();
				
				// Non-generic metabolites
				for (Metabolite reactant : reactantMetabolites) {
					newReaction.reactants.add(reactant);
					int count = reactant.stoichiometry;
					while (count > 0) {
						reactantBalance.add(reactant.metabolite.getLocalID());
						count--;
					}
				}
				for (Metabolite product : productMetabolites) {
					newReaction.products.add(product);
					int count = product.stoichiometry;
					while (count > 0) {
						productBalance.add(product.metabolite.getLocalID());
						count--;
					}
				}

				// Generic metabolites
				for (Frame term : genericReactants) {
					Metabolite newMetabolite = generateMetabolite(origReaction, reactantSlot, term, loadFrame(combinationSet.get(termCombinations.nameList.indexOf(term.getLocalID()))));
					
					int count = newMetabolite.stoichiometry;
					while (count > 0) {
						reactantBalance.add(newMetabolite.metabolite.getLocalID());
						count--;
					}
					newReaction.reactants.add(newMetabolite);
				}
				for (Frame term : genericProducts) {
					Metabolite newMetabolite = generateMetabolite(origReaction, productSlot, term, loadFrame(combinationSet.get(termCombinations.nameList.indexOf(term.getLocalID()))));
					
					int count = newMetabolite.stoichiometry;
					while (count > 0) {
						productBalance.add(newMetabolite.metabolite.getLocalID());
						count--;
					}
					newReaction.products.add(newMetabolite);
				}
				
				String nameModifier = "";
				for (String term : combinationSet) nameModifier += term + "_";
				if (nameModifier.endsWith("_")) nameModifier = nameModifier.substring(0, nameModifier.length()-1);
				
				if (isReactionBalanced(reactantBalance, productBalance)) {
					newReaction.name = newReaction.parentReaction.getCommonName() + nameModifier;
					newReactions.add(newReaction);
				}
			}
			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return newReactions;
	}
	
	private ListCombinationResults listCombinations2(ArrayList<NamedList> listOfNamedLists) {
		// This function takes in a list of lists and returns every possible combination of 1 item from each sublist.
		// Thus, if the lists [1,2,3], [4,5,6], and [7,8,9] were input, then the output would be
		// [1,4,7], [1,4,8], [1,4,8], [1,5,7], [1,5,8], [1,5,9] ...
		// This method was written as a way to instantiate general terms in a reaction. Each general term in a reaction has a list of possible
		// values, and every possible combination of terms is needed.
		// The position of the term in the output subarray is the same as the term's value array in the inputs main array.
		if (listOfNamedLists == null || listOfNamedLists.size() < 1) return new ListCombinationResults(new ArrayList<String>(), new ArrayList<ArrayList<String>>());
		
		NamedList namedList = listOfNamedLists.remove(0);
		ListCombinationResults results = listCombinations2(listOfNamedLists);
		results.nameList.add(namedList.name);
		ArrayList<ArrayList<String>> newListOfTuples = new ArrayList<ArrayList<String>>();
		
		if (results.listOfTuples.size() > 0) {
			for (String item : namedList.list) {
				for (ArrayList<String> tuple : results.listOfTuples) {
					tuple.add(item);
					newListOfTuples.add((ArrayList<String>)tuple.clone());
				}
			}
		} else {
			for (String item : namedList.list) {
				ArrayList<String> tuple = new ArrayList<String>();
				tuple.add(item);
				newListOfTuples.add(tuple);
			}
		}
		
		results.listOfTuples = newListOfTuples;
		
		return results;
		
//		if (listOfNamedLists.size() > 1) {
//			ArrayList<ArrayList<String>> listOfCombinations = new ArrayList<ArrayList<String>>();
//			NamedList list = listOfNamedLists.remove(0);
//			for (ArrayList<String> combinations : listCombinations(listOfNamedLists)) {
//				for (String item : list) {
//					ArrayList<String> combinationsCopy = (ArrayList<String>)combinations.clone();
//					combinationsCopy.add(0, item);
//					listOfTuples.add(combinationsCopy);
//				}
//			}
//			return listOfTuples;
//		}
		
		
		
		
//		for (String item : namedList.list) {
//			ArrayList<String> list = new ArrayList<String>();
//			list.add(item);
//			listOfNamedLists.add(list);
//		}
//		return listOfNamedLists;
	}
	
	private Metabolite generateMetabolite(Reaction origReaction, String slot, Frame origMetabolite, Frame newMetabolite) throws PtoolsErrorException {
		String compartment = conn.getValueAnnot(origReaction.getLocalID(), slot, origMetabolite.getLocalID(), "COMPARTMENT");
		if (compartment.equalsIgnoreCase("NIL")) compartment = defaultCompartment;
		
		int coeficient = 1;
		try {
			coeficient = Integer.parseInt(conn.getValueAnnot(origReaction.getLocalID(), slot, origMetabolite.getLocalID(), "COEFFICIENT"));
		} catch (Exception e) {
			coeficient = 1;
		}
		
		return new Metabolite(newMetabolite, compartment, coeficient);
	}
	
	private ArrayList<ReactionInstance> addBoundaryReactionsByCompartment(String compartment, ArrayList<Reaction> reactions, ArrayList<ReactionInstance> generatedReactions) {
		ArrayList<Frame> exchangeMetabolites = new ArrayList<Frame>();
		if (reactions == null && generatedReactions == null) {
			//?
		}
		
		if (reactions != null) {
			for (Reaction reaction : reactions) {
				try {
					ArrayList<String> allLeftIDs = reaction.getSlotValues("Left");
					ArrayList<String> allRightIDs = reaction.getSlotValues("Right");
					
					for (String leftID : allLeftIDs) {
						if (conn.getValueAnnot(reaction.getLocalID(), "Left", leftID, "COMPARTMENT").equalsIgnoreCase(compartment)) exchangeMetabolites.add(loadFrame(leftID));
					}
					for (String rightID : allRightIDs) {
						if (conn.getValueAnnot(reaction.getLocalID(), "Left", rightID, "COMPARTMENT").equalsIgnoreCase(compartment)) exchangeMetabolites.add(loadFrame(rightID));
					}
					
				} catch (PtoolsErrorException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (generatedReactions != null) {
			for (ReactionInstance reaction : generatedReactions) {
				ArrayList<Metabolite> allReactants = reaction.reactants;
				ArrayList<Metabolite> allProducts = reaction.products;
				
				for (Metabolite reactant : allReactants) {
					if (reactant.compartment.equalsIgnoreCase(compartment)) exchangeMetabolites.add(reactant.metabolite);
				}
				for (Metabolite product : allProducts) {
					if (product.compartment.equalsIgnoreCase(compartment)) exchangeMetabolites.add(product.metabolite);
				}
			}
		}
		
		
		// Generate exchange reactions
		ArrayList<ReactionInstance> exchangeReactions = new ArrayList<ReactionInstance>();
		for (Frame metabolite : exchangeMetabolites) {
			ArrayList<Metabolite> reactants = new ArrayList<Metabolite>();
			reactants.add(new Metabolite(metabolite, compartment, 1));
			ArrayList<Metabolite> products = new ArrayList<Metabolite>();
			products.add(new Metabolite(metabolite, "Boundary", 1));
			exchangeReactions.add(new ReactionInstance(null, null, metabolite.getLocalID() + "_exchange", true, reactants, products));
		}
		
		return exchangeReactions;
	}
	
	private SBMLDocument generateSBMLModel(SBMLDocument doc, ArrayList<ReactionInstance> reactionInstances) {
		Model model = doc.getModel();
		ArrayList<String> metabolites = new ArrayList<String>();
		ArrayList<String> compartments = new ArrayList<String>();
		
		try {
			// Create compartment list
			for (ReactionInstance reaction : reactionInstances) {
				ArrayList<Metabolite> reactantsProducts = new ArrayList<Metabolite>();
				reactantsProducts.addAll(reaction.reactants);
				reactantsProducts.addAll(reaction.products);
				for (Metabolite species : reactantsProducts) {
					if (!compartments.contains(species.compartment)) {
						Compartment compartment = model.createCompartment();
						compartment.setId(convertToSBMLSafe(species.compartment));
						compartment.setName(species.compartment);
//						if (compartment.setId(convertToSBMLSafe(species.compartment)) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
//						if (compartment.setName(species.compartment) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
						compartments.add(species.compartment);
					}
				}
			}
			
			// Create species list
			for (ReactionInstance reaction : reactionInstances) {
				ArrayList<Metabolite> reactantsProducts = new ArrayList<Metabolite>();
				reactantsProducts.addAll(reaction.reactants);
				reactantsProducts.addAll(reaction.products);
				for (Metabolite species : reactantsProducts) {
					if (!metabolites.contains(generateSpeciesID(species.metabolite.getLocalID(), species.compartment))) {
						Species newSpecies = model.createSpecies();
						String sid = generateSpeciesID(species.metabolite.getLocalID(), species.compartment);
						newSpecies.setId(sid);
						newSpecies.setName(species.metabolite.getCommonName());
						newSpecies.setCompartment(model.getCompartment(convertToSBMLSafe(species.compartment)).getId());
						newSpecies.setBoundaryCondition(false);
//						if (newSpecies.setId(sid) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
//						if (newSpecies.setName(species.metabolite.getCommonName()) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
//						if (newSpecies.setCompartment(model.getCompartment(convertToSBMLSafe(species.compartment)).getId()) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
//						if (newSpecies.setBoundaryCondition(false) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
						metabolites.add(sid);
						
						// Append Notes
						newSpecies.appendNotes("Palsson SID : \n");
						newSpecies.appendNotes("EcoCyc Frame ID : " + species.metabolite.getLocalID() + "\n");
						newSpecies.appendNotes("Chemical Formula : " + "\n");
					}
				}
			}
			
			// Create reaction list
			for (ReactionInstance reaction : reactionInstances) {
				org.sbml.libsbml.Reaction newReaction = model.createReaction();
				if (reaction.thisReactionFrame != null) newReaction.setId(generateReactionID(reaction.thisReactionFrame.getLocalID()));
				else if (reaction.parentReaction != null) newReaction.setId(generateReactionID(reaction.parentReaction.getLocalID()));
				else newReaction.setId(generateReactionID(reaction.name));
				newReaction.setName(reaction.name);
				newReaction.setReversible(reaction.reversible);
//				if (reaction.thisReactionFrame != null) {
//					if (newReaction.setId(convertToSBMLSafe(reaction.thisReactionFrame.getLocalID())) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
//				} else if (reaction.parentReaction != null) {
//					if (newReaction.setId(convertToSBMLSafe(reaction.parentReaction.getLocalID())) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
//				} else {
//					if (newReaction.setId(convertToSBMLSafe(reaction.name)) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
//				}
//				if (newReaction.setName(reaction.name) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
				
				for (Metabolite reactant : reaction.reactants) {
					String sid = generateSpeciesID(reactant.metabolite.getLocalID(), reactant.compartment);
					SpeciesReference ref = newReaction.createReactant();
					ref.setSpecies(sid);
					ref.setStoichiometry(reactant.stoichiometry);
//					if (ref.setSpecies(sid) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
//					if (ref.setStoichiometry(reactant.stoichiometry) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
//					if (newReaction.addReactant(ref) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
				}
				for (Metabolite product : reaction.products) {
					String sid = generateSpeciesID(product.metabolite.getLocalID(), product.compartment);
					SpeciesReference ref = newReaction.createProduct();
					ref.setSpecies(sid);
					ref.setStoichiometry(product.stoichiometry);
//					if (ref.setSpecies(sid) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
//					if (ref.setStoichiometry(product.stoichiometry) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
//					if (newReaction.addProduct(ref) != libsbml.LIBSBML_OPERATION_SUCCESS) throw new Exception();
				}
				
				// Kinetic Law
				ASTNode math = new ASTNode();
				math.setName("FLUX_VALUE");
				
				KineticLaw kl = newReaction.createKineticLaw();
				kl.setFormula("");
				kl.setMath(math);
				
				Parameter lb = kl.createParameter();
				lb.setId("LOWER_BOUND");
				if (newReaction.getReversible()) lb.setValue(-1000);
				else lb.setValue(0);
				lb.setUnits("mmol_per_gDW_per_hr");
				
				Parameter ub = kl.createParameter();
				ub.setId("UPPER_BOUND");
				ub.setValue(1000);
				ub.setUnits("mmol_per_gDW_per_hr");
				
				Parameter obj = kl.createParameter();
				obj.setId("OBJECTIVE_COEFFICIENT");
				obj.setValue(0);
				
				Parameter flux = kl.createParameter();
				flux.setId("FLUX_VALUE");
				flux.setValue(0);
				flux.setUnits("mmol_per_gDW_per_hr");
				
				// Append Notes
				newReaction.appendNotes("Palsson Reaction ID : \n");
				newReaction.appendNotes("EcoCyc Frame ID : \n");
				newReaction.appendNotes("Abbreviation : \n");
				newReaction.appendNotes("Synonyms : \n");
				newReaction.appendNotes("EC Number : \n");
				newReaction.appendNotes("SUBSYSTEM : \n");
				newReaction.appendNotes("Equation : \n");
				newReaction.appendNotes("Confidence Level : \n");
				if (reaction.thisReactionFrame != null) newReaction.appendNotes("Gene Rule : " + reactionGeneRule(reaction.thisReactionFrame.getLocalID()));
				else if (reaction.parentReaction != null) newReaction.appendNotes("Gene Rule : " + reactionGeneRule(reaction.parentReaction.getLocalID()));
				else newReaction.appendNotes("Gene Rule : ");
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return doc;
	}
	
	private ArrayList<ReactionInstance> reactionListToReactionInstances(ArrayList<Reaction> reactions) {
		ArrayList<ReactionInstance> reactionInstances = new ArrayList<ReactionInstance>();
		try {
			for (Reaction reaction : reactions) {
				ReactionInstance reactionInstance = new ReactionInstance(null, reaction, reaction.getCommonName(), reaction.isReversible(), new ArrayList<Metabolite>(), new ArrayList<Metabolite>());
				String reactionReactantSlot = reactionReactantSlot(reaction);
				for (String reactant : (ArrayList<String>)reaction.getSlotValues(reactionReactantSlot)) {
					Frame reactantFrame = loadFrame(reactant);
					reactionInstance.reactants.add(generateMetabolite(reaction, reactionReactantSlot, reactantFrame, reactantFrame));
				}
				String reactionProductSlot = reactionProductSlot(reaction);
				for (String product : (ArrayList<String>)reaction.getSlotValues(reactionProductSlot)) {
					Frame productFrame = loadFrame(product);
					reactionInstance.products.add(generateMetabolite(reaction, reactionProductSlot, productFrame, productFrame));
				}
				
				reactionInstances.add(reactionInstance);
			}
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		
		return reactionInstances;
	}
	
	private String reactionReactantSlot(Reaction reaction) throws PtoolsErrorException {
		if (reaction.getSlotValue("REACTION-DIRECTION") == null || !reaction.getSlotValue("REACTION-DIRECTION").equalsIgnoreCase("RIGHT-TO-LEFT")) {
			return "LEFT";
		} else {
			return "RIGHT";
		}
	}
	
	private String reactionProductSlot(Reaction reaction) throws PtoolsErrorException {
		if (reaction.getSlotValue("REACTION-DIRECTION") == null || !reaction.getSlotValue("REACTION-DIRECTION").equalsIgnoreCase("RIGHT-TO-LEFT")) {
			return "RIGHT";
		} else {
			return "LEFT";
		}
	}
	
	private String generateSpeciesID(String baseID, String compartment) {
		if (baseID.startsWith("_")) return convertToSBMLSafe(speciesPrefix + "" + baseID + "_" + compartmentAbrevs.get(compartment));
		else return convertToSBMLSafe(speciesPrefix + "_" + baseID + "_" + compartmentAbrevs.get(compartment));
	}
	
	private String generateReactionID(String baseID) {
		if (baseID.startsWith("_")) return convertToSBMLSafe(reactionPrefix + "" + baseID);
		else return convertToSBMLSafe(reactionPrefix + "_" + baseID);
	}
	
	
 	// Internal Classes
 	private class FilterResults {
		public ArrayList<Reaction> keepList;
		public ArrayList<Reaction> removedList;
		
		public FilterResults(ArrayList<Reaction> keepList, ArrayList<Reaction> removedList) {
			this.keepList = keepList;
			this.removedList = removedList;
		}
	}
 	
 	private class NamedList {
		public String name;
		public ArrayList<String> list;
		
		public NamedList(String name, ArrayList<String> list) {
			this.name = name;
			this.list = list;
		}
		
		/**
		A shallow test of equality. Test the names of two NamedLists for equality. Does not compare the list itself.
		@return true if both NamedLists have the name. 
		*/
		@Override public boolean equals(Object aThat) {
			//Based on example at http://www.javapractices.com/topic/TopicAction.do?Id=17
			
		    //Check for self-comparison
		    if (this == aThat) return true;

		    //Check for similar class
		    if (!(aThat instanceof NamedList)) return false;
		    
		    //Cast to native type
		    NamedList that = (NamedList)aThat;

		    //Compare frame IDs
		    return this.name.equals(that.name);
		  }

		@Override public int hashCode() {
			return this.name.hashCode();
		  }
	}
 	
 	private class ListCombinationResults {
 		public ArrayList<String> nameList;
		public ArrayList<ArrayList<String>> listOfTuples;
		
		public ListCombinationResults(ArrayList<String> nameList, ArrayList<ArrayList<String>> listOfTuples) {
			this.nameList = nameList;
			this.listOfTuples = listOfTuples;
		}
 	}
 	
 	private class ReactionInstance {
 		public Reaction parentReaction;
 		public Reaction thisReactionFrame;
 		public String name;
 		public boolean reversible;
		public ArrayList<Metabolite> reactants;
		public ArrayList<Metabolite> products;
		
		public ReactionInstance(Reaction parentReactionFrame, Reaction thisReactionFrame, String name, boolean reversible, ArrayList<Metabolite> reactants, ArrayList<Metabolite> products) {
			this.parentReaction = parentReactionFrame;
			this.thisReactionFrame = thisReactionFrame;
			this.name = name;
			this.reversible = reversible;
			this.reactants = reactants;
			this.products = products;
		}
 	}
 	
 	private class Metabolite {
 		public Frame metabolite;
		public String compartment;
		public int stoichiometry;
		
		public Metabolite(Frame metabolite, String compartment, int stoichiometry) {
			this.metabolite = metabolite;
			this.compartment = compartment;
			this.stoichiometry = stoichiometry;
		}
 	}
 	
 	private class InstantiationResults {
 		public ArrayList<ReactionInstance> instantiatedReactions;
 		public ArrayList<Frame> genericReactionsFound;
 		public ArrayList<Frame> genericReactionsFailedToInstantiate;
		
		public InstantiationResults(ArrayList<ReactionInstance> instantiatedReactions, ArrayList<Frame> genericReactionsFound, ArrayList<Frame> genericReactionsFailedToInstantiate) {
			this.instantiatedReactions = instantiatedReactions;
			this.genericReactionsFound = genericReactionsFound;
			this.genericReactionsFailedToInstantiate = genericReactionsFailedToInstantiate;
		}
 	}
}



// Reactions with non-existing metabolites 
//	RXN0-1321
//	CYTC-OX-RXN

// Reactions with two of the same generic metabolite on the same side
//	3.1.26.12-RXN
























// Concept Functions
