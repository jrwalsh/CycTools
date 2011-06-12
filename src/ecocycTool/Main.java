package ecocycTool;

import edu.iastate.javacyco.PtoolsErrorException;

/**
 * Main class for the ToolBox and PGDBUpdater classes.
 * 
 * @author Jesse Walsh
 *
 */
public class Main {

	/**
	 * Main method for the ToolBox and PGDBUpdater classes.  This method initializes a connection object and calls the run() method.
	 * 
	 * @param args Not Used
	 */
	public static void main(String[] args) {
		Long start = System.currentTimeMillis();
		sandBox();
//		testToolBox();
//		testPGDBUpdater();
//		run(args);
		Long stop = System.currentTimeMillis();
		Long runtime = (stop - start) / 1000;
		System.out.println("Runtime is " + runtime + " seconds.");
	}

	/**
	 * This method initializes a ToolBox object and calls its methods.  Currently used for testing.
	 */
	public static void testToolBox() {
//		ToolBox tb = new ToolBox(ToolBox.connectionStringLocal, ToolBox.defaultPort, ToolBox.organismStringCBIRC);
		ToolBox tb = new ToolBox(ToolBox.connectionStringLocal, ToolBox.defaultPort, ToolBox.organismStringK12);
//		ToolBox tb = new ToolBox(ToolBox.connectionStringEcoServer, ToolBox.defaultPort, ToolBox.organismStringCBIRC);
//		ToolBox tb = new ToolBox(ToolBox.connectionStringEcoServer, ToolBox.defaultPort, ToolBox.organismStringARA);

		try {
			tb.tester();
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method initializes a PGDBUpdater object and calls its methods.  Currently used for testing.
	 */
	public static void testPGDBUpdater() {
		PGDBUpdater updater = new PGDBUpdater();

		try {
			updater.tester();
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method initializes a SandBox object and calls its methods.  Currently used for testing.
	 */
	public static void sandBox() {
		System.out.println("Testing SandBox Now");
		SandBox sbox = new SandBox(SandBox.connectionStringEcoServer, SandBox.defaultPort, SandBox.organismStringARA);

		try {
			sbox.tester();
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method initializes a ToolBox object and calls its methods.  Currently used for testing.
	 */
	public static void run(String[] args) {
		String connectionString = ToolBox.connectionStringLocal;
		String organism = ToolBox.organismStringCBIRC;
		System.out.println("Connecting to " + connectionString + ". Using organism " + organism + ".");
		ToolBox tb = new ToolBox(connectionString, ToolBox.defaultPort, organism);
		tb.run(args);
	}
}
