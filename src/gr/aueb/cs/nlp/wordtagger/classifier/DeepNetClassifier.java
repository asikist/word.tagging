package gr.aueb.cs.nlp.wordtagger.classifier;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.recurrent.BaseRecurrentLayer;
import org.deeplearning4j.ui.weights.HistogramIterationListener;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import gr.aueb.cs.nlp.wordtagger.data.structure.Model;
import gr.aueb.cs.nlp.wordtagger.data.structure.Word;
import gr.aueb.cs.nlp.wordtagger.util.FileHelper;
/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class DeepNetClassifier implements Classifier, Serializable {
	private static final long serialVersionUID = 1L;
	private ComputationGraph net;
	private Model model;
	private int epochs;
	String modelPath = "resources/data/dmlp.ser";
			

	private static Logger log = Logger.getAnonymousLogger();
	private static Level lv = Level.INFO;

	public static Model generateModel(String catsPath, String trainPath) {

		Model vc = new Model(catsPath, 3);
		vc.generateStats(trainPath);
		return vc;
	}

	public DeepNetClassifier(Model model,boolean useHistogramListener, ComputationGraphConfiguration conf) {
		this.model = model;
		this.net = new ComputationGraph(conf);
		if (useHistogramListener)
			net.setListeners(
					Arrays.asList(new HistogramIterationListener(100)));
		this.net.init();

	}

	private List<DataSet> prepareDataSet(List<Word> dataSet) {
		List<DataSet> output = new ArrayList<>();
		List<double[]> features = new ArrayList<>();
		List<double[]> labels = new ArrayList<>();
		if(net.getLayers()[0] instanceof BaseRecurrentLayer){	//if the first layer is recurrent then the data should be reformed to sequeces	
			System.err.println("an LsTM");
			for (Word w : dataSet) {
				if (!w.getCategory().equals("null")) {
					features.add(w.getFeatureVec().getValues());
					labels.add(w.getFeatureVec().getLabels());
				}
				if (w.getCategory().equals("null") && features.size() > 0) {
					output.add(new DataSet(Nd4j.create(features.toArray(new double[0][])),
							Nd4j.create(labels.toArray(new double[0][]))));
					features = new ArrayList<>();
					labels = new ArrayList<>();
				}
			}			
		} else {
			for (Word w : dataSet) {
				if (!w.getCategory().equals("null")) {
					features.add(w.getFeatureVec().getValues());
					labels.add(w.getFeatureVec().getLabels());
				}				
			}	
			output.add(new DataSet(Nd4j.create(features.toArray(new double[0][])),
					Nd4j.create(labels.toArray(new double[0][]))));
		}
		
		// DataSet ds = new DataSet(Nd4j.create(features.toArray(new
		// double[0][])),
		// Nd4j.create(labels.toArray(new double[0][])));
		// ds.normalizeZeroMeanZeroUnitVariance();
		return output;

	}

	private int[] maxArrayToOne(double[] input) {
		int[] result = new int[input.length];
		int maxIndex = 0;
		for (int i = 1; i < input.length; i++) {
			double newnumber = input[i];
			if ((newnumber > input[maxIndex])) {
				maxIndex = i;
			}
		}
		for (int j = 0; j < input.length; j++) {
			result[j] = j == maxIndex ? 1 : 0;
		}
		// System.out.println(Arrays.toString(input) + " = " +
		// Arrays.toString(result));
		return result;
	}

	@Override
	public void train(List<Word> trainSet) {
		log = log == null ? Logger.getAnonymousLogger() : log; 
		lv = lv == null ? Level.INFO : lv;
		double best = Double.POSITIVE_INFINITY;
		List<DataSet> ds = prepareDataSet(trainSet);
		Nd4j.ENFORCE_NUMERICAL_STABILITY = true;
		for (int i = 0; i < epochs; i++) {
			for (int j = 0; j < ds.size(); j++) {
				net.fit(ds.get(j));
				//net.rnnClearPreviousState();
			}
			double score =  net.score();
			if(score < best){
				best = score;
				try {
					log.log(Level.INFO, "Now saving Model to " + modelPath);
					FileHelper.serialize(this, modelPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			log.log(lv, "Now on Epoch " + i + " score " + score);
		}
	}

	@Override
	public String classify(Word input) {
		if (input.getCategory().equals("null")) {
			net.rnnClearPreviousState();
		}
		return model.getCategogoryFromOneOfAK(
				maxArrayToOne(net.output(Nd4j.create(input.getFeatureVec().getValues()))[0].data().asDouble()));
	}

	@Override
	public List<Word> classifySet(List<Word> input) {
		List<Word> predicted = new ArrayList<>();
		for (Word w : input) {
			predicted.add(new Word(w.getValue(), classify(w)));
		}
		return predicted;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	public int getEpochs() {
		return epochs;
	}

	public void setEpochs(int epochs) {
		this.epochs = epochs;
	}
	
	
	
}
