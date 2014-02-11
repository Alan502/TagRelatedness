package edu.macalester.moviedatabase;

public class BibsonomyEntry implements Comparable<BibsonomyEntry>{
	

	String contentID;
	int frequency;
	
	public BibsonomyEntry(String content, int freq){
		contentID = content;
		frequency = freq;
	}

	public int compareTo(BibsonomyEntry o) {
		return frequency - o.frequency;
	}

}
