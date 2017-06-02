package test;

import org.junit.Test;

import gr.aueb.cs.nlp.wordtagger.tokenizer.BreakType;
import junit.framework.Assert;

/**
 * This class tests the BreakType class.
 * 
 * @author Alexandros
 * @since 6/2017
 */
public class BreakTypeTest {
	
	
	@Test
	/**
	 * This tests if the enum value breakBefore
	 * equals the String breakBefore
	 */
	public void breakTypeTest1(){
		Assert.assertEquals("breakBefore", BreakType.valueOf("breakBefore").toString());
	}
	
	@Test
	/**
	 * This tests if the enum value breakAfter
	 * equals the String breakAfter
	 */
	public void breakTypeTest2(){
		Assert.assertEquals("breakAfter", BreakType.valueOf("breakAfter").toString());
	}
}
