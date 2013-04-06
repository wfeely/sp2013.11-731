#!/bin/bash
#Weston Feely & Serena Jeblee

#Get start time
T="$(date +%s)"

#Generate training instances for logisitic regression
#./rerank -b 0 -x 100 > instances.txt

#Perform logistic regression to get best weight vector
#javac LR.java
#java -Xms2300m -Xmx2300m LR instances.txt 100 5 # instances_file, num_samples, num_feats

#Get best hypothesis for each sentence
./rerank -b 1 > output.txt

#Compute BLEU score
./score-bleu < output.txt

#Print time elapsed
T="$(($(date +%s)-T))"
printf "Time elapsed: %02d:%02d:%02d:%02d\n" "$((T/86400))" "$((T/3600%24))" "$((T/60%60))" "$((T%60))"
