#!/bin/bash
#batch_run_moses.sh
#Weston Feely
#4/10/13

echo "Running Moses training and tuning on EGYMADA..."
./run_moses.sh egymada
echo "Running Moses training and tuning on LEVMADA..."
./run_moses.sh levmada
echo "Running Moses training and tuning on EGYLEVMADA..."
./run_moses.sh egylevmada
