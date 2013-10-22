//
//  DigitReader3.java
//  
//
//  Created by Robert Laber on 10/8/12.
//  
//
//  This program is a classification algorithm for handwritten digit recognition.  
//  It uses the files text.csv and train.csv from Kaggle.com.  The basis for the 
//  algorithm is a knn-classifier, although we make use of a generalized version 
//  using the L^p norm.  For each test point, we compute the k closest points from   
//  the training set, and we then take a weighted mode as our classifier. 
//
//  The data for each of the 42000 training points consists of an intensity value between 
//  0 and 255 for each pixel in a 28x28 array, as well as the correct label for each 
//  point.  The test data consists only of the pixel intensities, without the label.
//
//  It should be noted that this version of DigitReader did not score as well as prior
//  versions, however, this version accommodates a high degree of flexibility in tuning
//  of parameters, and it includes the addition of the weightedMode() feature.


import java.util.*;
import java.io.*;


public class DigitReader3 {

	// The next method computes x^n.
	
	public static int exp(int x, int n){

		int a = 1;
		for (int i=0; i<n; i++) 
			a*= x;

		return a;
	}

	// Here we make use of the L^p norm in 784 dimensional space.  The standard euclidean
	// metric uses p=2, however, we leave this as a variable parameter in this algorithm.
	// It should alos be noted that here we neglect to take the p-th root, so that the
	// method actually returns the p-th power of the L^p norm.  Since p-th power is a
	// monotonic function for p>0, this will affect only magnitude, not order.

	public static int LpDist(int[] x, int[] y){

		int p = 3;		// p=3 seems to work well.
		
		int[] diff= new int[x.length-1]; // First create an array of pixel differences
		
		int d=0; // d is the sum of the p-th powers of the entries in diff[]
		
		for(int i=0; i<x.length-1; i++) 
			diff[i] = x[i+1]-y[i];  	// indices do not match because of extra  
										// column in training set
												
		int[] diff2 = rescale(diff);	// renormalize to get binned values
			
		for(int i =0; i<diff2.length; i++)
			d+= exp(diff2[i],p);		// compute sum of p-th powers
		
		return d;	
	}

	// The getData() method will read the train.csv file and return an array of ints
	// which is more suitable for our analysis.  We let s denote the path of the file 
	// to read in.

	public static int[][] getData(String s) throws IOException { 
	
		int[][] data = new int[42000][785];
	
		Scanner input = new Scanner(new FileReader(s));
	
		String[] p = new String[data.length];
	
		// each entry of p[] will be one data point.  Each point is a string consisting 
		// of the label followed by the pixel intensities, separated by commas.
		
		p[0]=input.nextLine(); 
	
		// rewrite over p[0] since first row of file is unnecessary.
		
		for(int i=0; i<p.length; i++) 
			p[i] = input.nextLine();
	
		// Now we break each data point into individual integers
		
		String[][] split = new String[p.length][data[0].length];
	
		for(int i = 0; i<p.length; i++){
			split[i] = p[i].split(","); // commas separate each integer
		}
	
		int[][] x = new int[p.length][split[0].length];
	
		for(int i =0; i<x.length; i++){
			for(int j = 0; j<x[0].length; j++) {
				x[i][j] = Integer.parseInt(split[i][j]); // interpret each entry as an int
			}
		}
		return x;
	}

	// The getData2() method is identical to the getData() method except it is designed 
	// to be used with the test.csv file, which contains 28000 entries and no 
	// label column.

	public static int[][] getData2(String s) throws IOException { // s in the name of the file to input
	
		int[][] data = new int[28000][784];
	
		Scanner input = new Scanner(new FileReader(s));
	
		String[] p = new String[data.length];
	
		p[0]=input.nextLine();
	
		for(int i=0; i<p.length; i++) 
			p[i] = input.nextLine();
	
		String[][] split = new String[p.length][data[0].length];
	
		for(int i = 0; i<p.length; i++){
			split[i] = p[i].split(",");
		}
	
		int[][] x = new int[p.length][split[0].length];
	
		for(int i =0; i<x.length; i++){
			for(int j = 0; j<x[0].length; j++) {
				x[i][j] = Integer.parseInt(split[i][j]);
			}
		}
		return x;
	}

