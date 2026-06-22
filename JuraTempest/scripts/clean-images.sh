#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
COMPOSE_FILE="$PROJECT_DIR/docker-compose.yaml"

if ! command -v docker >/dev/null 2>&1; then
    echo "Docker no esta instalado o no esta disponible en PATH."
    exit 1
fi

if [ ! -f "$COMPOSE_FILE" ]; then
    echo "No se encontro docker-compose.yaml en: $COMPOSE_FILE"
    exit 1
fi

echo "Borrando contenedores e imagenes del proyecto JuraTempest..."
docker compose -f "$COMPOSE_FILE" down --rmi all --remove-orphans
echo "Listo. No se borraron volumenes ni bases de datos."
