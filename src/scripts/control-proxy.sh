#!/bin/bash
WORKFOLDER=/home/clement/scheduler/work
FILEDENY=$WORKFOLDER/denyproxy
FILEALLOW=$WORKFOLDER/allowproxy
if [ -f $FILEDENY ]; then
   echo "Deny"
   cp /etc/tinyproxy.conf.deny /etc/tinyproxy.conf
   rm -f $FILEDENY
   NEEDRESTART=true
fi
if [ -f $FILEALLOW ]; then
   echo "Allow"
   cp /etc/tinyproxy.conf.allow /etc/tinyproxy.conf
   rm -f $FILEALLOW
   NEEDRESTART=true
fi

if [ ! -z ${NEEDRESTART+x} ]; then
   echo "Need retart"
   service tinyproxy restart
fi
