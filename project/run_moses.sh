#!/bin/bash
#run_moses.sh
#Weston Feely
#4/8/13

#Check for required arg
if [[ -z "$1" ]]; then
    echo "Usage: ./run_moses.sh language_prefix"
    exit 1
fi
lang=$1

#Get start time
T="$(date +%s)"

#Set up data filenames
train=${lang}train
train_ar=${lang}train.${lang}
train_en=${lang}train.en
dev_ar=${lang}dev.${lang}
dev_en=${lang}dev.en
test=${lang}test
test_ar=${lang}test.${lang}
test_en=${lang}test.en

#Copy data files to moses folder
mkdir -p ~/github/mosesdecoder/corpus
cp data/${train_ar} ~/github/mosesdecoder/corpus
cp data/${train_en} ~/github/mosesdecoder/corpus
cp data/${dev_ar} ~/github/mosesdecoder/corpus
cp data/${dev_en} ~/github/mosesdecoder/corpus
cp data/${test_ar} ~/github/mosesdecoder/corpus
cp data/${test_en} ~/github/mosesdecoder/corpus

################TRAINING################
echo "Moses Training phase"
#Make arpa language model for moses
echo "Creating ngram LM based on English side of training data..."
ngram-count -text ~/github/mosesdecoder/corpus/${train_en} -lm ~/github/mosesdecoder/corpus/en.arpa

#Convert LM to binary format
~/mosesdecoder/bin/build_binary ~/github/mosesdecoder/corpus/en.arpa ~/github/mosesdecoder/corpus/en.binlm

mkdir -p ~/github/mosesdecoder/working
mkdir -p ~/github/mosesdecoder/working/${lang}
cd ~/github/mosesdecoder/working/${lang}

#Run Moses translation model training
echo "Training models using training set..."
nohup nice ~/github/mosesdecoder/scripts/training/train-model.perl -root-dir train -corpus ~/github/mosesdecoder/corpus/${train} -f ${lang} -e en -alignment grow-diag-final-and -reordering msd-bidirectional-fe -lm 0:3:$HOME/github/mosesdecoder/corpus/en.binlm:8 -external-bin-dir ~/github/mosesdecoder/tools/ >& ~/github/mosesdecoder/working/${lang}/training.out &

################TUNING################
echo "Moses Tuning phase"
#Run Moses tuning for translation model
echo "Tuning models using dev set..."
nohup nice ~/github/mosesdecoder/scripts/training/mert-moses.pl ~/github/mosesdecoder/corpus/${dev_ar} ~/github/mosesdecoder/corpus/${dev_en} ~/github/mosesdecoder/bin/moses train/model/moses.ini --mertdir ~/github/mosesdecoder/bin/ --decoder-flags="-threads 4" &> mert.out &

#Binarise phrase table and lexical reordering models
echo "Binarising phrase table and lexical reordering models..."
mkdir -p ~/github/mosesdecoder/working/${lang}/binarised-model
~/github/mosesdecoder/bin/processPhraseTable -ttable 0 0 train/model/phrase-table.gz -nscores 5 -out binarised-model/phrase-table
~/github/mosesdecoder/bin/processLexicalTable -in train/model/reordering-table.wbe-msd-bidirectional-fe.gz -out binarised-model/reordering-table

#TODO: Edit moses.ini to point to binarised files
#Replace:
#0 0 0 5 /home/hermes/github/mosesdecoder/working/${lang}/train/model/phrase-table.gz
#With:
#1 0 0 5 /home/hermes/github/mosesdecoder/working/${lang}/binarised-model/phrase-table
#Replace:
#0-0 wbe-msd-bidirectional-fe-allff 6 /home/hermes/github/mosesdecoder/working/${lang}/train/model/reordering-table.wbe-msd-bidirectional-fe.gz
#With:
#0-0 wbe-msd-bidirectional-fe-allff 6 /home/hermes/github/mosesdecoder/working/${lang}/binarised-model/reordering-table

################TESTING################
#echo "Moses Testing phase"
#Filter model for testing
#echo "Filtering model for testing..."
#~/github/mosesdecoder/scripts/training/filter-model-given-input.pl filtered-${test} mert-work/moses.ini ~/github/mosesdecoder/corpus/${test_ar} -Binarizer ~/github/mosesdecoder/bin/processPhraseTable

#Translate test set and get BLEU score
#echo "Translating test set..."
#nohup nice ~/github/mosesdecoder/bin/moses -f ~/github/mosesdecoder/working/${lang}/filtered-${test}/moses.ini < ~/github/mosesdecoder/corpus/${test_ar} > ~/github/mosesdecoder/working/${lang}/${test}.translated.en 2> ~/github/mosesdecoder/working/${lang}/${test}.out &
#echo "Scoring translation using BLEU..."
#~/github/mosesdecoder/scripts/generic/multi-bleu.perl -lc ~/github/mosesdecoder/corpus/${test_en} < ~/github/mosesdecoder/working/${lang}/${test}.translated.en

#Let user know we've finished, and print time elapsed
echo "Done! (No testing)"
T="$(($(date +%s)-T))"
printf "Time elapsed: %02d:%02d:%02d:%02d\n" "$((T/86400))" "$((T/3600%24))" "$((T/60%60))" "$((T%60))"
