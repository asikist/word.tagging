package gr.aueb.cs.nlp.main;
/**
 * A class containing the paths and resources for ease of use.
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class Resources {
	
	//train set locations
	public static String TRAIN_LOW = "resources/data/train/train_low";
	public static String TRAIN_HIGH = "resources/data/train/train_high";

	//test set locations
	public static String TEST_LOW = "resources/data/test/test_low";
	public static String TEST_HIGH = "resources/data/test/test_high";

	
	//best embeddingss
	public static String EMBEDDINGS = "resources/data/embeddings/embeddings.zip";
	
	//where the results are being placed
	public static String EVAL_DIRECTORY = "resources/data/eval/";
	
	//the configuration files of categories
	public static String LOW_CATS = "resources/config/lowCats.txt";
	public static String HIGH_CATS = "resources/config/highCats.txt";
	
	//the delimeter for train and test files
	public static String fileDelimeter = " ";
	
	//the gazzete location, maybe wrong
	public static String HIGH_GAZZETE = "resources/config/highGazzete.txt";
	public static String LOW_GAZZETE = "resources/config/lowGazzete.txt";


	

}
