#!/bin/bash
while [ ! -f /opt/application.properties ]; do
  sleep 1
done
java -jar webapp-0.0.1-SNAPSHOT.jar
