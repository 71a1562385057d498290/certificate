@echo off
set PREV_DIR=%cd%

cd /d %~dp0 && java -jar certificate.jar %*
cd %PREV_DIR%
