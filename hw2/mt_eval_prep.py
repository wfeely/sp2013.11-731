#!/usr/bin/python
#mt_eval_prep.py
#Weston Feely
#3/2/13
import sys

def main(args):
	hyp1_tagged = open('hyp1.tagging.pred').readlines()
	hyp2_tagged = open('hyp2.tagging.pred').readlines()
	ref_tagged = open('ref.tagging.pred').readlines()
	hyp1_reformat = []
	hyp2_reformat = []
	ref_reformat = []
	combined_tagged = []	
	buff = []
	#Loop through lines in hyp1 tagged words 
	for line in hyp1_tagged:
		#If line is space, sentence has ended, join together tagged words in buffer and append to hyp1_reformat
		if line.isspace():
			hyp1_reformat.append(' '.join(buff))
			buff = []
			continue
		#Add next tagged word to buffer
		buff.append('_'.join(line.strip().split('\t')))
	buff = []	
	#Loop through lines in hyp2 tagged words 
	for line in hyp2_tagged:
		#If line is space, sentence has ended, join together tagged words in buffer and append to hyp1_reformat
		if line.isspace():
			hyp2_reformat.append(' '.join(buff))
			buff = []
			continue
		#Add next tagged word to buffer
		buff.append('_'.join(line.strip().split('\t')))
	buff = []
	#Loop through lines in ref tagged words 
	for line in ref_tagged:
		#If line is space, sentence has ended, join together tagged words in buffer and append to hyp1_reformat
		if line.isspace():
			ref_reformat.append(' '.join(buff))
			buff = []
			continue
		#Add next tagged word to buffer
		buff.append('_'.join(line.strip().split('\t')))
	#Loop through lines in each reformatted file
	for i in xrange(0,len(hyp1_reformat)):	
		combined_tagged.append(hyp1_reformat[i]+' ||| '+hyp2_reformat[i]+' ||| '+ref_reformat[i]+'\n')
	#Write reformatted tagged test data to file
	f = open('test.tagged','w')
	for line in combined_tagged:
		f.write(line)
	f.close()
	return 0

if __name__ == '__main__':
	sys.exit(main(sys.argv))
