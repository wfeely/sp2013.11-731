#!/usr/bin/python
#scientist.py
#Weston Feely & Serena Jeblee
#3/21/13
import sys,re
from collections import Counter
from math import log

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

class LM(object):
	#Read ARPA-format language model from file and save as dictionary
	def __init__(self,arpa):
		self.ngrams = {} # dictionary of strings (ngrams) : 2-tuples (logprob, backoffprob)
		for line in arpa:
			lis = line.strip().split('\t')
			if len(lis) > 1:
				logprob = lis[0] 
				ngram = lis[1]
				if len(lis) > 2:
					backoffprob = lis[2]
					self.ngrams[ngram] = (logprob,backoffprob)
				else:
					self.ngrams[ngram] = (logprob,)
	#Get probability for an input sentence as string
	def getprob(self,sentence):
		sentence = '<s> '+sentence+' </s>'
		words = sentence.split()
		bigrams = get_ngrams(words, 2)
		trigrams = get_ngrams(words, 3)
		for i in xrange(0,len(words)):
			pass
		return 0.0

class TM(object):
	#Read translation model from file and organize into dictionaries
	def __init__(self,trans):
		self.ngram_pairs = {} # dictionary of 2-tuples of strings (spanishngram, englishngram) : list of floats [probs]
		self.spanish_ngrams = {} # dictionary of strings (spanishngram) : list of 2-tuples of string,float [(englisngram, prob)]
		for line in trans:
			lis = line.strip().split(' ||| ')
			sp = lis[0].strip()
			en = lis[1].strip()
			prob = float(lis[2].strip())
			if (sp,en) in self.ngram_pairs:
				self.ngram_pairs[(sp,en)] += [prob]
			else:
				self.ngram_pairs[(sp,en)] = [prob]
			if sp in self.spanish_ngrams:
				self.spanish_ngrams[sp] += [(en,prob)]
			else:			
				self.spanish_ngrams[sp] = [(en,prob)]
	#Input is a list of unigrams, output is a dictionary, which is a subset of self.spanish
	#for each unigram, bigram, trigram from input that is in self.spanish	
	def getsubset(self,unigrams):
		bigrams = get_ngrams(unigrams,2) 
		trigrams = get_ngrams(unigrams,3)
		subset = {}
		for ngram in (unigrams+bigrams+trigrams):
			if ngram in self.spanish_ngrams:
				subset[ngram] = self.spanish_ngrams[ngram]
		return subset

#Node object for search graph
class Node(object):
	def __init__(self):
		pass

#Remove nodes ending in same bigram; keep only most probable duplicate
def fix_dups(stack):
	#Check for matching bigrams
	dups = Counter()
	for node in stack:
		if len(node.history) > 1:
			dups[' '.join(node.history[-2:])] +=1
	for item in dups:
		dups[item] -=1
	new_stack = []
	dup_map = {} # map of final bigram history strings : list of nodes
	#Remove duplicates from stack, place into lists of duplicates within dup_map	
	for node in stack:
		if len(node.history) > 1:
			history_string = ' '.join(node.history[-2:])
			if history_string in dups:
				#Put this node in a list of nodes with this history
				if history_string in dup_map:
					dup_map[history_string].append(node)
				else:
					dup_map[history_string] = [node]
			else:
				#Final bigram history string not a duplicate, append to stack1
				new_stack.append(node)
		else:
			#History is unigram string, append to stack1
			new_stack.append(node)
	#Find most probable node for each list of duplicates in dup_map	
	for history_string in dup_map:
		best = None
		best_value = float("-inf")
		for node in dup_map[history_string]:
			if node.prob > best_value:
				best_value = node.prob
				best = node
		new_stack.append(best) # append best node to stack1
	return new_stack

#Search for best translation; input is unigrams (list of words) from source sentence and searchspace dictionary
def search(unigrams, searchspace):
	#Initial stack for unigrams
	init_stack = []
	#Loop through unigrams
	for i in xrange(0,len(unigrams)):
		if unigrams[i] in searchspace:
			for entry in searchspace[unigrams[i]]:
				#Create a node in stack for each lexical translation of unigram
				currentnode = Node()
				currentnode.parent = None # set parent to null
				currentnode.text = entry[0] # set text to this english word
				currentnode.history = [currentnode.text.split()] # include current word in initial history
				currentnode.prob = entry[1] # set probability to this lexical prob
				bitstring = '0'*len(unigrams[i])
				bitstring[i] = '1'
				currentnode.coverage = bitstring # set coverage to bitstring of zeros, with ith as 1
				init_stack.append(currentnode) # append node to starting stack
	#Remove nodes ending in same bigram; keep only most probable duplicate
	stack1 = fix_dups(init_stack)
	#Loop for translations of length 2+
	for j in xrange(2,len(unigrams)):
		stack2 = []
		stack1 = list(stack2)
	return sentences

#def main(args):
#arpafile = open('data/lm').readlines()
transfile = open('data/tm').readlines()
data = open('data/input').readlines()
#lm = LM(arpafile)
tm = TM(transfile)
#for line in data:
unigrams = data[3].strip().split()
searchspace = tm.getsubset(unigrams)
#sentence = search(unigram,searchspace)
	#return 0

#if __name__ == '__main__':
#	sys.exit(main(sys.argv))
