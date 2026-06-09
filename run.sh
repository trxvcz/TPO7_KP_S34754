#!/bin/bash

echo "Uruchamianie brokera RabbitMQ..."
docker-compose up -d

echo "Czekam na start brokera..."
sleep 10

echo "Uruchamianie klientow czatu..."
java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar &
java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar &
java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar &
java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar &

echo "Klienci zostali uruchomieni w tle."