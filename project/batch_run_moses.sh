#!/bin/bash
#batch_run_moses.sh
#Weston Feely
#4/29/13

#echo "Running Moses testing on EGY..."
#./run_moses.sh egy
#echo "Running Moses testing on LEV..."
#./run_moses.sh lev
#echo "Running Moses testing on EGYLEV..."
#./run_moses.sh egylev

#echo "Running Moses testing on EGY-MADA-NP..."
#./run_moses.sh egymadanp
#echo "Running Moses testing on LEV-MADA-NP..."
#./run_moses.sh levmadanp
#echo "Running Moses testing on EGYLEV-MADA-NP..."
#./run_moses.sh egylevmadanp

#echo "Running Moses testing on EGY-MISH..."
#./run_moses.sh egymish
echo "Running Moses testing on LEV-MISH..."
./run_moses.sh levmish
echo "Running Moses testing on EGYLEV-MISH..."
./run_moses.sh egylevmish
