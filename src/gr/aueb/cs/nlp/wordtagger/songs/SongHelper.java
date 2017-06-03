package gr.aueb.cs.nlp.wordtagger.songs;

import gr.aueb.cs.nlp.wordtagger.util.CallBack;
import gr.aueb.cs.nlp.wordtagger.util.FileHelper;
/**
 * A class that helps with reading a song.
 * 
 * @author Alexandros
 * @since 5/2017
 */
public class SongHelper {
	
	/**
	 * This method is used to read a file and store it into a string
	 * for processing by the tokenize method.
	 * @param path	The path of the file to the user's selected directory.
	 * @return strb.toString()	A StringBuilder object which is turned into.
	 */
	
	public static String readSongFile(String path){		
		StringBuilder strb = new StringBuilder();
		
		FileHelper.readFile(path, new CallBack<String>()  {
			
			@Override
			public void call(String t) {
				String line = t;
				strb.append(line).append("\n");
			}
		});
		
		return strb.toString();
	}//end of readSongFile
}
