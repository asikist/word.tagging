package gr.aueb.cs.nlp.wordtagger.experiment;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import gr.aueb.cs.nlp.wordtagger.classifier.Classifier;
import gr.aueb.cs.nlp.wordtagger.classifier.MaxEntClassifier;
import gr.aueb.cs.nlp.wordtagger.classifier.MetaTagger;
import gr.aueb.cs.nlp.wordtagger.classifier.evaluation.Evaluation;
import gr.aueb.cs.nlp.wordtagger.data.structure.Model;
import gr.aueb.cs.nlp.wordtagger.data.structure.Word;
import gr.aueb.cs.nlp.wordtagger.data.structure.WordSet;
import gr.aueb.cs.nlp.wordtagger.data.structure.embeddings.Embeddings;
import gr.aueb.cs.nlp.wordtagger.data.structure.features.FeatureBuilder;
import gr.aueb.cs.nlp.wordtagger.util.CallBack;
import gr.aueb.cs.nlp.wordtagger.util.FileHelper;
/**
 * 
 * @description A class that helps us to setup nlp experiments on tagging words.  
 * it has builder and can be used to programmatically parametrize experiments based on nlp
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class ExperimentSetup {
	private String trainPath;
	private String categoriesPath;
	private String delimeter;

	private Evaluation eval;

	private Model model;
	private String testPath;
	private int totalShards;
	private Classifier classifier;

	private int suffixLength;
	private int lookAhead;
	private int lookBehind;

	private boolean useEmbeddings;
	private boolean useStatistical;
	private String embeddingsFile;

	private boolean evalToCSV;
	private String testOutput;
	String evalLocation;

	private Map<Double, Double> trainError = new LinkedHashMap<>();
	private Map<Double, Double> testError = new LinkedHashMap<>();

	public ExperimentSetup(String trainPath, String categoriesPath, String delimeter, Evaluation eval, Model model,
			String testPath, int totalShards, Classifier classifier, int suffixLength, int lookAhead, int lookBehind,
			boolean useEmbeddings, boolean useStatistical, String embeddingsFile, boolean evalToCSV,
			String evalLocation, Map<Double, Double> trainError, Map<Double, Double> testError, String testOutput) {
		super();
		this.trainPath = trainPath;
		this.categoriesPath = categoriesPath;
		this.delimeter = delimeter;
		this.eval = eval;
		this.model = model;
		this.testPath = testPath;
		this.totalShards = totalShards;
		this.classifier = classifier;
		this.suffixLength = suffixLength;
		this.lookAhead = lookAhead;
		this.lookBehind = lookBehind;
		this.useEmbeddings = useEmbeddings;
		this.useStatistical = useStatistical;
		this.embeddingsFile = embeddingsFile;
		this.evalToCSV = evalToCSV;
		this.evalLocation = evalLocation;
		this.trainError = trainError;
		this.testError = testError;
		this.testOutput = testOutput;
	}

	/**
	 * exwcutes an experiement
	 */
	public void execute() {
		int trainSize = getDataSet(trainPath).size();
		/*** Learning curve generation */
		for (int j = trainSize - 1; j > 0; j = j - trainSize / totalShards) {
			innerLoop(j);
		}

	}
	
	/**
	 * a loop which excecutes for each dataset
	 * @param size
	 */
	private void innerLoop(int size){
		Model model = totalShards <= 1 && this.model != null ? this.model : generateModel(size, null);
		FeatureBuilder fb = new FeatureBuilder(model);
		LinkedList<Word> trainSet = model.getTrainSet().size() > 0 ? model.getTrainSet() : getDataSet(trainPath);
		LinkedList<Word> testSet = getDataSet(testPath);
		trainSet = generateFeats(trainSet, fb);
		testSet = generateFeats(testSet, fb);
		classifier.reset();
		//trainSet.forEach(w->System.out.println(w.toString()));
		classifier.train(trainSet);
		try {
			List<Word> classifiedTrainSet = classifier.classifySet(trainSet);
			List<Word> classifiedTestSet = classifier.classifySet(testSet);
			Logger.getAnonymousLogger().log(Level.INFO, "[Experiment] Algorithm Accuracy on train Set: " + eval.evaluateAcc(trainSet, classifiedTrainSet));
			Logger.getAnonymousLogger().log(Level.INFO, "[Experiment] Algorithm Accuracy on train Set: " + eval.evaluateAcc(testSet, classifiedTestSet));
			trainError.put((double)size, eval.evaluateAcc(trainSet, classifiedTrainSet));
			testError.put((double) size, eval.evaluateAcc(testSet, classifiedTestSet));
			if(testOutput!=null){
				WordSet.toFile(testOutput, classifiedTestSet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (evalToCSV)
			toCSV();
	}
	
	/**
	 * generate features of words
	 * @param input
	 * @param fb
	 * @return
	 */
	//TODO move
	static Embeddings embs = null;
	private LinkedList<Word> generateFeats(LinkedList<Word> input, FeatureBuilder fb) {
		if (useStatistical)
			fb.generateFeats(input, lookBehind, lookAhead);
		if (useEmbeddings)
			fb.generateFeats(input, lookBehind, lookAhead, embs == null ? embs = new Embeddings(embeddingsFile) : embs);
			//input.stream().forEach(w->System.out.println(w.toString()));
		return input;
	}
	
	/**
	 * saves results to csv
	 */
	private void toCSV() {
		StringBuilder toWrite = new StringBuilder();
		toWrite.append("TrainPercentage,AccuracyTrain,AccuracyTest\n");
		for (Entry<Double, Double> entry : trainError.entrySet()) {
			toWrite.append(entry.getKey());
			toWrite.append(",");
			toWrite.append(entry.getValue());
			toWrite.append(",");
			toWrite.append(testError.get(entry.getKey()));
			toWrite.append("\n");
		}
		try {
			FileHelper.toFile(evalLocation == null ? "resources/data/eval" + Long.toString(System.currentTimeMillis() / 1000) + ".csv"
					: evalLocation, toWrite.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public LinkedList<Word> getDataSet(String path) {
		final LinkedList<Word> dataSet = new LinkedList<>();
		FileHelper.readFile(path, new CallBack<String>() {
			@Override
			public void call(String t) {
				String[] a = t.split(delimeter);
				dataSet.add(new Word(a[0], a[1]));
			}
		});
		return dataSet;
	}

	/**
	 * generate model based on experiment parametrization
	 * @return
	 */
	public Model generateModel() {
		String path = categoriesPath;
		Model vc = new Model(path, 3);
		vc.generateStats(trainPath);
		return vc;
	}

	/**
	 * generate model based on experiment parametrization, and on a sybset of the trainset
	 * @return
	 */
	public Model generateModel(int limit, Model model) {
		if (model == null)
			model = new Model(categoriesPath, suffixLength);
		if (eval == null) 
			eval = new Evaluation(new MetaTagger(model, "resources/config/lowGazzete.txt"), false, true);
		model.generateStats(trainPath, limit);
		return model;
	}
	

	
	public String getTrainPath() {
		return trainPath;
	}

	public String getCategoriesPath() {
		return categoriesPath;
	}

	public String getDelimeter() {
		return delimeter;
	}

	public Evaluation getEval() {
		return eval;
	}

	public Model getModel() {
		return model;
	}

	public String getTestPath() {
		return testPath;
	}

	public int getTotalShards() {
		return totalShards;
	}

	public Classifier getClassifier() {
		return classifier;
	}

	public int getSuffixLength() {
		return suffixLength;
	}

	public int getLookAhead() {
		return lookAhead;
	}

	public int getLookBehind() {
		return lookBehind;
	}

	public boolean isUseEmbeddings() {
		return useEmbeddings;
	}

	public boolean isUseStatistical() {
		return useStatistical;
	}

	public String getEmbeddingsFile() {
		return embeddingsFile;
	}

	public boolean isEvalToCSV() {
		return evalToCSV;
	}

	public String getEvalLocation() {
		return evalLocation;
	}

	public Map<Double, Double> getTrainError() {
		return trainError;
	}

	public Map<Double, Double> getTestError() {
		return testError;
	}



	/*Setters */

	public void setTrainPath(String trainPath) {
		this.trainPath = trainPath;
	}

	public void setCategoriesPath(String categoriesPath) {
		this.categoriesPath = categoriesPath;
	}

	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}

	public void setEval(Evaluation eval) {
		this.eval = eval;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setTestPath(String testPath) {
		this.testPath = testPath;
	}

	public void setTotalShards(int totalShards) {
		this.totalShards = totalShards;
	}

	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	public void setSuffixLength(int suffixLength) {
		this.suffixLength = suffixLength;
	}

	public void setLookAhead(int lookAhead) {
		this.lookAhead = lookAhead;
	}

	public void setLookBehind(int lookBehind) {
		this.lookBehind = lookBehind;
	}

	public void setUseEmbeddings(boolean useEmbeddings) {
		this.useEmbeddings = useEmbeddings;
	}

	public void setUseStatistical(boolean useStatistical) {
		this.useStatistical = useStatistical;
	}

	public void setEmbeddingsFile(String embeddingsFile) {
		this.embeddingsFile = embeddingsFile;
	}

	public void setEvalToCSV(boolean evalToCSV) {
		this.evalToCSV = evalToCSV;
	}

	public void setEvalLocation(String evalLocation) {
		this.evalLocation = evalLocation;
	}

	public void setTrainError(Map<Double, Double> trainError) {
		this.trainError = trainError;
	}

	public void setTestError(Map<Double, Double> testError) {
		this.testError = testError;
	}





	//start of Builder
	static public class Builder {
		private String trainPath = "resources/data/train/train_low_23675_edited_new.train";
		private String categoriesPath = "resources/config/lowCats.txt";
		private String delimeter = " ";

		private Evaluation eval;

		private Model model;
		private String testPath = "resources/data/test/test_low_7878.train";
		private int totalShards = 1;
		private Classifier classifier = new MaxEntClassifier();

		private int suffixLength = 3;
		private int lookAhead = 1;
		private int lookBehind = 0;

		private boolean useEmbeddings = false;
		private boolean useStatistical = true;
		private String embeddingsFile = "/resources/data/";

		private boolean evalToCSV = true;
		String evalLocation;
		private String testOutput;

		private Map<Double, Double> trainError = new LinkedHashMap<>();
		private Map<Double, Double> testError = new LinkedHashMap<>();

		public Builder setTrainPath(String trainPath) {
			this.trainPath = trainPath;
			return this;
		}

		public Builder setCategoriesPath(String categoriesPath) {
			this.categoriesPath = categoriesPath;
			return this;
		}

		public Builder setDelimeter(String delimeter) {
			this.delimeter = delimeter;
			return this;
		}

		public Builder setEval(Evaluation eval) {
			this.eval = eval;
			return this;
		}

		public Builder setModel(Model model) {
			this.model = model;
			return this;
		}

		public Builder setTestPath(String testPath) {
			this.testPath = testPath;
			return this;
		}

		public Builder setTotalShards(int totalShards) {
			this.totalShards = totalShards;
			return this;
		}

		public Builder setClassifier(Classifier classifier) {
			this.classifier = classifier;
			return this;
		}

		public Builder setSuffixLength(int suffixLength) {
			this.suffixLength = suffixLength;
			return this;
		}

		public Builder setLookAhead(int lookAhead) {
			this.lookAhead = lookAhead;
			return this;
		}


		public Builder setLookBehind(int lookBehind) {
			this.lookBehind = lookBehind;
			return this;
		}

		public Builder setUseEmbeddings(boolean useEmbeddings) {
			this.useEmbeddings = useEmbeddings;
			return this;

		}

		public Builder setUseStatistical(boolean useStatistical) {
			this.useStatistical = useStatistical;
			return this;
		}

		public Builder setEmbeddingsFile(String embeddingsFile) {
			this.embeddingsFile = embeddingsFile;
			return this;
		}

		public Builder setEvalToCSV(boolean evalToCSV) {
			this.evalToCSV = evalToCSV;
			return this;
		}
		
		public Builder setEvalLocation(String evalLocation) {
			this.evalLocation = evalLocation;
			return this;
		}
		
		public Builder setTrainError(Map<Double, Double> trainError) {
			this.trainError = trainError;
			return this;
		}

		public Builder setTestError(Map<Double, Double> testError) {
			this.testError = testError;
			return this;
		}

		
		public Builder setTestOutput(String testOutput) {
			this.testOutput = testOutput;
			return this;
		}
		public ExperimentSetup build() {
			return new ExperimentSetup(trainPath, categoriesPath, delimeter, eval, model,testPath, totalShards,
					classifier, suffixLength, lookAhead, lookBehind, useEmbeddings, useStatistical, embeddingsFile,
					evalToCSV, evalLocation, trainError, testError, testOutput);
		}

	}
}
