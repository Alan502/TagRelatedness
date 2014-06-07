package edu.macalester.tagrelatedness;

import java.util.Comparator;

public class CSVComparator implements Comparator<String>{

	public int compare(String r1, String r2) {
		String[] data1 = r1.split(",");
		String[] data2 = r2.split(",");
		double result;
		
		try{
			result = Double.parseDouble(data1[2]) - Double.parseDouble(data2[2]);
		}catch(NumberFormatException e){
			return 0;
		}
		
		if(result < 0)
			return 1;
		else if(result > 0)
			return -1;
		else
			return 0;
	}


}
