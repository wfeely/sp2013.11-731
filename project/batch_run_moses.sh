#!/bin/bash
#batch_run_moses.sh
#Weston Feely
#4/16/13

echo "Running Moses testing on EGYMADA..."
./run_moses.sh egymada
echo "Running Moses testing on LEVMADA..."
./run_moses.sh levmada
echo "Running Moses testing on EGYLEVMADA..."
./run_moses.sh egylevmada
