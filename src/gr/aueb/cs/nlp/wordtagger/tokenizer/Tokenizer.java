package gr.aueb.cs.nlp.wordtagger.tokenizer;

import java.util.List;

public interface Tokenizer {
	public List<String> tokenize(String input);
}
