#!/bin/bash

export FMW_HOME=/home/oracle/middleware

export JAVA_HOME=$FMW_HOME/jdk160_18

export WLS_HOME=$FMW_HOME/wlserver_10.3

export CLASSPATH=$WLS_HOME/server/lib/weblogic.jar

$JAVA_HOME/bin/java weblogic.WLST initJms.py
