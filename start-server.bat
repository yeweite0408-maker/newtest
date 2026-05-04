@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-26.0.1
set MAVEN_HOME=C:\tools\apache-maven-3.9.6
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%
cd /d C:\game-mall
echo [1/2] Starting backend...
cd backend
start "Blog-Backend" cmd /c "mvn.cmd spring-boot:run"
cd ..
timeout /t 15 /nobreak >/dev/null
echo [2/2] Starting frontend...
if not exist "frontend\node_modules" (
    cd frontend
    call npm install
    cd ..
)
cd frontend
start "Blog-Frontend" cmd /c "npm run dev -- --host 0.0.0.0"
cd ..
echo Done!
echo Frontend: http://localhost:3000
echo Backend:  http://localhost:8080
pause
