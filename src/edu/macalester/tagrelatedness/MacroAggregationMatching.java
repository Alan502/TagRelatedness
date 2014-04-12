package edu.macalester.tagrelatedness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
/**
 * This MacroAggregation similarity determines tag similarity by making each user have a vote on a projectional database.
 * For more reference on the construction of this algorithm look at: www2009.org/proceedings/pdf/p641.pdf
 * @author alan
 *
 */
public class MacroAggregationMatching implements TagSimilarityMeasure{
	
	CollaborativeDatabase db;
	
	public MacroAggregationMatching(CollaborativeDatabase database){
		db = database;
	}
	/**
	 * Calculates the similarity between two tags in the CollaborativeDatabase.
	 * @param tag1 first tag
	 * @param tag2 second tag
	 */
	@SuppressWarnings("unchecked")
	public double calculateSimilarity(String tag1, String tag2) {
		HashMap<String, ArrayList<HashMap<String, HashSet<String>>>> userMap = db.getUserMap();
		double similarity = 0.0;
				
		for(String user : userMap.keySet()){
		
			HashMap<String, HashSet<String>> tagsMap = userMap.get(user).get(1);
			
			if(null == tagsMap.get(tag1) || null == tagsMap.get(tag2))
				continue;
			
			HashSet<String> resources1 = (HashSet<String>) tagsMap.get(tag1).clone();
			HashSet<String> resources2 = (HashSet<String>) tagsMap.get(tag2).clone();
			
			resources1.retainAll(resources2);
						
			HashMap<String, HashSet<String>> resourcesMap = userMap.get(user).get(0);
			
			final double totalResources = resourcesMap.keySet().size();
			
			for(String resource : resources1 ){
				double associated = resourcesMap.get(resource).size();
				similarity += Math.log(associated / totalResources );
			}
			
			
		}
		return similarity*-1;
		
	}
	
}
