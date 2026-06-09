@echo off
echo Uruchamianie brokera RabbitMQ...
docker-compose up -d

echo Czekam na start brokera...
timeout /t 10

echo Uruchamianie klientow czatu...
start "Klient 1" java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar
start "Klient 2" java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar
start "Klient 3" java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar
start "Klient 4" java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar