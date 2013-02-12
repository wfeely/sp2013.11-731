#!/bin/bash

#echo "compiling..."
#javac Align.java

#echo "getting german counts..."
#python get_german_counts.py 100000 german_counts.txt

#echo "aligning..."
#java -Xms6144M -Xmx6144M Align data/dev-test-train.de-en data/dev.align german_counts.txt

echo "checking..."
./check < output.txt > output.al

echo "grading..."
./grade -n 5 < output.al

echo "done."
