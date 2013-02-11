Serena Jeblee
Wes Feely
MT Homework 1

First we implemented IBM Model 1 using the first 15,000 sentences as training data. We then added a transition probability matrix based on the gold-aligned data following the algorithm suggested in the HMM-based alignment paper. This showed improvements over IBM Model 1 when we multiplied together the translation probabilities with these transition probabilities.

We added smoothing for the transition probabilities following the HMM paper, but instead of using the ML estimates for the smoothing factor, we used a hand-tuned constant factor. We further improved the alignments by adding a "null" token to the German sentences.

We reconfigured our translation probabilities to initialize to better values so that we could run fewer iterations of EM and still converge. Finally we ran the system on a larger dataset of 30,000 sentences and got our final result.

