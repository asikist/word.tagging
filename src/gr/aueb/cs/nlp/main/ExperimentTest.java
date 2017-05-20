package gr.aueb.cs.nlp.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;

import gr.aueb.cs.nlp.computationgraphs.GraphConfigurations;
import gr.aueb.cs.nlp.wordtagger.classifier.CRFClassifier;
import gr.aueb.cs.nlp.wordtagger.classifier.Classifier;
import gr.aueb.cs.nlp.wordtagger.classifier.DeepNetClassifier;
import gr.aueb.cs.nlp.wordtagger.classifier.MaxEntClassifier;
import gr.aueb.cs.nlp.wordtagger.classifier.MetaClassifier;
import gr.aueb.cs.nlp.wordtagger.classifier.MetaTagger;
import gr.aueb.cs.nlp.wordtagger.classifier.SVMClassifier;
import gr.aueb.cs.nlp.wordtagger.classifier.evaluation.Evaluation;
import gr.aueb.cs.nlp.wordtagger.data.structure.Model;
import gr.aueb.cs.nlp.wordtagger.data.structure.Word;
import gr.aueb.cs.nlp.wordtagger.data.structure.WordSet;
import gr.aueb.cs.nlp.wordtagger.data.structure.features.FeatureBuilder;
import gr.aueb.cs.nlp.wordtagger.data.structure.features.FeatureVector;
import gr.aueb.cs.nlp.wordtagger.experiment.ExperimentSetup;
import gr.aueb.cs.nlp.wordtagger.util.CallBack;
import gr.aueb.cs.nlp.wordtagger.util.FileHelper;

/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis Permission is hereby granted, free
 *          of charge, to any person obtaining a copy of this software and
 *          associated documentation files (the "Software"), to deal in the
 *          Software without restriction, including without limitation the
 *          rights to use, copy, modify, merge, publish, distribute, sublicense,
 *          and/or sell copies of the Software, and to permit persons to whom
 *          the Software is furnished to do so, subject to the following
 *          conditions: The above copyright notice and this permission notice
 *          shall be included in all copies or substantial portions of the
 *          Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 *          KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *          WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *          NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 *          BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 *          ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *          CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *          SOFTWARE.
 */

public class ExperimentTest {
	static Logger log = Logger.getAnonymousLogger();
	static Level lv = Level.INFO;

	public static void main(String args[]) throws Exception {
		//qnStatFeatsTrainSmall(Resources.TRAIN_COMB_LOW, Resources.TEST_TH_LOW, "low_cats_stat_new.csv", 32);
		//qnEmbTrainSmall(Resources.TRAIN_COMB_LOW, Resources.TEST_TH_LOW, Resources.BEST_EMBEDDINGS, "low_cats_embeddings.csv", 32);
		
		
		//qnStatFeatsTrainBig(Resources.TRAIN_COMB_HIGH, Resources.TEST_TH_HIGH, "high_cats_stat_new.csv", 32);
		//sqnStatEmbTrainBig(Resources.TRAIN_HIGH, Resources.TEST_HIGH, Resources.EMBEDDINGS, "high_cats_embeddings.csv", 16);

		//new
		
		
		// qnStatFeatsTrainSmall();
		// qnStatFeatsTrainBig();
		crfFeatsTrainSmall();
		// qnStatFeatsTrainBig();
		// svmStatFeatsTrainBig();
		// System.gc();
		// crfStatFeatsTrainBig();
		// walkFiles("C:/Users/Thomas/Desktop/emb2");
		// dmlpStatFeatsTrainSmall(); //put in comment in
		// lstmStatFeatsTrainSmall();
		// votingSmallStatistical();
		// qnStatEmbTrainBig("resources/data/vecs/wikipedia/v_300","");

	}

