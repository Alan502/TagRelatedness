package edu.macalester.moviedatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CollaborativeMutualInformation implements TagSimilarityMeasure {

	private CollaborativeDatabase db;
	HashMap<String, ArrayList<HashMap<String, HashSet<String>>>>  userMap;
	public CollaborativeMutualInformation(CollaborativeDatabase database){
		db = database;
		userMap = db.getUserMap();
	}
	
	public double calculateSimilarity(String tag1, String tag2) {
		double similarity = 0.0;
		for(String user : userMap.keySet()){
			HashMap<String, HashSet<String>> tagsMap = userMap.get(user).get(1);
			HashMap<String, HashSet<String>> moviesMap = userMap.get(user).get(0);
			
			HashSet<String> movieSet1 = tagsMap.get(tag1);	
			HashSet<String> movieSet2 = tagsMap.get(tag2);
			
			if(null == movieSet1 || null == movieSet2)
				continue;
			
			double totalTags = tagsMap.keySet().size();
			similarity += Math.log(totalTags/ totalTags+1);
			
			for(String comparingMovie : movieSet1){
				double marginalProbability1 = (moviesMap.get(comparingMovie).size())/(totalTags+1);
				for(String comparedMovie : movieSet2){
					double marginalProbability2 = moviesMap.get(comparedMovie).size()/(totalTags+1);
					
					HashSet<String> tagsSet1 = (HashSet<String>) moviesMap.get(comparingMovie).clone();
					HashSet<String> tagsSet2 = (HashSet<String>) moviesMap.get(comparedMovie).clone();
					
					/* Add the joint probability for the intersections
					 */
					tagsSet1.retainAll(tagsSet2);
					
					double jointProbability = (double) tagsSet1.size()/(totalTags+1);
					
					similarity += jointProbability != 0 ? jointProbability * Math.log(jointProbability / (marginalProbability1* marginalProbability2)) : 0;
				}
				
			}	
		}
		return similarity;
	}

}
