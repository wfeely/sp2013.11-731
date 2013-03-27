#!/usr/bin/python
#scientist.py
#Weston Feely & Serena Jeblee
#3/27/13
import sys, random

class TM(object):
	#Read translation model from file and organize into dictionaries
	def __init__(self,transmodel):
		self.spanish_ngrams = {} # dictionary of strings (spanishngram) : list of 2-tuples of string,float [(englisngram, prob)]
		for line in transmodel:
			lis = line.strip().split(' ||| ')
			sp = lis[0].strip()
			en = lis[1].strip()
			prob = float(lis[2].strip())
			if sp in self.spanish_ngrams:
				self.spanish_ngrams[sp] += [(en,prob)]
			else:			
				self.spanish_ngrams[sp] = [(en,prob)]
	#Input is a list of unigrams, output is a dictionary, which is a subset of self.spanish
	#for each unigram, bigram, trigram from input that is in self.spanish_ngrams	
	def getsearchspace(self,unigrams):
		bigrams = get_ngrams(unigrams,2) 
		trigrams = get_ngrams(unigrams,3)
		subset = {}
		for ngram in (unigrams+bigrams+trigrams):
			if ngram in self.spanish_ngrams:
				subset[ngram] = self.spanish_ngrams[ngram]
		return subset

#Returns ngrams of given order for a sentence, as a list of words
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

#Make a random translation hypothesis for a given sentence's unigram list
def make_hypothesis(unigrams,searchspace):
	hyp = ''
	score = 0.0
	i=0
	#Loop through unigrams
	while i < len(unigrams):
		#Choose Spanish phrase of random length from beginning of unigrams
		if i == (len(unigrams)-1):
			phrase_size = 1
		else:		
			phrase_size = random.randint(1,len(unigrams)-i-1)
		poss = ' '.join(unigrams[i:(i+phrase_size)])
		#Make sure our possible Spanish phrase is in the searchspace
		while poss not in searchspace:
			phrase_size -=1
			poss = ' '.join(unigrams[i:(i+phrase_size)])
			if phrase_size < 1: break # unknown unigram
		#Get a random translation for this Spanish phrase
		if phrase_size >= 1:
			lex_trans = searchspace[poss]
			pair = lex_trans[random.randint(0,len(lex_trans)-1)]
			hyp += pair[0]+' ' # add English string to hypothesis
			score += pair[1] # add logprob to score
			i += phrase_size
		else:
			i += 1
	return hyp.strip(), score

def main(args):
	transfile = open('data/tm').readlines()
	data = open('data/input').readlines()
	tm = TM(transfile)
	for line in data:
		unigrams = line.strip().split()
		searchspace = tm.getsearchspace(unigrams)
		hyp,score = make_hypothesis(unigrams,searchspace)
		print str(score)+' '+hyp
	return 0

if __name__ == '__main__':
	sys.exit(main(sys.argv))