	// The makeFile() method takes the array of integer predictions and writes
	// them to a .csv file
	
	public static void makeFile(int[] x) {
		try {
			FileWriter writer = new FileWriter("Predictions.csv");
			for(int i = 0; i<x.length; i++) {
				writer.append(x[i]+"\n");
			}
			writer.flush();
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();			
		}
	}

	// The rescale() method puts pixel intensities into bins.  Pixel intensities are
	// values between 0 and 255, and these are binned uniformly into values 
	// from 0 to 15.
	
	public static int[] rescale(int[] x) {
	
		int[] y = new int[x.length];
	
		for(int i=0; i<x.length; i++) {
			int j = 0;
			while(j*16 < x[i])  // the number 16 arises from binning 256 pixel values
								// into 16 pixel values, since 256/16=16.
				j++;
			y[i]=j;
		}
		return y;		
	}

	// The max() method simply return the maximum entry in an array of ints.
	
	public static int max(int[] x) {
		
		int max = x[0];
		
		for( int i=0; i<x.length; i++) {
			if (x[i]>max )
				max = x[i];
		}
		return max;
	}
	
	// The maxIndex() method return the index of the largest entry.  If the largest
	// entry occurs at multiple indices, it returns the smallest such index.
	
	public static int maxIndex(int[] x) {
		
		int max = x[0];
		int index = 0;
		
		for( int i=0; i<x.length; i++) {
			if (x[i]>max ){
				max = x[i];
				index =i;
			}	
		}
		return index;
	}
	
	// The mode() method simply computes the mode of an array of integers.  It uses the
	// max() and maxIndex() methods and assumes that the minimum entry in the input 
	// array is at least 0.  This runs in O(n) time.
	// It was used in previous versions of DigitReader, but not in DigitReader3.
	
	public static int mode(int[] x) {
	
		int[] f = new int[max(x)+1];
		
		for (int i = 0; i<x.length; i++) {
			f[x[i]]++;	
		}
		return maxIndex(f);				
	}
	
	// The weightedMode() method uses a ranking system to compute the 'mode' of the
	// array.  The input double array consists of a values and their 
	// corresponding 'weights'.  The values are then ranked based on these weights, 
	// the value with the highest weight is returned.  In this case, the weight of a 
	// is the reciprocal of the L^p norm.
	
	public static int weightedMode(int[][] x) {

		float[] y = new float[10];	// one entry for each possible digit 0-9
	
		for (int i=0; i<x[0].length; i++) {
			y[x[1][i]]+= (1.0/x[0][i]);
		}
		
		// Here we rewrite the max() and maxIndex() methods since y[] is a float array.

		float maxWeight = 0;
		int maxValue = 0;
		
		for(int  i =0; i<y.length; i++) {
		
			if(y[i] > maxWeight) {
				maxWeight = y[i];
				maxValue = i;
			}
		}
		return maxValue;
	}

	// The main() method utilizes a custom PriorityQueue class.  This class ranks
	// the labels by their L^p distance and stores only the k closest labels.

	public static void main(String[] args) throws IOException {
	
		int[][] train = getData("/Users/robertlaber/Desktop/train.csv");
		
		int[][] test = getData2("/Users/robertlaber/Desktop/test.csv");
		
		int[] guesses = new int[test.length]; // this is the array of predicted values
		
		int k =10; // k is number of neighbors we will consider in weightedMode()
		
		for(int i = 0; i<test.length; i++) {
			PriorityQueue topTen = new PriorityQueue(k);
			
			// now compute k nearest neighbors for each test point
			
			for(int j =0; j<train.length; j++) 
				topTen.addValue(LpDist(train[j], test[i]), train[j][0]);
			
			// prediction is the weighted mode of k nearest neighbors
			
			guesses[i] = weightedMode(topTen.data);
		}
		
		// now create .csv file for submission and scoring.
		
		makeFile(guesses);
	}
}
