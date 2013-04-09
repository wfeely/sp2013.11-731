#!/bin/bash
#run_moses.sh
#Weston Feely
#4/9/13

#Check for required arg
if [[ -z "$1" ]]; then
    echo "Usage: ./run_moses.sh language_prefix"
    exit 1
fi
lang=$1

#Get start time and print current time
T="$(date +%s)"
date

#Set up data filenames
train=${lang}train
train_ar=${lang}train.${lang}
train_en=${lang}train.en
dev_ar=${lang}dev.${lang}
dev_en=${lang}dev.en
test=${lang}test
test_ar=${lang}test.${lang}
test_en=${lang}test.en

################TRAINING################
echo "Moses Training phase (approx 10 mins egy)"
#Copy data files to moses corpus folder
mkdir -p ~/github/mosesdecoder/corpus
cp -r data/${lang} ~/github/mosesdecoder/corpus/${lang}

#Make arpa language model for moses
echo "Creating ngram LM based on English side of training data..."
ngram-count -unk -text ~/github/mosesdecoder/corpus/${lang}/${train_en} -lm ~/github/mosesdecoder/corpus/${lang}/en.arpa

#Convert LM to binary format
~/github/mosesdecoder/bin/build_binary ~/github/mosesdecoder/corpus/${lang}/en.arpa ~/github/mosesdecoder/corpus/${lang}/en.binlm

mkdir -p ~/github/mosesdecoder/working
mkdir -p ~/github/mosesdecoder/working/${lang}

cd ~/github/mosesdecoder/working/${lang} # keep this line uncommented for all runs

#Run Moses translation model training
echo "Training models using training set..."
nohup nice ~/github/mosesdecoder/scripts/training/train-model.perl -root-dir train -corpus ~/github/mosesdecoder/corpus/${lang}/${train} -f ${lang} -e en -alignment grow-diag-final-and -reordering msd-bidirectional-fe -lm 0:3:$HOME/github/mosesdecoder/corpus/${lang}/en.binlm:8 -external-bin-dir ~/github/mosesdecoder/tools/ -cores 2 >& ~/github/mosesdecoder/working/${lang}/training.out #&

################TUNING################
echo "Moses Tuning phase (approx 1.5 hr egy)"
#Run Moses tuning for translation model
echo "Tuning models using dev set..."
nohup nice ~/github/mosesdecoder/scripts/training/mert-moses.pl ~/github/mosesdecoder/corpus/${lang}/${dev_ar} ~/github/mosesdecoder/corpus/${lang}/${dev_en} ~/github/mosesdecoder/bin/moses train/model/moses.ini --mertdir ~/github/mosesdecoder/bin/ --decoder-flags="-threads 4" &> mert.out #&

#Binarise phrase table and lexical reordering models
echo "Binarising phrase table and lexical reordering models..."
mkdir -p ~/github/mosesdecoder/working/${lang}/binarised-model
~/github/mosesdecoder/bin/processPhraseTable -ttable 0 0 train/model/phrase-table.gz -nscores 5 -out binarised-model/phrase-table
~/github/mosesdecoder/bin/processLexicalTable -in train/model/reordering-table.wbe-msd-bidirectional-fe.gz -out binarised-model/reordering-table

#TODO: After training and tuning, edit ~/github/mosesdecoder/working/${lang}/train/model/moses.ini to point to binarised files
#Replace:
#0 0 0 5 /home/hermes/github/mosesdecoder/working/${lang}/train/model/phrase-table.gz
#With:
#1 0 0 5 /home/hermes/github/mosesdecoder/working/${lang}/binarised-model/phrase-table
#Replace:
#0-0 wbe-msd-bidirectional-fe-allff 6 /home/hermes/github/mosesdecoder/working/${lang}/train/model/reordering-table.wbe-msd-bidirectional-fe.gz
#With:
#0-0 wbe-msd-bidirectional-fe-allff 6 /home/hermes/github/mosesdecoder/working/${lang}/binarised-model/reordering-table

################TESTING################
#echo "Moses Testing phase (approx 7 min egy)"
#Filter model for testing
#echo "Filtering model for testing..."
#~/github/mosesdecoder/scripts/training/filter-model-given-input.pl filtered-${test} mert-work/moses.ini ~/github/mosesdecoder/corpus/${lang}/${test_ar} -Binarizer ~/github/mosesdecoder/bin/processPhraseTable

#Translate test set and get BLEU score
#echo "Translating test set..."
#nohup nice ~/github/mosesdecoder/bin/moses -f ~/github/mosesdecoder/working/${lang}/filtered-${test}/moses.ini < ~/github/mosesdecoder/corpus/${lang}/${test_ar} > ~/github/mosesdecoder/working/${lang}/${test}.translated.en 2> ~/github/mosesdecoder/working/${lang}/${test}.out #&
#echo "Scoring translation using BLEU..."
#~/github/mosesdecoder/scripts/generic/multi-bleu.perl -lc ~/github/mosesdecoder/corpus/${lang}/${test_en} < ~/github/mosesdecoder/working/${lang}/${test}.translated.en > ~/github/mosesdecoder/working/${lang}/${lang}_results.txt
#echo "BLEU score written to ${lang}_results.txt"

#Let user know we've finished, and print time elapsed
echo "Done!"
T="$(($(date +%s)-T))"
printf "Time elapsed: %02d:%02d:%02d:%02d\n" "$((T/86400))" "$((T/3600%24))" "$((T/60%60))" "$((T%60))"
date
