#!/bin/bash

docker build -t open-lmis --rm --build-arg ENV=local . --no-cache
