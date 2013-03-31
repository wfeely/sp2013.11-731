#!/bin/bash
#run_moses.sh
#Weston Feely
#3/30/13

#Copy data files to moses folder
#cp data/egytrain.egy ~/github/mosesdecoder/corpus
#cp data/egytrain.en ~/github/mosesdecoder/corpus

#Make arpa language model for moses
#ngram-count -text ~/github/mosesdecoder/corpus/egytrain.en -lm ~/github/mosesdecoder/corpus/en.arpa

#Convert LM to binary format
#~/mosesdecoder/bin/build_binary ~/github/mosesdecoder/corpus/en.arpa ~/github/mosesdecoder/corpus/en.binlm

cd ~/github/mosesdecoder/working/

#Run Moses translation model training
#nohup nice ~/github/mosesdecoder/scripts/training/train-model.perl -root-dir train -corpus ~/github/mosesdecoder/corpus/egytrain -f egy -e en -alignment grow-diag-final-and -reordering msd-bidirectional-fe -lm 0:3:$HOME/github/mosesdecoder/corpus/en.binlm:8 -external-bin-dir ~/github/mosesdecoder/tools/ >& ~/github/mosesdecoder/working/training.out &

#Run Moses tuning for translation model
#nohup nice ~/github/mosesdecoder/scripts/training/mert-moses.pl ~/github/mosesdecoder/corpus/egytrain.egy ~/github/mosesdecoder/corpus/egytrain.en ~/github/mosesdecoder/bin/moses train/model/moses.ini --mertdir ~/github/mosesdecoder/bin/ &> mert.out &

#Run Moses using tuned translation model
~/github/mosesdecoder/bin/moses -f ~/github/mosesdecoder/working/mert-work/moses.ini
