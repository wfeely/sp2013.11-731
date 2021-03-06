/** MishMishDec.java
 * MT HW 3
 * Weston Feely & Serena Jeblee
 * Last Modified: 2 Apr 2013
 */

import java.io.*;
import java.util.*;

public class MishMishDec {

	public static HashMap<String, Pair<Double, Double>> lm;
	public static HashMap<String, ArrayList<Pair<String, Double>>> tm;
	public static HashMap<String, Double> fctable;
	public static int r;
	public static int hist_size;

	public static void main(String[] args) {

		if (args.length < 4) {
			System.out.println("Usage: java MishMishDec data/input data/lm data/tm reorder_distance histogram_size");
			System.exit(1);
		}

		long starttime = System.currentTimeMillis();

		String datafile = args[0];
		String lmfile = args[1];
		String tmfile = args[2];
		r = Integer.parseInt(args[3]);
		hist_size = Integer.parseInt(args[4]);

		try {

			System.out.println("Reorder Distance=" + r);
			System.out.println("Histogram Size=" + hist_size);
			lm = readLM(lmfile);
			//System.out.println("LM size: " + lm.size());
			tm = readTM(tmfile);

			// Read in heuristic hypotheses
			Scanner hypin = new Scanner(new FileReader("science.txt"));
			Scanner infile = new Scanner(new FileReader(datafile));
			FileWriter outfile = new FileWriter("output_r" + r + "_h" + hist_size + ".txt");
			int counter = 0;
			while (infile.hasNextLine()) {
				fctable = new HashMap<String, Double>();
				Scanner sentfile = new Scanner(new FileReader("fc/sent" + counter + ".txt"));
				while (sentfile.hasNextLine()) {
					StringTokenizer senttok = new StringTokenizer(
							sentfile.nextLine(), "\t");
					String cov = senttok.nextToken();
					double fc = Double.parseDouble(senttok.nextToken());
					fctable.put(cov, fc);
				}
				sentfile.close();

				long senttime = System.currentTimeMillis();
				String line = infile.nextLine();
				System.out.println(counter + ". Decoding: " + line);
				String hyp = hypin.nextLine();
				String[] sarray = line.trim().split(" ");
				ArrayList<String> unigrams = new ArrayList<String>();
				for (int x = 0; x < sarray.length; x++)
					unigrams.add(sarray[x]);
				HashMap<String, ArrayList<Pair<String, Double>>> searchspace = getsubset(unigrams);
				String sentence = search(unigrams, hyp, searchspace);
				outfile.write(sentence + "\n");
				counter++;
				long endtime = System.currentTimeMillis();
				long time = endtime - senttime;
				double timemins = (((double) time) / 1000) / 60;
				System.out.println("Sentence time: " + timemins + " mins");
				time = endtime - starttime;
				timemins = (((double) time) / 1000) / 60;
				System.out.println("Cumulative time: " + timemins + " mins");
				System.out.println("Estimated time remaining: " + ((timemins / (double) counter) * (54 - counter)));
			}// end while

			infile.close();
			hypin.close();
			outfile.close();
		}// try
		catch (IOException e) {
			System.out.println(e.getMessage());
		}

		long endtime = System.currentTimeMillis();
		long time = endtime - starttime;
		double timemins = (((double) time) / 1000) / 60;
		System.out.println("Total time: " + timemins + " mins");

	}// end main

	// Returns ngrams of given order for a sentence, as a list of words
	public static ArrayList<String> get_ngrams(ArrayList<String> sent, int n) {
		if ((sent.size() < n) || (n < 1))
			return new ArrayList<String>();
		ArrayList<String> ngrams = new ArrayList<String>();
		for (int i = 0; i < sent.size(); i++) {
			if (i > n - 1) {
				String buff = "";
				for (int j = n - 1; j > -1; j--)
					buff += sent.get(i - j) + " ";
				ngrams.add(buff.trim());
			}
		}// end for
		return ngrams;
	}// end get_ngrams

