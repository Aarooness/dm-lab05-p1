#!/bin/sh
set -eu

MAVEN_VERSION=3.9.9
BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MAVEN_HOME="${MAVEN_HOME:-${HOME}/.m2/wrapper/dists/apache-maven-${MAVEN_VERSION}}"
MVN="${MAVEN_HOME}/bin/mvn"

if [ ! -x "$MVN" ]; then
    command -v java >/dev/null 2>&1 || {
        echo "Error: se requiere Java 21 o superior para ejecutar Maven." >&2
        exit 1
    }
    mkdir -p "$(dirname "$MAVEN_HOME")"
    ARCHIVE="${MAVEN_HOME}.zip"
    curl -fsSL "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.zip" -o "$ARCHIVE"
    mkdir -p "$MAVEN_HOME"
    unzip -q -o "$ARCHIVE" -d "$(dirname "$MAVEN_HOME")"
    rm -f "$ARCHIVE"
fi

exec "$MVN" -f "$BASE_DIR/pom.xml" "$@"
