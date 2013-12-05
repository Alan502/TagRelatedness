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
		
		CollaborativeDatabase database = new CollaborativeDatabase();
		
		database.intializeMovieTags("ml-10M100K/tags.dat");
		
		CollaborativeMutualInformation similarityMeasure = new CollaborativeMutualInformation(database);
		
		FileWriter writer = null;
		
		try {
			writer = new FileWriter("tags.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		writer.append('"' + " Tag 1 " + '"'+ ',' + '"' + " Tag 2 " + '"' + " , " + "Similarity");
		writer.append('\n');
		
		LinkedList<String> tags = new LinkedList<String>(database.getTagsSet());
						
		int threads  = 12;
		
		for(int i = 0; i < threads; i++){
			new TagCalculationProcedure<>(similarityMeasure, tags, tags.size()/threads * i, tags.size()/threads * i+1, writer);
		}
				
		writer.flush();
	    writer.close();
		
	}

}
