# Async IO and its role in autoscaling

## Trying out async vs sync IO on a local Kubernetes(K8s) setup. 

### Clone this repo ahead of the below steps

### 1. Install docker

### 2. Setup minikube on your workstation

Please follow the steps in https://minikube.sigs.k8s.io/docs/start/ to setup minikube

The below steps were tried on a Dell Precision laptop running Linux Mint 20.1 to run minikube on docker.
```
 curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
 sudo install minikube-linux-amd64 /usr/local/bin/minikube
 minikube start --kubernetes-version=v1.22.0 --cpus=4 --memory=8g
```
At this point we have a functional K8s cluster with 1 node running on the workstation and we can use the Kubernetes command-line tool(kubectl) to interact with the K8s cluster

```
Eg.
kubectl get pods --all-namespaces
kubectl get all --all-namespaces
```

### 3. Setup monitoring on your local cluster
We can use prometheus operator to monitor the cluster. The operator requires multiple CRDs and services to work and so it is best installed using helm charts.

Setup helm on your workstation by following the steps in https://helm.sh/docs/intro/install/. The steps involve downloading the helm binary to the /usr/local/bin folder so that it is accessible on shell.

```
cd monitoring
kubectl create namespace monitoring
kubectl apply -f metricsserver.yaml 
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
helm install --namespace monitoring monitoring-stack prometheus-community/kube-prometheus-stack --version=19.2.2 -f conf.yaml
The stack installs grafana but it is setup as a cluster IP and we have to edit the service to make it a NodePort for accessing it outside the K8s cluster. Please note that all the monitoring components are deployed to a different namespace (monitoring) and so the namespace has to be provided while issuing kubectl commands
kubectl get services -n monitoring to see the services in the monitoring namespace. Copy the name of the grafana service
kubectl edit svc grafana-service-name -n monitoring (should be monitoring-stack-grafana if you have followed above steps) 
Edit the service type from ClusterIP to NodePort and save the config  //this should be in the 3rd last line of the service config
kubectl get services -n monitoring //This would indicate the port (in the 32000 range) that is assigned for the Grafana NodePort service
minikube ip //to get the IP address exposed by minikube
Navigate to http://minikubeip:nodeport on the browser to see the grafana dashboards. The default credentials are admin/prom-operator
Use the Import Dashboard option on grafana to import the CPUmetrics.json dashboard to grafana. The custom dashboard is used to view CPU metrics at a more granular rate of change. 
```

### 4. Source code

This repo includes an echo api that can be built by the following steps. The echo api exposes /echo/:msg/after/:time and API would respond back with the msg after the time interval. For eg. if you want the service to return hello after 50 milliseconds - curl /echo/hello/after/50

Build the echo API
```
 cd src/EchoAPI
 docker build --tag echoapi:0.0.1 .
 minikube image load echoapi:0.0.1  //To ensure that the locally created image is available in the minikube environment
```

There are 2 test services (SyncAPI & AsyncAPI) that invoke the echo API and return the response from echo to the caller. The SyncAPI is a spring boot app and the AsyncAPI is a Playframework app.

SyncAPI (Spring boot)
exposes /spring/syncapi/:delaytime //The delay time is sent to echo API

AsyncAPI (Play framework)
exposes /play/asyncapi:delaytime and /play/syncapi:delaytime //The delay time is sent to the echo API

The echo API base path in the code is set to invoke the echo API in a Kubernetes env after these have been deployed on K8s (steps below).

Build the APIs
```
 cd src/SyncAPI
 mvn clean package
 docker build --tag syncapi:0.0.1 .
 minikube image load syncapi:0.0.1  //To ensure that the locally created image is available in the minikube environment

 cd ../AsyncAPI
 mvn clean package play2:dist
 docker build --tag asyncapi:0.0.1 .
 minikube image load asyncapi:0.0.1  //To ensure that the locally created image is available in the minikube environment

```

### 5. Run the APIs in kubernetes

```
cd services
kubectl apply -f echo.yaml
kubectl apply -f echo.yaml
kubectl apply -f echo.yaml

//These yamls have the K8s definitions for the echo, sync and async api services. Each service is run as a NodePort and can be accessed from outside the K8s cluster
minikube ip //This prints the IP of the minikube node
kubectl get services //This would indicate the port (in the 32000 range) that is assigned for the 3 services

curl minikubeip:nodeport/spring/syncapi/20 //This should return hello in the response after 20 milliseconds
curl minikubeip:nodeport/play/syncapi/20 //This should return hello in the response after 20 milliseconds
curl minikubeip:nodeport/play/asyncapi/20 //This should return hello in the response after 20 milliseconds
```

### 6. Running a load test on these APIs

This would show us the CPU utilizations when the above mentioned 3 APIs are invoked with the same load

ab -c 500 -n 50000 -k http://minikubeip:nodeport/spring/syncapi/20
ab -c 500 -n 50000 -k http://minikubeip:nodeport/play/syncapi/20
ab -c 500 -n 50000 -k http://minikubeip:nodeport/play/asyncapi/20
