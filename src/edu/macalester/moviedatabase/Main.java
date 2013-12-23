package edu.macalester.moviedatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
						
		ProjectionalDatabase database = new ProjectionalDatabase();
		database.intializeMovieLensTags("ml-10M100K/tags.dat");		
		final DistributionalMatching similarityMeasure = new DistributionalMatching(database);
		
//		FileWriter fWriter = null;
//		try {
//			fWriter = new FileWriter("tags.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		fWriter.append('"' + " Tag 1 " + '"'+ ',' + '"' + " Tag 2 " + '"' + " , " + "Similarity");
//		fWriter.append('\n');
		
		final LinkedList<String> tags = new LinkedList<String>(database.getTagsSet());
						
		int threads  = 12;		
		
		// Initialize code necessary to calculate tau between this tag set and Wordnet
		WS4JConfiguration.getInstance().setMFS(true);
        ILexicalDatabase db = new NictWordNet();
		final JiangConrath rc = new JiangConrath(db);
		final ArrayList<Double> distMatchingSimilarities  = new ArrayList<Double>();
		final ArrayList<Double> wordnetSimilarities = new ArrayList<Double>();
		// End of code necessary for calculating tau
		
//		final FileWriter writer = fWriter;
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
//            					synchronized(writer){
//            					writer.append('"' + comparingTag.replace('"', ' ').replace('\n', ' ').replace(',', ' ') + '"'+ ',' + '"' + comparedTag.replace('"', ' ').replace('\n', ' ').replace(',', ' ') + '"' + " , " + cc);
//            					writer.append('\n');
//            					}
            					
            					// Adding to arrays used to calculate TAU
            					double jc = rc.calcRelatednessOfWords(comparedTag, comparingTag);
            					if(jc != 0){
            						
            						synchronized (wordnetSimilarities) {
            							wordnetSimilarities.add(jc);	
            						}
            						synchronized (distMatchingSimilarities) {
            							distMatchingSimilarities.add(cc);
									}
            					}
            						
            						
            						
            				}
            				
            			}
                    }
                }
				);

//		fWriter.flush();
//	    fWriter.close();
	    
		System.out.println(distMatchingSimilarities.size());
		System.out.println(wordnetSimilarities.size());
	    
	    System.out.println(KendallsCorrelation.correlation(distMatchingSimilarities, wordnetSimilarities));
		
	}

}
