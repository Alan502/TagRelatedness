package edu.macalester.tagrelatedness;
/**
 * This class is used to sort the bibsonomy entries into their frequencies. Avoid using it directly.
 * @author alan
 *
 */
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
