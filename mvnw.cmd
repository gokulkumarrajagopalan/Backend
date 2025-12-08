@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM This script is used to build the Maven project
@REM If Maven is not installed, it will download Maven automatically
@REM
@REM Required: JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM M2_HOME - location of maven's installed home dir
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM
@REM BEGIN LICENSE
@REM
@REM Licensed to the Apache Software Foundation (ASF)
@REM
@REM To use this batch file, you will also need the Maven wrapper JAR
@REM
@REM END LICENSE
@REM

@echo off
setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

if not defined JAVA_HOME (
  echo.
  echo Error: JAVA_HOME not found in your environment.
  echo Please set the JAVA_HOME variable in your environment to match the
  echo location of your Java installation.
  echo.
  goto error
)

for /F "tokens=1,2 delims==" %%A in ('wmic os get localdatetime /value') do set "dt=%%B"
set /A year=%dt:~0,4%, month=%dt:~4,2%, day=%dt:~6,2%

echo Building TallyBackend...
echo Java Home: %JAVA_HOME%
echo.

REM Use the JAR to run Maven
if exist "%APP_HOME%\target\maven-wrapper.jar" (
    "%JAVA_HOME%\bin\java.exe" -cp "%APP_HOME%\target\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain %*
    goto end
)

REM If JAR doesn't exist, try to find Maven in PATH
for /f "delims=" %%i in ('where mvn.cmd 2^>nul') do (
    echo Found Maven at: %%i
    call %%i %*
    goto end
)

REM If Maven not found, show error
echo.
echo Error: Maven not found.
echo Please install Maven from https://maven.apache.org/download.cgi
echo Or set M2_HOME environment variable to your Maven installation directory.
echo.

:error
exit /b 1

:end
endlocal
