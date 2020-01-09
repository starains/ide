@echo off & setlocal enabledelayedexpansion
title TeamIDE
cd %~dp0
cd ..

set "THIS_HOME=%cd%"

java -Dfile.encoding=UTF-8 -Xms1024m -Xmx2048m -jar %THIS_HOME%/ide.jar

goto end