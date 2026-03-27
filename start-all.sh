#!/bin/bash

# ============================================================
#  Hotel Management System — Start All Services
#  IT4020 Assignment 2 | SLIIT 2026
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$SCRIPT_DIR/logs"
mkdir -p "$LOG_DIR"

echo "=============================================="
echo "  🏨 Hotel Management System — Starting Up"
echo "=============================================="
echo ""

build_and_run() {
  local SERVICE=$1
  local PORT=$2
  echo "▶  Starting $SERVICE on port $PORT ..."
  cd "$SCRIPT_DIR/$SERVICE"
  mvn clean package -DskipTests -q
  nohup java -jar target/*.jar > "$LOG_DIR/$SERVICE.log" 2>&1 &
  echo "   ✅ $SERVICE started (PID $!). Log: logs/$SERVICE.log"
  sleep 5
  cd "$SCRIPT_DIR"
}

build_and_run "auth-service"     8081
build_and_run "customer-service" 8082
build_and_run "room-service"     8083
build_and_run "booking-service"  8084
build_and_run "payment-service"  8085

echo ""
echo "▶  Starting API Gateway on port 8086 (last) ..."
cd "$SCRIPT_DIR/api-gateway"
mvn clean package -DskipTests -q
nohup java -jar target/*.jar > "$LOG_DIR/api-gateway.log" 2>&1 &
echo "   ✅ API Gateway started (PID $!). Log: logs/api-gateway.log"
cd "$SCRIPT_DIR"

echo ""
echo "=============================================="
echo "  ✅ All Services Started!"
echo "=============================================="
echo ""
echo "  📖 Swagger URLs:"
echo "     Gateway (ALL-IN-ONE) : http://localhost:8086/swagger-ui.html"
echo "     Auth Service         : http://localhost:8081/swagger-ui.html"
echo "     Customer Service     : http://localhost:8082/swagger-ui.html"
echo "     Room Service         : http://localhost:8083/swagger-ui.html"
echo "     Booking Service      : http://localhost:8084/swagger-ui.html"
echo "     Payment Service      : http://localhost:8085/swagger-ui.html"
echo ""
echo "  🔑 First step: POST http://localhost:8086/api/auth/register"
echo "     Then: POST http://localhost:8086/api/auth/login"
echo "     Use the returned token as: Authorization: Bearer <token>"
echo ""
