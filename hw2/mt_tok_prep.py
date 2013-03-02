#!/usr/bin/python
#mt_tagging_prep.py
#Weston Feely
#3/2/13
import sys

def main(args):
	hyp1 = []
	hyp2 = []
	ref = []
	#Loop through lines in test data
	for line in open('test.hyp1-hyp2-ref'):
		#Split line into hyp1, hyp2, ref sentences			
		lis = line.split('|||')
		hyp1.append(lis[0].strip()+'\n')
		hyp2.append(lis[1].strip()+'\n')
		ref.append(lis[2].strip()+'\n')
	#Write hyp1 sentences to file
	f = open('hyp1.txt','w')
	for line in hyp1:
		f.write(line)
	f.close()
	#Write hyp1 sentences to file
	f = open('hyp2.txt','w')
	for line in hyp2:
		f.write(line)
	f.close()
	#Write hyp1 sentences to file
	f = open('ref.txt','w')
	for line in ref:
		f.write(line)
	f.close()
	return 0

if __name__ == '__main__':
	sys.exit(main(sys.argv))
