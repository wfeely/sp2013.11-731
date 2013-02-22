#!/usr/bin/python
#eval.py
#Weston Feely
#2/15/13
import sys
from collections import Counter	

'''Simple METEOR metric'''
def meteor(hyp,ref):
	hyp = hyp.split()
	ref = ref.split()
	ngrams = list((Counter(hyp) & Counter(ref)).elements()) # mutual ngrams in hyp and ref, preserving multiples
	p = len(ngrams)/float(len(hyp)) # precision
	r = len(ngrams)/float(len(ref)) # recall
	score = 0.0
	if not ((p == 0.0) and (r == 0.0)):
		score = (10.0*p*r)/(r+(9.0*p)) # weighted harmonic mean of precision and recall
	return score

'''Evaluation function, returns -1 if h1 is best, 1 if h2 is best, 0 if neither is best'''
def evaluate(triple):
	h1,h2,e = triple
	#Score both hypotheses
	score1 = meteor(h1,e)
	score2 = meteor(h2,e)
	#Return result, indicating best hypothesis
	result = '0'
	if score1 > score2:
		result = '-1'
	elif score2 > score1:
		result = '1'
	else:
		result = '0'
	return result

def main(args):
	data = []
	gold = []
	#Check args
	if len(args) < 2:
		print 'Usage: python eval.py train|test'
		return 1
	#Get data from file
	if str(args[1]) == 'train':
		print 'Reading training data from file...'
		data = open('data/train.hyp1-hyp2-ref').readlines()
		gold = open('data/train.gold').readlines()
	elif str(args[1]) == 'test':
		print 'Reading test data from file...'
		data = open('data/test.hyp1-hyp2-ref').readlines()
	else:
		print 'Usage: python eval.py train|test'
		return 1
	#Organize data
	tuples = []
	for line in data:
		#Split line and place in 3-tuple
		lis = line.split('|||')
		h1 = lis[0].strip() # hypothesis 1
		h2 = lis[1].strip() # hypothesis 2
		e = lis[2].strip() # reference
		tuples.append((h1,h2,e))
	if gold:
		#Convert elements from string to int
		gold = [int(x) for x in gold]
	#Evaluate data
	print 'Evaluating hypotheses...'
	ans = []
	for triple in tuples:
		ans.append(evaluate(triple))
	#Write answers to file
	print 'Writing results to output.txt ...'
	outfile = open('output.txt','w')
	for digit in ans:
		outfile.write(str(digit)+'\n')
	outfile.close()
	print 'Done.'
	return 0

if __name__ == '__main__':
	sys.exit(main(sys.argv))
