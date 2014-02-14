package edu.macalester.moviedatabase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ProjectionalDatabase implements Database{

	protected HashMap<String, HashSet<String>> tagsMap;
	protected HashMap<String, HashSet<String>> moviesMap;
	private int totalEntries;
	
	public ProjectionalDatabase(){
		tagsMap = new HashMap<String, HashSet<String>>();
		moviesMap = new HashMap<String, HashSet<String>>();
		totalEntries = 0;
	}
	
	public void addTag(String movieName, String tagName){
		
		HashSet<String> tagsSet = moviesMap.get(movieName);
		
		if(null == tagsSet)
			tagsSet = new HashSet<String>();
		
		tagsSet.add(tagName);
		moviesMap.put(movieName, tagsSet);
		
		HashSet<String> moviesSet = tagsMap.get(tagName);
		
		if(null == moviesSet)
			moviesSet = new HashSet<String>();
		
		moviesSet.add(movieName);			
		tagsMap.put(tagName, moviesSet);
		
		totalEntries++;
	}
	
	public Set<String> getTagsSet(){
		return tagsMap.keySet();
	}
	
	public Set<String> getMoviesSet(){
		return moviesMap.keySet();
	}
	
	public HashMap<String, HashSet<String>> getTagsMap(){
		return tagsMap;
	}
	
	public HashMap<String, HashSet<String>> getMoviesMap(){
		return moviesMap;
	}
	
	public void initializeMovieLensTags(String tagsDataFileDir){
		
		FileInputStream fileStream;
		BufferedInputStream bufferedStream;
		BufferedReader readerStream;
		
		try {
			
			fileStream = new FileInputStream(tagsDataFileDir);
			bufferedStream = new BufferedInputStream(fileStream);
			readerStream = new BufferedReader(new InputStreamReader(bufferedStream));
			
			while(readerStream.ready()){
				
				String line = readerStream.readLine();	
				
				String tagInfo[] = line.split("::");
				
				String movie = tagInfo[1];
				String tag = tagInfo[2];
				
				if(tagInfo.length == 5)
					addTag(movie, tag);
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception: "+e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Input output exception: "+e.toString());
			e.printStackTrace();
		}
	}
	
	public void intializeBibsonomyTags(String dir){
		FileInputStream fileStream;
		BufferedInputStream bufferedStream;
		BufferedReader readerStream;
		
		try {
			fileStream = new FileInputStream(dir);
			bufferedStream = new BufferedInputStream(fileStream);
			readerStream = new BufferedReader(new InputStreamReader(bufferedStream));
			
			while(readerStream.ready()){
				
				String line = readerStream.readLine();	
				
				String tagInfo[] = line.split("\t");
				
				String tag = tagInfo[1];
				String resource = tagInfo[2];
				
				if(tagInfo.length == 4)
					addTag(resource, tag);
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception: "+e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Input output exception: "+e.toString());
			e.printStackTrace();
		}
	}

	public int getTotalEntries() {
		return totalEntries;
	}


}
