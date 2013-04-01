/** Node class for GetDeps
* @author sjeblee@cs.cmu.edu
* Last Modified: 30 March 2013
*/

import java.util.ArrayList;
import java.util.HashMap;

public class Node{

public String name;
public ArrayList<String> history;
public Node parent;
public double tmprob;
public double lmprob;
public double score;
public int[] coverage;
public String lastsp;

public Node(String n, double tm, Node p){
	name = n;
	tmprob = tm;
	parent = p;
}

public ArrayList<String> history(){
	ArrayList<String> histcopy = new ArrayList<String>();
	for(String s : this.history){
		histcopy.add(s);
	}
	return histcopy;
}

public int[] coverage(){
	int[] arr = new int[this.coverage.length];
	for(int i=0; i<arr.length; i++)
		arr[i] = coverage[i];
	return arr;
}

public String coveragestring(){
	String h = "";
	for(int i=0; i<coverage.length; i++)
		h += coverage[i];
	return h.trim();
}

public String historystring(){
	String h = "";
	for(String s : this.history)
		h += s + " ";
	return h.trim();
}

public String toString(){
	String s = "**********************\n" + this.historystring() + "\n" + score + "\n";
	for(int i=0; i<coverage.length; i++){
		s += coverage[i];
	}
	s += "\n**********************";
	return s;
}

}//end class
















