#!/bin/bash

set -e

echo "Iniciando POST-DEPLOY"

APP_DIR="/opt/NuboAPI"
LOG_DIR="/var/log/NuboAPI"
JAR_NAME="mi-apiNubo.jar" # Nombre del JAR copiado por GitHub Actions

# Verificar que el JAR nuevo existe (usando el nombre real del JAR)
echo "Verificando que el archivo JAR fue copiado..."
if [ ! -f "$APP_DIR/$JAR_NAME" ]; then
    echo "Error: No se encontró $JAR_NAME en $APP_DIR"
    exit 1
fi
echo "JAR encontrado: $JAR_NAME"

# Crear directorio de logs si no existe
if [ ! -d "$LOG_DIR" ]; then
    echo "Creando directorio de logs..."
    sudo mkdir -p $LOG_DIR
    # Nota: Es más seguro usar la variable $USER o el usuario específico del servicio (User_Nubo)
    # Aquí asumimos que $USER es quien ejecuta el script (root o deployer)
    sudo chown -R User_Nubo:User_Nubo $LOG_DIR
fi

# Cargar variables de entorno
echo "Cargando variables de entorno..."
if [ -f "$APP_DIR/.env" ]; then
    # Usamos bash -a para exportar automáticamente todas las variables
    set -a
    source $APP_DIR/.env
    set +a
    echo "Variables de entorno cargadas"
else
    echo "Archivo .env no encontrado, usando valores por defecto"
fi

# Crear/actualizar el servicio systemd
echo "⚙Configurando servicio systemd..."
# Asegúrate de que User y Group coincidan con la configuración de tu servidor (ej. User=User_Nubo)
# En este ejemplo usamos $USER, pero si usaste User_Nubo, reemplázalo aquí.
sudo tee /etc/systemd/system/NuboAPI.service > /dev/null << EOF
[Unit]
Description=NUBO API - Ktor Application
After=network.target

[Service]
Type=simple
User=User_Nubo # CAMBIAR si usaste User_Nubo
WorkingDirectory=$APP_DIR
EnvironmentFile=$APP_DIR/.env
ExecStart=/usr/bin/java -jar $APP_DIR/$JAR_NAME
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
# El servicio debe ser NuboAPI, no NubiAPI
sudo systemctl enable NuboAPI

echo "▶Iniciando aplicación..."
sudo systemctl start NuboAPI

echo "Esperando a que la aplicación inicie (15 segundos)..."
sleep 15

# Verificar que el servicio está corriendo
echo "Verificando estado del servicio..."
if systemctl is-active --quiet NuboAPI; then # Corregido a NuboAPI
    echo "Servicio NuboAPI está activo"
else
    echo "Error: El servicio no pudo iniciarse"
    echo "Últimas líneas del log:"
    sudo journalctl -u NuboAPI -n 20 --no-pager
    exit 1
fi

# Health check - verificar endpoints
echo "Realizando health check..."
# (El resto de la verificación del puerto 9000 sigue igual)
# ...

# (Los comandos finales y el resumen siguen igual, usando NuboAPI)
# ...

echo ""
echo "POST-DEPLOY completado exitosamente"
echo "================================================"
# ... (rest of the script is identical, referencing NuboAPI) ...

exit 0