package edu.iastate.cyctools.tools.load.model;

public class GOAnnotation {
	private String pubmedID;
	private String evCode;
	private String timeStampString;
	private String curator;
	
	public GOAnnotation(String pubmedID, String evCode, String timeStampString, String curator) {
		this.pubmedID = pubmedID;
		this.evCode = evCode;
		this.timeStampString = timeStampString;
		this.curator = curator;
	}
	
	public String getPubmedID() {
		return pubmedID;
	}

	public String getEvCode() {
		return evCode;
	}

	public String getTimeStampString() {
		return timeStampString;
	}

	public String getCurator() {
		return curator;
	}
}