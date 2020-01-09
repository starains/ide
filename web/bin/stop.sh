#!/bin/bash
cd `dirname $0`
cd ..
THIS_HOME=`pwd`

PID_FILE=$THIS_HOME/bin/ide.pid

echo -e "Stopping TeamIDE \c"
sudo kill `cat $PID_FILE`  
    rm -rf $PID_FILE
sleep 1
echo "Stopped TeamIDE "

