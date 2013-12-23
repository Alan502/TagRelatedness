package edu.macalester.moviedatabase;

import java.awt.BufferCapabilities;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;


public class Main {

	public static void main(String[] args) throws IOException {
		tauBetweenCSVandWordnet("distributional_matching.csv");		
	}
	
	public static void tauBetweenCSVandWordnet(String file){
		
		WS4JConfiguration.getInstance().setMFS(true);
        ILexicalDatabase db = new NictWordNet();
		JiangConrath rc = new JiangConrath(db);
		
		ArrayList<Double> distMatchingSimilarities  = new ArrayList<Double>();
		ArrayList<Double> wordnetSimilarities = new ArrayList<Double>();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			br.readLine(); // Skip first line, which is just the column tags.
			while ((line = br.readLine()) != null) {
				 String[] column = line.split(",");
				 double jc = rc.calcRelatednessOfWords(column[0].replace("\"", "") , column[1].replaceAll("\"", ""));
				 if(jc != 0){
					 distMatchingSimilarities.add(Double.parseDouble(column[2]));
					 wordnetSimilarities.add(jc);
				 }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(distMatchingSimilarities.size()+" "+wordnetSimilarities.size());
	    System.out.println(KendallsCorrelation.correlation(distMatchingSimilarities, wordnetSimilarities));
	}
	
	public static void generateTagSimilarityCSV() throws IOException{
		ProjectionalDatabase database = new ProjectionalDatabase();
		database.intializeMovieLensTags("ml-10M100K/tags.dat");		
		final DistributionalMatching similarityMeasure = new DistributionalMatching(database);
		
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

		fWriter.flush();
	    fWriter.close();
		
	}

}
