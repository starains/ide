@echo off & setlocal enabledelayedexpansion
title coos server
cd %~dp0
cd ..

set "COOS_HOME=%cd%"

set LIB_JARS=""
cd lib
for %%i in (*) do set LIB_JARS=!LIB_JARS!;%COOS_HOME%\lib\%%i
cd ..

java  -Dfile.encoding=UTF-8 -Dapp.home=%COOS_HOME% -Xms1024m -Xmx2048m -classpath conf;%LIB_JARS% com.coospro.IDEMain
goto end