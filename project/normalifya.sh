#!/bin/bash
#sjeblee@cs.cmu.edu
#Normalize alif and ya

#set file to convert
file="inputfile"

sed 's/أ/ا/g' $file |
sed 's/إ/ا/g' |
sed 's/آ/ا/g' |
sed 's/ي /ى /g' |
sed 's/ي$/ى/g' |
sed 's/َ//g' | 
sed 's/ُ//g' | 
sed 's/ِ//g' | 
sed 's/ً//g' | 
sed 's/ٌ//g' | 
sed 's/ٍ//g' |
sed 's/ْ//g' | 
sed 's/ّ//g' > temp #change output dir
