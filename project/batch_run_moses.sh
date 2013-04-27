#!/bin/bash
#batch_run_moses.sh
#Weston Feely
#4/26/13

echo "Running Moses testing on EGY-MADA-NP..."
./run_moses.sh egymadanp
echo "Running Moses testing on LEV-MADA-NP..."
./run_moses.sh levmadanp
echo "Running Moses testing on EGYLEV-MADA-NP..."
./run_moses.sh egylevmadanp
