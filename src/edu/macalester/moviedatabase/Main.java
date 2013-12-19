package edu.macalester.moviedatabase;


import java.awt.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		CollaborativeDatabase database = new CollaborativeDatabase();
		
		database.intializeMovieTags("ml-10M100K/tags.dat");
		
		final CollaborativeMatching similarityMeasure = new CollaborativeMatching(database);
		
		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter("tags.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fWriter.append('"' + " Tag 1 " + '"'+ ',' + '"' + " Tag 2 " + '"' + " , " + "Similarity");
		fWriter.append('\n');
		
		final LinkedList<String> tags = new LinkedList<String>(database.getTagsSet());
						
		int threads  = 12;
		
		HashSet<TagCalculationProcedure> procedures = new HashSet<TagCalculationProcedure>();
		final FileWriter writer = fWriter;
		ParallelForEach.loop(tags,
				threads,
				new Procedure<String>() {
                    @Override
                    public void call(String comparingTag) throws Exception {
                    	int start = tags.indexOf(comparingTag);
                    	for(String comparedTag : tags.subList(start, tags.size() )){
							
            				double cc = similarityMeasure.calculateSimilarity(comparingTag, comparedTag);
            				
            				if(cc != 0){
            					
            					// Remove newlines, commas and apostrophes that may distort the CSV file when being written.
            					synchronized(writer){
            					writer.append('"' + comparingTag.replace('"', ' ').replace('\n', ' ').replace(',', ' ') + '"'+ ',' + '"' + comparedTag.replace('"', ' ').replace('\n', ' ').replace(',', ' ') + '"' + " , " + cc);
            					writer.append('\n');
            					}
            				}
            				
            			}
                    }
                }
				);
		for(int i = 0; i < threads; i++){
			procedures.add(new TagCalculationProcedure<>(similarityMeasure, tags, tags.size()/threads * i, tags.size()/threads * (i+1), fWriter));
		}
				
		fWriter.flush();
	    fWriter.close();
		
	}

}
