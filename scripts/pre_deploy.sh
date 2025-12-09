#!/bin/bash
set -e

echo "Iniciando PRE-DEPLOY"

# Directorio de la aplicación
APP_DIR="/opt/NuboAPI"
BACKUP_DIR="/opt/NuboAPI/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
JAR_NAME="mi-apiNubo.jar" # Nombre del JAR a respaldar

echo "Verificando directorios..."
if [ ! -d "$APP_DIR" ]; then
    echo "⚠Directorio $APP_DIR no existe. Creándolo..."
    sudo mkdir -p $APP_DIR
    sudo chown -R User_Nubo:User_Nubo $APP_DIR
fi

# Crear directorio de backups si no existe
if [ ! -d "$BACKUP_DIR" ]; then
    echo "Creando directorio de backups..."
    mkdir -p $BACKUP_DIR
fi

# Hacer backup de la versión actual (si existe)
if [ -f "$APP_DIR/$JAR_NAME" ]; # Usamos el nombre real del JAR para el backup
then
    echo "Creando backup de la versión actual..."
    cp $APP_DIR/$JAR_NAME $BACKUP_DIR/app_backup_$TIMESTAMP.jar
    echo "Backup creado: app_backup_$TIMESTAMP.jar"

    # Mantener solo los últimos 5 backups
    echo "Limpiando backups antiguos (manteniendo los últimos 5)..."
    ls -t $BACKUP_DIR/app_backup_*.jar | tail -n +6 | xargs -r rm
else
    echo "No hay versión anterior para hacer backup (primer despliegue)"
fi

# Verificar conectividad con la base de datos (se mantiene el host RDS)
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

# Verificar espacio en disco (sigue igual)
# ...

# Detener la aplicación actual (si está corriendo)
echo "Deteniendo aplicación actual..."
if systemctl is-active --quiet NuboAPI; then # Corregido a NuboAPI
    sudo systemctl stop NuboAPI
    echo "Aplicación detenida"

    sleep 3
else
    echo "ℹLa aplicación no estaba corriendo"
fi

# Verificar que el puerto 9000 esté libre (sigue igual)
# ...

# Limpiar logs antiguos (sigue igual)
# ...

# Crear archivo de mantenimiento (sigue igual)
# ...

echo ""
echo "PRE-DEPLOY completado exitosamente"
echo "================================================"
# ... (el resumen sigue igual, referenciando NuboAPI) ...