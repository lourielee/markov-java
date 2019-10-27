package hmmLyricGen;

import java.io.*;
import java.util.*;


public class MainClass {

	public static void main(String[] args)throws Exception {
		//read in word file
		String infile = "childrens_song_lyrics.txt";
		File openFile = new File(infile);
		System.out.println(new File("childrens_song_lyrics.txt").getAbsolutePath());
		Scanner sc = new Scanner(openFile);
		
		
		HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
		
		//{first word newline, frequency} :
		HashMap<String, Integer> firstWords = new HashMap<String, Integer>(); 
		
		//{unique newline words, words that immediately follow this newline word} :
		HashMap<String, List<String>> secondWords = new HashMap<String, List<String>>(); 
		
		
		HashMap< String[], List<String>> transitions = new HashMap< String[], List<String>>(); 
		
		

	
		int count = 0;
		int freq = 0;
		
		//transition tokens: t, t-1, t-2
		String token_t_minus_2;  
		String token_t_minus_1;  
		String token_t;  
		String[] t_2_t_1 = new String[2]; //[t_minus_2, t_minus_1] this is first parameter in transitions hash
		
		List<String> secondWords_vals_list = new ArrayList<String>(); 
		List<String> transitions_vals_list = new ArrayList<String>();
		List<String> temp_seconds = new ArrayList<String>();
		List<String> temp_transitions = new ArrayList<String>();
		
		while( true ) {
			try {
				String line = sc.nextLine();
				String[] splits = line.split("\\s+");
				
				
				//get/put beginning of line into firstWords dictionary
				if(firstWords.containsKey(splits[0])) {
					int currentFreq = firstWords.get(splits[0]);
					firstWords.put(splits[0], currentFreq+1);	
				}
				else
					firstWords.put(splits[0], 1);
				
				//get/put secondWords:
				if(splits.length > 1 && 
					secondWords.containsKey(splits[0])) {
					temp_seconds = secondWords.get(splits[0]);
					temp_seconds.add(splits[1]);
					secondWords.put(splits[0], temp_seconds);
				}
				if(splits.length > 1 && 
					!secondWords.containsKey(splits[0])) {
				
					secondWords.put(splits[0], new ArrayList<String>(Arrays.asList(splits[1])));
				}
					
				
				//get/put rest of words in line in transitions<>
				// first iteration: splits[t-2, t-1, t,..., last word in line]
				if(splits.length > 2) { 
					for(int i = 1; i < splits.length -1 ; i++) {
						token_t_minus_1 = splits[i];
						token_t_minus_2 = splits[i -1];
						token_t = splits[i+1];
						t_2_t_1[0] = token_t_minus_2;
						t_2_t_1[1] = token_t_minus_1;
						
						
						//associate t with word pair [t-2, t-1] :
						if(transitions.containsKey(t_2_t_1)) {
							
							temp_transitions = transitions.get(t_2_t_1);
							temp_transitions.add(token_t);
							transitions.put(t_2_t_1, temp_transitions);
							//System.out.println("IN IF");
						}
						else {
							transitions.put(t_2_t_1, new ArrayList<String>(Arrays.asList(token_t)));	
							//System.out.println("IN ELSE");
						}
							
					}
				
				//last word
				t_2_t_1[0] = splits[splits.length-2]; 
				t_2_t_1[1] = splits[splits.length-1];
				
				if(transitions.containsKey(t_2_t_1)) {
					temp_transitions = transitions.get(t_2_t_1);
					temp_transitions.add(".");
					transitions.put(t_2_t_1, temp_transitions);
				}
				else
					transitions.put(t_2_t_1, new ArrayList<String>(Arrays.asList(".")));						
			}
				
				System.out.println(line + "\t" + count);
				
				count++;
			}
			catch(NoSuchElementException e) {
				//output: number of lines in input file
				System.out.println(e.getMessage() + "\t" + count);
				break;
				
			}
		}
		
	
		for(String firsts: firstWords.keySet()) {
			String key = firsts.toString();
			String value = firstWords.get(firsts).toString();
			System.out.println(key + " " + value);
		}
		
		int x = 0;
		for(Map.Entry<String, List<String>> entry : secondWords.entrySet()) {
			String key = entry.getKey().toString();
			List<String> val = secondWords.get(key);
			System.out.print(key + ": \t");
			for(String strVal:val) {
				System.out.print(strVal + "\t");
			}
			System.out.println("\n");
			x++;
		}
		
		
		
		System.out.println(x);
		System.out.println(secondWords.size());
		System.out.println(firstWords.size());
		System.out.println(transitions.size());
		System.out.println(transitions.toString());
		
		
		
		
	}

}


