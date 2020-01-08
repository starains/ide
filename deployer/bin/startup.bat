@echo off & setlocal enabledelayedexpansion
title coos server
cd %~dp0
cd ..
set "COOS_HOME=%cd%"
cd bin

set LIB_JARS=""
cd ..\lib
for %%i in (*) do set LIB_JARS=!LIB_JARS!;..\lib\%%i
cd ..\bin

java  -Dfile.encoding=UTF-8 -Dapp.home=../ -Xms56m -Xmx56m -classpath ..\conf;%LIB_JARS% com.coospro.IDEMain
goto end