#!/bin/bash
scriptDir=$(dirname $0)

IMAGE_NAME=quay.io/ibmcase/eda-store-simulator
mvn -U clean install -pl :store-simulator-frontend
mvn -U clean install -pl :store-simulator-backend -DskipTests
docker build -f backend/src/main/docker/Dockerfile.jvm -t ${IMAGE_NAME} backend
docker push ${IMAGE_NAME}
