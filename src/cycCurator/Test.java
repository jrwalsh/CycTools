package cycCurator;

import java.util.ArrayList;

import edu.iastate.javacyco.Complex;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.Gene;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.Protein;
import edu.iastate.javacyco.PtoolsErrorException;

public class Test {
	static private JavacycConnection conn = null;
	
	public static void main(String[] args) {
		Long start = System.currentTimeMillis();
		test();
		Long stop = System.currentTimeMillis();
		Long runtime = (stop - start) / 1000;
		System.out.println("Runtime is " + runtime + " seconds.");
	}
	
	private static void test() {
//		System.out.println("Testing CycCurator on THT Server");
//		conn = new JavacycConnection("tht.vrac.iastate.edu",4444);
//		conn.selectOrganism("CBIRC");
		
		System.out.println("Testing CycCurator on Local Server");
		conn = new JavacycConnection("localhost",4444);
		conn.selectOrganism("CORN");
		
		try {
			// CornCyc Test
			
			Frame f = Frame.load(conn, "|GO:0000003|");
			f.print();
			
//			ArrayList<String> instances = conn.getClassAllInstances("|Gene-Ontology-Terms|");
//			for (String instance : instances) {
//				Frame.load(conn, instance).print();
//				
//			}
			
			
////			Gene gene = (Gene) Gene.load(conn, "GDQC-106529");
//			Protein protein = (Protein) Protein.load(conn, "GDQC-106529-MONOMER");
//			
//			System.out.println(conn.getValueAnnots(protein.getLocalID(), "GO-TERMS", "|GO:0000003|", "CITATIONS").toString());
////			ArrayList<String> values = new ArrayList<String>();
////			values.add("|GO:0000003|");
////			protein.putSlotValues("GO-TERMS", values);
////			conn.addAnnotation(protein.getLocalID(), "GO-TERMS", "|GO:0000003|", "CITATIONS", "\"125264\\:EV-COMP\\:3548526155\\:jesse\"");
//////			putAnnotations(String frame,String slot,String value,String annotLabel,ArrayList annotValues)
////			protein.commit();
////			
//////			gene.print();
//			protein.print();
			
			
			
//			|GO:0000287|
//			--CITATIONS	"16866375:EV-EXP-IDA:3501264281:keseler"
			
			
			
			
//====================================================================================================================\\
			
			// EcoCyc Test
			
//			Gene gene = (Gene) Gene.load(conn, "EG10700"); // Gene -> PRODUCT -> Protein
//			Protein protein = (Protein) Protein.load(conn, "6PFK-2-MONOMER"); // Protein -> COMPONENT-OF -> Complex
//			Complex complex = (Complex) Complex.load(conn, "6PFK-2-CPX");
//			
//			gene.print();
//			protein.print();
//			complex.print();
			
//			Frame.load(conn, "CREDITS").print();
//			Frame.load(conn, "LAST-UPDATE").print();
//			Frame.load(conn, "GO-TERMS").print();
//			Frame.load(conn, "EV-COMP").print();
//			for (Frame f : Frame.allFrames(conn)) {
//				if (f.getLocalID().toString().startsWith("EV-")) System.out.println(f.getLocalID());
//			}
//			|Gene-Ontology-Terms|
			
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
	}
}
