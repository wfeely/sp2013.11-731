#!/bin/bash
#batch_run_moses.sh
#Weston Feely
#4/9/13

echo "Running Moses training and tuning on EGY..."
./run_moses.sh egy
echo "Running Moses training and tuning on LEV..."
./run_moses.sh lev
echo "Running Moses training and tuning on EGYLEV..."
./run_moses.sh egylev
