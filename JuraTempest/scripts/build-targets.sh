#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

SERVICES=(
    "eureka_server"
    "api-gateway"
    "ms_auth"
    "ms_usuarios"
    "ms_maquinas"
    "ms-horarios"
    "ms_reservas"
    "ms_fidelizacion"
    "ms_notificaciones"
    "ms_pagos"
    "ms_promociones"
    "ms_mantenimiento"
    "ms_eventos_torneos"
)

for SERVICE in "${SERVICES[@]}"; do
    SERVICE_DIR="$PROJECT_DIR/$SERVICE"

    if [ ! -d "$SERVICE_DIR" ]; then
        echo "[SKIP] No existe el servicio: $SERVICE"
        continue
    fi

    if [ ! -x "$SERVICE_DIR/mvnw" ]; then
        echo "[ERROR] $SERVICE no tiene mvnw ejecutable"
        exit 1
    fi

    if [ -d "$SERVICE_DIR/target" ]; then
        echo "[CLEAN] Borrando jars antiguos de $SERVICE"
        rm -f "$SERVICE_DIR"/target/*.jar "$SERVICE_DIR"/target/*.jar.original
    fi

    echo "[BUILD] Generando jar actualizado para $SERVICE"
    (cd "$SERVICE_DIR" && ./mvnw -DskipTests package)
done

echo "Targets actualizados."
