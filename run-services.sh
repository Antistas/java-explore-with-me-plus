#!/bin/zsh

set -euo pipefail

PROJECT_DIRECTORY="${0:A:h}"
JAVA_21_HOME="$(/usr/libexec/java_home -v 21)"
INTELLIJ_MAVEN="/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn"

export JAVA_HOME="${JAVA_21_HOME}"
export PATH="${JAVA_HOME}/bin:${PATH}"

cd "${PROJECT_DIRECTORY}"

if [[ -x "${INTELLIJ_MAVEN}" ]]; then
  MAVEN="${INTELLIJ_MAVEN}"
elif command -v mvn >/dev/null 2>&1; then
  MAVEN="$(command -v mvn)"
else
  print -u2 "Maven не найден. Установите Maven или проверьте путь к IntelliJ IDEA."
  exit 1
fi

print "Запускаю автотесты всех Maven-модулей..."
"${MAVEN}" clean test

print "Все тесты пройдены. Собираю сервисы..."
"${MAVEN}" \
  -pl stats/stats-server,ewm-main-service,explore-gateway \
  -am package -DskipTests

print "Пересобираю и запускаю контейнеры сервисов..."
docker compose up -d \
  --build \
  --force-recreate \
  --no-deps \
  stats-server ewm-service explore-gateway

wait_for_service() {
  local service_name="$1"
  local health_url="$2"

  print "Ожидаю запуск ${service_name}..."
  for attempt in {1..30}; do
    if curl --fail --silent --show-error "${health_url}" >/dev/null 2>&1; then
      print "${service_name} готов."
      return 0
    fi
    sleep 1
  done

  print -u2 "${service_name} не запустился за 30 секунд."
  docker compose logs --no-color --tail=100 "${service_name}"
  return 1
}

wait_for_service "stats-server" "http://localhost:9090/actuator/health"
wait_for_service "ewm-service" "http://localhost:8080/actuator/health"
wait_for_service "explore-gateway" "http://localhost:7070/actuator/health"

print "Текущее состояние сервисов:"
docker compose ps stats-server ewm-service explore-gateway
