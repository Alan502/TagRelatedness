package edu.macalester.tagrelatedness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;
/**
 * The CollaborativeMatching similarity measure is able to extract signals of similarity on the basis of the same user
 * tagging two different resources. For reference of the construction of this algorithm look at: www2009.org/proceedings/pdf/p641.pdf
 * @author Alan Morales
 * @see CollaborativeDatabase
 */
public class CollaborativeMatching implements TagSimilarityMeasure{
	
	CollaborativeDatabase db;
	
	public CollaborativeMatching(CollaborativeDatabase database){
		db = database;
	}
	/**
	 * Calculate the CollaborativeMatching similarity between two tags in the CollaborativeDatabase.
	 * @param tag1 the first tag
	 * @param tag2 the secnod tag
	 */
	@SuppressWarnings("unchecked")
	public double calculateSimilarity(String tag1, String tag2) {
		HashMap<String, ArrayList<HashMap<String, HashSet<String>>>> userMap = db.getUserMap();
		double similarity = 0.0;
				
		for(String user : userMap.keySet()){
		
			HashMap<String, HashSet<String>> tagsMap = userMap.get(user).get(1);
			
			if(null == tagsMap.get(tag1) || null == tagsMap.get(tag2))
				continue;			
			
			HashMap<String, HashSet<String>> resourcesMap = userMap.get(user).get(0);
			
			double totalTags = (double) tagsMap.size();
			
			similarity += Math.log(totalTags/ (totalTags+1));				
			
			HashSet<String> resources1 = (HashSet<String>) tagsMap.get(tag1).clone();
			HashSet<String> resources2 = (HashSet<String>) tagsMap.get(tag2).clone();
			
			resources1.retainAll(resources2);
			
			
			for(String resource : resources1 ){
				double associated = (double) resourcesMap.get(resource).size();
				similarity += Math.log(associated / (totalTags+1) );					
			}
			
			
		}
		return similarity*-1;
		
	}
	
}
