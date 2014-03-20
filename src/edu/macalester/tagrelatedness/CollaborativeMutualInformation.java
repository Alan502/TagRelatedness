package edu.macalester.tagrelatedness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
/**
 * The CollaborativeMutualInformation similarity measure is able to extract signals of similarity on the basis of the same user
 * tagging two different resources. For reference of the construction of this algorithm look at: www2009.org/proceedings/pdf/p641.pdf
 * @author Alan Morales
 * @see ColaborativeDatabase
 */
public class CollaborativeMutualInformation implements TagSimilarityMeasure {

	private CollaborativeDatabase db;
	HashMap<String, ArrayList<HashMap<String, HashSet<String>>>>  userMap;
	public CollaborativeMutualInformation(CollaborativeDatabase database){
		db = database;
		userMap = db.getUserMap();
	}
	/**
	 * Calculates the similarity between two tags in the database.
	 * @param tag1 the first tag
	 * @param tag2 the second tag
	 */
	public double calculateSimilarity(String tag1, String tag2) {
		double similarity = 0.0;
		for(String user : userMap.keySet()){
			HashMap<String, HashSet<String>> tagsMap = userMap.get(user).get(1);
			HashMap<String, HashSet<String>> resourcesMap = userMap.get(user).get(0);
			
			HashSet<String> resourceSet1 = tagsMap.get(tag1);	
			HashSet<String> resourceSet2 = tagsMap.get(tag2);
			
			if(null == resourceSet1 || null == resourceSet2)
				continue;
			
			double totalTags = tagsMap.keySet().size();
			similarity += Math.log(totalTags/ (totalTags+1));
			
			for(String comparingResource : resourceSet1){
				double marginalProbability1 = (resourcesMap.get(comparingResource).size())/(totalTags+1);
				for(String comparedResource : resourceSet2){
					double marginalProbability2 = resourcesMap.get(comparedResource).size()/(totalTags+1);
					
					HashSet<String> tagsSet1 = (HashSet<String>) resourcesMap.get(comparingResource).clone();
					HashSet<String> tagsSet2 = (HashSet<String>) resourcesMap.get(comparedResource).clone();
					
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
