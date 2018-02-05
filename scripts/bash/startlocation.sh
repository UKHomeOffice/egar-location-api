#!/bin/sh
echo Starting Location-API version: $LOCATION_API_VER
rm -rf /home/centos/egar-location-api/scripts/kube/location-api-deployment.yaml; envsubst < "/home/centos/egar-location-api/scripts/kube/location-api-deployment-template.yaml" > "/home/centos/egar-location-api/scripts/kube/location-api-deployment.yaml"
kubectl create -f /home/centos/egar-location-api/scripts/kube/location-api-deployment.yaml
kubectl create -f /home/centos/egar-location-api/scripts/kube/location-api-service.yaml

