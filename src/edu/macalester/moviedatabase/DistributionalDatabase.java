package edu.macalester.moviedatabase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DistributionalDatabase implements Database{

	private HashMap<String, ArrayList<String>> tagsMap;
	private HashMap<String, ArrayList<String>> moviesMap;
	private int totalEntries = 0;
	
	public DistributionalDatabase(){
		tagsMap = new HashMap<String, ArrayList<String>>();
		moviesMap = new HashMap<String, ArrayList<String>>();
	}
	
	public void addTag(String movieName, String tagName){
		ArrayList<String> tagsList = moviesMap.get(movieName);
		
		if(null == tagsList)
			tagsList = new ArrayList<String>();
		
		tagsList.add(tagName);
		
		moviesMap.put(movieName, tagsList);
		
		ArrayList<String> moviesList = tagsMap.get(tagName);
		
		if(null == moviesList)
			moviesList = new ArrayList<String>();
		
		moviesList.add(movieName);		
		
		tagsMap.put(tagName, moviesList);
		
		
		totalEntries++;
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
				
				if(tagInfo.length == 4)
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

	public HashSet<String> getTagsSet() {
		return (HashSet<String>) tagsMap.keySet();
	}
	
	public Set<String> getMoviesSet(){
		return moviesMap.keySet();
	}
	
	public HashMap<String, ArrayList<String>> getTagsMap(){
		return tagsMap;
	}
	public HashMap<String, ArrayList<String>> getMoviesMap(){
		return moviesMap;
	}
	public int getTotalEntries(){
		return totalEntries;
	}

}
