/** Node class for GetDeps
* @author sjeblee@cs.cmu.edu
* Last Modified: 6 March 2013
*/

import java.util.ArrayList;
import java.util.HashMap;

public class Node{

public String name;
public ArrayList<String> history;
public Node parent;
public double prob;
public int[] coverage;

public Node(String n, double score, Node p){
	name = n;
	prob = score;
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

public String historystring(){
	String h = "";
	for(String s : this.history)
		h += s + " ";
	return h.trim();
}

public String toString(){
	String s = name + " " + prob + " ";
	for(int i=0; i<coverage.length; i++){
		s += coverage[i];
	}
	return s;
}

}//end class
















