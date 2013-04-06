#!/bin/bash
#Weston Feely & Serena Jeblee

#Generate training instances for logisitic regression
./rerank -b 0 > instances.txt

#Perform logistic regression to get best weight vector
javac LR.java
java -Xms2300m -Xmx2300m LR instances.txt

#Get best hypothesis for each sentence
./rerank -b 1 > output.txt

#Compute BLEU score
./score-bleu < output.txt
