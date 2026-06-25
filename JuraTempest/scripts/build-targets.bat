@echo off
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."
set "SERVICES=eureka_server api-gateway ms_usuarios ms_maquinas ms-horarios ms_reservas ms_fidelizacion ms_notificaciones ms_pagos ms_promociones ms_auth ms_mantenimiento ms_eventos_torneos"

for %%S in (%SERVICES%) do (
    set "SERVICE_DIR=%PROJECT_DIR%\%%S"

    if not exist "!SERVICE_DIR!" (
        echo [SKIP] No existe el servicio: %%S
    ) else (
        if not exist "!SERVICE_DIR!\mvnw.cmd" (
            echo [ERROR] %%S no tiene mvnw.cmd
            exit /b 1
        )

        if exist "!SERVICE_DIR!\target" (
            echo [CLEAN] Borrando jars antiguos de %%S
            del /q "!SERVICE_DIR!\target\*.jar" >nul 2>nul
            del /q "!SERVICE_DIR!\target\*.jar.original" >nul 2>nul
        )

        echo [BUILD] Generando jar actualizado para %%S
        pushd "!SERVICE_DIR!" >nul
        call mvnw.cmd -DskipTests package
        if errorlevel 1 (
            popd >nul
            exit /b 1
        )
        popd >nul
    )
)

echo Targets actualizados.
endlocal
