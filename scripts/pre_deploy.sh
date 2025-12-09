#!/bin/bash
set -e

echo "Iniciando PRE-DEPLOY"

# Directorio de la aplicación
APP_DIR="/opt/NuboAPI"
BACKUP_DIR="/opt/NuboAPI/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

echo "Verificando directorios..."
if [ ! -d "$APP_DIR" ]; then
    echo "⚠Directorio $APP_DIR no existe. Creándolo..."
    sudo mkdir -p $APP_DIR
    sudo chown -R $USER:$USER $APP_DIR
fi

# Crear directorio de backups si no existe
if [ ! -d "$BACKUP_DIR" ]; then
    echo "Creando directorio de backups..."
    mkdir -p $BACKUP_DIR
fi

# Hacer backup de la versión actual (si existe)
if [ -f "$APP_DIR/app.jar" ]; then
    echo "Creando backup de la versión actual..."
    cp $APP_DIR/app.jar $BACKUP_DIR/app_backup_$TIMESTAMP.jar
    echo "Backup creado: app_backup_$TIMESTAMP.jar"

    # Mantener solo los últimos 5 backups
    echo "Limpiando backups antiguos (manteniendo los últimos 5)..."
    ls -t $BACKUP_DIR/app_backup_*.jar | tail -n +6 | xargs -r rm
else
    echo "ℹNo hay versión anterior para hacer backup (primer despliegue)"
fi

# Verificar conectividad con la base de datos
echo "Verificando conexión a base de datos..."
DB_HOST="nubo.caiqszafsxyd.us-east-1.rds.amazonaws.com"
DB_PORT="5432"

if nc -z -w5 $DB_HOST $DB_PORT 2>/dev/null; then
    echo "Conexión a base de datos exitosa"
else
    echo "No se puede conectar a la base de datos"
    echo "   Host: $DB_HOST:$DB_PORT"
    exit 1
fi

# Verificar espacio en disco
echo "Verificando espacio en disco..."
AVAILABLE_SPACE=$(df -BG $APP_DIR | awk 'NR==2 {print $4}' | sed 's/G//')
MIN_SPACE=1  # GB

if [ "$AVAILABLE_SPACE" -lt "$MIN_SPACE" ]; then
    echo "Espacio insuficiente en disco. Disponible: ${AVAILABLE_SPACE}GB, Requerido: ${MIN_SPACE}GB"
    exit 1
else
    echo "Espacio en disco suficiente: ${AVAILABLE_SPACE}GB disponibles"
fi

# Detener la aplicación actual (si está corriendo)
echo "Deteniendo aplicación actual..."
if systemctl is-active --quiet NuboAPI; then
    sudo systemctl stop NuboAPI
    echo "Aplicación detenida"

    sleep 3
else
    echo "ℹLa aplicación no estaba corriendo"
fi

# Verificar que el puerto 9000 esté libre
echo "🔌 Verificando que el puerto 9000 esté libre..."
if lsof -Pi :9000 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "⚠El puerto 9000 aún está en uso. Intentando liberar..."
    sudo fuser -k 9000/tcp || true
    sleep 2
fi
echo "Puerto 9000 disponible"

# Limpiar logs antiguos
LOG_DIR="/var/log/NuboAPI"
if [ -d "$LOG_DIR" ]; then
    echo "🗑️  Limpiando logs antiguos (mayores a 7 días)..."
    find $LOG_DIR -name "*.log" -mtime +7 -delete || true
fi

# Crear archivo de mantenimiento
echo "Activando modo mantenimiento..."
cat > $APP_DIR/maintenance.html << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <title>Mantenimiento - NUBO API</title>
    <style>
        body { font-family: Arial; text-align: center; padding: 50px; }
        h1 { color: #333; }
    </style>
</head>
<body>
    <h1>Sistema en mantenimiento</h1>
    <p>La API NUBO está siendo actualizada. Volveremos en breve.</p>
</body>
</html>
EOF

echo ""
echo "PRE-DEPLOY completado exitosamente"
echo "================================================"
echo "Resumen:"
echo "   - Backup creado: ${TIMESTAMP}"
echo "   - Aplicación detenida"
echo "   - Espacio disponible: ${AVAILABLE_SPACE}GB"
echo "   - Base de datos: Conectada"
echo "   - Puerto 9000: Liberado"