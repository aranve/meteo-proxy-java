# Read Me First

The meteoproxy app contains a single endpoint for fetching current weather by latitude and longitude.

Swagger UI documentation:
http://localhost:8080/swagger-ui/index.html

App is secured with basic auth: user/password

## Local deployment process

### Docker image

Build the Docker image:
```shell
docker build -t meteoproxy-java:1.0.0-SNAPSHOT .
```

Tag the image for Docker Hub:
```shell
docker tag meteoproxy-java:1.0.0-SNAPSHOT aranve/meteoproxy-java:1.0.0-SNAPSHOT
```

Push to Docker Hub:
```shell
docker push aranve/meteoproxy-java:1.0.0-SNAPSHOT
```


### K8s deployment

Update the image version in `k8s/deployment.yaml` to match the version you want to deploy:
```yaml
image: aranve/meteoproxy-java:1.0.0-SNAPSHOT
```

Apply the deployment:
```shell
kubectl apply -f k8s/deployment.yaml
```

Check pods status:
```shell
kubectl get pods
```

Port forward to access the app:
```shell
kubectl port-forward pod/<pod-name> 8080:8080
```

To update to a new version:
1. Build and push the new Docker image with the new version tag
2. Update the image version in `k8s/deployment.yaml`
3. Apply the changes: `kubectl apply -f k8s/deployment.yaml`
4. Monitor rollout: `kubectl rollout status deployment/meteoproxy-java-deployment`

