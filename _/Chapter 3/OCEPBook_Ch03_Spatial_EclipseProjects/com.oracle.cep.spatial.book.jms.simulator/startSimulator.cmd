@ECHO OFF

call ".\setEnv.cmd" %*

set LIB_DIR=%~dp0\lib

set CLIENT_CLASSPATH=%LIB_DIR%\shippos-simulator.jar;%LIB_DIR%\commons-lang-2.5.jar;%LIB_DIR%\commons-logging-1.1.1.jar;%LIB_DIR%\spatial-demo-jaxb.jar;%LIB_DIR%\wlclient.jar;%LIB_DIR%\wljmsclient.jar

echo starting JMS Simulator client to send messages to CEP

"%JAVA_HOME%\bin\java" -classpath %CLIENT_CLASSPATH% com.oracle.cep.demo.simulator.Simulator