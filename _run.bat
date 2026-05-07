@echo off
echo ==========================================
echo   Lancement de MTG Market Service
echo ==========================================
echo.

:: On se place dans le dossier du projet si le script est lance d'ailleurs
cd /d "%~dp0"

:: Commande Maven pour lancer le module specifique
mvn -pl mtg-market-service spring-boot:run

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERREUR] Le service n'a pas pu demarrer.
    pause
)
