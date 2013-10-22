//
//  PriorityQueue.java
//  
//
//  Created by Robert Laber on 10/8/12.
//  
//	This is a priority queue class for use in the DigitReader program.  It consists of 
//  an array of pairs of integers.  It ranks data based on the first entry in each 
//  pair where smaller entries are kept and larger entries are discarded.

public class PriorityQueue {

	public int[][] data;

	public PriorityQueue(int x) {
		
		data = new int[2][x];
		
		for (int i = 0; i<x; i++) {
			data[0][i]=-1;
			data[1][i]=0;
		}
	}
	
	public void print() {
	
		for(int  i = 0; i<data[0].length; i++) {
			System.out.println("Number "+data[1][i]+" has a distance of "+data[0][i]);
		}	
	}
	
	public void addValue(int x, int y){
	
		int i = 0;
		
		while(x > data[0][i] && data[0][i]!=-1) {
			i++;
			if(i==data[0].length)
				break;
		}		
		if(i<data[0].length) {
			
			for(int j = data[0].length-1; j>i; j--) {
				data[0][j] = data[0][j-1];
				data[1][j] = data[1][j-1];
			}
				
			data[1][i] = y;
			data[0][i] = x;		
		}	
	}
}
