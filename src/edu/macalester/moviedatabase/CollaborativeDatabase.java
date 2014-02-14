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

public class CollaborativeDatabase implements Database{
	private HashMap<String, ArrayList<HashMap<String, HashSet<String>>> > userMap;
	private HashSet<String> allTags;
	// Each key in the map above is a user tag, and each user is asociated with two hashmaps:
	// at index 0 one that maps every movie to a set of tags that the movie is associated with
	// at index 1 that maps every tag to a set of movies that the tag is associated with
		
	public CollaborativeDatabase(){
		userMap = new HashMap<String, ArrayList<HashMap<String,HashSet<String>>>>();
		allTags = new HashSet<String>();
	}

	public void addTag(String user, String resourceName, String tagName) {
		
		ArrayList<HashMap<String, HashSet<String>>> mapList = userMap.get(user);
		
		if(null == mapList){
			mapList = new ArrayList<HashMap<String, HashSet<String>>>();
			mapList.add(0, new HashMap<String, HashSet<String>>()); // moviesMap
			mapList.add(1, new HashMap<String, HashSet<String>>()); // tagsMap
		}
		
		HashSet<String> tagsSet = mapList.get(0).get(resourceName);
		
		if(null == tagsSet)
			tagsSet = new HashSet<String>();
		
		tagsSet.add(tagName);
		mapList.get(0).put(resourceName, tagsSet);
		
		HashSet<String> moviesSet = mapList.get(1).get(tagName);
		
		if(null == moviesSet)
			moviesSet = new HashSet<String>();
		
		moviesSet.add(resourceName);
		mapList.get(1).put(tagName, moviesSet);
		
		userMap.put(user, mapList);
		allTags.add(tagName);
				
	}

	public void initializeMovieLensTags(String dir) {
		FileInputStream fileStream;
		BufferedInputStream bufferedStream;
		BufferedReader readerStream;
		
		try {
			
			fileStream = new FileInputStream(dir);
			bufferedStream = new BufferedInputStream(fileStream);
			readerStream = new BufferedReader(new InputStreamReader(bufferedStream));
			
			while(readerStream.ready()){
				
				String line = readerStream.readLine();	
				
				String tagInfo[] = line.split("::");
				
				String user = tagInfo[0];
				String movie = tagInfo[1];
				String tag = tagInfo[2];
				
				if(tagInfo.length == 4)
					addTag(user, movie, tag);
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
				
				String user = tagInfo[0];
				String tag = tagInfo[1];
				String resource = tagInfo[2];
				
				if(tagInfo.length == 5)
					addTag(user, resource, tag);
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception: "+e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Input output exception: "+e.toString());
			e.printStackTrace();
		}
	}
	
	public HashMap<String, ArrayList<HashMap<String, HashSet<String>>>> getUserMap(){
		return userMap;
	}

	public HashSet<String> getTagsSet() {
		return allTags;
	}




}
