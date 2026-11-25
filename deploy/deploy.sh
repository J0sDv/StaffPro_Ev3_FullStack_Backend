#!/bin/bash
# Uso (en el terminal de EC2):  bash ~/backend/deploy/deploy.sh
set -e
REPO="$HOME/StaffPro_Ev3_FullStack_Backend"
SERVICIOS="ms-usuarios ms-personal ms-solicitudes api-gateway"
export MAVEN_OPTS="-Xmx300m"

cd "$REPO"
git pull

# Instala o actualiza la plantilla de systemd
sudo cp deploy/staffpro@.service /etc/systemd/system/staffpro@.service
sudo systemctl daemon-reload

# Se detienen los servicios mientras Maven compila, para liberar memoria
for s in $SERVICIOS; do
  if [ -f "$s/pom.xml" ]; then
    sudo systemctl stop "staffpro@$s" 2>/dev/null || true
  fi
done

for s in $SERVICIOS; do
  if [ -f "$s/pom.xml" ]; then
    echo "==> Compilando $s"
    mvn -q -f "$s/pom.xml" clean package -DskipTests
    cp "$s/target/$s.jar" "/opt/staffpro/$s/$s.jar"
    sudo systemctl enable "staffpro@$s" > /dev/null 2>&1
    sudo systemctl start "staffpro@$s"
  fi
done

echo
echo "==> Estado de los servicios"
for s in $SERVICIOS; do
  if [ -f "$s/pom.xml" ]; then
    printf '%-16s %s\n' "$s" "$(systemctl is-active staffpro@$s)"
  fi
done