package edu.macalester.moviedatabase;

public class EntryFrequency implements Comparable<EntryFrequency>{
	
	String contentID;
	int frequency;
	
	public EntryFrequency(String content, int freq){
		contentID = content;
		frequency = freq;
	}

	public int compareTo(EntryFrequency o) {
		return frequency - o.frequency;
	}

}
