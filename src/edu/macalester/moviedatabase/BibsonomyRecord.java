package edu.macalester.moviedatabase;

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
