package edu.macalester.moviedatabase;


import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		ProjectionalDatabase database = new ProjectionalDatabase();
		
		database.intializeMovieTags("ml-10M100K/tags.dat");
		
		ProjectionalOverlap similarityMeasure = new ProjectionalOverlap(database);
		
		FileWriter writer = null;
		
		try {
			writer = new FileWriter("tags.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		writer.append('"' + " Tag 1 " + '"'+ ',' + '"' + " Tag 2 " + '"' + " , " + "Similarity");
		writer.append('\n');
		
		int i = 0;
		LinkedList<String> tags = new LinkedList<String>();
		for(String tag : database.getTagsSet()){
			tags.add(tag);
		}
		
		for(String comparingTag : tags){
			i++;
			for(String comparedTag : tags.subList(i, tags.size())){
							
				double cc = similarityMeasure.calculateSimilarity(comparingTag, comparedTag);
				
				if(cc != 0){
					
					// Remove newlines, commas and apostrophes that may distort the CSV file when being written.							
					writer.append('"' + comparingTag.replace('"', ' ').replace('\n', ' ').replace(',', ' ') + '"'+ ',' + '"' + comparedTag.replace('"', ' ').replace('\n', ' ').replace(',', ' ') + '"' + " , " + cc);
					writer.append('\n');
				}
				
			}
		}
				
		writer.flush();
	    writer.close();
		
	}

}
