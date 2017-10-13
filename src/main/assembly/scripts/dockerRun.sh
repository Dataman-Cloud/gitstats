#!/bin/bash

docker run -d -p 8099:8081 gitstats:v1

#指定配置
#docker run -d -p 8099:8081 ipam-console:v1 java -jar /usr/project/app.jar /
# --spring.data.mongodb.uri="mongodb://192.168.199.32:27027/gitstats" /


