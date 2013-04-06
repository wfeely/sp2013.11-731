#!/bin/bash
#Weston Feely & Serena Jeblee

#Generate training instances for logisitic regression
./rerank -b 0 > instances.txt

#Perform logistic regression to get best weight vector
#...

#Get best hypothesis for each sentence
./rerank -b 1 > output.txt