	public static void walkFiles(String init) {
		Collection<File> files = FileUtils.listFiles(new File(init), FileFileFilter.FILE,
				DirectoryFileFilter.DIRECTORY);
		for (File f : files) {
			System.out.println(f.getName() + " " + f.isFile());

			// qnStatEmbTrainSmall(f.getAbsolutePath(), f.getName());

			//qnStatEmbTrainBig(f.getAbsolutePath(), f.getName());

		}
	}

	public static void qnStatFeatsTrainSmall(String trainPath, String testPath, String evalName, int shards) {
		/*
		 * Model model = new Model(configFolder+"lowCats.txt", 3);
		 * model.generateStats(
		 * "resources/data/train/train_low_23675_edited_new.train"); MetaTagger
		 * meta = new MetaTagger(model, "resources/config/lowGazzete.txt")
		 * .setUseAdvDer(false) .setUseNumeric(false)
		 * .setUseWordDictionary(false) .setUseSingleCat(false); Evaluation eval
		 * = new Evaluation(null, false, true);
		 */
		ExperimentSetup exs = new ExperimentSetup.Builder().setClassifier(new MaxEntClassifier()).setTotalShards(shards)
				.setEvalToCSV(true).setTestPath(testPath)
				.setTrainPath(trainPath)
				/*
				 * .setModel(model) .setEval(eval)
				 */
				.setEvalLocation(Resources.EVAL_DIRECTORY + evalName).build();
		exs.execute();
	}

	public static void votingSmallStatistical() {
		Model model = new Model(Resources.LOW_CATS, 3);
		Classifier[] classifiers = new Classifier[] { new MaxEntClassifier(), new CRFClassifier(1, 0, 10),
				new SVMClassifier() };
		Classifier voteLearner = new MaxEntClassifier();
		Classifier vs = new MetaClassifier(Arrays.asList(classifiers), voteLearner, true, model);
		ExperimentSetup exs = new ExperimentSetup.Builder().setClassifier(vs).setTotalShards(1).setEvalToCSV(true)
				.setEvalLocation("C:/Users/Thomas/Desktop/eval.csv").build();
		exs.execute();
	}

	public static void qnEmbTrainSmall(String trainPath, String testPath, String embeddings, String evalName,
			int shards) {
		ExperimentSetup exs = new ExperimentSetup.Builder().setClassifier(new MaxEntClassifier()).setTotalShards(shards)
				.setUseStatistical(false).setTrainPath(trainPath).setUseEmbeddings(true).setEmbeddingsFile(embeddings)
				.setTestPath(testPath).setEvalLocation(Resources.EVAL_DIRECTORY + evalName).build();
		exs.execute();
	}

	public static void qnStatEmbTrainBig(String trainPath, String testPath, String embeddingsFile, String evalName, int shards) {
		Model model = new Model(Resources.HIGH_CATS, 3);
		MetaTagger meta = new MetaTagger(model, Resources.HIGH_GAZZETE);
		Evaluation eval = new Evaluation(meta, true, false);
		ExperimentSetup exs = new ExperimentSetup.Builder().setLookAhead(1) 
				.setLookBehind(2) // how many words behind of the current should
									// the algorithm take into account
				.setTotalShards(shards) // if the algorithm should create learning
									// curves from the trainset. i.e. 10 shards
									// means to test with 10%,20%... 90%,100% of
									// the dataset
				.setCategoriesPath(Resources.HIGH_CATS)
				.setTrainPath(trainPath) 
				// .setTestPath(dataFolder+"test/test_high_7878.train")
				.setTestPath(testPath) 
				//.setTestOutput("C:/Users/Thomas/Desktop/ntest_high_tagged.txt") 
				.setEval(eval) // what kind of evaluation
				.setUseStatistical(false) // use statistical features from
											// Evaggelua
				.setUseEmbeddings(true) // use embeddings
				.setEmbeddingsFile(embeddingsFile) // the location of the
													// embeddings file
				.setEvalToCSV(true) // wheteher to print the evaluation results
									// to a csv
				.setEvalLocation(Resources.EVAL_DIRECTORY+evalName)
				.setModel(model)// which model to use
				.build();

		exs.execute();
	}

