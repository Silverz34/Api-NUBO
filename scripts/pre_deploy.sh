#!/bin/bash
SERVICE_NAME="nubo-api.service"
APP_DIR="/opt/apps/nubo-api/"
BACKUP_DIR="/opt/apps/nubo-api/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

echo ">>> [PRE-DEPLOY] INICIANDO..."

if systemctl list-unit-files | grep -q "$SERVICE_NAME"; then
    if systemctl is-active --quiet $SERVICE_NAME; then
        echo "Deteniendo servicio $SERVICE_NAME..."
        sudo systemctl stop $SERVICE_NAME

        sleep 3

        if sudo lsof -ti:9000 > /dev/null 2>&1; then
            echo "Advertencia: El puerto 9000 aún está en uso. Intentando liberar..."
            sudo kill -9 $(sudo lsof -ti:9000) 2>/dev/null || true
            sleep 2
        fi

        echo "Servicio detenido correctamente"
    else
        echo "El servicio ya se encuentra detenido"
    fi
else
    echo "El servicio $SERVICE_NAME no existe aún. Continuando con el despliegue inicial..."
fi

if [ ! -d "$BACKUP_DIR" ]; then
    echo "Creando directorio de backups: $BACKUP_DIR"
    sudo mkdir -p "$BACKUP_DIR"
fi

if [ -f "$APP_DIR/nubo-api.jar" ]; then
    echo "Creando backup del JAR actual..."
    sudo cp "$APP_DIR/nubo-api.jar" "$BACKUP_DIR/nubo-api_$TIMESTAMP.jar"
    echo "Backup guardado en: $BACKUP_DIR/nubo-api_$TIMESTAMP.jar"
fi

echo "Eliminando JARs antiguos en $APP_DIR..."
find "$APP_DIR" -maxdepth 1 -name "*.jar" -type f -delete || true

echo "Limpiando backups antiguos (manteniendo los últimos 5)..."
cd "$BACKUP_DIR" && ls -t nubo-api_*.jar 2>/dev/null | tail -n +6 | xargs -r rm -f

echo ">>> [PRE-DEPLOY] COMPLETADO"
exit 0