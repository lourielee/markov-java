package hmmLyricGen;

import java.io.*;
import java.lang.Math;
import java.util.*;


public class MainClass {

	public static void main(String[] args)throws Exception {
		//read in word file
		String infile = "childrens_song_lyrics_plus.txt";
		File openFile = new File(infile);
		Scanner sc = new Scanner(openFile);
		
		
		HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
		
		//{first word newline, frequency} :
		HashMap<String, Double> firstWords = new HashMap<String, Double>(); 
		
		//{unique newline words, words that immediately follow this newline word} :
		HashMap<String, List<String>> secondWords = new HashMap<String, List<String>>(); 
		
		
		HashMap< String, List<String>> transitions = new HashMap< String, List<String>>(); 
		
		

	
		int count = 0;
		int freq = 0;
		
		//transition tokens: t, t-1, t-2
		String token_t_minus_2;  
		String token_t_minus_1;  
		String token_t;  
		String[] t_2_t_1 = new String[2]; //[t_minus_2, t_minus_1] this is first parameter in transitions hash
		String t2t1; //concatenated t-2 and t-1 separated by underscore
		
		List<String> secondWords_vals_list = new ArrayList<String>(); 
		List<String> transitions_vals_list = new ArrayList<String>();
		List<String> temp_seconds = new ArrayList<String>();
		List<String> temp_transitions = new ArrayList<String>();
		
		int firstWord_count = 0;
		int secondWord_count = 0;
		
	
		while(sc.hasNextLine()) {
				
				String line = sc.nextLine();
				String[] splits = line.split("\\s+");
			if(splits.length > 2) {
				
					firstWord_count++; //newline with at least one word, skip lines with less than 3 words
				
				
				//get/put beginning of line into firstWords dictionary
				if(firstWords.containsKey(splits[0])) {
					double currentFreq = firstWords.get(splits[0]);
					firstWords.put(splits[0], currentFreq+1);	
				}
				else
					firstWords.put(splits[0], 1.0);
				
				
				//get/put secondWords:
				if( secondWords.containsKey(splits[0])) {
					temp_seconds = secondWords.get(splits[0]);
					temp_seconds.add(splits[1]);
					secondWords.put(splits[0], temp_seconds);
					secondWord_count++;
				}
				if(!secondWords.containsKey(splits[0])) {
				
					secondWords.put(splits[0], new ArrayList<String>(Arrays.asList(splits[1])));
					secondWord_count++;
				}
					
				
				//get/put rest of words in line in transitions<>
				// first iteration: splits[t-2, t-1, t,..., last word in line]
				
					for(int i = 1; i < splits.length -1 ; i++) {
						token_t_minus_1 = splits[i];
						token_t_minus_2 = splits[i -1];
						token_t = splits[i+1];
						t_2_t_1[0] = token_t_minus_2;
						t_2_t_1[1] = token_t_minus_1;
						
						t2t1 = token_t_minus_2 + "_" + token_t_minus_1; 
						
						//associate t with word pair [t-2, t-1] :
						if(transitions.containsKey(t2t1)) {
							
							temp_transitions = transitions.get(t2t1);
							temp_transitions.add(token_t);
							transitions.put(t2t1, temp_transitions);
							
						}
						else {
							transitions.put(t2t1, new ArrayList<String>(Arrays.asList(token_t)));	
							
				}
							
			}
				
				//last word
				t_2_t_1[0] = splits[splits.length-2]; 
				t_2_t_1[1] = splits[splits.length-1];
				
				t2t1 = splits[splits.length-2] + "_" + splits[splits.length-1];
				
				if(transitions.containsKey(t2t1)) {
					temp_transitions = transitions.get(t2t1);
					temp_transitions.add(".");
					transitions.put(t2t1, temp_transitions);
					}
				else
					transitions.put(t2t1, new ArrayList<String>(Arrays.asList(".")));						
				
				
			
				
				count++;
			}
		}
		
		// print to file, visualize data cohesively in the hashes; gives idea of quality of input file in generating novel outputs: 
		PrintToFile.printHash(secondWords, "check_seconds");
		PrintToFile.printHash(transitions, "check_transitions");
	
				
		//normalize firstWords:
				for(String key_x: firstWords.keySet()) {
					firstWords.put(key_x, firstWords.get(key_x)/firstWord_count);
					
				}
				
		//normalize second words:
				double currentFreq = 0;
				HashMap<String, HashMap<String, Double>> secondWords_stats = new HashMap<String, HashMap<String, Double>>();
				for(String key_x: secondWords.keySet()) {
					
					List<String> val_words = new ArrayList<String>();
					
					val_words = secondWords.get(key_x);
					double listLen = val_words.size();
					//secondWords_stats HashMap vals: <second words, probabilities>
					HashMap<String, Double> val_words_prob = new HashMap<String, Double>();
					double current_prob = 0;
					for(String w : val_words) {
						
						if(!val_words_prob.containsKey(w))
							val_words_prob.put(w, 1.0);
						else {
							
							currentFreq = val_words_prob.get(w) + 1;
							val_words_prob.put(w, currentFreq);	//word counts following second word
						
							
						}
						
					}
					
					for(String key_w : val_words_prob.keySet()) {
						current_prob = val_words_prob.get(key_w) / listLen;
						val_words_prob.put(key_w, current_prob);
			
						
					}
			
					
					secondWords_stats.put(key_x, val_words_prob);
				}
				
		//normalize transitions:
				currentFreq = 0;
				HashMap<String, HashMap<String, Double>> transitions_stats = new HashMap<String, HashMap<String, Double>>();
				for(String key_y: transitions.keySet()) {
					
					List<String> val_words = new ArrayList<String>();
					
					val_words = transitions.get(key_y);
					double listLen = val_words.size();
					
					//transitions_stats HashMap vals: <second words, probabilities>
					HashMap<String, Double> val_words_prob = new HashMap<String, Double>();
					double current_prob = 0;
					for(String w : val_words) {
						
						if(!val_words_prob.containsKey(w))
							val_words_prob.put(w, 1.0);
							
						else {
								currentFreq = val_words_prob.get(w) + 1;
								val_words_prob.put(w, currentFreq);	//word counts following second word
							}
						
						
					}
					for(String key_w : val_words_prob.keySet()) {
						current_prob = val_words_prob.get(key_w) / listLen;
						val_words_prob.put(key_w, current_prob);
						//System.out.println(key_w+ "\t->\t"+key_w + "\t prob \t" + current_prob);
						//System.out.println(val_words_prob.size());
					}
					
					
					transitions_stats.put(key_y, val_words_prob);
				}
				
	
				
				PrintWriter out = new PrintWriter("output.txt", "UTF-8");
				
				//generate text using model:
				
				for(int i = 0; i < 10; i++) {
					String sentence = "ok";
					String word_one = "";
					String word_two = "";
				
					double seed = Math.random();
					
					double x = 0.0;
					for(String firsts: firstWords.keySet()) {
						
						Double value = firstWords.get(firsts);
						x = x+value;
						
						if(seed < x) {
							sentence = firsts + " ";
							word_one = firsts;
						
							
							break;
						}
						
					
					}//first word generated
					
					HashMap<String, Double> val = secondWords_stats.get(word_one);
					seed = Math.random();
					x = 0.0;
					String temp_word_two = "";
				
					for(String seconds: val.keySet()) {
						temp_word_two = seconds;
						Double value = val.get(seconds);
						x = x+value;
						
						
						if(seed < x) {
						
							sentence = sentence + " " + seconds + " ";
							word_two = seconds;
						
							
							break;
						}
						
					}
					
					if(word_two == "") {
						
						word_two = temp_word_two;
						sentence = sentence + " " + word_two + " ";
					}
			
					//}//second word generated
					
					String word_pair = word_one + "_" + word_two;
					String previous = word_two;
					String next = "";
					do {
						HashMap<String, Double> val_trans = transitions_stats.get(word_pair);
						seed = Math.random();
						x = 0.0;
						for(String trans: val_trans.keySet()) {
						
							Double value = val_trans.get(trans);
							x = x+value;
						
							if(seed < x) {
						
								sentence = sentence + " " + trans + " ";
								next = trans;
								
								break;
							}
						}//next word generated
						word_pair = word_two + "_" + next;
						word_two = next;
						
					}while(word_two != ".");
					
				System.out.println(sentence);
					out.println(sentence);
				
				}
				
				out.close();
		
	}

}


