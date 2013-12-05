package edu.macalester.moviedatabase;

import java.io.FileWriter;
import java.util.LinkedList;

public class TagCalculationProcedure<T> implements Procedure<T> {
	TagSimilarityMeasure similarityMeasure;
	LinkedList<String> tagsList;
	int start;
	int end;
	FileWriter writer;
	
	public TagCalculationProcedure(TagSimilarityMeasure measure, LinkedList<String> tags, int startIndex, int endIndex, FileWriter fileWriter){
		similarityMeasure = measure;
		tagsList = tags;
		start = startIndex;
		end = endIndex;
	}
	
	public void call(T arg) throws Exception {
		int i = 0;
		for(String comparingTag : tagsList.subList(start, end)){
			i++;
			for(String comparedTag : tagsList.subList(start+i, tagsList.size())){
							
				double cc = similarityMeasure.calculateSimilarity(comparingTag, comparedTag);
				
				if(cc != 0){
					
					// Remove newlines, commas and apostrophes that may distort the CSV file when being written.
					synchronized(writer){
					writer.append('"' + comparingTag.replace('"', ' ').replace('\n', ' ').replace(',', ' ') + '"'+ ',' + '"' + comparedTag.replace('"', ' ').replace('\n', ' ').replace(',', ' ') + '"' + " , " + cc);
					writer.append('\n');
					}
				}
				
			}
			System.out.println("Tags associated with index: "+i+start+" have been calculated and written to tags.csv");
		}
	}

}