	public static HashMap<String, Pair<Double, Double>> readLM(String filename)
			throws IOException {
		// Read ARPA-format language model from file and save as dictionary
		HashMap<String, Pair<Double, Double>> lm = new HashMap<String, Pair<Double, Double>>();

		Scanner infile = new Scanner(new FileReader(filename));
		while (infile.hasNextLine()) {
			String line = infile.nextLine();
			// System.out.println(line);
			if (!line.equals("") && (line.charAt(0) != '\\')) {
				StringTokenizer tok = new StringTokenizer(line, "\t");
				double logprob = Double.parseDouble(tok.nextToken());
				String ngram = tok.nextToken();
				double backoffprob = 0.0;
				if (tok.hasMoreTokens())
					backoffprob = Double.parseDouble(tok.nextToken());
				lm.put(ngram, new Pair<Double, Double>(logprob, backoffprob));
			}
		}// end while

		return lm;
	}// end readLM

	public static HashMap<String, ArrayList<Pair<String, Double>>> readTM(
			String filename) throws IOException {
		// Read translation model from file and organize into dictionaries
		HashMap<String, ArrayList<Pair<String, Double>>> spanish_ngrams = new HashMap<String, ArrayList<Pair<String, Double>>>(); // spanish
																																	// ->
																																	// english,logprob
		Scanner infile = new Scanner(new FileReader(filename));
		while (infile.hasNextLine()) {
			String line = infile.nextLine();
			String sp = line.substring(0, line.indexOf(" ||| "));
			String en = line.substring(line.indexOf(" ||| ") + 5,
					line.lastIndexOf(" ||| "));
			Double prob = Double.parseDouble(line.substring(line
					.lastIndexOf(" ||| ") + 5));
			if (spanish_ngrams.containsKey(sp))
				spanish_ngrams.get(sp).add(new Pair<String, Double>(en, prob));
			else {
				spanish_ngrams.put(sp, new ArrayList<Pair<String, Double>>());
				spanish_ngrams.get(sp).add(new Pair<String, Double>(en, prob));
			}
		}// end while

		return spanish_ngrams;

	}// end read readTM

	// Input is a list of unigrams, output is a dictionary, which is a subset of
	// self.spanish
	// for each unigram, bigram, trigram from input that is in self.spanish
	public static HashMap<String, ArrayList<Pair<String, Double>>> getsubset(
			ArrayList<String> unigrams) {
		ArrayList<String> bigrams = get_ngrams(unigrams, 2);
		ArrayList<String> trigrams = get_ngrams(unigrams, 3);
		HashMap<String, ArrayList<Pair<String, Double>>> subset = new HashMap<String, ArrayList<Pair<String, Double>>>();
		ArrayList<String> ngrams = new ArrayList<String>(unigrams);
		ngrams.addAll(bigrams);
		ngrams.addAll(trigrams);
		for (String ngram : ngrams) {
			if (tm.containsKey(ngram))
				subset.put(ngram, tm.get(ngram));
		}
		return subset;
	}// end getsubset

	public static double getLMscore(String sentence) {
		//Add context cues
		sentence = "<s> "+sentence+" </s>";
		ArrayList<String> words = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(sentence);
		while (tok.hasMoreTokens())
			words.add(tok.nextToken());
		String s = words.get(0) + " " + words.get(1);
		double score = ngramscore(s);
		for (int i = 0; i < words.size() - 2; i++) {
			s = words.get(i) + " " + words.get(i + 1) + " " + words.get(i + 2);
			score += ngramscore(s);
		}// end for

		return score;
	}// end getLMscore()

