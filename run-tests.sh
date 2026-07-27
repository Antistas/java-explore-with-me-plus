#!/bin/zsh

set -euo pipefail

PROJECT_DIRECTORY="${0:A:h}"
INTELLIJ_MAVEN="/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn"

if ! JAVA_21_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null)"; then
  print -u2 "Java 21 не найдена. Установите JDK 21 и повторите запуск."
  exit 1
fi

export JAVA_HOME="${JAVA_21_HOME}"
export PATH="${JAVA_HOME}/bin:${PATH}"

if [[ -x "${PROJECT_DIRECTORY}/mvnw" ]]; then
  MAVEN="${PROJECT_DIRECTORY}/mvnw"
elif command -v mvn >/dev/null 2>&1; then
  MAVEN="$(command -v mvn)"
elif [[ -x "${INTELLIJ_MAVEN}" ]]; then
  MAVEN="${INTELLIJ_MAVEN}"
else
  print -u2 "Maven не найден. Установите Maven или проверьте путь к IntelliJ IDEA."
  exit 1
fi

cd "${PROJECT_DIRECTORY}"

print "Java: $(${JAVA_HOME}/bin/java -version 2>&1 | head -n 1)"
print "Запускаю тесты всех Maven-модулей..."

"${MAVEN}" clean test

print "Все тесты успешно пройдены."
