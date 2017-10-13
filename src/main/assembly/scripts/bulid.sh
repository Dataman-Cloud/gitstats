#!/bin/bash
#dockerfile add 只能操作本目录或者本目录一下目录的文件
mv ../lib/gitstats.jar gitstats.jar
docker build . -t gitstats:v1

