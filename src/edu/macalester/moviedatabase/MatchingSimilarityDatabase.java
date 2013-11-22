package edu.macalester.moviedatabase;

import java.util.HashMap;
import java.util.HashSet;

public class MatchingSimilarityDatabase extends MovieDatabase implements TagSimilarityMeasure{
	
	public MatchingSimilarityDatabase(){
		tagsMap = new HashMap<String, HashSet<String>>();
		moviesMap = new HashMap<String, HashSet<String>>();
	}
	
	public int calculateSimilarity(String tag1, String tag2) {
		HashSet<String> movieSet1 = (HashSet<String>) tagsMap.get(tag1).clone();
		HashSet<String> movieSet2 = (HashSet<String>) tagsMap.get(tag2).clone();
		
		movieSet1.retainAll(movieSet2);
		return movieSet1.size();
	}
}
