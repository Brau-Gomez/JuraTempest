@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."
set "COMPOSE_FILE=%PROJECT_DIR%\docker-compose.yaml"

where docker >nul 2>nul
if errorlevel 1 (
    echo Docker no esta instalado o no esta disponible en PATH.
    exit /b 1
)

if not exist "%COMPOSE_FILE%" (
    echo No se encontro docker-compose.yaml en: %COMPOSE_FILE%
    exit /b 1
)

echo Borrando contenedores e imagenes del proyecto JuraTempest...
docker compose -f "%COMPOSE_FILE%" down --rmi all --remove-orphans
if errorlevel 1 exit /b 1

echo Listo. No se borraron volumenes ni bases de datos.
endlocal
