#!/usr/bin/python
#mt_tag_prep.py
#Weston Feely
#3/2/13
import sys, re

def main(args):
	hyp1_tokenized = open('hyp1.tokenized').readlines()
	hyp2_tokenized = open('hyp2.tokenized').readlines()
	ref_tokenized = open('ref.tokenized').readlines()
	hyp1_tagging = []
	hyp2_tagging = []
	ref_tagging = []
	#Loop through sentences in each file
	for i in xrange(0,len(hyp1_tokenized)):
		#Regex replace "& quot ;" with "&quot;" as a single token
		hyp1_sent = re.sub("& quot ;","&quot;",hyp1_tokenized[i])
		hyp2_sent = re.sub("& quot ;","&quot;",hyp2_tokenized[i])
		ref_sent = re.sub("& quot ;","&quot;",ref_tokenized[i])
		#Split each sentence into words, and format for tagging
		for word in hyp1_sent.split():
			hyp1_tagging.append(word+'\t0\n')
		for word in hyp2_sent.split():
			hyp2_tagging.append(word+'\t0\n')
		for word in ref_sent.split():
			ref_tagging.append(word+'\t0\n')
		#Add a blank line at the end of each sentence		
		hyp1_tagging.append('\n')
		hyp2_tagging.append('\n')
		ref_tagging.append('\n')
	#Write formatted data to file
	f = open('hyp1.tagging','w')
	for line in hyp1_tagging:
		f.write(line)
	f.close()
	f = open('hyp2.tagging','w')
	for line in hyp2_tagging:
		f.write(line)
	f.close()
	f = open('ref.tagging','w')
	for line in ref_tagging:
		f.write(line)
	f.close()
	return 0

if __name__ == '__main__':
	sys.exit(main(sys.argv))
