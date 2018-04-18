export JSDIR ?= $(shell pwd)/js

build: DUMMY
	docker build -t kt-webapp .
	pushd js && docker build -t kt-webapp-hotreload . && popd

start-dev: build
	kubectl config use-context docker-for-desktop
	kubectl delete deployment,service,ingress -l appgroup=kt-webapp
	kubectl create -f ./deployment/ingress.yaml
	kubectl create -f ./deployment/kt-webapp.yaml
	kubectl create -f ./deployment/default-backend.yaml
	kubectl create -f ./deployment/nginx-controller.yaml
	cat ./deployment/kt-webapp-hotreload.yaml | sed -e 's|{{JSDIR}}|${JSDIR}|g' | kubectl apply -f -

DUMMY: