@echo off

docker-compose up -d


timeout /t 10

start "Klient 1" java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar
start "Klient 2" java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar
start "Klient 3" java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar
start "Klient 4" java -jar target/TPO7_KP_S34754-1.0-SNAPSHOT-jar-with-dependencies.jar