	public static void qnStatFeatsTrainBig(String trainPath, String testPath,  String evalName, int shards) {
		Model model = new Model(Resources.HIGH_CATS, 3);
		MetaTagger meta = new MetaTagger(model, null);
		Evaluation eval = new Evaluation(meta, true, false);
		ExperimentSetup exs = new ExperimentSetup.Builder().setLookAhead(1).setLookBehind(2).setTotalShards(shards)
				.setCategoriesPath(Resources.HIGH_CATS)
				.setTrainPath(trainPath)
				// .setTestPath(dataFolder+"test/test_high_7878.train")
				.setTestPath(testPath).setEval(eval).setUseEmbeddings(false)
				.setEvalToCSV(true).setEvalLocation(Resources.EVAL_DIRECTORY+evalName)
				// .setModel(model) you have to initialize the model to set it,
				// so if not it will create a model on its own and train it
				.build();
		exs.execute();
	}

	public static void dmlpStatFeatsTrainSmall() throws Exception {
		Model model = new Model(Resources.LOW_CATS, 3);
		FeatureBuilder fb = new FeatureBuilder(model); 
		List<Word> train = WordSet.fileToList(Resources.TEST_LOW, " ");
		List<Word> test = WordSet.fileToList(Resources.TEST_LOW, " ");
		fb.generateFeats(train, 0, 1);
		fb.generateFeats(test, 0, 1);
		FeatureBuilder.generateBinDoubleLabels(train, model);
		FeatureBuilder.generateBinDoubleLabels(test, model);
		DeepNetClassifier clf = new DeepNetClassifier(model, false, GraphConfigurations.DeepMLPGraph(train));
		clf.setEpochs(10);
		System.out.println(fb.noFeats(0, 1) + " == " + train.get(0).getFeatureVec().getValues().length);
		clf.train(train);
		MetaTagger meta = new MetaTagger(model, Resources.LOW_GAZZETE);
		Evaluation eval = new Evaluation(meta, false, true);
		double acc = eval.evaluateAcc(test, clf.classifySet(test));
		System.out.println("Accuracy " + acc);
		/*
		 * ExperimentSetup exs = new ExperimentSetup.Builder()
		 * .setTotalShards(32) .setEvalToCSV(true)
		 * .setEvalLocation("C:/Users/Thomas/Desktop/eval.csv") .build();
		 * exs.setClassifier(new
		 * DeepMLPClassifier(exs.getModel(),fb.noFeats(exs.getLookBehind(),
		 * exs.getLookBehind()), model.getCategories().size())); exs.execute();
		 */
	}

	public static void lstmStatFeatsTrainSmall() throws Exception {
		Model model = new Model(Resources.LOW_CATS, 3);
		FeatureBuilder fb = new FeatureBuilder(model);
		List<Word> train = WordSet.fileToList(Resources.TRAIN_LOW, " ");
		List<Word> test = WordSet.fileToList(Resources.TEST_LOW, " ");
		fb.generateFeats(train, 0, 0);
		fb.generateFeats(test, 0, 0);
		// fb.generateFeats(train, 0, 1, new
		// Embedding("C:/Users/Thomas/Desktop/emb2/v_300"));

		FeatureBuilder.generateBinDoubleLabels(train, model);
		// fb.generateFeats(test, 0, 1, new
		// Embedding("C:/Users/Thomas/Desktop/emb2/v_300"));
		int i = 0;
		for (Word w : test) {
			if (w.getFeatureVec().getValues().length != train.get(0).getFeatureVec().getValues().length) {
				throw new Exception(i + " " + w.getValue() + " " + w.getFeatureVec().getValues().length);
			}
			i++;
		}
		FeatureBuilder.generateBinDoubleLabels(test, model);
		System.out.println(
				train.get(0).getFeatureVec().getValues().length + " " + test.get(0).getFeatureVec().getValues().length);
		DeepNetClassifier clf = new DeepNetClassifier(model, false, GraphConfigurations.LSTMGraph(train));
		clf.setEpochs(100);
		// clf = (DeepNetClassifier)
		// FileHelpers.deserialize("C:/Users/Thomas/Desktop/lstm.ser");
		System.out.println(fb.noFeats(0, 0) + " == " + train.get(0).getFeatureVec().getValues().length);
		clf.train(train);
		// FileHelpers.serialize(clf, "C:/Users/Thomas/Desktop/lstm.ser");

		MetaTagger meta = new MetaTagger(model, Resources.LOW_GAZZETE);
		Evaluation eval = new Evaluation(meta, false, true);
		double acc = eval.evaluateAcc(test, clf.classifySet(test));
		System.out.println("Accuracy " + acc);
		/*
		 * ExperimentSetup exs = new ExperimentSetup.Builder()
		 * .setTotalShards(32) .setEvalToCSV(true)
		 * .setEvalLocation("C:/Users/Thomas/Desktop/eval.csv") .build();
		 * exs.setClassifier(new
		 * DeepMLPClassifier(exs.getModel(),fb.noFeats(exs.getLookBehind(),
		 * exs.getLookBehind()), model.getCategories().size())); exs.execute();
		 */
	}

