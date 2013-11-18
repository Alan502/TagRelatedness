package edu.macalester.moviedatabase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		MovieDatabase database = new MovieDatabase();
		
		database.intializeMovieTags("ml-10M100K/tags.dat");
		
		LinkedList<String> tags = new LinkedList<String>();
		
		for(String tag : database.getTagsKeySet()){
			tags.add(tag);
		}
		
		FileWriter writer = null;
		
		try {
			writer = new FileWriter("tags.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		writer.append('"' + " Tag 1 " + '"'+ ',' + '"' + " Tag 2 " + '"' + " , " + "Number of Co Occurrent movies");
		writer.append('\n');
		
		int i = 0;
		for(String comparingTag : tags){
			i++;
			for(String comparedTag : tags.subList(i, tags.size())){
							
				int cc = database.calculateCoOccurrence(comparingTag, comparedTag);
				
				if(cc != 0){
					writer.append('"' + comparingTag + '"'+ ',' + '"' + comparedTag + '"' + " , " + cc);
					writer.append('\n');
				}
				
			}
		}
				
		writer.flush();
	    writer.close();
		
	}

}
