/** MT HW 1 Alignment
 * @author sjeblee@.cs.cmu.edu
 * 7 Feb 2013
 **/

import java.io.*;
import java.util.*;

public class Align {

	public static void main(String[] args) {

		long starttime = System.currentTimeMillis();

		if (args.length < 2) {
			System.out.println("Usage: java Align sentences dev-alignments");
			System.exit(1);
		}

		// data structures
		ArrayList<String> sentences = new ArrayList<String>();
		HashMap<String, Double> efprob = new HashMap<String, Double>();
		HashMap<String, Double> efcount = new HashMap<String, Double>();
		HashMap<String, Double> decount = new HashMap<String, Double>();
		HashMap<String, Double> encount = new HashMap<String, Double>();
		HashMap<String, Double> transprob = new HashMap<String, Double>();
		HashMap<Integer, Double> jumpwidth = new HashMap<Integer, Double>();
		int maxD = 1;

		// initialize jumpwidth
		for (int jw = -81; jw < 81; jw++)
			jumpwidth.put(jw, 0.0);

		try { // read in sentences
			System.out.println("Reading in dev sentences...");
			Scanner infile = new Scanner(new FileReader(args[0]));
			Scanner alignfile = new Scanner(new FileReader(args[1]));

			while (alignfile.hasNextLine()) {
				String a = alignfile.nextLine();
				String line = infile.nextLine();
				sentences.add(line); // save sentences

				// System.out.println(line);

				int barindex = line.indexOf("|||");
				String de = line.substring(0, barindex);
				String en = line.substring(barindex + 3);
				StringTokenizer detok = new StringTokenizer(de);
				StringTokenizer entok = new StringTokenizer(en);
				ArrayList<String> delist = new ArrayList<String>();
				ArrayList<String> enlist = new ArrayList<String>();
				delist.add("NULL");
				while (detok.hasMoreTokens())
					delist.add(detok.nextToken());
				while (entok.hasMoreTokens())
					enlist.add(entok.nextToken());

				for (String deword : delist) {
					decount.put(deword, 0.0);
					for (String enword : enlist) {
						// encount.put(enword, 0.0);
						efcount.put(enword + " " + deword, 0.0);
						efprob.put(enword + " " + deword, 0.0);
					}
				}

				int length = delist.size(); // length of german sentence
				if (length > maxD)
					maxD = length;

				// record what words are aligned to what other words
				StringTokenizer atok = new StringTokenizer(a);

				int j1 = -1;
				int[] aligns = new int[enlist.size()];

				while (atok.hasMoreTokens()) {
					String pair = atok.nextToken();
					int dash = pair.indexOf('-');
					if (dash != -1) { // skip if possible alignment
						int j = Integer.parseInt(pair.substring(0, dash));
						j++; // increment j to account for NULL
						int e = Integer.parseInt(pair.substring(dash + 1));
						// System.out.println("d=" + j + " e=" + e);
						// System.out.println("jprime = " + j1 + " jw = " + (j -
						// j1));

						// if(j1 >= 0)
						// jumpwidth.put(j - j1, jumpwidth.get(j-j1) +
						// 1.0);//count(diff)++

						aligns[e] = j;

						String deword = delist.get(j);
						String enword = enlist.get(e);
						// String deen = deword + " " + enword;
						String ende = enword + " " + deword;

						// record counts
						if (!decount.containsKey(deword))
							decount.put(deword, 0.0);
						// decount.put(deword, decount.get(deword)+1.0);
						if (!encount.containsKey(enword))
							encount.put(enword, 0.0);
						// encount.put(enword, encount.get(enword)+1.0);
						if (!efprob.containsKey(ende)) {
							efprob.put(ende, 0.0);
							efcount.put(ende, 0.0);
						}

						// j1 = j;
					}// end if dash
				}// end while atok hasmoretokens

				for (int ea = 0; ea < aligns.length; ea++) {
					int j = aligns[ea];
					if (j1 >= 0)
						jumpwidth.put(j - j1, jumpwidth.get(j - j1) + 1.0);// count(diff)++
					j1 = j;
				}
			}// end while alignfile
			alignfile.close();

			int counter = 0;
			while (infile.hasNextLine() && counter < 99850) {
				if ((counter % 10000) == 0)
					System.out.print("\n" + counter / 1000);
				else if ((counter % 1000) == 0)
					System.out.print(".");
				String line = infile.nextLine();
				sentences.add(line); // save sentences
				int barindex = line.indexOf("|||");
				String de = line.substring(0, barindex);
				String en = line.substring(barindex + 3);
				StringTokenizer detok = new StringTokenizer(de);
				StringTokenizer entok = new StringTokenizer(en);
				ArrayList<String> delist = new ArrayList<String>();
				ArrayList<String> enlist = new ArrayList<String>();
				delist.add("NULL");
				while (detok.hasMoreTokens())
					delist.add(detok.nextToken());
				while (entok.hasMoreTokens())
					enlist.add(entok.nextToken());
				int length = delist.size(); // length of english sentence
				if (length > maxD)
					maxD = length;

				for (String deword : delist) {
					decount.put(deword, 0.0);
					for (String enword : enlist) {
						encount.put(enword, 0.0);
						efcount.put(enword + " " + deword, 0.0);
						efprob.put(enword + " " + deword, 0.0);
					}
				}
				counter++;
			}// end while infile has next
			infile.close();
		}// end try

		catch (IOException e) {
			System.out.println(e.getMessage());
		}

		//Set<Integer> set = jumpwidth.keySet();
		//for (int index : set) {
		//	System.out.println(index + ": " + jumpwidth.get(index));
		//}

		// calculate transition probs
		for (int D = 2; D <= maxD; D++) {
			for (int j = 0; j < D; j++) {
				for (int jprime = 0; jprime < D; jprime++) {
					String key = j + " " + jprime + " " + D;
					int sum = 0;
					for (int x = 0; x < D; x++)
						sum += jumpwidth.get(x - jprime);
					if (j == 0)
						transprob.put(key, 0.01);
					else {
						transprob.put(key, jumpwidth.get(j - jprime)
								/ (double) sum);
						// smooth prob
						transprob.put(key, 0.99 * ((0.00001 / ((double) D)) + (0.99999 * transprob.get(key))));
					}// end else
				}
			}
		}// end calculate transition probs

		// Model 1 EM
		System.out.println("\nRunning EM...");

		// initialize t(e|f) uniformly
		try{
			Scanner incounts = new Scanner(new FileReader(args[2]));
			HashMap<String,Integer> germanhash = new HashMap<String,Integer>();
			while(incounts.hasNextLine()){
				String line = incounts.nextLine();
				StringTokenizer counttok = new StringTokenizer(line);
				germanhash.put(counttok.nextToken(), Integer.parseInt(counttok.nextToken()));
			}

			// initialize t(e|f) uniformly
			Set<String> tef = efprob.keySet();
			double efsize = (double) efprob.size();
			for (String ef : tef){
				String f = ef.substring(ef.indexOf(' ')+1);
				try{
					efprob.put(ef, 1.0 / germanhash.get(f));
				}
				catch(NullPointerException npe){
					System.out.println("NPE: " + f);
					System.exit(1);	
				}
			}

			incounts.close();

		}//end try
		catch(IOException exp){
			System.out.println(exp.getMessage());
		}

		// do until convergence
		for (int x = 0; x < 10; x++) {
			System.out.println("\titeration " + x);

			HashMap<String, Double> total_s = new HashMap<String, Double>();

			// set count(e|f) to 0 for all e,f
			Set<String> allef = efcount.keySet(); // assume all other pairs are
			// never observed
			for (String ende : allef) {
				efcount.put(ende, 0.0);
			}

			// set total(f) to 0 for all f
			Set<String> countsf = decount.keySet();
			for (String c : countsf)
				decount.put(c, 0.0);
			// for all sentence pairs (e_s,f_s)
			for (String line : sentences) {

				int barindex = line.indexOf("|||");
				String de = line.substring(0, barindex);
				String en = line.substring(barindex + 3);
				StringTokenizer detok = new StringTokenizer(de);
				StringTokenizer entok = new StringTokenizer(en);
				ArrayList<String> delist = new ArrayList<String>();
				ArrayList<String> enlist = new ArrayList<String>();
				delist.add("NULL");
				while (detok.hasMoreTokens())
					delist.add(detok.nextToken());
				while (entok.hasMoreTokens())
					enlist.add(entok.nextToken());

				// set total_s(e) = 0 for all e
				for (String e : enlist)
					total_s.put(e, 0.0);

				// for all words e in e_s
				for (String e : enlist) {
					// for all words f in f_s
					for (String f : delist) {
						// total_s(e) += t(e|f)
						total_s.put(e, total_s.get(e) + efprob.get(e + " " + f));
					}
				}

				// for all words e in e_s
				for (String e : enlist) {
					// for all words f in f_s
					for (String f : delist) {
						String ef = e + " " + f;
						// count(e|f) += t(e|f) / total_s(e)
						efcount.put(ef, efcount.get(ef)
								+ (efprob.get(ef) / total_s.get(e)));
						// total(f) += t(e|f) / total_s(e)
						decount.put(f, decount.get(f)
								+ (efprob.get(ef) / total_s.get(e)));
					}
				}
			}// end for all esfs

			for (String ende : allef) {
				// t(e|f) = count(e|f) / total(f)
				String deword = ende.substring(ende.indexOf(' ') + 1);
				efprob.put(ende, efcount.get(ende) / decount.get(deword));
			}

		}// end for x iterations

		try {
			System.out.println("Printing Alignments...");
			FileWriter outfile = new FileWriter("output.txt");
			for (String line : sentences) {
				String alignment = "";
				// System.out.println(line);
				int barindex = line.indexOf("|||");
				String de = line.substring(0, barindex);
				String en = line.substring(barindex + 3);
				StringTokenizer detok = new StringTokenizer(de);
				StringTokenizer entok = new StringTokenizer(en);
				ArrayList<String> delist = new ArrayList<String>();
				ArrayList<String> enlist = new ArrayList<String>();
				delist.add("NULL");
				while (detok.hasMoreTokens())
					delist.add(detok.nextToken());
				while (entok.hasMoreTokens())
					enlist.add(entok.nextToken());

				int jprime = -1;
				// double[][] aligntable = double[][];

				for (int eindex = 0; eindex < enlist.size(); eindex++) {
					int bestindex = 0;
					double bestprob = 0.0;
					String e = enlist.get(eindex);
					// System.out.println("d: " + dindex + " " + d);
					for (int dindex = 0; dindex < delist.size(); dindex++) {
						String d = delist.get(dindex);
						String ed = e + " " + d;
						double prob = 0.0;

						if (jprime == -1)
							prob = efprob.get(ed);
						else {
							String key = dindex + " " + jprime + " "
									+ delist.size();
							try {
								prob = (0.75 * efprob.get(ed))
										* (0.25 * transprob.get(key));
							} catch (NullPointerException npe) {
								System.out.println("NullPointer Exception");
								System.out.println("e: " + eindex + " " + e);
								System.out.println("\tfindex: " + dindex + " "
										+ d);
								System.out.println("\tmaxD = " + maxD);
								System.out.println("\tkey = " + key + " tp = "
										+ transprob.get(key));
								System.out.println("\ted = " + ed
										+ " efprob = " + efprob.get(ed));
								System.out.println("jprime = " + jprime);
								System.out.println("\tprob = " + prob);
							}
						}

						// aligntable[eindex][dindex] = prob; //save the
						// alignment prob

						if (prob > bestprob) {
							bestprob = prob;
							bestindex = dindex;
						}
					}// end for en

					if (bestindex != 0)
						alignment += (bestindex - 1) + "-" + eindex + " ";
					jprime = bestindex;

					// System.out.println("jprime = " + jprime);

				}// end for each german word

				// write alignments to file
				outfile.write(alignment + "\n");
			}// end for each sentence

			outfile.close();

		}// end try
		catch (IOException e) {
			System.out.println(e.getMessage());
		}

		long endtime = System.currentTimeMillis();
		long time = endtime - starttime;
		double printtime = ((double) time) / 1000;
		printtime = printtime / 60.0;
		System.out.printf("\talignment time: %.2f min \n", printtime);

		System.out.println("Done.");

	}// end main

}
