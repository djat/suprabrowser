java -jar supra_launch.jar
@echo off
if not errorlevel 1 goto success
echo Can't run SupraSphere.
pause
goto end
:success
echo SupraLaunch started successfully.
:end
