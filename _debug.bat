@echo off
echo ==========================================
echo   Lancement de MTG Copilot (MODE DEBUG)
echo ==========================================

:: Gateway - Debug Port 5004
start "MTG Gateway [DEBUG]" mvn -pl mtg-gateway spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5004"

:: Collection Service - Debug Port 5005
start "MTG Collection [DEBUG]" mvn -pl mtg-collection-service spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

:: Market Service - Debug Port 5006
start "MTG Market [DEBUG]" mvn -pl mtg-market-service spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5006"

:: AI Orchestrator - Debug Port 5007
start "MTG AI [DEBUG]" mvn -pl mtg-ai-orchestrator spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5007"

pause
