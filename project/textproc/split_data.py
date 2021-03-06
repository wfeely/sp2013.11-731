#!/usr/bin/python
#split_data.py
#Weston Feely
#3/30/13
import sys, random

#Splits the Arabic parallel corpus into a randomly selected 80% training set, 10% dev set, 10% test set
def main(args):
	#Check args
	if len(args) < 1:
		print 'Usage: python split_data.py arabic_corpus english_corpus dialect'
		return 1
	#Read in corpora
	ar_corpus = open(args[1]).readlines()
	en_corpus = open(args[2]).readlines()
	dialect = args[3]
	N = len(ar_corpus)
	#Pair up corpora, shuffle, and unpack
	parallel = []	
	for i in xrange(0,N):
		parallel.append((ar_corpus[i],en_corpus[i]))
	random.shuffle(parallel)
	ar_corpus = [pair[0] for pair in parallel]
	en_corpus = [pair[1] for pair in parallel]
	#Set up lists
	ar_train = []
	ar_dev = []
	ar_test = []
	en_train = []
	en_dev = []
	en_test = []
	#Set up percentages
	num_train = int(0.8*N)
	num_dev = int(0.1*N)
	#Loop through data
	for i in xrange(0,N):
		if i <= num_train:
			ar_train.append(ar_corpus[i])
			en_train.append(en_corpus[i])
		elif i <= (num_train+num_dev):
			ar_dev.append(ar_corpus[i])
			en_dev.append(en_corpus[i])
		else:
			ar_test.append(ar_corpus[i])
			en_test.append(en_corpus[i])
	#Write lists to file
	f = open(dialect+'train.'+dialect,'w')
	for line in ar_train:
		f.write(line)
	f.close()
	f = open(dialect+'train.en','w')
	for line in en_train:
		f.write(line)
	f.close()
	f = open(dialect+'dev.'+dialect,'w')
	for line in ar_dev:
		f.write(line)
	f.close()
	f = open(dialect+'dev.en','w')
	for line in en_dev:
		f.write(line)
	f.close()	
	f = open(dialect+'test.'+dialect,'w')
	for line in ar_test:
		f.write(line)
	f.close()
	f = open(dialect+'test.en','w')
	for line in en_test:
		f.write(line)
	f.close()
	return 0

if __name__ == '__main__':
	sys.exit(main(sys.argv))
