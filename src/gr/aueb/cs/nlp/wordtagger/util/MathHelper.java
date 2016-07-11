package gr.aueb.cs.nlp.wordtagger.util;

public class MathHelper {
	
	/**
	 * A method to return a double value for a boolean
	 * @param bool
	 * @return
	 */
	public static double booleanToDouble(boolean bool){
		return bool ? 1.0 : 0;
	}
	
	/**
	 * A quick method for simple array division in Java. It returns a newn array
	 * @param array, the array to be divided
	 * @param number, the number to divide the array with
	 * @return array, the new array
	 */
	public static double[] arrayDivision(double[] array, double number){
		double[] newArray =  new double[array.length];
		for (int i = 0; i < array.length; i++) {
			newArray[i] = array[i]/number; 
		}
		return newArray;
	}
	
	/**
	 * Return the max between 2 numbers. If the
	 * current max is initialized as NaN, it automatically
	 * sets as max the new number.
	 * @param newNumber, the number we are going to compare against the existing
	 * max.
	 * @param currentMax, the existing max, can be initialized as Double.NaN
	 * if it doesn't exist yet.
	 * @return the max between the 2 numbers.
	 */
	public static double findMax(double newNumber, double currentMax){
		return Double.isNaN(currentMax) ?
							newNumber : currentMax > newNumber ?
									currentMax : newNumber;		
	}
	
	
	/**
	 * Return the min between 2 numbers. If the
	 * current min is initialized as NaN, it automatically
	 * sets as min the new number.
	 * @param newNumber, the number we are going to compare against the existing
	 * min.
	 * @param currentMin, the existing min, can be initialized as Double.NaN
	 * if it doesn't exist yet.
	 * @return the min between the 2 numbers.
	 */
	public static double findMin(double newNumber, double currentMin){
		return Double.isNaN(currentMin) ?
				newNumber : currentMin > newNumber ?
						currentMin : newNumber;	
	}
	
	/**
	 * A method which returns an array which has 0 values and at the position index a value of 1 
	 * If out of index provided all indeces will be set to zero
	 * @param pos, element 0 is the first
	 * @param total, how many elements should be had at total
	 * @return the array in format [0, 0, 0, 1, 0, ...]
	 */
	public static int[] oneOfAK(int pos, int total){
		int[] oneOfAK = new int[total];
		for(int i = 0 ; i <  total ; i++){
			oneOfAK[i] = pos == i ? 1 : 0;
		}
		return oneOfAK;
	}
	
	/**
	 * A method which returns an array which has 0.0 double values and at the position index a value of 1.0 
	 * If out of index provided all indeces will be set to zero
	 * @param pos, element 0 is the first
	 * @param total, how many elements should be had at total
	 * @return the array in format [0.0, 0.0, 0.0, 1.0, 0.0, ...]
	 */
	public static double[] oneOfAKDouble(int pos, int total){
		double[] oneOfAK = new double[total];
		for(int i = 0 ; i <  total ; i++){
			oneOfAK[i] = pos == i ? 1.0 : 0.0;
		}
		return oneOfAK;
	}
}