	public static double ngramscore(String ngram) {
		// get trigram score
		// System.out.print("scoring: " + ngram);
		Pair<Double, Double> pair = lm.get(ngram);
		double gramscore;
		if (pair != null) {
			gramscore = pair.first();
			// System.out.println(" " + gramscore);
			return gramscore;
		} else { // backoff unknown bigram or trigram
					// System.out.println(" - ngram unknown");
			if (ngram.contains(" ")) {
				String left = ngram.substring(0, ngram.lastIndexOf(' '));
				String right = ngram.substring(ngram.indexOf(' ') + 1);

				pair = lm.get(left);
				if (pair != null) { // bigram is there, return backoff prob
					gramscore = pair.second() + ngramscore(right);
					// System.out.println(" " + gramscore);
					return gramscore;
				} else { // left context is null; either unknown left bigram or
							// unknown left unigram
					gramscore = ngramscore(left) + ngramscore(right);
					// System.out.println(" " + gramscore);
					return gramscore;
				}
			} else
				// unknown unigram
				return 0.0;
		}// end else
	}// end ngramscore

	public static boolean check_coverage(Node n, ArrayList<String> unigrams, int stackindex, HashMap<String, ArrayList<Pair<String, Double>>> searchspace){
		//Check for zeros in reorder-window
		if(n.coveragestring().substring(0,Math.max(stackindex - r, 0)).contains("0")){
			return false;
		}

		int[] cov = n.coverage;		
		int i=0;
		String indices = "";

		while(i < cov.length){
			//Get next contiguous string of zeros
			while((i < cov.length) && (cov[i]==0)){
				indices += i + "";
				i++;
			}//end while i < cov.length && cov[i]==0
			if((indices != "") && (indices.length()<=3)){
				//Build up phrase for this uncovered unigram, bigram, or trigram
				String phrase = "";
				for(int s=0; s<indices.length(); s++){
					int index = Integer.parseInt(Character.toString(indices.charAt(s)));
					phrase += unigrams.get(index) + " ";
				}
				phrase = phrase.trim();
				/* Debugging: Print all translations for our phrase				
				 * System.out.println("Translations for "+ phrase +":");
				 * for(Pair<String,Double> trans : searchspace.get(phrase))
					 System.out.println(trans.first());
				 */
				//Check to see if this uncovered phrase has a translation
				if(searchspace.get(phrase) == null)
					return false;
			}
			i++;
			indices = "";
		}//end while i < cov.length
	
		return true;

	}//end check_coverage

