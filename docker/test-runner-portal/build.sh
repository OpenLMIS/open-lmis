#!/bin/bash
FILE=./installers/jdk-7u80-linux-x64.tar.gz

if [ ! -f "$FILE" ]; then
	wget -O installers/jdk-7u80-linux-x64.tar.gz http://52.69.16.156:8000/installers/jdk-7u80-linux-x64.tar.gz
fi

docker build -t test-runner-portal --rm . --no-cache
