#!/usr/bin/python
#eval.py
#Weston Feely
#3/2/13
import sys,re
from time import time
from collections import Counter	

class WN(object):
	def __init__(self):
		self.word_to_ids = {} # dictionary of each word to its synset IDs
		self.id_to_words = {} # dictionary of each synset ID to its words
		self.populate() # populate the dictionaries
	def populate(self):
		#Loop through each part of speech category
		for pos in ['adj','adv','noun','verb']:
			#Loop through each line in corresponding WordNet index file
			for line in open('./WN/index.'+pos):
				#print line
				lis = line.split() # split line into list
				word = lis[0]+'.'+pos # get word and append part of speech (e.g. "mouse.n")
				synsets = [x for x in lis[1:] if len(x)>2] # get list of synset IDs
				#print 'Word:'+word
				#print 'Synsets:'+' '.join(synsets)
				self.word_to_ids[word] = synsets # set word:synset list in hash
				#Loop through synsets
				for synset in synsets:
					#Check if this is a new synset
					if not synset in self.id_to_words:
						#Create a new entry with the set of words it contains
						self.id_to_words[synset] = set([word])
					else:
						#Add a new word to the word set for this synset
						self.id_to_words[synset].add(word)
		#print '#Synsets='+str(len(self.id_to_words))
		#print '#Unique words='+str(len(self.word_to_ids))
	def get_synonyms(self,word):
		syns = set([])
		#Check if word is in hash
		if word in self.word_to_ids:
			#Loop through IDs corresponding to this word
			for ID in self.word_to_ids[word]:
				#Add synonyms to list
				syns = syns | self.id_to_words[ID]
		#if syns != set([]):
		#	print "Synonyms: "+word+' : '+' '.join(syns)
		return syns

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

'''Converts a word+Penn POS tag combo (dog.NN) into a wordnet POS tag combo (dog.noun) for all words in a list'''
def penn_to_wn_tags(pennlist):
	wnlist = []
	for pennword in pennlist:
		word_and_pos = pennword.split('_')
		wnword = word_and_pos[0]
		#Check Penn POS tag for noun
		if word_and_pos[1][0] == 'N':
			wnword = wnword+'.noun'
		#Check Penn POS tag for verb
		elif word_and_pos[1][0] == 'V':
			wnword = wnword+'.verb'
		#Check Penn POS tag for adjective
		elif word_and_pos[1][0] == 'J':
			wnword = wnword+'.adj'
		#Check Penn POS tag for adverb
		elif word_and_pos[1][0:2] == 'RB':
			wnword = wnword+'.adv'
		wnlist.append(wnword)
	return wnlist

'''BLEU metric for single reference'''
def bleu(hyp,ref,order):
	hyp = hyp.split()
	ref = ref.split()
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
def meteor(hyp,ref,wordnet):
	hyp = hyp.split()
	ref = ref.split()
	#Convert tags
	hyp = penn_to_wn_tags(hyp)
	ref = penn_to_wn_tags(ref)
	#Turn input lists into counters
	hyp_count = Counter(hyp)
	ref_count = Counter(ref)
	#Get shared unigrams in hyp and ref
	unigrams = list((hyp_count & ref_count).elements())
	#Get set difference with shared unigrams	
	hyp_count = hyp_count - Counter(unigrams)
	ref_count = ref_count - Counter(unigrams)
	#Check for WordNet synonyms
	for word in list(hyp_count.elements()):
		for poss in wordnet.get_synonyms(word):
			#Check for matching synonym
			if poss in ref_count:
				#print 'Synonym Match: "'+poss+'" is a synonym of "'+word+'"'
				unigrams.append(poss)
				ref_count = ref_count - Counter([poss])
				hyp_count = hyp_count - Counter([word])
				break
	#Calculate precision and recall
	p = len(unigrams)/float(len(hyp)) # precision
	r = len(unigrams)/float(len(ref)) # recall
	score = 0.0
	if not ((p == 0.0) and (r == 0.0)):
		score = (10.0*p*r)/(r+(9.0*p)) # weighted harmonic mean of precision and recall
	return score

'''Evaluation function, returns -1 if h1 is best, 1 if h2 is best, 0 if neither is best'''
def evaluate(triple,wordnet,i):
	#Unpack triple and split each sentence into list of words
	h1,h2,e = triple
	if i % 1000 == 0:
		sys.stdout.write('.')
		sys.stdout.flush()
	#Create "clean" version of each sentence, without POS tags, for BLEU
	h1_clean = re.sub('\_\S+\s',' ',h1)
	h1_clean.strip()
	h2_clean = re.sub('\_\S+\s',' ',h2)
	h2_clean.strip()
	e_clean = re.sub('\_\S+\s',' ',e)
	e_clean.strip()
	#Score both hypotheses	
	score1 = (0.3*bleu(h1_clean,e_clean,3))+(0.7*meteor(h1,e,wordnet))
	score2 = (0.3*bleu(h2_clean,e_clean,3))+(0.7*meteor(h2,e,wordnet))
	#score1 = meteor(h1,e,wordnet)
	#score2 = meteor(h2,e,wordnet)
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
	starttime = time()
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
		#data = open('data/test.hyp1-hyp2-ref').readlines()
		data = open('data/test.tagged').readlines()
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
	#Get WN data
	wordnet = WN()
	#Evaluate data
	print 'Evaluating hypotheses...'
	ans = []
	for i in xrange(0,len(tuples)):
		ans.append(evaluate(tuples[i], wordnet, i))
	#Write answers to file
	print 'Writing results to output.txt ...'
	outfile = open('output.txt','w')
	for digit in ans:
		outfile.write(str(digit)+'\n')
	outfile.close()
	print 'Time elapsed: '+str((time() - starttime)/60)
	print 'Done.'
	return 0

if __name__ == '__main__':
	sys.exit(main(sys.argv))
