#!/usr/bin/python
#model1.py
#Weston Feely
#2/9/13
import sys

def main(args):
	#Check for correct args
	if len(args) < 2:
		print 'Usage: python model1.py dev-test-train.de-en #EM_iterations'
		return 1
	if int(args[2]) < 1:
		print 'Error! Invalid number of EM iterations.'
		return 1
	#Get data from file
	print 'Reading sentence-aligned data from file'
	data = open(args[1]).readlines()
	sentence_pairs = []
	i=1
	for line in data:
		if i >= 15000:
			break
		lis = line.split('|||')
		f_sent = lis[0].strip().split() #German sentence
		e_sent = lis[1].strip().split() #English sentence
		sentence_pairs.append((e_sent,f_sent))
		i+=1
	#Initialize t(e|f) uniformly
	sys.stdout.write(str('Initializing translation probabilities'))
	sys.stdout.flush()
	t = {}
	for i in xrange(0,len(sentence_pairs)):
		(e_sent,f_sent) = sentence_pairs[i]
		if i % 5000 == 0:
			sys.stdout.write(str('.'))
			sys.stdout.flush()
		for e in e_sent:
			for f in f_sent:
				t[(e,f)] = 1.0
	for (e,f) in t:
		t[(e,f)] = t[(e,f)] / len(t)
	#Iterate EM until convergence
	sys.stdout.write(str('\nRunning EM'))
	sys.stdout.flush()
	iterations = 0
	while (iterations < int(args[2])):
		sys.stdout.write(str('.'))
		sys.stdout.flush()
		#Initialize
		count = {} # count(e|f)
		total = {} # total(f)
		s_total = {} # s_total(e)
		for (e,f) in t:
			count[(e,f)] = 0.0
			total[f] = 0.0
		for (e_sent,f_sent) in sentence_pairs:
			#Compute normalization
			for e in e_sent:
				s_total[e] = 0.0
				for f in f_sent:
					s_total[e] += t[(e,f)]
			#Collect counts
			for e in e_sent:
				for f in f_sent:
					count[(e,f)] += t[(e,f)] / s_total[e]
					total[f] += t[(e,f)] / s_total[e]
		#Estimate probabilities
		for (e,f) in t:
			t[(e,f)] = count[(e,f)] / total[f]
		iterations+=1
	#Align sentences based on model
	print '\nAligning Sentences'
	outfile = open('output.txt','w')
	for (e_sent,f_sent) in sentence_pairs:
		aligns = []
		#Loop through English words
		for i in xrange(0,len(e_sent)):
			e = e_sent[i]
			best_j = 0
			best_prob = 0.0
			#Loop through German words
			for j in xrange(0,len(f_sent)):
				f = f_sent[j]
				#Check if the current alignment is best
				if t[(e,f)] > best_prob:
					#Keep best alignment
					best_prob = t[(e,f)]
					best_j = j
			aligns.append(str(best_j)+'-'+str(i))
		#Write alignments for this sentence pair to file
		outfile.write(' '.join(aligns)+'\n')
	outfile.close()
	print 'Done.'
	return 0

if __name__ == '__main__':
	sys.exit(main(sys.argv))
