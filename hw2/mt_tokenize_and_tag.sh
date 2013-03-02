#!/bin/sh
#mt_tokenize_and_tag.sh
#Weston Feely
#3/2/13

#Split test data into separate files
#echo 'Splitting test data for tokenization...'
#./mt_tok_prep.py
#Tokenize each test data file
#echo 'Tokenizing...'
#../scripts/tokenizer.sed < hyp1.txt > hyp1.tokenized
#../scripts/tokenizer.sed < hyp2.txt > hyp2.tokenized
#../scripts/tokenizer.sed < ref.txt > ref.tokenized
#Prepare tokenized test data for tagging
echo 'Preparing data for tagging...'
./mt_tag_prep.py
#Tag test data
echo 'Tagging hyp1 sentences...'
../scripts/run_tagger.sh hyp1.tagging
echo 'Tagging hyp2 sentences...'
../scripts/run_tagger.sh hyp2.tagging
echo 'Tagging ref sentences...'
../scripts/run_tagger.sh ref.tagging
#Reorganize tagged test data back into one file
echo 'Reorganizing tagged data...'
./mt_eval_prep.py
echo 'Done.'
