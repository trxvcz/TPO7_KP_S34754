#!/bin/bash

docker-compose up -d

echo "10s dla dockera"
sleep 10

java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar &
java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar &
java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar &
java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar &

