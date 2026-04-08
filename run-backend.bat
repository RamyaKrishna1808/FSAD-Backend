@echo off
cd /d "%~dp0"
echo Starting LMS backend on http://localhost:8080
mvn spring-boot:run
