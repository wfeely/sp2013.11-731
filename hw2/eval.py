#!/usr/bin/python
#eval.py
#Weston Feely
#2/15/13
import sys
from collections import Counter	

'''Returns ngrams of given order for a sentence, as a list of words'''
def get_ngrams(sent,n):
	if (len(sent) < n) or (n < 1): return []
	ngrams = []
	for i in xrange(0,len(sent)):
		if i < n-1: continue
		buff = ''
		for j in xrange(n-1,-1,-1):
			buff += sent[i-j]+' '
		ngrams.append(buff.rstrip())
	return ngrams

'''BLEU metric for single reference'''
def bleu(hyp,ref,order):
	#Check order against length of each sentence
	order = min(len(hyp),len(ref),order)
	#Get shared unigrams in hyp and ref
	unigrams = list((Counter(hyp) & Counter(ref)).elements())
	#Temp variables to avoid excess computation
	bigrams = []
	bidenom = list(hyp)
	trigrams = []
	tridenom = list(hyp)
	quadgrams = []
	quaddenom = list(hyp)
	#Get shared ngrams in hyp and ref
	if order > 1:
		bidenom = get_ngrams(hyp,2) # denominator for calculating precision
		bigrams = list((Counter(bidenom) & Counter(get_ngrams(ref,2))).elements())
	if order > 2:
		tridenom = get_ngrams(hyp,3) # denominator for calculating precision
		trigrams = list((Counter(tridenom) & Counter(get_ngrams(ref,3))).elements())
	if order > 3:
		quaddenom = get_ngrams(hyp,4) # denominator for calculating precision
		quadgrams = list((Counter(quaddenom) & Counter(get_ngrams(ref,4))).elements())
	#Calculate precision for each ngram order
	p1 = len(unigrams)/float(len(hyp))
	p2 = len(bigrams)/float(len(bidenom))
	p3 = len(trigrams)/float(len(tridenom))
	p4 = len(quadgrams)/float(len(quaddenom))	
	#Calculate brevity penalty
	brev = min(1.0,float(len(hyp))/len(ref))
	#Return BLEU score for given order
	score = 0.0
	if order == 4:
		score = brev*p1*p2*p3*p4
	elif order == 3:
		score = brev*p1*p2*p3
	elif order == 2:
		score = brev*p1*p2
	else:
		score = brev*p1
	return score

'''Simple METEOR metric'''
def meteor(hyp,ref):
	#Get shared unigrams in hyp and ref
	unigrams = list((Counter(hyp) & Counter(ref)).elements())
	p = len(unigrams)/float(len(hyp)) # precision
	r = len(unigrams)/float(len(ref)) # recall
	score = 0.0
	if not ((p == 0.0) and (r == 0.0)):
		score = (10.0*p*r)/(r+(9.0*p)) # weighted harmonic mean of precision and recall
	return score

'''Evaluation function, returns -1 if h1 is best, 1 if h2 is best, 0 if neither is best'''
def evaluate(triple):
	#Unpack triple and split each sentence into list of words
	h1,h2,e = triple
	h1 = h1.split()
	h2 = h2.split()
	e = e.split()
	#Score both hypotheses
	score1 = (0.3*bleu(h1,e,3))+(0.7*meteor(h1,e))
	score2 = (0.3*bleu(h2,e,3))+(0.7*meteor(h2,e))
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
