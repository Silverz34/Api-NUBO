#!/bin/bash
SERVICE_NAME="nubo-api.service"
APP_DIR="/opt/apps/nubo-api/"
JAR_FILE="$APP_DIR/nubo-api.jar"
MAX_WAIT=30
HEALTH_CHECK_URL="http://localhost:9000/health"

echo "[POST-DEPLOY] INICIANDO"

if [ ! -f "$JAR_FILE" ]; then
    echo "ERROR: No se encontró el archivo JAR en $JAR_FILE"
    exit 1
fi

echo "Configurando permisos del JAR..."
sudo chmod +x "$JAR_FILE"
sudo chown ubuntu:ubuntu "$JAR_FILE"

echo "Recargando configuración de systemd..."
sudo systemctl daemon-reload

echo "Habilitando servicio $SERVICE_NAME..."
sudo systemctl enable $SERVICE_NAME 2>/dev/null || echo "Nota: El servicio ya está habilitado"

echo "Iniciando servicio $SERVICE_NAME..."
sudo systemctl start $SERVICE_NAME

echo "Esperando que el servicio inicie (máximo ${MAX_WAIT}s)..."
for i in $(seq 1 $MAX_WAIT); do
    if systemctl is-active --quiet $SERVICE_NAME; then
        echo "Servicio activo después de ${i}s"
        break
    fi
    echo -n "."
    sleep 1
done
echo ""

echo ""
echo "VERIFICANDO ESTADO DEL SERVICIO"
if systemctl is-active --quiet $SERVICE_NAME; then
    echo "✓ SUCCESS: EL SERVICIO ESTÁ EN LÍNEA"

    echo ""
    echo "INFORMACIÓN DEL SERVICIO"
    sudo systemctl status $SERVICE_NAME --no-pager -l

    echo ""
    echo "VERIFICANDO PUERTO 9000"
    if sudo netstat -tulpn | grep -q ":9000"; then
        echo "✓ Puerto 9000 está escuchando correctamente"
        sudo netstat -tulpn | grep ":9000"
    else
        echo "Advertencia: El puerto 9000 no está escuchando aún"
    fi

    echo ""
    echo "VERIFICANDO HEALTH ENDPOINT"
    sleep 2
    if command -v curl &> /dev/null; then
        HEALTH_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_CHECK_URL 2>/dev/null || echo "000")
        if [ "$HEALTH_RESPONSE" = "200" ]; then
            echo "✓ Health check exitoso (HTTP $HEALTH_RESPONSE)"
        else
            echo "⚠ Health check falló (HTTP $HEALTH_RESPONSE) - La API puede necesitar más tiempo para iniciar"
        fi
    else
        echo "ℹ curl no disponible - saltando health check"
    fi

    echo ""
    echo "ÚLTIMAS LÍNEAS DEL LOG"
    sudo journalctl -u $SERVICE_NAME -n 15 --no-pager

    echo ""
    echo "DESPLIEGUE COMPLETADO EXITOSAMENTE"
    exit 0
else
    echo "ERROR: El servicio falló al iniciar"
    echo ""
    echo "ESTADO DEL SERVICIO"
    sudo systemctl status $SERVICE_NAME --no-pager -l
    echo ""
    echo "ÚLTIMAS 30 LÍNEAS DEL LOG"
    sudo journalctl -u $SERVICE_NAME -n 30 --no-pager
    echo ""
    echo "VERIFICANDO ERRORES COMUNES"

    if sudo lsof -ti:9000 > /dev/null 2>&1; then
        echo "El puerto 9000 está siendo usado por otro proceso:"
        sudo lsof -i:9000
    fi

    if ! java -jar "$JAR_FILE" --version 2>/dev/null; then
        echo "El archivo JAR podría estar corrupto o no ser ejecutable"
    fi

    exit 1
fi