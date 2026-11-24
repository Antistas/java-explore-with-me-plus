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
"${MAVEN}" package -DskipTests

print "Пересобираю и запускаю контейнеры..."
docker compose up -d --build --force-recreate

wait_for_url() {
  local name="$1"
  local url="$2"

  print "Ожидаю ${name}..."
  for attempt in {1..120}; do
    if curl --fail --silent "${url}" >/dev/null 2>&1; then
      print "${name} готов."
      return 0
    fi
    sleep 1
  done

  print -u2 "${name} не ответил за 120 секунд."
  docker compose logs --no-color --tail=100
  return 1
}

wait_for_url "discovery-server" "http://localhost:8761/actuator/health"
wait_for_url "gateway-server" "http://localhost:8080/actuator/health"
# Маршрут работает, когда main-service получил настройки, зарегистрировался в Eureka и виден шлюзу
wait_for_url "main-service через gateway-server" "http://localhost:8080/categories"

print "Текущее состояние сервисов:"
docker compose ps
