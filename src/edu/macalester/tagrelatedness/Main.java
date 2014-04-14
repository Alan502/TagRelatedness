package edu.macalester.tagrelatedness;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.print.DocFlavor.URL;


import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.WS4J;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class Main {
	static int threads  = Runtime.getRuntime().availableProcessors();
	
	public static void main(String[] args) {
		ParallelForEach.LOG.info("Running program with "+threads+" threads.");
		
//		if(args.length < 2){
//			System.out.println("Not enough arguments were provided to run the program.");
//			System.exit(0);
//		}
//		
//		switch(args[0]){
//			case "generate-bibsonomy-top-2000":
//				String file = null;
//				String output = null;
//				for(int i = 1; i<args.length; i++){
//					if(args[i].startsWith("-")){
//						if(args.length == i-1 || args[i+1].startsWith("-")){
//							System.out.println("No argument for the option "+args[i]);
//							System.exit(1);
//						}else if(args[i].equals("-file")){
//							File f = new File(args[i+1]);
//						      if(f.exists()){
//						    	  file = args[i+1];
//						      }else{
//						          System.out.println("Bibsonomy file "+args[i+1]+" was not found!");
//						          System.exit(1);
//						      }
//						}else if(args[i].equals("-output")){
//							File f = new File(args[i+1]);
//						      if(f.exists()){
//						    	  System.out.println("Output File "+args[i+1]+" already exists!");
//						    	  System.exit(1);
//						      }else{
//						          output = args[i+1];
//						      }
//						}
//					}
//				}
//				if(null == file){
//					System.out.println("Unspecified option: -file bibsonomy_input_file");
//					System.exit(1);
//				}else if(null == output){
//					System.out.println("Unspecified option: -output top_2000_output_file");
//					System.exit(1);
//				}else{
//					generateMostFrequentResources(file, output);	
//				}
//			break;
//			case "tau-with-wordnet":
//				String csv = null;
//				for(int i = 1; i<args.length; i++){
//					if(args[i].startsWith("-")){
//						if(args.length == i-1 || args[i+1].startsWith("-")){
//							System.out.println("No argument for the option "+args[i]);
//							System.exit(1);
//						}else if(args[i].equals("-csv")){
//							File f = new File(args[i+1]);
//						      if(f.exists()){
//						    	  csv = args[i+1];
//						      }else{
//						          System.out.println("CSV file "+args[i+1]+" was not found!");
//						          System.exit(1);
//						      }
//						}
//					}
//				}
//				if(null == csv){
//					System.out.println("Unspecified option: -csv csv_input_file");
//					System.exit(1);
//				}else{
//					tauBetweenCSVandWordnet(csv);
//				}
//			break;
//			case "generate-csv":
//				String tagMeasure = null;
//				String output_file = null;
//				String bibsonomy = null;
//				String movielens = null;
//				for(int i = 1; i<args.length; i++){
//					if(args[i].startsWith("-")){
//						if(args.length == i-1 || args[i+1].startsWith("-")){
//							System.out.println("No argument for the option "+args[i]);
//							System.exit(1);
//						}else if(args[i].equals("-tag_measure")){
//							if(args[i+1].equals("collaborative_matching") || args[i+1].equals("collaborative_mutual_information") || args[i+1].equals("distributional_matching") || args[i+1].equals("distributional_mutual_information")){
//								tagMeasure = args[i+1];
//							}else{
//								System.out.println("Unrecognized tag measure: "+args[i+1]);
//								System.exit(1);
//							}
//						}else if(args[i].equals("-output")){
//							File f = new File(args[i+1]);
//						      if(f.exists()){
//						    	  System.out.println("Output File "+args[i+1]+" already exists!");
//						    	  System.exit(1);
//						      }else{
//						          output_file = args[i+1];
//						      }							
//						}else if(args[i].equals("-bibsonomy")){
//							File f = new File(args[i+1]);
//						      if(f.exists()){
//						    	  bibsonomy = args[i+1];
//						      }else{
//						          System.out.println("Bibsonomy file "+args[i+1]+" was not found!");
//						          System.exit(1);
//						      }							
//						}else if(args[i].equals("-movielens")){
//							File f = new File(args[i+1]);
//						      if(f.exists()){
//						    	  movielens = args[i+1];
//						      }else{
//						          System.out.println("Movielens file "+args[i+1]+" was not found!");
//						          System.exit(1);
//						      }		
//						}
//					}
//				}
//				if(null == tagMeasure){
//					System.out.println("Unspecified option: -tag_measure collaborative_matching,collaborative_mutual_information,distributional_matching,distributional_mutual_information");
//					System.exit(1);
//				}else if(null == output_file){
//					System.out.println("Unspecified option: -output output_csv_file");
//					System.exit(1);
//				}else if(null == bibsonomy && null == movielens){
//					System.out.println("Unspecified option: -bibsonomy bibsonomy_input_file OR -movielens movielens_input_file");
//					System.exit(1);
//				}else{
//					TagSimilarityMeasure measure = null;
//					switch(tagMeasure){
//						case "collaborative_matching":
//							CollaborativeDatabase db = new CollaborativeDatabase();
//							if(null != bibsonomy)
//								db.initializeBibsonomyTags(bibsonomy);
//							if(null != movielens)
//								db.initializeMovieLensTags(movielens);
//							measure = new CollaborativeMatching(db);
//						break;
//						case "collaborative_mutual_information":
//							CollaborativeDatabase db2 = new CollaborativeDatabase();
//							if(null != bibsonomy)
//								db2.initializeBibsonomyTags(bibsonomy);
//							if(null != movielens)
//								db2.initializeMovieLensTags(movielens);
//							measure = new CollaborativeMutualInformation(db2);
//						break;
//						case "distributional_matching":
//							ProjectionalDatabase db3 = new ProjectionalDatabase();
//							if(null != bibsonomy)
//								db3.initializeBibsonomyTags(bibsonomy);
//							if(null != movielens)
//								db3.initializeMovieLensTags(movielens);
//							measure = new DistributionalMatching(db3);
//						break;
//						case "distributional_mutual_information":
//							DistributionalDatabase db4 = new DistributionalDatabase();
//							if(null != bibsonomy)
//								db4.initializeBibsonomyTags(bibsonomy);
//							if(null != movielens)
//								db4.initializeMovieLensTags(movielens);
//							measure = new DistributionalMutualInformation(db4);
//						break;
//						default:
//							System.out.println("Tag similartiy measure "+tagMeasure+" not defined.");
//							System.exit(1);
//						break;
//					}
//				}
//			break;
//			default:
//				System.out.println("Unrecognized task in the program's parameters.");
//				System.exit(1);
//			break;
//		}
//		
//		System.exit(0);

//		generateMostFrequentResources("bibsonomy/2007-10-31/tas", "bibsonomy/2007-10-31/tas-2000-most-common");		
		
//		CollaborativeDatabase db = new CollaborativeDatabase();
		//db.initializeMovieLensTags("ml-10M100K/tags.dat");
//		db.initializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
	
//		try {
//			generateTagSimilarityCSV(new LinkedList<>(db.getTagsSet()), new CollaborativeMatching(db), "collab_matching.csv");
//			generateTagSimilarityCSV(new LinkedList<>(db.getTagsSet()), new CollaborativeMutualInformation(db), "collab_MI.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		DistributionalDatabase ddb = new DistributionalDatabase();
		//ddb.initializeMovieLensTags("ml-10M100K/tags.dat");
//		ddb.initializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
//		try {
//			generateTagSimilarityCSV(new LinkedList<>(ddb.getTagsSet()), new DistributionalMutualInformation(ddb), "dist_MI.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		ProjectionalDatabase pdb = new ProjectionalDatabase();
//		pdb.initializeMovieLensTags("ml-10M100K/tags.dat");
//		pdb.initializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
//		try {
//			generateTagSimilarityCSV(new LinkedList<>(pdb.getTagsSet()), new DistributionalMatching(pdb), "dist_matching.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		WikAPIdiaEnsemble wikApidia = new WikAPIdiaEnsemble(System.getProperty("user.home")+"/.wikAPIdia/");
//		try {
//			generateTagSimilarityCSV(new LinkedList<>(pdb.getTagsSet()), wikApidia, "wikAPIdia_ensemble.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println("Calculation for collaborative matching:");
//		tauBetweenCSVandWordnet("collab_matching.csv");
//		System.out.println("Calculation for collaborative MI:");
//		tauBetweenCSVandWordnet("collab_MI.csv");
//		System.out.println("Calculation for distributional matching:");
//		tauBetweenCSVandWordnet("dist_matching.csv");
//		System.out.println("Calculation for distributional MI:");
//		tauBetweenCSVandWordnet("dist_MI.csv");
//		System.out.println("Calculation for wikAPIdia:");
//		tauBetweenCSVandWordnet("wikAPIdia_ensemble.csv");
		
		ProjectionalDatabase pdb = new ProjectionalDatabase();
		pdb.initializeMovieLensTags("ml-10M100K/tags.dat");
				
		WikAPIdiaEnsemble wikApidia = new WikAPIdiaEnsemble(System.getProperty("user.home")+"/.wikAPIdia/");
		
		try {
			generateTagSimilarityCSV(new LinkedList<>(pdb.getTagsSet()), wikApidia, "movielens_wikapidia_ensemble.csv");
		} catch (IOException e1) {
			System.out.println("IO Exception: "+e1.getMessage());
			e1.printStackTrace();
		}
		
		System.out.println("WikAPIdiaEnsemble for movielens:");
		tauBetweenCSVandWordnet("movielens_wikapidia_ensemble.csv");
		
		ProjectionalDatabase pdb2 = new ProjectionalDatabase();
		pdb.initializeBibsonomyTags("bibsonomy/2007-10-31/tas");
		try {
			generateTagSimilarityCSV(new LinkedList<>(pdb2.getTagsSet()), wikApidia, "bibsonomy_wikapidia_ensemble.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("WikAPIdiaEnsemble for all of bibsonomy:");
		
		tauBetweenCSVandWordnet("bibsonomy_wikapidia_ensemble.csv");
		
	}
	public static void tauBetweenCSVandWordnet(String file){
		ILexicalDatabase db = new NictWordNet();
		final RelatednessCalculator rc = new JiangConrath(db);
		WS4JConfiguration.getInstance().setMFS(true);

		final ArrayList<Double> measurementSimilarities  = new ArrayList<Double>();
		final ArrayList<Double> wordnetSimilarities = new ArrayList<Double>();
		
		java.util.List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
		ParallelForEach.loop(lines.subList(1, lines.size()), threads, new Procedure<String>() {
			public void call(String line){
				String[] column = line.split(",");
				 String word1 = column[0].replace("\"", "").replace(" ", "");
				 String word2 = column[1].replace("\"", "").replace(" ", "");
				 double jc = rc.calcRelatednessOfWords(word1, word2);
				 
				 if(!(jc < 0.00000000000000001 && jc > -.00000000000000001)){
					 synchronized (measurementSimilarities) {
						 try{
							 measurementSimilarities.add(Double.parseDouble(column[2]));
							 wordnetSimilarities.add(jc);
						 }catch(NumberFormatException e){
							 System.out.println("NumberFormatException Ex: "+column[2]);
						 }catch(ArrayIndexOutOfBoundsException e){
							 System.out.println("ArrayIndexOutOfBounds Ex: "+Arrays.toString(column));
						 }
					 }
				 }
			}
		});
	    System.out.println("Tau: "+KendallsCorrelation.correlation(measurementSimilarities, wordnetSimilarities));
	}
	
	public static void generateTagSimilarityCSV(LinkedList<String> tagsList, final TagSimilarityMeasure similarityMeasure, String outputFile) throws IOException{
		final LinkedList<String> tags = tagsList;
		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fWriter.append('"' + " Tag 1 " + '"'+ ',' + '"' + " Tag 2 " + '"' + " , " + "Similarity");
		fWriter.append('\n');
		
		final DecimalFormat formatter = new DecimalFormat("0.000000000000");
		formatter.setRoundingMode(RoundingMode.HALF_UP);
								
		final FileWriter writer = fWriter;
		ParallelForEach.loop(tags,
				threads,
				new Procedure<String>() {
                    @Override
                    public void call(String comparingTag) throws Exception {
                    	int start = tags.indexOf(comparingTag);
                    	for(String comparedTag : tags.subList(start+1, tags.size() )){
            				double cc = similarityMeasure.calculateSimilarity(comparingTag, comparedTag);
//            				if(!(cc < 0.001 && cc > -.001)){
            					// Remove newlines, commas and apostrophes that may distort the CSV file when being written.
            					synchronized(writer){
            					writer.append("\"" + comparingTag.replace("\"", "").replace("\n", "").replace(",", "") + '"'+ ',' + '"' + comparedTag.replace("\"", "").replace("\n", "").replace(",", "") + '"' + "," + formatter.format(cc) +"\n");
            					}
//            				}
            				
            			}
                    }
                }
				);
		
		

		fWriter.flush();
	    fWriter.close();
		
	}
	
	public static void generateMostFrequentResources(String bibsonomyDSdir, String outputDir){
		FileInputStream fileStream;
		BufferedInputStream bufferedStream;
		BufferedReader readerStream;
		
		List<BibsonomyRecord> overlappingTagEntries = new LinkedList<BibsonomyRecord>();
		
		try {
			fileStream = new FileInputStream(bibsonomyDSdir);
			bufferedStream = new BufferedInputStream(fileStream);
			readerStream = new BufferedReader(new InputStreamReader(bufferedStream));
			
			while(readerStream.ready()){
				String line = readerStream.readLine();
				String tagInfo[] = line.split("\t");
				if( tagInfo.length == 5 && (
						!WS4J.findDefinitions(tagInfo[1], POS.a).isEmpty() ||
						!WS4J.findDefinitions(tagInfo[1], POS.n).isEmpty() ||
						!WS4J.findDefinitions(tagInfo[1], POS.r).isEmpty() ||
						!WS4J.findDefinitions(tagInfo[1], POS.v).isEmpty()
						)){
					overlappingTagEntries.add(new BibsonomyRecord(tagInfo[2], line));
				}
			}
						
			readerStream.close();
			fileStream.close();
			bufferedStream.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception: "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException: "+e.getMessage());
			e.printStackTrace();
		}
		
		Collections.sort(overlappingTagEntries);
		
		List<EntryFrequency> bibsonomyEntries  = new LinkedList<EntryFrequency>();
		
		String lastContentID = overlappingTagEntries.get(0).contentID;
		int frequency = 1;
		
		for(BibsonomyRecord rec : overlappingTagEntries.subList(1, overlappingTagEntries.size())){
			if(lastContentID.equals(rec.contentID)){
				if(rec.equals(overlappingTagEntries.get(overlappingTagEntries.size()-1)))
					bibsonomyEntries.add(new EntryFrequency(lastContentID, frequency));
				else
					frequency++;
			}else{
				bibsonomyEntries.add(new EntryFrequency(lastContentID, frequency));
				lastContentID = rec.contentID;
				frequency = 1;
			}
		}
		
		Collections.sort(bibsonomyEntries);
		Collections.reverse(bibsonomyEntries);
						
		ArrayList<String> mostCommonResources= new ArrayList<String>();
		for(EntryFrequency bib : bibsonomyEntries.subList(0, 2000)){
			mostCommonResources.add(bib.contentID);
		}
		
		FileWriter writer = null;
		
		try {
			writer = new FileWriter(outputDir);
			for(BibsonomyRecord rec : overlappingTagEntries){
				if(mostCommonResources.contains(rec.contentID)){
					writer.write(rec.line+"\n");
				}	
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("IOException: "+e.getMessage());
		}
        
	}
	/**
	 * Taken from http://stackoverflow.com/questions/202302/rounding-to-an-arbitrary-number-of-significant-digits
	 * @param num number to round
	 * @param n number of significant figures
	 * @return a number with the specified number of significant figures.s
	 */
	public static double roundToSignificantFigures(double num, int n) {
	    if(num == 0) {
	        return 0;
	    }

	    final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
	    final int power = n - (int) d;

	    final double magnitude = Math.pow(10, power);
	    final long shifted = Math.round(num*magnitude);
	    return shifted/magnitude;
	}
	  
}



