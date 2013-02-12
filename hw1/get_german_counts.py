#!/usr/bin/python
#get_german_counts.py
#Weston Feely
#2/11/13
import sys

def main(args):
	if len(args) < 3:
		print 'Usage: python get_german_counts.py #lines output_filename'
		return 1
	#Read data from file
	data = open('data/dev-test-train.de-en').readlines()
	sentence_pairs = []
	i=1
	for line in data:
		if i > int(args[1]):
			break
		lis = line.split('|||')
		f_sent = ('NULL '+lis[0].strip()).split() #German sentence
		e_sent = lis[1].strip().split() #English sentence
		sentence_pairs.append((e_sent,f_sent))
		i+=1
	#Initialize t(e|f) uniformly
	t = {}
	for i in xrange(0,len(sentence_pairs)):
		(e_sent,f_sent) = sentence_pairs[i]
		for e in e_sent:
			for f in f_sent:
				t[(e,f)] = 1.0
	#Sort (e,f) pairs and count number of english translations for each german word
	outfile = open(args[2],'w')
	sorted_pairs = sorted(t, key=lambda x: x[1])
	c=0
	prevf = sorted_pairs[0][1]
	outfile.write(prevf+' ')
	for (e,f) in sorted_pairs:
		if f == prevf:
			c+=1
		else:
			outfile.write(str(c)+'\n')
			c=1
			prevf = f
			outfile.write(prevf+' ')
	outfile.write(str(c)+'\n')
	outfile.close()
	return 0

if __name__ == '__main__':
	sys.exit(main(sys.argv))
