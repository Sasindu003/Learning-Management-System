call mvn spring-boot:run
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ---------------------------------------------------
    echo An error occurred! Keeping the window open for you.
    echo ---------------------------------------------------
    pause
)