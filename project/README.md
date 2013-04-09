Egyptian Arabic-to-English Translation Course Project
Serena Jeblee & Weston Feely
4/9/13

Papers:
- CCB paper (Zbib et al)
- Egyptian segmenter paper (Mohamed et al)
- Iraqi segmenter paper (Yang et al)
- MADA paper (Habash et al)
- CALIMA paper (Habash et al)

Project plan:

(1) Translate using Moses
(2) Translate using Moses, analyzing data with MADA
(3) Translate using Moses, analyzing data with CALIMA
(4) Translate using Moses, analyzing data with our own analyzer, and converting Levantine morphology to Egyptian morphology

Baseline:	(1)	EGY --> Moses --> BLEU
			LEV --> Moses --> BLEU
			EGYLEV --> Moses --> BLEU
MADA:		(2)	EGY --> MADA --> Moses --> BLEU
			LEV --> MADA --> Moses --> BLEU
			EGYLEV --> MADA --> Moses --> BLEU
CALIMA:		(3)	EGY --> CALIMA --> Moses --> BLEU
			LEV --> CALIMA --> Moses --> BLEU
			EGYLEV --> CALIMA --> Moses --> BLEU
Our Work:	(4)	EGY --> Our Analyzer --> Moses --> BLEU
			LEV --> LEV-to-EGY Converter --> Our Analyzer --> Moses --> BLEU
			EGYLEV --> LEV-to-EGY Converter --> Our Analyzer --> Moses --> BLEU
To Do List:
- (Done) Read CCB paper - Serena & Wes
- (Done) Make script to divide EGY, LEV, EGYLEV data into train/dev/test sets - Wes
- (In Progress) Linguistic analysis of Egyptian & Levantine morphosyntax - Serena
- (Done) Download Moses, get familiar with usage, set up script to run on each data set - Wes
- (Done) Read MADA paper, download MADA, get familiar with usage - Serena
- (Done) Run Egyptian text through MADA - Serena
- (In Progress) Do Levantine & English data pre-processing - Serena
	- (DONE) Alif and ya normalization: normalifya.sh
	- ...
- (In Progress) Run Moses to get initial baseline (1) - Wes
- (In Progress) Run MADA on each data set - Serena
- Run Moses to get MADA baseline (2) - Wes
- Run CALIMA on each data set - Serena
- Run Moses to get CALIMA baseline (3) - Wes
- Implement our own Egyptian analyzer in FOMA - Wes & Serena
- Run Moses to get final condition (4)
