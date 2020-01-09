#!/bin/bash
cd `dirname $0`
cd ..
COOS_HOME=`pwd`
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf
LOGS_DIR=$DEPLOY_DIR/logs

APP_MAINCLASS=com.coospro.IDEMain

PIDS=`ps -ef | grep -v grep | grep "$CONF_DIR" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: already started!"
    echo "PID: $PIDS"
    exit 1
fi

if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi
STDOUT_FILE=$LOGS_DIR/start.log
CLOG_FILE=$LOGS_DIR/gc.log

LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'| xargs | sed "s/ /:/g"`
JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "

JAVA_MEM_OPTS="-Xms56m -Xmx56m"
echo -e "starting server ...\c"
nohup java  -Dfile.encoding=UTF-8   -Dapp.home=$DEPLOY_DIR $JAVA_OPTS $JAVA_MEM_OPTS -classpath $CONF_DIR:$LIB_JARS $APP_MAINCLASS >$STDOUT_FILE 2>&1 &
sleep 1
echo "started"
PIDS=`ps -ef | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"

