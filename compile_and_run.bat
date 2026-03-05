@echo off
REM Compile advancedBreakout.java with acm.jar in the classpath
echo Compiling advancedBreakout.java...
javac -cp .;acm.jar advancedBreakout.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo Running advancedBreakout...
    java -cp .;acm.jar advancedBreakout
) else (
    echo Compilation failed! Check the errors above.
)

pause
