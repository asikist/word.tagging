package gr.aueb.cs.nlp.wordtagger.tokenizer;

import java.util.List;
/**
 * An interface which can be used for tokenization.
 * 
 * @author Alexandros
 * @since 5/2017
 */
public interface Tokenizer {
	
	/**
	 * The method that performs the tokenization.
	 * 
	 * @param input	The String to be tokenized.
	 * @return	The (now) tokenized String, morphed 
	 * into a List<String>.
	 */
	public List<String> tokenize(String input);
}
