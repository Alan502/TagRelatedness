package edu.macalester.tagrelatedness;
/**
 * BisonomyRecord class holds the entries from the Bibsonomy dataset. Each record has a content ID
 * and a line that holds all the other information from the entry. It is used for the evaluation
 * of the results but you may want to avoid using it otherwise.
 */
public class BibsonomyRecord implements Comparable<BibsonomyRecord> {
	String contentID;
	String line;
	
	public BibsonomyRecord(String content, String infoLine){
		contentID = content;
		line = infoLine;
	}

	@Override
	public int compareTo(BibsonomyRecord o) {
		return this.contentID.compareTo(o.contentID);
	}
}
