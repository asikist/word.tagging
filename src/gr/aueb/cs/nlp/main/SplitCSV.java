package gr.aueb.cs.nlp.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import gr.aueb.cs.nlp.wordtagger.util.FileHelper;


/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class SplitCSV {
	

	
	public static void main(String[] args) throws IOException{
		
		Files.walk(new File("C:/Users/Thomas/Desktop/inputEmbeddings").toPath()).forEach(p->{			
			SplitCSV split =  new SplitCSV("C:/Users/Thomas/Desktop/inputEmbeddings", p.getFileName().toFile().getName(), "C:/Users/Thomas/Desktop/output");
			try {
				split.readStreamOfLinesUsingFilesWithTryBlock();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		});
		
	}
	
	
	public String folder;
	public String file;
	public String outputFolder;
	
	
	
	
	public SplitCSV(String folder, String file, String outputFolder) {
		this.folder = folder;
		this.file = file;
		this.outputFolder = outputFolder;		
		
		try {
			Files.createDirectories(Paths.get(outputFolder));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readStreamOfLinesUsingFilesWithTryBlock() throws IOException
	{
	    Path path = Paths.get(folder, file);
	    try(Stream<String> lines = Files.lines(path)){
	      lines.forEach(l->toFile(l)); 
	    }
	}
	
	public void toFile(String line){
		String[] split  = line.split(" ");
		StringBuilder strb1 =  new StringBuilder(split[0]+" ");
		StringBuilder strb2 =  new StringBuilder(split[0]+" ");
		int half = (split.length-1)/2;
		for(int i = 1 ; i <= (split.length-1)/2 ; i++){
			strb1.append(split[i]);
			strb2.append(split[half + i]);
			if(i < half){
				strb1.append(" ");
				strb2.append(" ");
			} else {
				strb1.append("\n");
				strb2.append("\n");
			}
		}
		FileHelper.toFile(outputFolder+"/"+file+"_pre"+"_"+half, strb1.toString());
		FileHelper.toFile(outputFolder+"/"+file+"_su"+"_"+half, strb2.toString());
	}

}
