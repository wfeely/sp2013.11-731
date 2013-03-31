/** MishMishDec.java
* MT HW 3
* Weston Feely & Serena Jeblee
* Last Modified: 27 Mar 2013
*/

import java.io.*;
import java.util.*;

public class MishMishDec{

public static HashMap<String, Pair<Double, Double>> lm;
public static HashMap<String, ArrayList<Pair<String, Double>>> tm;

public static void main(String[] args){

	if(args.length < 3){
		System.out.println("Usage: java MishMishDec data languagemodel translationmodel");
		System.exit(1);
	}

	String datafile = args[0];
	String lmfile = args[1];
	String tmfile = args[2];
	
	try{

		lm = readLM(lmfile);
		System.out.println("LM size: " + lm.size());
		tm = readTM(tmfile);

		//Read in heuristic hypotheses
		Scanner hypin = new Scanner(new FileReader("hypotheses.txt"));
		Scanner infile = new Scanner(new FileReader(datafile));
		FileWriter outfile = new FileWriter("output3.txt");
		int counter = 0;
		while(infile.hasNextLine()){
			String line = infile.nextLine();
			System.out.println(counter + ". Decoding: " + line);
			String hyp = hypin.nextLine();
			String[] sarray = line.trim().split(" ");
			ArrayList<String> unigrams = new ArrayList<String>();
			for(int x=0; x<sarray.length; x++)
				unigrams.add(sarray[x]);
			HashMap<String, ArrayList<Pair<String, Double>>> searchspace = getsubset(unigrams);
			String sentence = search(unigrams, hyp, searchspace);
			outfile.write(sentence + "\n");
			counter++;
		}//end while

		infile.close();
		hypin.close();
		outfile.close();
	}//try
	catch(IOException e){
		System.out.println(e.getMessage());
	}

}//end main

//Returns ngrams of given order for a sentence, as a list of words
public static ArrayList<String> get_ngrams(ArrayList<String> sent, int n){
	if ((sent.size() < n) || (n < 1))
		 return new ArrayList<String>();
	ArrayList<String> ngrams = new ArrayList<String>();
	for(int i=0; i<sent.size(); i++){
		if(i > n-1){
			String buff = "";
			for(int j=n-1; j>-1; j--)
				buff += sent.get(i-j) + " ";
			ngrams.add(buff.trim());
		}
	}//end for
	return ngrams;
}//end get_ngrams

public static HashMap<String, Pair<Double, Double>> readLM(String filename) throws IOException{
	//Read ARPA-format language model from file and save as dictionary
	HashMap<String, Pair<Double, Double>> lm = new HashMap<String, Pair<Double,Double>>();

	Scanner infile = new Scanner(new FileReader(filename));
	while(infile.hasNextLine()){
		String line = infile.nextLine();
		//System.out.println(line);
		if(!line.equals("") && (line.charAt(0)!='\\')){
			StringTokenizer tok = new StringTokenizer(line, "\t");
			double logprob = Double.parseDouble(tok.nextToken());
			String ngram = tok.nextToken();
			double backoffprob = 0.0;
			if(tok.hasMoreTokens())
				backoffprob = Double.parseDouble(tok.nextToken());
			lm.put(ngram, new Pair<Double,Double>(logprob,backoffprob));
		}
	}//end while
	
	return lm;
}//end readLM

public static HashMap<String, ArrayList<Pair<String,Double>>> readTM(String filename) throws IOException{
	//Read translation model from file and organize into dictionaries
	HashMap<String, ArrayList<Pair<String,Double>>> spanish_ngrams = new HashMap<String, ArrayList<Pair<String,Double>>>(); // spanish -> english,logprob
	Scanner infile = new Scanner(new FileReader(filename));
	while(infile.hasNextLine()){
		String line = infile.nextLine();
		String sp = line.substring(0, line.indexOf(" ||| "));
		String en = line.substring(line.indexOf(" ||| ")+5, line.lastIndexOf(" ||| "));
		Double prob = Double.parseDouble(line.substring(line.lastIndexOf(" ||| ")+5));
		if(spanish_ngrams.containsKey(sp))
			spanish_ngrams.get(sp).add(new Pair<String,Double>(en,prob));
		else{			
			spanish_ngrams.put(sp, new ArrayList<Pair<String,Double>>());
			spanish_ngrams.get(sp).add(new Pair<String,Double>(en,prob));
		}
	}//end while

	return spanish_ngrams;
	
}//end read readTM

//Input is a list of unigrams, output is a dictionary, which is a subset of self.spanish
	//for each unigram, bigram, trigram from input that is in self.spanish	
public static HashMap<String, ArrayList<Pair<String,Double>>> getsubset(ArrayList<String> unigrams){
		ArrayList<String> bigrams = get_ngrams(unigrams,2); 
		ArrayList<String> trigrams = get_ngrams(unigrams,3);
		HashMap<String, ArrayList<Pair<String,Double>>> subset = new HashMap<String, ArrayList<Pair<String,Double>>>();
		ArrayList<String> ngrams = new ArrayList<String>(unigrams);
		ngrams.addAll(bigrams);
		ngrams.addAll(trigrams);
		for(String ngram : ngrams){
			if(tm.containsKey(ngram))
				subset.put(ngram, tm.get(ngram));
		}
		return subset;
}//end getsubset

public static double getLMscore(String sentence){
	ArrayList<String> words = new ArrayList<String>();
	StringTokenizer tok = new StringTokenizer(sentence);
	while(tok.hasMoreTokens())
		words.add(tok.nextToken());
	double score = 0.0;
	for(int i=0; i<words.size(); i++){
		String s;
		if(i>=2)
			s = words.get(i-2) + " " + words.get(i-1) + " " + words.get(i);
		else if(i>=1)
			s = words.get(i-1) + " " + words.get(i); 
		else 
			s = words.get(i);

		//get trigram score
		try{
			Pair<Double,Double> pair = lm.get(s);
			double gramscore;
			if(pair!=null)
				gramscore = pair.first();
			else{
				while(pair == null){ //backoff
					if(s.contains(" ")){
						s = s.substring(s.indexOf(' ')+1);
						pair = lm.get(s);
					}
					else 
						pair = new Pair<Double,Double>(0.0, 0.0);
				}//end while backoff
				gramscore = pair.second();
			}
			score += gramscore;
		}
		catch(NullPointerException npe){
			System.out.println("NullPointerException: s = " + s + "\n" + npe.getMessage()
				+ "\nin sentence: " + sentence);
			System.exit(1);
		}
	}//end for

	return score;
}//end getLMscore()


/**

Done: No matter what is added, the worse path can be dropped if the last two English
	words are the same OR if the spanish coverage vectors are the same
TODO: Should we be comparing the heuristic hypothesis based on tm score or lm score? current: tm score
**/

//Search for best translation; input is unigrams (list of words) from source sentence and searchspace dictionary
public static String search(ArrayList<String> unigrams, String hyp, HashMap<String, ArrayList<Pair<String, Double>>> searchspace){
	//Initial stack for unigrams
	ArrayList<Node> init_stack = new ArrayList<Node>();
	int stackindex = 1;
	int numstacks = unigrams.size(); // #stacks = 0, 1 through numstacks

	//get heuristic hypothesis
	double hypscore = Double.parseDouble(hyp.substring(0, hyp.indexOf(' ')));
	hyp = hyp.substring(hyp.indexOf(' '));
	double hyplmscore = getLMscore(hyp);
	System.out.println("hyplmscore = " + hyplmscore);
	
	ArrayList<ArrayList<Node>> stacks = new ArrayList<ArrayList<Node>>();
	//initialize stacks
	for(int i=0; i<=numstacks; i++)
		stacks.add(new ArrayList<Node>());

	//add null node
	Node nullnode = new Node("", 0.0, null);
	nullnode.coverage = new int[numstacks];
	for(int i=0; i<numstacks; i++)
		nullnode.coverage[i] = 0;
	nullnode.history = new ArrayList<String>();
	nullnode.prob = 0.0;
	stacks.get(0).add(nullnode);
	
	int r = 0; //Reorder distance

	while(stackindex <= numstacks){
		System.out.print("Building stack " + stackindex + " : ");

	for(int s=Math.max(stackindex-1, 0); s>=Math.max(stackindex-3, 0); s--){
		int ngramorder = stackindex-s; //length of phrase
		if((stackindex + ngramorder) <= numstacks){
		//System.out.println("\tcheck ngram order " + ngramorder);
		for(Node node : stacks.get(s)){ //check each potential parent node, enumerate its children
			for(int i=Math.max(stackindex-1-r, 0); i<=Math.min(stackindex-1+r, numstacks-1); i++){
				String cov = "";
				for(int c=i; c<Math.min(i+ngramorder,numstacks); c++)
					cov += node.coverage[c];
				//System.out.println("\ti: " + i + " coverage: " + cov);
				if(!cov.contains("1")){ 	//check the rest of phrase in coverage vector
					//System.out.println("\tchecking spanish index " + i);
					//add searchspace[unigrams.get(i)] to stacks.get(stackindex)
					String searchphrase = "";
					for(int p=i; p<Math.min(i+ngramorder,numstacks); p++)
						searchphrase += unigrams.get(p) + " ";
					searchphrase = searchphrase.trim();
					//System.out.println("\t\tlooking up: " + searchphrase);
					ArrayList<Pair<String, Double>> list = searchspace.get(searchphrase);
					if(list == null)
						list = new ArrayList<Pair<String,Double>>();
					//System.out.println("\t\tmatches: " + list.size());
					for(Pair<String, Double> pair : list){
						//check score against hyp
						double score = pair.second();
						//System.out.println("\t\tmatch: " + pair.first() + " " + score);

						//make node object, set parent pointer, add to stack
							String phrase = pair.first();
							Node n = new Node(phrase, score+node.prob, node);
							n.coverage = n.parent.coverage();
							for(int c=i; c<Math.min(i+ngramorder,numstacks); c++)
								n.coverage[c] = 1;
							n.history = n.parent.history();
							StringTokenizer phrasetok = new StringTokenizer(phrase);
							while(phrasetok.hasMoreTokens()) //update history
								n.history.add(phrasetok.nextToken());

							double lmscore = getLMscore(n.historystring());

						//if(lmscore >= hyplmscore){
						if(score >= hypscore){
							
							//check stack for duplicates
							if((stackindex > 1) && (n.history.size()>=2)){
								int removeindex = -1;
								boolean dup = false;
								for(Node stacknode : stacks.get(stackindex)){
									if(stacknode.history.size() >=2){

										String stackhist = stacknode.history.get(stacknode.history.size()-2) 
										+ " " + stacknode.history.get(stacknode.history.size()-1);
										if(n.history.size() < 2)
											System.out.println("hist < 2: " + n.historystring());
										String hist = n.history.get(n.history.size()-2) + " " 
											+ n.history.get(n.history.size()-1);
										String ncov = "";
										String sncov = "";
										for(int c=0; c<numstacks; c++){
											ncov += n.coverage[c];
											sncov += stacknode.coverage[c];
										}
										//System.out.println("\t\t\tcomparing " + hist + " " + ncov);
										//System.out.println("\t\t\t and " + stackhist + " " + sncov);
										if(stackhist.equals(hist) || ncov.equals(sncov)){ //duplicate found!
											//System.out.println("\t\t\tduplicate!");
											dup = true;
											if(stacknode.prob < n.prob){
												removeindex = stacks.get(stackindex).indexOf(stacknode);
												//stacks.get(stackindex).add(n);
												break;
											}//end if stacknode.prob
										}//end if duplicate
									}//end if stacknode.history.size() >=2
								}//end for stacknode in stack
								if(dup && (removeindex >= 0)){
									stacks.get(stackindex).remove(removeindex);
									//System.out.println("\t\t\tremoved old node");
									stacks.get(stackindex).add(n);
									//System.out.println("\t\t\tadded new node");
								}
								else if(!dup){
									stacks.get(stackindex).add(n);
									//System.out.println("\t\t\tadded new node");
								}
								//else dup && remove = -1, keep old one, don't add new, nothing to do
							}//end if stackindex > 1
							else stacks.get(stackindex).add(n);
						}//end if score < hypscore
					}//end for pair in list
				}//end if node.coverage
			}//end for i
		}//end for node in stacks
		}//end if stackindex+ngramorder <= numstacks
	}//end for s

	System.out.println("Added " + stacks.get(stackindex).size() + " nodes");
	for(Node n : stacks.get(stackindex))
		System.out.println(n);
	stackindex++;
	}//end while stackindex <= numstacks

	double bestscore = Double.NEGATIVE_INFINITY;
	Node bestnode = null;
	//System.out.println("hypscore: " + hypscore);
	for(Node n : stacks.get(numstacks)){
		System.out.println("node: " + n.toString());
		String hist = n.historystring();
		boolean full = true;
		/*for(int c=0; c<n.coverage.length; c++){	 //check to make sure translation covers all words
			if(n.coverage[c] == 0){
				full = false;
				break;
			}
		}
		*/

		//compute lm score
		if(full){
			double lmscore = getLMscore(hist);
			if(lmscore >= bestscore){
				bestnode = n;
				bestscore = lmscore;
			}//end if lmscore
		}//end if full
	}//end for node n

	String result = bestnode.historystring();
	System.out.println("best: " + bestscore + "\n" + result);
	return result;
}//end search

}//end class
