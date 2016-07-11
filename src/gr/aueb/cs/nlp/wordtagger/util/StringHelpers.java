package gr.aueb.cs.nlp.wordtagger.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.google.common.base.Splitter;

public class StringHelpers {
	/**
	 * A method that returns a desired length suffix.
	 * If the desired length is greater than the word suffix
	 * it returns the whole word instead.
	 * @param word the word, that we going to extract the suffix from
	 * @param suffixLength the desired suffix length
	 * @return the suffix
	 */
	public static String getSuffix(String word, int suffixLength){
		int wordLength = word.length();
		String suffix;
		//System.out.println(wordLength + " :: " + suffixLength);
		suffixLength = suffixLength < wordLength ?
				suffixLength : wordLength;		
		
		suffix = word.substring(wordLength - suffixLength);
		//System.out.println("woot"+ " " + word +" "+suffix+ " " + suffixLength);
		return suffix;		
	}
	
	
	//theese helpers could be moved/used inside word objects for better usability
	public static boolean hasApostrophe(String word){
		return word.contains("'");
	}
	
	public static boolean hasNumber(String word){
		return word.matches(".*\\d.*");
	}
	
	public static boolean hasPunct(String word){
		return word.matches(".*\\..*");
	}
	
	public static boolean hasComma(String word){
		return word.matches(".*,.*");
	}
	
	public static boolean hasLatin(String word){
		return word.matches("[A-Za-z]+");
	}
	
	public static String[] splitter(String line, char delimeter) {
		List<String> strs = new ArrayList<>();
		int idxOfNextWord = 0;
		for (int i = 0; i < line.length(); i++) {
		    if(line.charAt(i)==delimeter) {
		        strs.add(line.substring(idxOfNextWord, i));
		        idxOfNextWord = i+1;
		    } 
		    
		    if (i == line.length() - 1 && line.charAt(i) == delimeter){
		        strs.add("");
		    }           
		}
		return strs.toArray(new String[0]);		
	}
	public static void main(String[] args){
		String input = "a-s-d-f--12321-12.223--";
		StopWatch stp = new StopWatch();
		stp.start();
		for (int i = 0; i < 1; i++) {
			System.out.println(Arrays.toString(input.split("-",-1)));
		}
		System.out.println("normal "+stp.getTime());
		
		stp.reset();
		stp.start();
		for (int i = 0; i < 1; i++) {
			System.out.println(Arrays.toString(splitter(input, '-')));
		}
		System.out.println("splitter "+stp.getTime());
		
		stp.reset();
		stp.start();
		for (int i = 0; i < 1; i++) {
			System.out.println(Arrays.toString(StringUtils.split(input, '-')));
		}
		System.out.println("apache "+stp.getTime());
		
		stp.reset();
		stp.start();
		Splitter split = Splitter.on('-');
		for (int i = 0; i < 1; i++) {
			System.out.println(split.splitToList(input).toString());
		}
		System.out.println("guava "+stp.getTime());
		
	}
 
}