	public static void svmExampleOnHighCategories() {
		Model model = new Model(Resources.HIGH_CATS, 3);//a dummy model, empty for the metatagger
		MetaTagger meta = new MetaTagger(model, Resources.HIGH_GAZZETE); // a new metatagger
		Evaluation eval = new Evaluation(meta, true, false); // a new evaluation with the metatager
		ExperimentSetup exs = new ExperimentSetup.Builder()
				.setLookAhead(1)//the current word's features will include the next 1 features (not recursively), via horizontal concat
				.setLookBehind(2)//the current word;s features will include the previous 2 words features (not recrsively), via horizontal concat
				.setClassifier(new SVMClassifier()) //set a class that implements the Classifier interface, can be trained
				.setTotalShards(1)//if the algorithm should create learning curves on X points of the trainset
				.setCategoriesPath(Resources.HIGH_CATS) //The file path of the categories
				.setTrainPath(Resources.TRAIN_HIGH) // The file path of the train set
				.setTestPath(Resources.TEST_HIGH) // The filepath of the testset
				.setEval(eval) //The evaluation object
				.setEvalToCSV(true) //append or write eval to a csv
				.setEvalLocation(Resources.EVAL_DIRECTORY+"newEvaluation.csv")//where the CSV is located
				.build();
		exs.execute();//execute experiment
	}

	public static void crfStatFeatsTrainBig() {
		//...create file
		//SongTokenizer.tokenizeToFile(S lyrics, pathTowrite);
		
		
		Model model = new Model(Resources.HIGH_CATS, 3);
		MetaTagger meta = new MetaTagger(model, Resources.HIGH_GAZZETE);
		Evaluation eval = new Evaluation(meta, true, false);
		ExperimentSetup exs = new ExperimentSetup.Builder()
				.setLookAhead(0)
				.setClassifier(new SVMClassifier())
				.setLookBehind(0)
				.setTotalShards(1)
				.setClassifier(new CRFClassifier(1, 2, 10))
				.setCategoriesPath(Resources.HIGH_CATS)
				.setTrainPath(Resources.TRAIN_HIGH)
				.setTestPath(Resources.TEST_HIGH).setEval(eval).setEvalToCSV(true)
				.setEvalLocation(Resources.EVAL_DIRECTORY + "evalCRFBIG.csv")
				.setModel(model)
				.build();
		exs.execute();
	}

	public static void crfFeatsTrainSmall() {
		ExperimentSetup exs = new ExperimentSetup.Builder()
				.setClassifier(new CRFClassifier(1, 0, 15))
				.setTotalShards(1)
				.setTrainPath("resources/data/train/train_low")
				.setTestPath("resources/data/train/train_low")
				.setEvalLocation("resources/eval.csv")
				.build();
		exs.execute();

	}

	
}
