package edu.macalester.moviedatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CollaborativeMatching implements TagSimilarityMeasure{
	
	CollaborativeDatabase db;
	
	public CollaborativeMatching(CollaborativeDatabase database){
		db = database;
	}
	@SuppressWarnings("unchecked")
	public double calculateSimilarity(String tag1, String tag2) {
		HashMap<String, ArrayList<HashMap<String, HashSet<String>>>> userMap = db.getUserMap();
		double similarity = 0.0;
				
		for(String user : userMap.keySet()){
		
			HashMap<String, HashSet<String>> tagsMap = userMap.get(user).get(1);
			
			if(null == tagsMap.get(tag1) || null == tagsMap.get(tag2))
				continue;
			
			HashSet<String> movies1 = (HashSet<String>) tagsMap.get(tag1).clone();
			HashSet<String> movies2 = (HashSet<String>) tagsMap.get(tag2).clone();
			
			double totalTags = tagsMap.keySet().size();
			similarity += Math.log(totalTags/ (totalTags+1) );	
			
			movies1.retainAll(movies2);
						
			HashMap<String, HashSet<String>> moviesMap = userMap.get(user).get(0);
						
			for(String movie : movies1 ){
				double associated = moviesMap.get(movie).size();
				similarity += Math.log(associated / (totalTags + 1) );
			}
			
			
		}
		return similarity*-1;
		
	}
	
}
