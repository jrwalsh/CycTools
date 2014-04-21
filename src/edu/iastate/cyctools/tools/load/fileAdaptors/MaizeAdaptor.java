package edu.iastate.cyctools.tools.load.fileAdaptors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.cyctools.tools.load.model.GOAnnotation;
import edu.iastate.cyctools.tools.load.model.GOTermAnnotationUpdate;
import edu.iastate.cyctools.tools.load.model.SlotUpdate;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

// Designed for the MaizeGDB teams curation updates.  To be designed to handle their specifically formated input file.
@SuppressWarnings("unchecked")
public class MaizeAdaptor implements FileAdaptor {
	private boolean append;
	private boolean ignoreDuplicates;
	@SuppressWarnings("unused")
	private String multipleValueDelimiter; //Contains this parameter to conform to the interface, but this custom adaptor does not allow multiple values in a column
	
	public MaizeAdaptor() {
		append = true;
		ignoreDuplicates = true;
		multipleValueDelimiter = "$"; //TODO convert multiple value entries into arrays before insert
	}
	
	// Assumes exact format for file with a defined column for each piece of info
	// Assumes one value per cell
	// Assumes headers are ignored, effectively skipping row one
	// Assumes frameID is in first column
	public ArrayList<AbstractFrameEdit> tableToFrameUpdates(TableModel tb) {
		ArrayList<AbstractFrameEdit> frameUpdates = new ArrayList<AbstractFrameEdit>();
		
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			String frameID = (String) tb.getValueAt(rowIndex, 0);
			String goTerm = (String) tb.getValueAt(rowIndex, 1);
			String pubMedID = (String) tb.getValueAt(rowIndex, 2);
			String evCode = (String) tb.getValueAt(rowIndex, 3);
			String timeStampString = (String) tb.getValueAt(rowIndex, 4);
			String curator = (String) tb.getValueAt(rowIndex, 5);
			
			JavacycConnection conn = new JavacycConnection("jrwalsh.student.iastate.edu", 4444);//TODO attach the default connection object here
			conn.selectOrganism("CORN");
			String encodedTime = encodeTimeStampString(timeStampString, conn);
			
			if (!goTerm.startsWith("|")) goTerm = "|" + goTerm;
			if (!goTerm.endsWith("|")) goTerm = goTerm + "|";
			frameUpdates.add(new SlotUpdate(frameID, "GO-TERMS", goTerm, append, ignoreDuplicates, new int[] {rowIndex}));
			
			ArrayList<GOAnnotation> goAnnotationValues = new ArrayList<GOAnnotation>();
			goAnnotationValues.add(new GOAnnotation(pubMedID, evCode, encodedTime, curator));
			frameUpdates.add(new GOTermAnnotationUpdate(frameID, "GO-TERMS", goTerm, "CITATIONS", goAnnotationValues, append, ignoreDuplicates, new int[] {rowIndex}));
			
//			ArrayList<String> annotValues = new ArrayList<String>();
//			annotValues.add("\"" + pubMedID + ":" + evCode + ":" + encodedTime + ":" + curator + "\"");
//			frameUpdates.add(new AnnotationUpdate(frameID, "GO-TERMS", goTerm, "CITATIONS", annotValues, append, ignoreDuplicates, new int[] {rowIndex}));
		}
		return frameUpdates;
	}
	
	public DefaultTableModel framesToTable(ArrayList<Frame> frames, JavacycConnection conn) throws PtoolsErrorException {
		Object[] header = new String[]{"FRAMEID", "GO-TERM", "Citation"};
		ArrayList<Object[]> dataArray = new ArrayList<Object[]>();
		
		for (Frame frame : frames) {
			String frameID = frame.getLocalID();
			ArrayList<String> goTerms = (ArrayList<String>) frame.getSlotValues("GO-TERMS");
			for (String goTerm : goTerms) {
				ArrayList<String> citations = (ArrayList<String>) conn.getValueAnnots(frameID, "GO-TERMS", goTerm, "CITATIONS");
				for (String citation : citations) {
					String decodedCitation = decodeCitationTimeStamp(citation, conn);
					dataArray.add(new String[]{frameID, goTerm, decodedCitation});
				}
				if (citations.isEmpty()) {
					dataArray.add(new String[]{frameID, goTerm, ""});
				}
			}
			if (goTerms.isEmpty()) {
				dataArray.add(new String[]{frameID, "", ""});
			}
		}
		
		Object[][] data = new Object[dataArray.size()][header.length];
		for (int i=0; i<dataArray.size(); i++) {
			data[i] = dataArray.get(i);
		}
		
		DefaultTableModel dtm = new DefaultTableModel(data, header);
		return dtm;
	}
	
	private String decodeCitationTimeStamp(String citation, JavacycConnection conn) {
		if (citation == null || citation.equalsIgnoreCase("")) return "";
		
		String parsedString = "";
		try {
			String[] citationArray = citation.split(":");
			int timeStampIndex;
			if (citationArray.length == 4) timeStampIndex = 2;
			else if (citationArray.length == 5) timeStampIndex = 3;
			else {
				return citation;
			}
			
			String encodedTime = citationArray[timeStampIndex];
			SimpleDateFormat simpleDate = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss");
			String decodedTime = simpleDate.format(conn.decodeTimeStamp(encodedTime).getTime());
			
			for (int i = 0; i < citationArray.length ; i++) {
				if (i == timeStampIndex && i == citationArray.length-1) parsedString += decodedTime;
				else if (i == timeStampIndex) parsedString += decodedTime + ":";
				else if (i == citationArray.length-1) parsedString += citationArray[i];
				else parsedString += citationArray[i] + ":";
			}
		} catch (Exception e) {
			System.err.println("Error parsing citation: " + citation);
			return citation;
		}
		return parsedString;
	}
	
	
	// Agreed upon format is dd-mm-yyyy hh-mm-ss
	private String encodeTimeStampString(String timeStampString, JavacycConnection conn) {
		String encodedTime = "";
		
		if (timeStampString == null || timeStampString.length() == 0) {
			return "";
		}
		
		try {
			String date = timeStampString.split(" ")[0];
			String time = timeStampString.split(" ")[1];
			
			String month = date.split("-")[0];
			String day = date.split("-")[1];
			String year = date.split("-")[2];
			
			String hour = time.split("-")[0];
			String minute = time.split("-")[1];
			String second = time.split("-")[2];
			
			encodedTime = conn.encodeTimeStamp(second, minute, hour, day, month, year);
		} catch (Exception e) {
			System.err.println("Failed to encode timestamp " + timeStampString);
			e.printStackTrace();
		}
		
		return encodedTime;
	}

	@Override
	public void setAppend(boolean append) {
		this.append = append;
	}

	@Override
	public void setIgnoreDuplicates(boolean ignoreDuplicates) {
		this.ignoreDuplicates = ignoreDuplicates;
	}

	@Override
	public void setMultipleValueDelimiter(String multipleValueDelimiter) {
		this.multipleValueDelimiter = multipleValueDelimiter;
	}
}
