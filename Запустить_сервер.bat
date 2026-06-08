@echo off
title Kinopolka Server
if not defined JAVA_HOME set "JAVA_HOME=D:\AndroidStudio\jbr"
if not exist "%JAVA_HOME%\bin\java.exe" set "JAVA_HOME=D:\AndroidStudio\jbr"
cd /d "%~dp0server"
echo ======================================================
echo  Starting Kinopolka server. Keep this window open!
echo  Wait for: Started KinopolkaApplicationKt
echo  URL: http://localhost:8080
echo ======================================================
echo.
call gradlew.bat bootRun
echo.
echo Server stopped. Press any key to close.
pause >nul
