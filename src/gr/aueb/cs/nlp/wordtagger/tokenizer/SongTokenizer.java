package gr.aueb.cs.nlp.wordtagger.tokenizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import gr.aueb.cs.nlp.wordtagger.songs.SongHelper;
/**
 * A class containing methods that perform tokenizing on a song.
 * 
 * @author Alexandros
 * @since 5/2017
 */

public class SongTokenizer implements Tokenizer {

	/**
	 * Here we declare the variables regarding the tokenization by verse,
	 * lyric, repetition(start & end) and word. Each -Regex variable
	 * contains the regular expression which will be used to identify the
	 * above and each -Token to specify the token that marks them. To mark
	 * a repetition start, the string split has to be performed in a
	 * different way than the others because it's the only case we need to
	 * keep the part after the regular expression pattern. This is specified
	 * by an enum operator.
	 */
	
	static final String verseRegex = "(\n\n)";
	static final String verseToken = "|<new verse>|";

	static final String lyricRegex = "(\n)";
	static final String lyricToken = "|<new lyric>|";

	static final String repetitionStartRegex = "(\\[)";
	static final String repetitionStartToken = "|<repetition start>|";

	static final String repetitionEndRegex = "(\\]\\s?x\\d{1}\\s*)";
	static final String repetitionEndToken = "|<repetition end>|";

	static final String wordRegex = "(\\s{1,2})";
	static final String wordToken = "|<word>|";

	static List<String> songAnalysed = new ArrayList<String>();
	static List<String> songAnalysedTemp = new ArrayList<String>();

	/**
	 * The main method used to run the SongTokenizer as a standalone tool.
	*/
	public static void main(String[] args) {

		SongTokenizer sr = new SongTokenizer();
		/*
		 * This is the path set for running it on my PC,
		 * obviously has to be set differently for someone else
		*/
		String rawSongText = SongHelper.readSongFile(
				FileUtils.getUserDirectoryPath()
				+ File.separator + "Desktop"
				+ File.separator + "Workspace"
				+ File.separator + "Soft_Engine"
				+ File.separator + "lyricsTest");
		sr.tokenize(rawSongText);
		int i = 0;
		for (String s:songAnalysed) {
			System.out.println(s);
		}
	} //end of main

	/**
	 * This method combines the other methods and
	 * performs cleanup processes on the content
	 * of the song in order to perform a tokenization process to a song.
	 * @param rawSongText	The input to tokenize. The untokenized song.
	 * @return songAnalysed  A List<String> with each list element
	 * 	containing either a token or a word from the song.
	 */
	public List<String> tokenize(String rawSongText) {

		/*
		 * FIRST CLEANUP
		 * here we clean up our input from puncuation marks (,.!"')
		*/
		rawSongText = rawSongText.replaceAll("[,.!\"]", "");
		//done separately because words may be linked by it
		rawSongText = rawSongText.replaceAll("'", " ");

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

		/*
		 * SECOND CLEANUP, we remove the |<word>| token because
		 * it comes as a collateral due to the way the tokenizing occurs
		 */
		songAnalysedTemp.add(wordToken);
		songAnalysed.removeAll(songAnalysedTemp);
		songAnalysedTemp.clear();

		return songAnalysed;
	} //end of tokenize

	/**
	 * This method takes a list of tokens as an input and further splits it
	 * based on some regular expression patterns
	 * @param pattern	the delimiter pattern to do the splitting
	 * @param delimit	the delimiters replacement
	 * @param chosenBreaker before or after, can be done as enumeration with
	 * javadoc for clarity
	 */
	private static void splitSongTokens(String pattern, String delimit, BreakType chosenBreaker) {
		processList(pattern, delimit, chosenBreaker);
		songAnalysed.clear();
		songAnalysed.addAll(songAnalysedTemp);
		songAnalysedTemp.clear();
	} //end of splitSongTokens

	/*TODO List may require memory on processing. A process Stream<String>
	 * can also be implemented for memory intensive tasks...
	 */
	/**
	 * This method runs through the list and identifies any delimiters
	 * that exist and calls the breaker method for further tokenizing.
	 * Calls the stringBreak by passing on a list element which will be
	 * examined for tokenization.
	 * @param patternSet	The defined pattern(regular expression)
	 * based on which the tokenizing will be performed.
	 * @param delimitSet	The delimiter which will signify the string
	 * split and the token if the pattern is found.
	 * @param chosenBreaker		The enum field defining where the string
	 * has to be split(depending on the kind of tokenization process).
	 */
	private static void processList(String patternSet, String delimitSet, BreakType chosenBreaker) {

		for (String element : songAnalysed) {
			switch (element) {
				case verseToken:
					songAnalysedTemp.add(verseToken);
					break;
				case lyricToken:
					songAnalysedTemp.add(lyricToken);
					break;
				case repetitionStartToken:
					songAnalysedTemp.add(repetitionStartToken);
					break;
				case repetitionEndToken :
					songAnalysedTemp.add(repetitionEndToken);
					break;
				default:
					songAnalysedTemp.addAll(stringBreak(element, patternSet,
												delimitSet, chosenBreaker));
					break;
			}
		}
	} //end of processList

	/**
	 * This method used regular expressions on a string passed on by
	 * the method processList and examines it for tokenizing.
	 * @param input		The string passed on by the method processList
	 * @param patternSet	The defined pattern(regular expression) based
	 * 	on which the tokenizing will be performed.
	 * @param delimitSet	The delimiter which will signify the string
	 * 	split and the token if the pattern is found.
	 * @param chosenBreaker		The enum field defining where the string
	 * 	has to be split(depending on the kind of tokenization process).
	 * @return	results		A List<String> containing the tokens and
	 * 	split strings that result from the tokenization.
	 */
	private static List<String> stringBreak(String input, String patternSet, 
										String delimitSet, BreakType chosenBreaker) {

		List<String> results = new ArrayList<String>();

		Pattern r = Pattern.compile(patternSet);
		Matcher m = r.matcher(input);

		if (m.find()) {
			switch (chosenBreaker) {
				case breakAfter:
					for (String thing:input.split(patternSet)) {
										results.add(delimitSet);
										results.add(thing);
					}
					break;
				case breakBefore:
					results.add(delimitSet);
					results.add(input.split(patternSet)[1]);
					break;
			}
		} else {
			results.add(input);
		}
		return results;
	} //end of stringBreaker
	
	/**
	 * This returns the songAnalysed list
	 * (mainly used for the testing).
	 * @return	songAnalysed	The list containing
	 * the tokenized song.
	 */
	public static List<String> getSongAnalysed() {
		return songAnalysed;
	}
} //end of SongTokenizer
