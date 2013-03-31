#!/bin/bash
#textproc.sh
#Weston Feely
#3/30/13

#Do text processing on raw data, tokenize Arabic text
./preprocess.py ../data/levantine_data.txt ../data/levcorpus.lev ../data/levcorpus.en

#Tokenize English text
~/github/mosesdecoder/scripts/tokenizer/tokenizer.perl -l en < ~/github/sp2013.11-731/project/data/levcorpus.en > ~/github/sp2013.11-731/project/data/levcorpus.en.tok

#Fix English tokenization issues
./fix_en_tok.py ../data/levcorpus.en.tok ../data/levcorpus.en

#Randomly split data into training, dev, and test sets
./split_data.py ../data/egycorpus.egy ../data/egycorpus.en egy
./split_data.py ../data/levcorpus.lev ../data/levcorpus.en lev
