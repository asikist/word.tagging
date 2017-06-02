package gr.aueb.cs.nlp.wordtagger.tokenizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import wordtagger.songs.SongHelper;
/**
 * A class containing methods that perform tokenizing on a song.
 * Methods are:
 * 		main
 * 		tokenize
 * 		splitSongTokens
 * 		processList
 * 		BreakType(enum)
 * 		stringBreak
 * 		readSongFile
 * @author Alexandros
 *
 */
public class SongTokenizer implements Tokenizer{
	
	/*
	 * Here we declare the variables regarding the tokenization by verse, lyric, repetition(start & end) and word.
	 * Each -Regex variable contains the regular expression which will be used to identify the above and each -Token
	 * to specify the token that marks them.
	 * To mark a repetition start, the string split has to be performed in a different way than the others because it's the
	 * only case we need to keep the part after the regular expression pattern. This is specified by an enum operator
	 */
	final static String verseRegex = "(\n\n)";
	final static String verseToken = "|<new verse>|";
	
	final static String lyricRegex = "(\n)";
	final static String lyricToken = "|<new lyric>|";
	
	final static String repetitionStartRegex = "(\\[)";
	final static String repetitionStartToken = "|<repetition start>|";
	
	final static String repetitionEndRegex = "(\\]\\s?x\\d{1}\\s*)";
	final static String repetitionEndToken = "|<repetition end>|";
	
	final static String wordRegex = "(\\s{1,2})";
	final static String wordToken = "|<word>|";
	
	static List<String> songAnalysed = new ArrayList<String>();
	static List<String> songAnalysedTemp = new ArrayList<String>(); //for various tedious uses
	
	/**
	 * The main method used to run the SongTokenizer as a standalone tool
	 */
	public static void main (String [] args){
		
		SongTokenizer sr = new SongTokenizer();
		//This is the path set for running it on my PC, obviously has to be set differently for someone else
		String rawSongText = SongHelper.readSongFile(FileUtils.getUserDirectoryPath() 
				+ File.separator +"Desktop" 
				+ File.separator +"Workspace" 
				+ File.separator +"Soft_Engine"
				+ File.separator + "songTestGR");
		sr.tokenize(rawSongText);
		for (String s:songAnalysed)
			System.out.println(s);
		
	}//end of main
	
	/**
	 * This method combines the other methods and performs cleanup processes
	 * on the content of the song in order to perform a tokenization process to a song.
	 * @return songAnalysed  a List<String> with each list element containing either a token or a word from the song
	 */
	public List<String> tokenize (String rawSongText) {

		/*FIRST CLEANUP
		* here we try to clean up our input from puncuation marks (,.!"') 
		*/
		rawSongText = rawSongText.replaceAll("[,.!\"]", "");
		rawSongText = rawSongText.replaceAll("'", " "); //done separately because words may be linked by it
				
		songAnalysed.add(rawSongText);
		
		//initial tokenization, separating the verses
		splitSongTokens(verseRegex, verseToken, BreakType.breakAfter);
		//second tokenization, separating the lyrics
		splitSongTokens(lyricRegex, lyricToken, BreakType.breakAfter);
		//third tokenization, marking a repetition start
		splitSongTokens(repetitionStartRegex, repetitionStartToken, BreakType.breakBefore);
		//fourth tokenization, marking a repetition end
		splitSongTokens(repetitionEndRegex, repetitionEndToken, BreakType.breakAfter);
		//fifth tokenization, separating words
		splitSongTokens(wordRegex, wordToken, BreakType.breakAfter);

		//SECOND CLEANUP, we remove the |<word>| token because it comes as a collateral due to the way the tokenizing occurs
		songAnalysedTemp.add(wordToken);
		songAnalysed.removeAll(songAnalysedTemp);
		songAnalysedTemp.clear();

		return songAnalysed;
	}//end of tokenize
	
	//takes care of the object's lists and calls listHandler
	/**
	 * This method takes a list of tokens as an input and further splits it based on some regular expression patterns
	 * @param songAnalysed input string tokens from a song
	 * @param pattern	the dlimeteter pattern to do the splitting
	 * @param delimit	the delimeter replacement
	 * @param whereToBreak before or after, can be done as enumeration with javadoc for clarity
	 */
	private static void splitSongTokens(String pattern, String delimit, BreakType chosenBreaker){
		processList (pattern, delimit, chosenBreaker);
		songAnalysed.clear();
		songAnalysed.addAll(songAnalysedTemp);
		songAnalysedTemp.clear();
	}//splitSongTokens
	
	//TODO List may require memory on processing. A process Stream<String> can also be implemented for memory intensive tasks...
	/**
	 * This method runs through the list and identifies any delimiters that exist and calls a breaker for further tokenizing. 
	 * Calls the stringBreak by passing on a list element which will be examined for tokenization.
	 * @param patternSet	the defined pattern(regular expression) based on which the tokenizing will be performed
	 * @param delimitSet	the delimiter which will signify the string split and the token if the pattern is found
	 * @param chosenBreaker		the enum field defining where the string has to be split(depending on the kind of tokenization process)
	 */
	private static void processList (String patternSet, String delimitSet, BreakType chosenBreaker){

		for (String element : songAnalysed){
			switch (element){
				case verseToken				:	songAnalysedTemp.add(verseToken);
												break;
				case lyricToken				:	songAnalysedTemp.add(lyricToken);
					 						 	break;
				case repetitionStartToken	:	songAnalysedTemp.add(repetitionStartToken);
											 	break;
				case repetitionEndToken 	:	songAnalysedTemp.add(repetitionEndToken);
											 	break;
				case wordToken				:	songAnalysedTemp.add(wordToken);
												break;
				default						: 	songAnalysedTemp.addAll(stringBreak(element, patternSet, delimitSet, chosenBreaker));
												break;				  		  		  
			}
		}		
	}//end of processList
	
	//A method that used regex to break a string.
	/**
	 * This method used regular expressions on a string passed on by the method processList and examines it for tokenizing.
	 * @param input		the string passed on by the method processList 
	 * @param patternSet	the defined pattern(regular expression) based on which the tokenizing will be performed
	 * @param delimitSet	the delimiter which will signify the string split and the token if the pattern is found
	 * @param chosenBreaker		the enum field defining where the string has to be split(depending on the kind of tokenization process)
	 * @return	results		a List<String> containing the tokens and split strings that result from the tokenization
	 */
	private static List<String> stringBreak(String input, String patternSet, String delimitSet, BreakType chosenBreaker){
		
		List<String> results = new ArrayList<String>();
				
		Pattern r = Pattern.compile(patternSet);
		Matcher m = r.matcher(input);
	
		if (m.find()){
			switch (chosenBreaker){
				case breakAfter:	for(String thing:input.split(patternSet)){
										results.add(delimitSet);
										results.add(thing);
									}	
									break;
				case breakBefore:	results.add(delimitSet);
									results.add(input.split(patternSet)[1]);
									break;
			}
		}else{
			results.add(input);
		}
		return results;	
	}//end of stringBreaker
	
	/**
	 * This method is used to read a file and store it into a string for processing by the tokenize method.
	 * @param path	the path of the file to the user's selected directory
	 * @return strb.toString()	a StringBuilder object which is turned into 
	 */

	
	
}//end of SongTokenizer