	// Search for best translation; input is unigrams (list of words) from
	// source sentence and searchspace dictionary
	public static String search(ArrayList<String> unigrams, String hyp, HashMap<String, ArrayList<Pair<String, Double>>> searchspace) {
		// Initial stack for unigrams
		ArrayList<Node> init_stack = new ArrayList<Node>();
		int stackindex = 1;
		int numstacks = unigrams.size(); // #stacks = 0, 1 through numstacks
		System.out.println("numstacks: " + numstacks);

		// get heuristic hypothesis
		double hypscore = Double.parseDouble(hyp.substring(0, hyp.indexOf(' ')));
		hyp = hyp.substring(hyp.indexOf(' ')+1);
		double hyplmscore = getLMscore(hyp);
		double hyptotalscore = hypscore + hyplmscore;
		System.out.println("hyp = " + hyp + "\ntmscore: " + hypscore + "\nlmscore: " + hyplmscore);
		System.out.println("hypscore = " + hyptotalscore);

		ArrayList<ArrayList<Node>> stacks = new ArrayList<ArrayList<Node>>();
		// initialize stacks
		for (int i = 0; i <= numstacks; i++)
			stacks.add(new ArrayList<Node>());

		// add null node
		Node nullnode = new Node("", 0.0, null);
		nullnode.coverage = new int[numstacks];
		for (int i = 0; i < numstacks; i++)
			nullnode.coverage[i] = 0;
		nullnode.history = new ArrayList<String>();
		nullnode.tmprob = 0.0;
		nullnode.lmprob = 0.0;
		nullnode.score = 0.0;
		stacks.get(0).add(nullnode);

		// int r = 1; //Reorder distance
		int maxsize = hist_size;

		while (stackindex <= numstacks) {
			System.out.print("Building stack " + stackindex + " : ");
			Node worstnode = new Node("null", 0.0, null);
			worstnode.score = 0.0;
			worstnode.lmprob = 0.0;

			Node initialworst = worstnode;

			for (int s = Math.max(stackindex - 1, 0); s >= Math.max(
					stackindex - 3, 0); s--) {
				int ngramorder = stackindex - s; // length of phrase
				// System.out.println("s: " + s + " ngramorder: " + ngramorder);
				if ((stackindex + ngramorder) <= numstacks + 1) {
					// System.out.println("\tcheck ngram order " + ngramorder);
					for (Node node : stacks.get(s)) { // check each potential
														// parent node,
														// enumerate its
														// children
						for (int i = Math.max(s - r, 0); i <= Math.min(s + r,
								numstacks - 1); i++) {
							String cov = "";
							for (int c = i; c < Math.min(i + ngramorder,
									numstacks); c++)
								cov += node.coverage[c];
							// System.out.println("\ti: " + i + " coverage: " +
							// cov);
							if (!cov.contains("1")) { // check the rest of
														// phrase in coverage
														// vector
								// System.out.println("\tchecking spanish index "
								// + i);
								// add searchspace[unigrams.get(i)] to
								// stacks.get(stackindex)
								String searchphrase = "";
								for (int p = i; p < Math.min(i + ngramorder,
										numstacks); p++)
									searchphrase += unigrams.get(p) + " ";
								searchphrase = searchphrase.trim();
								// System.out.println("\t\tlooking up: " +
								// searchphrase);
								ArrayList<Pair<String, Double>> list = searchspace
										.get(searchphrase);
								if (list == null)
									list = new ArrayList<Pair<String, Double>>();
								// System.out.println("\t\tmatches: " +
								// list.size());
								for (Pair<String, Double> pair : list) {
									// check score against hyp
									double tmscore = pair.second();
									// System.out.println("\t\tmatch: " +
									// pair.first() + " " + score);

									// make node object, set parent pointer, add
									// to stack
									String phrase = pair.first();
									//Add new node update prob
									Node n = new Node(phrase, tmscore+ node.tmprob, node);
									// Set coverage vector
									n.coverage = n.parent.coverage();
									for (int c = i; c < Math.min(
											i + ngramorder, numstacks); c++)
										n.coverage[c] = 1;
									// Set end of last spanish phrase translated
									if (phrase.contains(" "))
										n.lastsp = phrase.substring(phrase.lastIndexOf(' '));
									else
										n.lastsp = phrase;
									// Set English translation history
									n.history = n.parent.history();
									StringTokenizer phrasetok = new StringTokenizer(phrase);
									while (phrasetok.hasMoreTokens())
										// update history
										n.history.add(phrasetok.nextToken());

									// compute language model score
									double lmscore = getLMscore(n.historystring());
									n.lmprob = lmscore;

									// set score = lmprob + tmprob + future cost
									if (check_coverage(n,unigrams,stackindex,searchspace)) {
										n.score = n.lmprob + n.tmprob + fctable.get(n.coveragestring());

										// System.out.println(n);

										// if(n.score >= hyptotalscore){
										// System.out.println("node score greater than hypscore");

										// check stack for duplicates
										if ((stackindex > 1) && (n.history.size() >= 2)) {
											int removeindex = -1;
											boolean dup = false;
											for (Node stacknode : stacks.get(stackindex)) {
												if (stacknode.history.size() >= 2) {

													String stackhist = stacknode.history.get(stacknode.history
																	.size() - 2)
															+ " "
															+ stacknode.history
																	.get(stacknode.history
																			.size() - 1);
													if (n.history.size() < 2)
														System.out
																.println("hist < 2: "
																		+ n.historystring());
													String hist = n.history
															.get(n.history
																	.size() - 2)
															+ " "
															+ n.history
																	.get(n.history
																			.size() - 1);
													String ncov = "";
													String sncov = "";
													for (int c = 0; c < numstacks; c++) {
														ncov += n.coverage[c];
														sncov += stacknode.coverage[c];
													}
													// System.out.println("\t\t\tcomparing "
													// + hist + " " + ncov);
													// System.out.println("\t\t\t and "
													// + stackhist + " " +
													// sncov);
													// Recombination: check if
													// any set of nodes has the
													// same (1) last two English
													// words translated, (2)
													// coverage vectors, (3)
													// last Spanish word
													// translated; then keep
													// only the best node in
													// stack
													if (stackhist.equals(hist)
															&& ncov.equals(sncov)
															&& stacknode.lastsp
																	.equals(n.lastsp)) {
														// System.out.println("\t\t\tduplicate!");
														dup = true;
														if ((stacknode.score) < (n.score)) {
															removeindex = stacks
																	.get(stackindex)
																	.indexOf(
																			stacknode);
															// stacks.get(stackindex).add(n);
															break;
														}// end if
															// stacknode.prob
													}// end if duplicate
												}// end if
													// stacknode.history.size()
													// >=2
											}// end for stacknode in stack
											if (dup && (removeindex >= 0)) {
												Node rn = stacks.get(stackindex).get(removeindex);
												stacks.get(stackindex).remove(removeindex);
												// System.out.println("\t\t\tremoved old node");

												stacks.get(stackindex).add(n);
												if (n.score < worstnode.score)
													worstnode = n;
												if (rn == worstnode) {
													worstnode = initialworst;
													for (Node sn : stacks.get(stackindex)) { // reset
																				// worstnode
														if (sn.score < worstnode.score)
															worstnode = sn;
													}
												}
												// System.out.println("\t\t\tadded new node");
											} else if (!dup) {
												if (stacks.get(stackindex).size() > maxsize) {
													//Only delete if its not worse
													if (n.score > worstnode.score) {
														stacks.get(stackindex).add(n);
														//Drop worst
														boolean bool = stacks.get(stackindex).remove(worstnode);
														if (!bool)
															System.out.println("\tWARNING: worstnode not removed!");
														worstnode = initialworst;
														//Reset worstnode
														for (Node sn : stacks.get(stackindex)) {
															if (sn.score < worstnode.score)
																worstnode = sn;
														}
													}// end if n.score >
														// worstnode.score
												} else {
													stacks.get(stackindex).add(n);
													//Reset worstnode
													if (n.score < worstnode.score)
														worstnode = n;
												}
												// System.out.println("\t\t\tadded new node");
											}
											// else dup && remove = -1, keep old
											// one, don't add new, nothing to do
										}// end if stackindex > 1
										else {
											stacks.get(stackindex).add(n);
										}
									}// end if coverage vector acceptable
									// }//end if score < hypscore
								}// end for pair in list
							}// end if node.coverage
						}// end for i
					}// end for node in stacks
				}// end if stackindex+ngramorder <= numstacks
			}// end for s

			System.out.println("Added " + stacks.get(stackindex).size() + " nodes");
			/* Debugging: Print all nodes in the current stack
			 * for(Node n : stacks.get(stackindex))
			 * 	System.out.println(n);
			 */
			//Increment stack index, move to next stack
			stackindex++;
		}// end while stackindex <= numstacks

		double bestscore = Double.NEGATIVE_INFINITY;
		Node bestnode = null;
		// System.out.println("hypscore: " + hypscore);
		for (Node n : stacks.get(numstacks)) {
			System.out.println(n);
			String hist = n.historystring();
			n.lmprob = getLMscore(hist);
			n.score = n.lmprob + n.tmprob;
			boolean full = true;
			//Check to make sure translation covers all words
			for (int c = 0; c < n.coverage.length; c++) {
				if (n.coverage[c] == 0) {
					full = false;
					break;
				}
			}

			// compute lm score
			if (full) {
				double score = n.score;
				if (score >= bestscore) {
					bestnode = n;
					bestscore = score;
				}// end if lmscore
			}// end if full
		}// end for node n

		String result = hyp;
		if (bestnode != null) {
			result = bestnode.historystring();
			result = result.substring(result.indexOf(' ') + 1,
					result.lastIndexOf(' '));
		}
		System.out.println("best: " + bestscore + "\n" + result);
		return result;
	}// end search

}// end class
