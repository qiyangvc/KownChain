#!/bin/bash
cd "$(dirname "$0")/backend/KnowChain" || exit 1

# 可选：如果你有本地 JDK 和 Maven，可以设置 JAVA_HOME 和 MAVEN_HOME
# export JAVA_HOME="$(dirname "$0")/jdk-17.0.12_macos/jdk-17.0.12"
# export MAVEN_HOME="$(dirname "$0")/maven_temp/apache-maven-3.9.10"
# export PATH="$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH"

echo "Starting backend..."
mvn spring-boot:run