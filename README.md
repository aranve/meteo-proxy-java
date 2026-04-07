# Read Me First

The meteoproxy app contains a single endpoint for fetching current weather by latitude and longitude.

Swagger UI documentation:
http://localhost:8080/swagger-ui/index.html

App is secured with basic auth for demo purposes only: user/password

## Local deployment process

### Docker image
`docker build -t meteoproxy-java .`

`docker tag meteoproxy-java:latest aranve/meteoproxy-java:latest`

`docker push aranve/meteoproxy-java:latest`

### K8s deployment

`kubectl apply -f k8s/deployment.yaml`

`kubectl get pods`

`kubectl port-forward pod/<pod-name> 8080:8080`
