#!/bin/bash
cd `dirname $0`

APP_DIR=../lib
LOG_DIR=../logs

SERVERPORT=8081
MONGODBURL=mongodb://192.168.199.32:27027/gitstats


APP_NAME="${project.build.finalName}.${project.packaging}"

if [ ! -d "$LOG_DIR" ] ; then
   mkdir $LOG_DIR
fi

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "

JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ -n "$BITS" ]; then
    JAVA_MEM_OPTS=" -server -Xms1g -Xmx2g "
else
    JAVA_MEM_OPTS=" -server -Xms1g -Xmx2g "
fi

echo -e "Starting the $APP_NAME ...\n"
nohup java  $JAVA_MEM_OPTS -jar $APP_DIR/$APP_NAME --spring.data.mongodb.uri="$MONGODBURL" --server.port="$SERVERPORT" > /dev/null 2>&1 & 

PIDS=`ps -f | grep java | grep "$APP_NAME" | awk '{print $2}'`
echo "PID: $PIDS"
echo "$APP_NAME is running..."
