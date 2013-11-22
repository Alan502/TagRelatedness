package edu.macalester.moviedatabase;

import java.util.HashMap;
import java.util.HashSet;

public class OverlapSimilarityDatabase extends MovieDatabase implements TagSimilarityMeasure{

	public OverlapSimilarityDatabase(){
		tagsMap = new HashMap<String, HashSet<String>>();
		moviesMap = new HashMap<String, HashSet<String>>();
	}
	
	public int calculateSimilarity(String tag1, String tag2) {
		HashSet<String> movieSet1 = (HashSet<String>) tagsMap.get(tag1).clone();
		HashSet<String> movieSet2 = (HashSet<String>) tagsMap.get(tag2).clone();
		
		int minimum = movieSet1.size() - movieSet2.size() > 0 ? movieSet1.size() : movieSet2.size();
		
		movieSet1.retainAll(movieSet2);
		int intersection =  movieSet1.size();
		
		return intersection/minimum;
	}


}
