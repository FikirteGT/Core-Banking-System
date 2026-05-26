@echo off
echo ========================================
echo  Core Banking System - Build and Run
echo ========================================

REM Create output folder if it doesn't exist
if not exist out mkdir out

REM Compile all Java files with the SQLite driver on the classpath
echo Compiling...
javac -cp "lib\*" -d out src\*.java

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation FAILED. Check errors above.
    pause
    exit /b 1
)

echo Compilation successful!
echo.

REM Run the Main class
echo Starting application...
java -cp "out;lib\*" Main

pause
