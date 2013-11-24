package edu.macalester.moviedatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DistributionalMutualInformation implements TagSimilarityMeasure{

	private DistributionalDatabase db;
	public DistributionalMutualInformation(DistributionalDatabase database){
		db = database;
	}
	
	public double calculateSimilarity(String tag1, String tag2) {
		HashMap<String, ArrayList<String>> tagsMap = db.getTagsMap();
		
		ArrayList<String> moviesList1 = tagsMap.get(tag1);
		ArrayList<String> moviesList2 = tagsMap.get(tag2);
		
		HashSet<String> movieSet1 = new HashSet<String>();
		for(String movie : moviesList1)
			movieSet1.add(movie);
		
		HashSet<String> movieSet2 = new HashSet<String>();
		for(String movie : moviesList2)
			movieSet2.add(movie);
		
		movieSet1.retainAll(movieSet2); // movieSet1 now holds the intersection between movieSet1 and movieSet2
		
		double jointProbability = 0.0;
		
		for(String intersectingMovie : movieSet1){
			
			int list1Count = 0;
			int list2Count = 0;
			
			for(String movie : moviesList1){
				if(intersectingMovie.equals(movie))
					list1Count++;
			}
			
			for(String movie : moviesList2){
				if(intersectingMovie.equals(movie))
					list2Count++;
			}
			
			int minimum = list1Count - list2Count > 0 ? list1Count : list2Count;
			
			jointProbability += ( (double) minimum )/( (double) db.getTotalEntries()) ;				
			
		}
		
		double marginalProbability1 = ( (double) moviesList1.size() )/( (double) db.getTotalEntries() );
		double marginalProbability2 = ( (double) moviesList2.size() )/( (double) db.getTotalEntries() );
				
		return jointProbability * Math.log(jointProbability / (marginalProbability1*marginalProbability2));
	}
	

}
