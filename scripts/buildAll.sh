#!/bin/bash
scriptDir=$(dirname $0)

IMAGE_NAME=quay.io/ibmcase/eda-store-simulator
TAG=latest
mvn -U clean package -pl :store-simulator-frontend
cp  -r frontend/target/dist backend/src/main/resources/META-INF/resources
mvn -U clean package -pl :store-simulator-backend -DskipTests
docker build -f backend/src/main/docker/Dockerfile.jvm -t ${IMAGE_NAME}:${TAG} backend
docker push ${IMAGE_NAME}:${TAG}

