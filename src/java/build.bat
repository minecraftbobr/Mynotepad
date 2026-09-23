@echo off
chcp 65001 >nul
setlocal

echo Building...

if not exist Main.java exit /b 1
if not exist Uninstaller.java exit /b 1
if not exist Installer.java exit /b 1
if not exist Sha256SumCalc.java exit /b 1

set JAVAC_FLAGS=-encoding UTF-8 -source 1.8 -target 1.8 -Xlint:-options

javac %JAVAC_FLAGS% Main.java
if errorlevel 1 exit /b 1

jar cfe NotepadApp.jar Main Main.class Main$*.class
if errorlevel 1 exit /b 1

javac %JAVAC_FLAGS% Uninstaller.java
if errorlevel 1 exit /b 1

jar cfe unins.jar Uninstaller Uninstaller*.class
if errorlevel 1 exit /b 1

javac %JAVAC_FLAGS% Sha256SumCalc.java
if errorlevel 1 exit /b 1

jar cfe sha256sumcalc.jar Sha256SumCalc Sha256SumCalc*.class
if errorlevel 1 exit /b 1

javac %JAVAC_FLAGS% Installer.java
if errorlevel 1 exit /b 1

if not exist resources mkdir resources
copy /Y NotepadApp.jar     resources\NotepadApp.jar     >nul
copy /Y unins.jar          resources\unins.jar          >nul
copy /Y sha256sumcalc.jar  resources\sha256sumcalc.jar  >nul
jar cfe install.jar Installer Installer*.class resources
if errorlevel 1 exit /b 1

del /Q *.class
rmdir /S /Q resources

echo Done
pause
exit /b 0