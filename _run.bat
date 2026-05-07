@echo off
echo ==========================================
echo   Lancement de MTG Market Service
echo ==========================================
echo.

:: On se place dans le dossier du projet si le script est lance d'ailleurs
cd /d "%~dp0"

:: Lancement du service Collection (Port 8081)
echo Demarrage de MTG Collection Service...
start "MTG Collection Service" mvn -pl mtg-collection-service spring-boot:run

:: Lancement du service Market (Port 8082)
echo Demarrage de MTG Market Service...
start "MTG Market Service" mvn -pl mtg-market-service spring-boot:run

echo.
echo Les deux services sont en cours de demarrage.
echo L'interface sera bientot disponible sur : http://localhost:8081/ui/collection
echo.

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERREUR] Le service n'a pas pu demarrer.
    pause
)
