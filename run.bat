@echo off
REM Compile and run the Student Management System (Windows)
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
del sources.txt
if errorlevel 1 exit /b 1
java -cp out com.sms.Main
