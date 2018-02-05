#!/bin/sh
kubectl delete -f /home/centos/egar-location-api/scripts/kube/location-api-deployment.yaml
kubectl delete -f /home/centos/egar-location-api/scripts/kube/location-api-service.yaml

