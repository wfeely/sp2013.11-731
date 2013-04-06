/** Serena Jeblee
* MT Homework 4
* Logistic Regression classifier
* Last modified: 6 April 2013
*/

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class LR{

//private static ArrayList<String> examples;

//Logistic Regression data structures
private static double[] weightvector;
private static final double rate = 0.0001;
private static int numfeatures;
private static int exsize;

public static void main(String[] args){

	if(args.length < 2){
		System.out.println("Usage: java LR examplefile numexamples numfeatures");
		System.exit(1);
	}

	numfeatures = Integer.parseInt(args[2]);
	exsize = Integer.parseInt(args[1]);
	
	try{
		Scanner infile = new Scanner(new FileReader(args[0]));
		FileWriter outfile = new FileWriter("weights.txt");
		int index = 0;
		while(infile.hasNextLine()){
			System.out.println("Creating model for k-best list num " + index);
			ArrayList<Double[]> trainexamples = new ArrayList<Double[]>(exsize);
			int n = 0;

			//get the next 100 examples
			while(infile.hasNextLine() && (n<exsize)){
				String line = infile.nextLine();
				StringTokenizer tok = new StringTokenizer(line);
				Double[] ex = new Double[numfeatures+1];
				for(int k=0; k<(numfeatures+1); k++)
					ex[k] = Double.parseDouble(tok.nextToken());	
				trainexamples.add(ex);
				n++;
			}//end while

			//train logistic regression
			if(trainexamples.size()<exsize){
				System.out.println("ERROR: trainexamples has only " + trainexamples.size() + " items!");
				System.exit(1);
			}
			trainLR(trainexamples);

			//print weight vector
			System.out.println("Done, printing weight vector");
			for(int k=0; k<weightvector.length; k++){
				outfile.write(weightvector[k] + " ");
			}
			outfile.write("\n");
			index++;

		}//end while next k-best list
		infile.close();
		outfile.close();
		
	}//end try
	
	catch(IOException e){
		System.out.println(e.getMessage());
	}
	
}//end main

public static void trainLR(ArrayList<Double[]> trainexs){
	System.out.println("Training Logistic Regression...");
	weightvector = new double[numfeatures+1];
	double[] newweights = new double[numfeatures+1];

	//initialize newweights
	for(int j=0; j<(numfeatures+1); j++){
		newweights[j] = 1.0;
	}
	double diff = 1.0;
	
	//learn weights via gradient descent
	while(Math.sqrt(diff) > .001){

		//reset diff
		diff = 0.0;

		//switch to new weights
		for(int n=0; n<weightvector.length; n++){
			weightvector[n] = newweights[n];
		}

		//calculate weight updates
		for(int k=0; k<(numfeatures+1); k++){
			double isum = 0.0; //sum over n (yi*xi*g(yi*zi)

			for(Double[] ex : trainexs){
				int yi = 0;
				double xik = 1.0;
				double label = ex[0];
				if(label == 1.0)	yi = 1;
				else	yi = -1;

				if(k!=0)
					xik = ex[k];

				isum += yi * xik * g(ex, yi); // add to isum
			}//end for ex in examples

			double update = rate * isum;
			newweights[k] = weightvector[k] + update;
			diff += Math.pow(update, 2);	//calculate squared difference
		}//end for k
	}//end while

	//switch to new weights
	for(int n=0; n<weightvector.length; n++){
		weightvector[n] = newweights[n];
	}
}//end trainLR


public static double g(Double[] xi, int yi){
	//yi sum (k, wk xik)
	double sum = weightvector[0]; //w0
	
	for(int k=1; k<numfeatures+1; k++){
		sum += weightvector[k] * xi[k];
	}
	return (1/(1 + Math.exp(yi*sum))); //exp(-(-yi*sum))
}//end g(z)

/*
public static void predictLR(ArrayList<String> testexs){
	char label = '-';
	for(String ex : testexs){
		String features = ex.substring(1); //cut off correct answer
		double logprobA = 1.0, logprobB = 1.0;   //set all initial counts to 1
		double ksum = 0.0;

		//calculate ksum
		for(int k=1; k<=16; k++){
			ksum += weightvector[k] * Integer.parseInt(ex.substring(k, k+1));
		}

		//calculate prob
		double prob = 1/(1 + Math.exp(-1*(weightvector[0] + ksum)));
		logprobA = log2(prob);
		logprobB = log2(1-prob);
		
		//label
		if(logprobA > logprobB)
			label = 'A';
		else
			label = 'B';

		//check label
		if(label == ex.charAt(0)){
			LRnumright++;
		}
		
	}//end for ex
}//end predictLr
*/


public static double log2(double num)
{
	return (Math.log(num)/Math.log(2));
} 

}//end class











