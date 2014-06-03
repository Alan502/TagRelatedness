package edu.macalester.tagrelatedness;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.ws4j.WS4J;

import java.io.*;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CSVUtils {
	
	public static void generateTagSimilarityCSV(LinkedList<String> tagsList, final TagSimilarityMeasure similarityMeasure, String outputFile, int threads) throws IOException{
		final LinkedList<String> tags = tagsList;
		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		final DecimalFormat formatter = new DecimalFormat("0.00000000000000000");
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
// 	        				}
            				
            			}
                    }
                }
				);
		
		

		fWriter.flush();
	    fWriter.close();
		
	}
	
	public static void generateTagSimilarityCSV(LinkedList<String> tagsList, final TagSimilarityMeasure similarityMeasure, String outputFile) throws IOException{
		generateTagSimilarityCSV(tagsList, similarityMeasure, outputFile, Runtime.getRuntime().availableProcessors());
	}
	
	public static void fileSplit(String inputFile, int divisions) throws IOException {
        File file = new File(inputFile);
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new FileReader(file));

        }catch (FileNotFoundException e){
            System.out.println(e.toString());
        }catch (IOException e){
            System.out.println(e.toString());
        }

        int totalLines = 0;
        while (lnr.readLine() != null){
            totalLines++;
        }
		
		double base = Math.pow(totalLines, (1/divisions));
		
		for(int i = 0; i<divisions; i++){
			int start = (int) Math.pow(base, i);
			int end = (int) Math.pow(base, i+1) > totalLines ? totalLines : (int) Math.pow(base, i+1);
            System.out.println("Going to output the lines from "+start+" to "+end+" now.");
			FileWriter fWriter = null;
			try {
				fWriter = new FileWriter(new File(inputFile.replace(".csv", "")+"-"+i+".csv" ));

                lnr.setLineNumber(start);

                for(int j = start; j<end; j++){
                    fWriter.write(lnr.readLine()+"\n");
                }

				fWriter.flush();
				fWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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


}