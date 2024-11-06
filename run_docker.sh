#!/bin/bash

#build image locally
docker build -t thermostat-enhancer:latest .

#run the container on port 9080
docker run --name="thermostat-enhancer" -p9080:8080 thermostat-enhancer