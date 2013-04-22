Egyptian Arabic-to-English Translation Course Project
Serena Jeblee & Weston Feely
4/21/13

Papers:
- CCB paper (Zbib et al)
- Egyptian segmenter paper (Mohamed et al)
- Iraqi segmenter paper (Yang et al)
- MADA paper (Habash et al)
- CALIMA paper (Habash et al)

Project plan:

(1) Translate using Moses
(2) Translate using Moses, analyzing data with MADA
(3) Translate using Moses, analyzing data with our own analyzer, and converting Levantine morphology to Egyptian morphology

Baseline:	(1)	EGY --> Moses --> BLEU
			LEV --> Moses --> BLEU
			EGYLEV --> Moses --> BLEU
MADA:		(2)	EGY --> MADA --> Moses --> BLEU
			LEV --> MADA --> Moses --> BLEU
			EGYLEV --> MADA --> Moses --> BLEU
Our Work:	(3)	EGY --> Our Analyzer --> Moses --> BLEU
			LEV --> LEV-to-EGY Converter --> Our Analyzer --> Moses --> BLEU
			EGYLEV --> LEV-to-EGY Converter --> Our Analyzer --> Moses --> BLEU
To Do List:
- (Done) Read CCB paper - Serena & Wes
- (Done) Make script to divide EGY, LEV, EGYLEV data into train/dev/test sets - Wes
- (Done) Linguistic analysis of Egyptian & Levantine morphosyntax - Serena
- (Done) Download Moses, get familiar with usage, set up script to run on each data set - Wes
- (Done) Read MADA paper, download MADA, get familiar with usage - Serena
- (Done) Run Moses to get initial baseline (1) - Wes
- (Done) Run MADA on each data set - Serena
- (Done) Run Moses to get MADA baseline (2) - Wes
- (In Progress) Do Levantine & English data pre-processing - Serena
	- (Done) Alif and ya normalization: normalifya.sh
	- (Done) Lexical replacement rules
	- WH-movement reordering
- (In Progress) Implement our own Egyptian analyzer in FOMA - Wes & Serena
	- (Done) Make Lexicon files for Egyptian closed-class words
	 	Currently: Pron, Prep
	- (Done) Generate Egyptian roots for open-class words, add to lexicon files
	 	Currently: N, V, Adj, Adv
	- (Done) Generate additional stems, based on roots and verbal morphology
	- (Done) Write affix analysis into open-class lexicon files
	- (Done) Add color adjectives to adjective lexicon
	- Add verb conjugations to verb lexicon
	- (Done) Learn how to run FOMA analyzer on a data file
	- Get precision-recall on dev set, update analysis accordingly
	- Get precision-recall on test set
- Run Levantine pre-processor and FOMA analyzer on each data set - Serena
- Run Moses to get final condition (3)
