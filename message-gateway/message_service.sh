#!/bin/sh
export FILENAME='message-gateway-0.0.1-SNAPSHOT.jar'

export APP_NAME='message_service'


export CONFIG='configs/message_service.properties'
export JAR=$FILENAME

ps -ef | grep java | grep $JAR | awk '{print "kill -9 ", $2}' > kill_process_$APP_NAME.sh
chmod 777 kill_process_$APP_NAME.sh
./kill_process_$APP_NAME.sh

rm -rf kill_process_$APP_NAME.sh

nohup /opt/jdk/bin/java -Xms128m -Xmx256m -jar $JAR -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC+7" --spring.config.location=$CONFIG >> logs/$APP_NAME.log &
sleep 2

#tail -f logs/$APP_NAME.log