#!/bin/bash
scriptDir=$(dirname $0)

IMAGE_NAME=quay.io/ibmcase/eda-store-simulator
if [[ $# -eq 1 ]]
then
  TAG=$1
else
  TAG=latest
fi

mvn -U clean package -pl :store-simulator-frontend
cp  -r frontend/target/dist backend/src/main/resources/META-INF/resources
mvn -U clean package -pl :store-simulator-backend -DskipTests
docker build -f backend/src/main/docker/Dockerfile.jvm -t ${IMAGE_NAME}:${TAG} backend
docker tag  ${IMAGE_NAME}:${TAG}   ${IMAGE_NAME}:latest
#docker push ${IMAGE_NAME}:${TAG}
#docker push ${IMAGE_NAME}:latest