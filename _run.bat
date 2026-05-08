@echo off
echo ==========================================
echo   Lancement de MTG Copilot
echo ==========================================
echo.

:: On se place dans le dossier du projet si le script est lance d'ailleurs
cd /d "%~dp0"

:: Rappel pour les dépendances externes
echo [INFO] Assurez-vous que PostgreSQL est lance (ex: docker-compose up -d)
echo [INFO] Assurez-vous que Ollama est lance pour les fonctions IA.
echo.

:: Lancement du Gateway (Port 8080) - Point d'entree principal
echo Demarrage de MTG Gateway...
start "MTG Gateway" mvn -pl mtg-gateway spring-boot:run

:: Lancement du service Collection (Port 8081)
echo Demarrage de MTG Collection Service...
start "MTG Collection Service" mvn -pl mtg-collection-service spring-boot:run

:: Lancement du service Market (Port 8082)
echo Demarrage de MTG Market Service...
start "MTG Market Service" mvn -pl mtg-market-service spring-boot:run

:: Lancement du service AI Orchestrator (Port 8083)
echo Demarrage de MTG AI Orchestrator...
start "MTG AI Orchestrator" mvn -pl mtg-ai-orchestrator spring-boot:run

echo.
echo Tous les services sont en cours de demarrage.
echo L'interface sera bientot disponible sur le Gateway :
echo http://localhost:8080/ui/collection
echo.
pause
