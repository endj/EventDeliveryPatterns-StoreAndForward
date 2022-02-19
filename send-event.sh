#!/bin/sh

curl -H 'Content-Type: application/json' -d "{ \"data\": \"$(uuidgen)\", \"id\": \"$(uuidgen)\" }" localhost:8080/forwarder/event