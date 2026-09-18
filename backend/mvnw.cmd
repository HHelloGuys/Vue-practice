@REM Maven Wrapper 실행 스크립트
@echo off
setlocal

set "MAVEN_PROJECTBASEDIR=%~dp0"
set "MAVEN_WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set "MAVEN_PROJECTBASEDIR_NO_SLASH=%MAVEN_PROJECTBASEDIR:~0,-1%"

if not exist "%MAVEN_WRAPPER_JAR%" (
  echo Maven Wrapper JAR not found: %MAVEN_WRAPPER_JAR%
  exit /b 1
)

java "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR_NO_SLASH%" -classpath "%MAVEN_WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
endlocal
