package gr.aueb.cs.nlp.wordtagger.classifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.RVFDataset;
import edu.stanford.nlp.classify.SVMLightClassifier;
import edu.stanford.nlp.ling.Datum;
import gr.aueb.cs.nlp.wordtagger.data.structure.Word;
import gr.aueb.cs.nlp.wordtagger.data.structure.WordSet;
import gr.aueb.cs.nlp.wordtagger.util.FileHelper;

public class SVMClassifier implements Classifier, Serializable{
	private static final long serialVersionUID = 1L;
	private SVMWindows64Factory<String, String> classifierFactory;
	private SVMLightClassifier<String, String> classifier;
	
	/**
	 * 
	 * @author Thomas Asikis
	 * @license Copyright (c) 2016 Thomas Asikis
	 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
	 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
	 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
	 */
	public SVMClassifier(){
		this(true, 3, true, 6.8, 1);
	}
	
	/**
	 * More parameters constructor,
	 * @param verbose, if the classifier should log while training
	 * @param verbosityLevel, how much info th classifier logs
	 * @param useSigmoid, if the SVM should use sigmoid function.
	 * @param c, SVM hyperparameter, usually calculated with cross validations
	 * @param folds, the folds for cross validation.
	 */
	public SVMClassifier(boolean verbose, int verbosityLevel, boolean useSigmoid, double c,int folds){
		classifierFactory = new SVMWindows64Factory<>();
		classifierFactory.verbose = true;
		classifierFactory.setSvmLightVerbosity(3);
		classifierFactory.setUseSigmoid(true);
		classifierFactory.setC(6.8);
		//no cross validation for sigma selections
		classifierFactory.setFolds(1);
	}
	
	
	@Override
	public void train(List<Word> trainSet) {
		RVFDataset<String, String> ds = new RVFDataset<>();
		ds.addAll(WordSet.toStanfordSet(trainSet));
		//classifierFactory.heldOutSetC(ds, 0.1, new MultiClassAccuracyStats<String>(1), new GoldenSectionLineSearch(true));
		//classifierFactory.crossValidateSetC(ds, 2, new MultiClassAccuracyStats<String>(1), new GoldenSectionLineSearch(true));
		this.classifier = classifier == null ? classifierFactory.trainClassifierBasic(ds) : classifier;		
	}


	@SuppressWarnings("unchecked")
	public static LinearClassifier<String, Double> deserialize(String path) {
		String classifierJ = FileHelper.fromFile(path);
		Gson gson = new Gson();
		LinearClassifier<String, Double> myClass = gson.fromJson(classifierJ, LinearClassifier.class);
		return myClass;
	}

	@Override
	public String classify(Word input) {
		Datum<String, String> datum = WordSet.word2Datum(input);
		return classifier.classOf(datum);
	}

	@Override
	public List<Word> classifySet(List<Word> input) {
		List<Word> result = new ArrayList<>();
		for (Word w : input) {
			result.add(new Word(w.getValue(), classify(w)));
		}
		return result;
	}


	@Override
	public void reset() {
		classifier = null;
		
	}

	
}
