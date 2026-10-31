@echo off
setlocal
set "MAVEN_VERSION=3.9.9"
set "BASE_DIR=%~dp0"
if not defined MAVEN_HOME set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%"
set "MVN=%MAVEN_HOME%\bin\mvn.cmd"

if exist "%MVN%" goto run_maven
where java >nul 2>nul
if errorlevel 1 (
    echo Error: se requiere Java 21 o superior para ejecutar Maven.
    exit /b 1
)
if not exist "%USERPROFILE%\.m2\wrapper\dists" mkdir "%USERPROFILE%\.m2\wrapper\dists"
set "ARCHIVE=%MAVEN_HOME%.zip"
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%ARCHIVE%'; Expand-Archive -LiteralPath '%ARCHIVE%' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force; Remove-Item -LiteralPath '%ARCHIVE%'"
if errorlevel 1 exit /b 1

:run_maven
call "%MVN%" -f "%BASE_DIR%pom.xml" %*
exit /b %ERRORLEVEL%
