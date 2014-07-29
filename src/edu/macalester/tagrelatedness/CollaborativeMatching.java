package edu.macalester.tagrelatedness;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
			int totalTags = tagsMap.keySet().size();
			
			if(null == tagsMap.get(tag1) || null == tagsMap.get(tag2))
				continue;
			
			HashMap<String, HashSet<String>> resourcesMap = userMap.get(user).get(0);
			
			double userSimilarity = 0.0;
			
			for(String resource : resourcesMap.keySet()){
				HashSet<String> tags = resourcesMap.get(resource);
				
				if(tags.contains(tag1) && tags.contains(tag2)){
					userSimilarity += Math.log(
							( (double) tags.size() ) /
							( ( (double) totalTags ) + 1.0 )
							);
				}else{
					continue;
				}
								
			}
			
			similarity += -userSimilarity;
			
		}
		
		return similarity; //rounding is necessary to match the results given at: www2009.org/proceedings/pdf/p641.pdf
		
	}
	
}
