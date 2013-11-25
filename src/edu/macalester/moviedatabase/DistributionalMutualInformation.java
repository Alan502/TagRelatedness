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
		
		final double marginalProbability1 = ( (double) moviesList1.size() )/( (double) db.getTotalEntries() );
		final double marginalProbability2 = ( (double) moviesList2.size() )/( (double) db.getTotalEntries() );
		double similarity = 0.0;
		
		for(String comparingMovie : movieSet1){
			int list1Count = 0;
			int list2Count = 0;
			
			for(String movie : moviesList1){
				if(comparingMovie.equals(movie))
					list1Count++;
			}
			
			for(String movie : moviesList2){
				if(comparingMovie.equals(movie))
					list2Count++;
			}
			
			double minimum = list1Count - list2Count > 0 ? list2Count : list1Count;		
			double jointProbability = ( minimum )/( (double) db.getTotalEntries()) ;
			
			similarity+= jointProbability * Math.log(jointProbability / (marginalProbability1*marginalProbability2));	
		}
		
		return similarity;
	}
	

}
