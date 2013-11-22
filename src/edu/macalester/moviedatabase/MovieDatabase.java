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

public class MovieDatabase{

	protected HashMap<String, HashSet<String>> tagsMap;
	protected HashMap<String, HashSet<String>> moviesMap;
	
	public MovieDatabase(){
		tagsMap = new HashMap<String, HashSet<String>>();
		moviesMap = new HashMap<String, HashSet<String>>();
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
	}
	
	public Set<String> getTagsKeySet(){
		return tagsMap.keySet();
	}
	
	public void intializeMovieTags(String tagsDataFileDir){
		
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

}
