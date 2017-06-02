package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import gr.aueb.cs.nlp.wordtagger.tokenizer.SongTokenizer;
import junit.framework.Assert;

public class SongTokenizerTest {
	
	
	SongTokenizer song = new SongTokenizer();
	
	String songLyrics = "You can run on for a long time\n"
							+ "Run on for a long time\n"
							+ "Run on for a long time\n"
							+ "[Sooner or later God'll cut you down]x2\n\n"
							+ "Go tell that long tongue liar\n"
							+ "Go and tell that midnight rider\n"
							+ "Tell the rambler, the gambler, the back biter\n"
							+ "[Tell 'em that God's gonna cut 'em down]x2";
	List<String> songLyricsTokenized = new ArrayList<String>();
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
							  
	@Test
	public void tokenizeTest(){
		songLyricsTokenized.add(verseToken);
		songLyricsTokenized.add(lyricToken);
		songLyricsTokenized.add("You");
		songLyricsTokenized.add("can");
		songLyricsTokenized.add("run");
		songLyricsTokenized.add("on");
		songLyricsTokenized.add("for");
		songLyricsTokenized.add("a");
		songLyricsTokenized.add("long");
		songLyricsTokenized.add("time");
		songLyricsTokenized.add(lyricToken);
		songLyricsTokenized.add("Run");
		songLyricsTokenized.add("on");
		songLyricsTokenized.add("for");
		songLyricsTokenized.add("a");
		songLyricsTokenized.add("long");
		songLyricsTokenized.add("time");
		songLyricsTokenized.add(lyricToken);
		songLyricsTokenized.add("Run");
		songLyricsTokenized.add("on");
		songLyricsTokenized.add("for");
		songLyricsTokenized.add("a");
		songLyricsTokenized.add("long");
		songLyricsTokenized.add("time");
		songLyricsTokenized.add(lyricToken);
		songLyricsTokenized.add(repetitionStartToken);
		songLyricsTokenized.add(repetitionEndToken);
		songLyricsTokenized.add("Sooner");
		songLyricsTokenized.add("or");
		songLyricsTokenized.add("later");
		songLyricsTokenized.add("God");
		songLyricsTokenized.add("ll");
		songLyricsTokenized.add("cut");
		songLyricsTokenized.add("you");
		songLyricsTokenized.add("down");
		songLyricsTokenized.add(verseToken);
		songLyricsTokenized.add(lyricToken);
		songLyricsTokenized.add("Go");
		songLyricsTokenized.add("tell");
		songLyricsTokenized.add("that");
		songLyricsTokenized.add("long");
		songLyricsTokenized.add("tongue");
		songLyricsTokenized.add("liar");
		songLyricsTokenized.add(lyricToken);
		songLyricsTokenized.add("Go");
		songLyricsTokenized.add("and");
		songLyricsTokenized.add("tell");
		songLyricsTokenized.add("that");
		songLyricsTokenized.add("midnight");
		songLyricsTokenized.add("rider");
		songLyricsTokenized.add(lyricToken);
		songLyricsTokenized.add("Tell");
		songLyricsTokenized.add("the");
		songLyricsTokenized.add("rambler");
		songLyricsTokenized.add("the");
		songLyricsTokenized.add("gambler");
		songLyricsTokenized.add("the");
		songLyricsTokenized.add("back");
		songLyricsTokenized.add("biter");
		songLyricsTokenized.add(lyricToken);
		songLyricsTokenized.add(repetitionStartToken);
		songLyricsTokenized.add(repetitionEndToken);
		songLyricsTokenized.add("Tell");
		songLyricsTokenized.add("em");
		songLyricsTokenized.add("that");
		songLyricsTokenized.add("God");
		songLyricsTokenized.add("s");
		songLyricsTokenized.add("gonna");
		songLyricsTokenized.add("cut");
		songLyricsTokenized.add("em");
		songLyricsTokenized.add("down");
		
		List<String> songLyricsToTest = new ArrayList<String>();
		SongTokenizer songTokenizerTest = new SongTokenizer();
		songLyricsToTest = songTokenizerTest.tokenize(songLyrics);
		Assert.assertEquals(songLyricsToTest, songLyricsTokenized);
	}
}
