package gr.aueb.cs.nlp.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import gr.aueb.cs.nlp.wordtagger.util.CallBack;
import gr.aueb.cs.nlp.wordtagger.util.FileHelper;

public class NERExample {
	//example on how to use a CRf classifier from the NER package
	public static void main(String args[]) throws IOException{
		String testFile ="resources/data/test/test_low_7878.train";
		SeqClassifierFlags scf = new SeqClassifierFlags();
		scf.trainFile = "resources/data/train/train_low_23675_edited_new.train";
		scf.map = "word=0,answer=1";
		scf.selfTrainIterations = 1000;
		scf.maxLeft = 1;
		scf.maxRight = 2;
		scf.useGenericFeatures = true;
		scf.useFloat = true;
		scf.serializeTo = "data/ner-model.ser.gz";
		scf.useEmbedding = true;
		scf.embeddingVectors = "";

		CRFClassifier<CoreLabel> crf = new CRFClassifier<CoreLabel>(scf);
		
		crf.train();
		DocumentReaderAndWriter<CoreLabel> readerAndWriter = crf.makeReaderAndWriter();

		crf.classifyAndWriteAnswers(testFile, readerAndWriter, true);

	    		
		

}
	
	public static List<CoreLabel> loadTest(){
		List<CoreLabel> cl = new ArrayList<>();
		String testPath  = "data/test/test_low_7878.train";
		FileHelper.readFile(testPath, new CallBack<String>() {
			@Override
			public void call(String t) {
				String[] res = t.split(" ");
				CoreLabel cr = new CoreLabel(new String[]{"word","anser"}, res);
				cl.add(cr);		
				System.out.println(cr.toString());
			}
		});
		System.out.println(cl.toString());
		return cl;
	}
	
	
}
