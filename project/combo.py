#!/usr/bin/python
#combo.py
#Weston Feely
#4/23/13
import sys
import itertools

#Combines text from two files, taking the union of the two sets of data, and union of that set
#with the cartesian product of both sets, prints output set to stdout 
def main(args):
	#Check for required args
	if len(args) < 3:
		print "Usage: python combo.py list1.txt list2.txt"
		return 1
	s1 = set([x.strip() for x in open(args[1]).readlines()]) # read in first text file as a set
	s2 = set([x.strip() for x in open(args[2]).readlines()]) # read in second text file as a set
	#Make an output set, as the union of both s1 and s2
	out_set = s1 | s2
	#Add cartesian product of s1 and s2 to out_set
	for element in itertools.product(s1,s2):
		out_set.add(''.join(element))
	#Print output set to stdout
	for item in out_set:
		print item
	return 0

if __name__ == "__main__":
	sys.exit(main(sys.argv))
