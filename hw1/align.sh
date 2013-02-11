#!/bin/bash

echo "compiling..."
javac Align.java

echo "getting german counts..."
python get_german_counts.py 30150 german_counts.txt

echo "aligning..."
java -Xms2500m -Xmx2500m Align data/dev-test-train.de-en data/dev.align german_counts.txt

echo "checking..."
./check < output.txt > output.al

echo "grading..."
./grade -n 5 < output.al

echo "done."

