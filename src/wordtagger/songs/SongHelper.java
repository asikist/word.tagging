package wordtagger.songs;

import gr.aueb.cs.nlp.wordtagger.util.CallBack;
import gr.aueb.cs.nlp.wordtagger.util.FileHelper;

public class SongHelper {
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
