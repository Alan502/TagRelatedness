package edu.macalester.tagrelatedness;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.ws4j.WS4J;
import org.apache.commons.cli.*;
import org.h2.command.Command;


import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CSVUtils {

    public static void main(String[] args){

        String availableActions = "bibsonomy-most-frequent " +
                "filesplit";

        CommandLineParser parser = new PosixParser();
        Options options = new Options();

        options.addOption(OptionBuilder.withLongOpt("input-file")
                .withDescription("File to process")
                .hasArg()
                .withArgName("FILE")
                .create());
        options.addOption(OptionBuilder.withLongOpt("action")
                .withDescription("Action to perform on the file. It should be one of " + availableActions)
                .hasOptionalArgs(2)
                .create());
        options.addOption(OptionBuilder.withLongOpt("output-file")
                .withDescription("Output of file ")
                .hasArg()
                .withArgName("FILE")
                .create());

        HelpFormatter formatter = new HelpFormatter();

        File input = null;
        File output = null;
        String action = "";

        try {
            CommandLine line = parser.parse(options, args);

            if(!line.hasOption("input-file")){
                System.out.println("Please specify an input file. ");
                formatter.printHelp("tagrelatedness - CSVUtils", options);
                System.exit(1);
            }else{
                input = new File(line.getOptionValue("input-file"));
            }

            if(line.hasOption("output-file")){
                output = new File(line.getOptionValue("output-file"));
            }else{
                output = new File("output-"+input.getName());
            }

            if(!line.hasOption("action")){
                System.out.println("Please specify an action file. ");
                formatter.printHelp("tagrelatedness - CSVUtils", options);
                System.exit(1);
            }else{
                String[] optionValues =  line.getOptionValues("action");
                switch(optionValues[0]){
                    case "bibsonomy-most-frequent":
                        generateMostFrequentResources(input, output);
                        break;
                    case "filesplit":
                        int div;
                        try{
                            div = Integer.parseInt(optionValues[1]);
                        }catch (ArrayIndexOutOfBoundsException | NumberFormatException ex){
                            System.out.println("defaulting, optionValues "+optionValues.length);
                            div = 5;
                        }
                        fileSplit(input, div);
                        break;
                    default:
                        System.out.println("Unrecognized action. ");
                        formatter.printHelp("tagrelatedness - CSVUtils", options);
                        System.exit(1);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public static void generateTagSimilarityCSV(LinkedList<String> tagsList, final TagSimilarityMeasure similarityMeasure, File outputFile, int threads){
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


        try {
            fWriter.flush();
            fWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void generateTagSimilarityCSV(LinkedList<String> tagsList, final TagSimilarityMeasure similarityMeasure, File outputFile){
        generateTagSimilarityCSV(tagsList, similarityMeasure, outputFile , Runtime.getRuntime().availableProcessors());
    }

    public static void fileSplit(File input, int divisions) {
        File file = input;
        LineNumberReader lnr = null;
        FileReader fr = null;
        int totalLines = 0;
        try {
            fr = new FileReader(file);
            lnr = new LineNumberReader(fr);
            while (lnr.readLine() != null){
                totalLines++;
            }

        }catch (FileNotFoundException e){
            System.out.println(e.toString());
        }catch (IOException e){
            System.out.println(e.toString());
        }


        double base = Math.pow( (double) totalLines, (1/ (double) divisions) );

//        System.out.println("Total Lines: "+totalLines+" divisions: "+divisions+" base: "+base);
        BufferedReader bfr = null;
        try {
            bfr = new BufferedReader(new FileReader(file));
        }catch (FileNotFoundException e){
            System.out.println(e.toString());
        }

        for(int i = 0; i<divisions; i++){
            int start = (int) Math.pow(base, i);
            int end = (int) Math.pow(base, i+1) > totalLines ? totalLines : (int) Math.pow(base, i+1);
            System.out.println("Going to output the lines from "+start+" to "+end+" now.");
            FileWriter fWriter = null;
            try {
                fWriter = new FileWriter(new File(input.getName().replace(".csv", "")+"-"+i+".csv" ));

                for(int j = start; j<=end; j++){
                    fWriter.write(bfr.readLine()+"\n");
                }

                fWriter.flush();
                fWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateMostFrequentResources(File bibsonomy, File output){
        FileInputStream fileStream;
        BufferedInputStream bufferedStream;
        BufferedReader readerStream;

        List<BibsonomyRecord> overlappingTagEntries = new LinkedList<BibsonomyRecord>();

        try {
            fileStream = new FileInputStream(bibsonomy);
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
            writer = new FileWriter(output);
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