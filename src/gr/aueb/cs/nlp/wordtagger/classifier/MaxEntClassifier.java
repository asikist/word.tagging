package gr.aueb.cs.nlp.wordtagger.classifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.classify.RVFDataset;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.optimization.DiffFunction;
import edu.stanford.nlp.optimization.Minimizer;
import edu.stanford.nlp.optimization.QNMinimizer;
import edu.stanford.nlp.util.Factory;
import gr.aueb.cs.nlp.wordtagger.data.structure.Word;
import gr.aueb.cs.nlp.wordtagger.data.structure.WordSet;
import gr.aueb.cs.nlp.wordtagger.util.FileHelper;
/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class MaxEntClassifier implements Classifier, Serializable {

	private static final long serialVersionUID = 1L;
	private final LinearClassifierFactory<String, String> classifierFactory;
	private LinearClassifier<String, String> classifier;
	
	/**
	 * A default verbose Stanford classifier, 
	 * with 1000 maximum iterations, 
	 * 100 states in memory and 10^9 tolerance
	 */
	public MaxEntClassifier() {
		this(true, 1000,100,0.000000001);
	}
	
	/**
	 * Constructor for more parameters.
	 * @param verbose, whether the classifier should log while training
	 * @param maxIterations, the maximum iterations, after which the classifier should stop.
	 * @param memStates, the number of previous estimate vector pairs to store
	 * @param tolerance, early stopping, checking the Loss Function at every iteration,
	 * and then stopping the algorithm, if it is smaller than tolerance.
	 */
	public MaxEntClassifier(boolean verbose, int maxIterations, int memStates, double tolerance){
		LinearClassifierFactory<String, String> factory = new LinearClassifierFactory<>(tolerance);
		Factory<Minimizer<DiffFunction>> minimizerCreator = customQN(verbose, maxIterations, memStates);
		factory.setMinimizerCreator(minimizerCreator);
		this.classifierFactory = factory;
	}

	final private Factory<Minimizer<DiffFunction>> customQN(boolean verbose, int iterations, int mem) {
		return new Factory<Minimizer<DiffFunction>>() {
			private static final long serialVersionUID = 9028306475652690036L;

			@Override
			public Minimizer<DiffFunction> create() {
				QNMinimizer qnMinimizer = new QNMinimizer(mem);
				qnMinimizer.terminateOnMaxItr(iterations);
				qnMinimizer.terminateOnNumericalZero(true);
				if (!verbose) {
					qnMinimizer.shutUp();
				}
				return qnMinimizer;
			}
		};
	}


	@Override
	public void train(List<Word> trainSet) {
		RVFDataset<String, String> ds = new RVFDataset<>();
		ds.addAll(WordSet.toStanfordSet(trainSet));
		//unfortunately couldn't find hot to retrain;
		this.classifier = classifier == null ? classifierFactory.trainClassifier(ds) : classifier;
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
		this.classifier = null;
		// TODO Unimplemented, may not be needed at all
		
	}

}
