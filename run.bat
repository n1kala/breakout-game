@echo off
REM Delete old compiled class files
echo Deleting old class files...
del advancedBreakout.class 2>nul
del Breakout.class 2>nul

REM Change to the Assignment3 directory (if not already there)
echo.
echo Compiling advancedBreakout.java with Java 8 compatibility...
javac -cp .;acm.jar -source 8 -target 8 advancedBreakout.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Compilation successful!
    echo.
    echo Running advancedBreakout...
    echo.
    java -cp .;acm.jar advancedBreakout
) else (
    echo.
    echo ✗ Compilation FAILED! See errors above.
    echo.
)

pause
