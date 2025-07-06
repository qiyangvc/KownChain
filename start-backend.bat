@echo off
cd /d "%~dp0backend\KnowChain"
set JAVA_HOME=%~dp0jdk-17.0.12_windows-x64_bin\jdk-17.0.12
set MAVEN_HOME=%~dp0maven_temp\apache-maven-3.9.10
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%
echo Starting backend...
mvn spring-boot:run
pause
