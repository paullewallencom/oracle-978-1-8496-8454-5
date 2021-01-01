#!/bin/bash

export FMW_HOME=/home/oracle/11gR1PS2/SOA

export JAVA_HOME=$FMW_HOME/jdk160_18
export WL_LIB=$FMW_HOME/wlserver_10.3/server/lib

CUR_DIR=`dirname $0`
LIB_DIR=`cd $CUR_DIR/lib; pwd`

export CLIENT_CLASSPATH="$DIST/creditcard-simulator.jar":"$LIB_DIR/commons-lang-2.5.jar":"$LIB_DIR/creditcard-demo-jaxb.jar":"$LIB_DIR/commons-logging-1.1.1.jar":"$WL_LIB/wlclient.jar":"$WL_LIB/wljmsclient.jar"

echo starting JMS Client to send messages to CEP

$JAVA_HOME/bin/java -classpath "$CLIENT_CLASSPATH" com.oracle.cep.demo.simulator.TransactionSimulator

