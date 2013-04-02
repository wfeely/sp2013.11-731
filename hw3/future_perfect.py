#!/usr/bin/python
#future_perfect.py
#Weston Feely
#4/1/13
import sys
import models

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

#Returns a list of all strings of length n for characters in alphabet sigma
def gen_strings(sigma,n):
	string_set = ['']
	for i in xrange(n):
		string_set = [char+string for char in sigma for string in string_set]	
	return string_set

#Returns coverage vectors for future cost table
def gen_coverage_vectors(r,n):
	vectors = []
	for stdin in xrange(0,n):
		left = '1'*max(0,stdin-r)
		mid_list = gen_strings(list('01'),(min(stdin+r+1,n)-max(stdin-r,0)))
		right = '0'*(n-len(mid_list[0])-len(left))
		for x in mid_list:
			vectors.append(left+x+right)
	return vectors

#Make future cost table for a particular sentence, given the searchspace and the LM
def fc_table(sentence,searchspace,lm):
	output = []
	r = 2 # reorder distance
	n = len(sentence) # length of sentence
	#Enumerate all possible coverage vectors for a sentence of length n
	vectors = gen_coverage_vectors(r,n)
	#Get future cost for each cov
	for cov in vectors:
		cost = 0.0
		for i in xrange(0,len(cov)):
			if cov[i] == '0':
				#Add logprob of translation for all translations of this unigram
				#print 'Coverage: '+cov
				#print 'Len(vector): '+str(len(cov))+' len(sent): '+str(len(sentence))
				#print 'Sentence: '+' '.join(sentence)	
				if sentence[i] in searchspace:
					#print 'Searching for: '+sentence[i]+' '+str(i)
					for pair in searchspace[sentence[i]]:
						cost += pair[1] # add TM score to cost
						#Add LM score to cost
						lm_state = lm.begin()
						for word in pair[0].split():
							(lm_state, word_logprob) = lm.score(lm_state, word)
							cost += word_logprob
						cost += lm.end(lm_state)
				#Check for '00'
				if i > 0 and cov[i-1] == '0':
					bigram = sentence[i-1]+' '+sentence[i]
					if bigram in searchspace:
						for pair in searchspace[bigram]:
							cost += pair[1] # add TM score to cost
							#Add LM score to cost
							lm_state = lm.begin()
							for word in pair[0].split():
								(lm_state, word_logprob) = lm.score(lm_state, word)
								cost += word_logprob
							cost += lm.end(lm_state)
					#Check for '000'
					if i > 1 and cov[i-2:i-1] == '00':
						trigram = sentence[i-2]+' '+sentence[i-1]+' '+sentence[i]
						if trigram in searchspace:
							for pair in searchspace[trigram]:
								cost += pair[1] # add TM score to cost
								#Add LM score to cost
								lm_state = lm.begin()
								for word in pair[0].split():
									(lm_state, word_logprob) = lm.score(lm_state, word)
									cost += word_logprob
								cost += lm.end(lm_state)
		#Print coverage vector and cost
		output.append(cov+'\t'+str(cost))
	return output

#Generates a future cost table for 
def main(args):
	lm = models.LM('data/lm')
	transfile = open('data/tm').readlines()
	data = open('data/input').readlines()
	tm = TM(transfile)
	for i in xrange(0,len(data)):
		sentence = data[i].strip().split()
		searchspace = tm.getsearchspace(sentence)
		output = fc_table(sentence,searchspace,lm)
		f = open('fc/sent'+str(i)+'.txt','w')
		for line in output:
			f.write(line+'\n')
		f.close()
	return 0

if __name__ == '__main__':
	sys.exit(main(sys.argv))
