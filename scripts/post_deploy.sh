#!/bin/bash

set -e

echo "Iniciando POST-DEPLOY"

APP_DIR="/opt/nubo-api"
LOG_DIR="/var/log/nubo-api"

# Verificar que el JAR nuevo existe
echo "Verificando que el archivo JAR fue copiado..."
if [ ! -f "$APP_DIR/app.jar" ]; then
    echo "Error: No se encontró app.jar en $APP_DIR"
    exit 1
fi
echo "JAR encontrado"

# Crear directorio de logs si no existe
if [ ! -d "$LOG_DIR" ]; then
    echo "Creando directorio de logs..."
    sudo mkdir -p $LOG_DIR
    sudo chown -R $USER:$USER $LOG_DIR
fi

# Cargar variables de entorno
echo "Cargando variables de entorno..."
if [ -f "$APP_DIR/.env" ]; then
    export $(cat $APP_DIR/.env | xargs)
    echo "Variables de entorno cargadas"
else
    echo "Archivo .env no encontrado, usando valores por defecto"
fi

# Crear/actualizar el servicio systemd
echo "⚙Configurando servicio systemd..."
sudo tee /etc/systemd/system/nubo-api.service > /dev/null << EOF
[Unit]
Description=NUBO API - Ktor Application
After=network.target

[Service]
Type=simple
User=$USER
WorkingDirectory=$APP_DIR
EnvironmentFile=$APP_DIR/.env
ExecStart=/usr/bin/java -jar $APP_DIR/app.jar
Restart=always
RestartSec=10
StandardOutput=append:$LOG_DIR/app.log
StandardError=append:$LOG_DIR/error.log

# Límites de recursos
LimitNOFILE=65536
MemoryLimit=1G

[Install]
WantedBy=multi-user.target
EOF

echo "Servicio systemd configurado"

# Recargar systemd y habilitar el servicio
echo "Recargando configuración de systemd..."
sudo systemctl daemon-reload
sudo systemctl enable nubo-api

echo "▶Iniciando aplicación..."
sudo systemctl start nubo-api

echo "Esperando a que la aplicación inicie (15 segundos)..."
sleep 15

# Verificar que el servicio está corriendo
echo "Verificando estado del servicio..."
if systemctl is-active --quiet nubo-api; then
    echo "Servicio nubo-api está activo"
else
    echo "Error: El servicio no pudo iniciarse"
    echo "Últimas líneas del log:"
    sudo journalctl -u nubo-api -n 20 --no-pager
    exit 1
fi

# Health check - verificar endpoints
echo "Realizando health check..."

# Verificar endpoint raíz
if curl -f http://localhost:9000/ > /dev/null 2>&1; then
    echo "Endpoint raíz (/): OK"
else
    echo "Endpoint raíz no responde"
fi

# Verificar endpoint de health
if curl -f http://localhost:9000/health > /dev/null 2>&1; then
    echo "Endpoint health (/health): OK"
else
    echo "Endpoint health no responde"
fi

# Verificar logs recientes
echo "erificando logs recientes..."
if [ -f "$LOG_DIR/error.log" ]; then
    ERROR_COUNT=$(grep -i "error\|exception" $LOG_DIR/error.log 2>/dev/null | tail -5 | wc -l)
    if [ "$ERROR_COUNT" -gt 0 ]; then
        echo "Se encontraron $ERROR_COUNT errores recientes:"
        grep -i "error\|exception" $LOG_DIR/error.log | tail -5
    else
        echo "No se encontraron errores en los logs"
    fi
fi

# Limpiar archivos temporales de despliegue
echo "Limpiando archivos temporales..."
rm -f $APP_DIR/maintenance.html

# Reiniciar nginx/proxy reverso (si aplica)
if systemctl is-active --quiet nginx; then
    echo "Reiniciando nginx..."
    sudo systemctl reload nginx
    echo "Nginx recargado"
fi

# Limpiar caché de aplicación (si existe)
CACHE_DIR="$APP_DIR/cache"
if [ -d "$CACHE_DIR" ]; then
    echo "🗑Limpiando caché..."
    rm -rf $CACHE_DIR/*
    echo "Caché limpiada"
fi

# Verificar uso de memoria
echo "Verificando uso de memoria..."
MEMORY_USAGE=$(free | grep Mem | awk '{print int($3/$2 * 100)}')
echo "   Uso de memoria: ${MEMORY_USAGE}%"
if [ "$MEMORY_USAGE" -gt 90 ]; then
    echo "Uso de memoria alto (${MEMORY_USAGE}%)"
fi

# Información final del deployment
echo ""
echo "POST-DEPLOY completado exitosamente"
echo "================================================"
echo "Información del deployment:"
echo "   - Servicio: ACTIVO"
echo "   - Puerto: 9000"
echo "   - PID: $(systemctl show -p MainPID nubo-api | cut -d= -f2)"
echo "   - Logs: $LOG_DIR"
echo "   - Memoria: ${MEMORY_USAGE}%"
echo ""
echo "Endpoints disponibles:"
echo "   - http://localhost:9000/"
echo "   - http://localhost:9000/health"
echo "   - http://localhost:9000/teacher/register"
echo "   - http://localhost:9000/teacher/login"
echo ""
echo "Comandos útiles:"
echo "   - Ver logs:         sudo journalctl -u nubo-api -f"
echo "   - Estado servicio:  sudo systemctl status nubo-api"
echo "   - Reiniciar:        sudo systemctl restart nubo-api"
echo "   - Detener:          sudo systemctl stop nubo-api"
echo "================================================"

exit 0