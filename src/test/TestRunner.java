package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * This class initializes all the other tests and
 * prints the results.
 * 
 * @author Alexandros
 * @since 6/2017
 */
public class TestRunner {
	public static void main(String[] args){
		TestRunner tr = new TestRunner();
		Result result = JUnitCore.runClasses(BreakTypeTest.class, 
					SongHelperTest.class, SongTokenizerTest.class);
		for(Failure failure : result.getFailures()){
			System.out.println(failure.toString());
		}
		
		System.out.println(result.wasSuccessful());
	}
}
