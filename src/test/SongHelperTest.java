package test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import junit.framework.Assert;
import wordtagger.songs.SongHelper;

/**
 * The test class for the SongHelper class.
 * For the purposes of the test, it was decided
 * to use lyrics in the English language, to avoid
 * any errors due to the encoding
 * 
 * @author Alexandros
 * @since 6/2017
 */
public class SongHelperTest {
	String songLyrics = "You can run on for a long time\n"
			+ "Run on for a long time\n"
			+ "Run on for a long time\n"
			+ "[Sooner or later God'll cut you down]x2\n\n"
			+ "Go tell that long tongue liar\n"
			+ "Go and tell that midnight rider\n"
			+ "Tell the rambler, the gambler, the back biter\n"
			+ "[Tell 'em that God's gonna cut 'em down]x2\n";
	
	@Test
	/**
	 * This method tests the readSongFile method.
	 */
	public void readSongFileTest(){
		String songLyricsRead;
		SongHelper songHelperTest = new SongHelper();
		songLyricsRead = songHelperTest.readSongFile(FileUtils.getUserDirectoryPath()
														+ File.separator + "Desktop"
														+ File.separator + "Workspace"
														+ File.separator + "Soft_Engine"
														+ File.separator + "lyricsTest");
		Assert.assertEquals(songLyrics, songLyricsRead);

	}
	
}
