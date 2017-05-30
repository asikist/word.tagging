package gr.aueb.cs.nlp.wordtagger.tokenizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import gr.aueb.cs.nlp.wordtagger.util.FileHelper;
import gr.aueb.cs.nlp.wordtagger.util.CallBack;

/*patterns
 *\n\n = |<new verse>|
 *\n = |<new lyric>|
 *\\[ = |<repetition start>|
 *\\]\\s?x\\d{1}\\s* = |<repetition end>|
 *\\s = |<word>|
*/

//TODO rename to SongTokenizer
public class SongReader {
	
	List<String> songAnalysed = new ArrayList<String>();
	List<String> songAnalysedTemp = new ArrayList<String>(); //for various tedious uses
	
	public static void main (String [] args){
		
		SongReader sr = new SongReader();
		sr.tokenizer(FileUtils.getUserDirectoryPath() + File.separator +"Desktop" + File.separator + "songTestGR");
		for (String s:sr.songAnalysed)
			System.out.println(s);
	}//end of main
	
	//the following method returns a List<String> with each list element repressenting a word or a token
	//TODO tokenize, input String text not path!
	public List<String> tokenizer (String path){
		//something that reads a file
		StringBuilder strb = new StringBuilder();
		
		FileHelper.readFile(path, new CallBack<String>()  {
			
			@Override
			public void call(String t) {
				String line = t;
				strb.append(line).append("\n");
			}
		});//TODO break in 2 methods: one that reads the file and returns the String and then one that takes a string as an input and tokenizesa
		
		//FIRST CLEANUP
		//here we try to clean up our input from puncuation marks (,.!"')
		String strbTemp;
		strbTemp = strb.toString().replaceAll("[,.!\"]", "");
		strbTemp = strbTemp.replaceAll("'", " "); //done separately because words may be linked by it

		//the part that analyses the content and does the tokenizing, while creating the List<String>
				
		this.songAnalysed.add(strbTemp);
		//TODO remove self empty construction and replace with static
		SongReader sr = new SongReader();
		
		
		//initial ignition, separating the strophes
		sr.separationIgnition(songAnalysed, "(\\n\\n)", "|<new strophe>|", "backBreaker");
		
		//second ignition, separating the lyrics
		sr.separationIgnition(songAnalysed, "(\\n)", "|<new lyric>|", "backBreaker");
		//third ignition, marking a repetition start
		sr.separationIgnition(songAnalysed, "(\\[)", "|<repetition start>|", "frontBreaker");
		//fourth ignition, marking a repetition end
		sr.separationIgnition(songAnalysed, "(\\]\\s?x\\d{1}\\s*)", "|<repetition end>|", "backBreaker");
		//fifth ignition, separating words
		sr.separationIgnition(songAnalysed, "(\\s{1,2})", "|<word>|", "backBreaker");

		//SECOND CLEANUP, we remove the |<word>| token because it comes as a collateral due to the way the tokenizing occurs
		songAnalysedTemp.add("|<word>|");
		songAnalysed.removeAll(songAnalysedTemp);
		songAnalysedTemp.clear();

		return songAnalysed;
	}//end of tokenizer
	
	//takes care of the object's lists and calls listHandler
	//TODO rename to splitSongTokens
	/**
	 * This method takes a list of tokens as an input and further splits it based on some regular expression patterns
	 * @param songAnalysed input string tokens from a song
	 * @param pattern	the dlimeteter pattern to do the splitting
	 * @param delimit	the delimeter replacement
	 * @param whereToBreak before or after, can be done as enumeration with javadoc for clarity
	 */
	public void separationIgnition(List<String> songAnalysed, String pattern, String delimit, String whereToBreak){
		songAnalysedTemp.addAll(listHandler (songAnalysed, pattern, delimit, whereToBreak));
		songAnalysed.clear();
		songAnalysed.addAll(songAnalysedTemp);
		songAnalysedTemp.clear();
	}//end of separationIgnition
	
	//this reads each element of the list and identifies any delimiters that exist and calls a breaker for further tokenizing
	//TODO processList, List may require memory on processing. A process Stream<String> can also be implemented for memory intensive tasks...
	public List<String> listHandler (List<String> songList, String patternSet, String delimitSet, String chosenBreaker){

		List<String> songListTemp = new ArrayList<String>();
		for (String element : songList){
			switch (element){
				case "|<new strophe>|"		: songListTemp.add("|<new strophe>|");
											  break;
				case "|<new lyric>|"		: songListTemp.add("|<new lyric>|");
					 						  break;
				case "|<repetition start>|"	: songListTemp.add("|<repetition start>|");
											  break;
				case "|<repetition end>|" 	: songListTemp.add("|<repetition end>|");
											  break;
				default				   : 	  switch (chosenBreaker){
											 	 case "backBreaker"		: songListTemp.addAll(backBreaker(element, patternSet, delimitSet));
																  		  break;
											 	 case "frontBreaker"	: songListTemp.addAll(frontBreaker(element, patternSet, delimitSet));
											 	 						  break;
											  }
			}
		}
		
		return songListTemp;
		
	}//end of listHandler
	
	//TODO javadoc, rename to breakAfterString
	//A method that used regex to break a string. Used if we need to separate it and discart the remaining part
	public List<String> backBreaker(String input, String patternSet, String delimitSet){
		
		//the list to be returned
		List<String> results = new ArrayList<String>();
				
		Pattern r = Pattern.compile(patternSet);
		Matcher m = r.matcher(input);
	
		if (m.find()){
			for(String thing:input.split(patternSet)){
				results.add(delimitSet);
				results.add(thing);
			}	
		}else{
			results.add(input);
		}
		return results;	
	}//end of backBreaker
	
	//TODO rename to breakBeforeString
	//A method that used regex to break a string. Used if we need to separate it and keep the remaining part
	public static List<String> frontBreaker(String input, String patternSet, String delimitSet){
		
		//the list to be returned
		List<String> results = new ArrayList<String>();
				
		Pattern r = Pattern.compile(patternSet);
		Matcher m = r.matcher(input);
	
		if (m.find()){ //TODO repetition with previous method, maybe you can replace with a boolean input and a triad Operator
			results.add(delimitSet);
			results.add(input.split(patternSet)[1]);		
		}else{
			results.add(input);
		}
		return results;	
	}//end of frontBreaker
}
