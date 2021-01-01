set CEP_HOME=C:\OraclePS6\Middleware
set WORKSPACE_HOME=C:\OraclePS6\workspace

set PROJECT_HOME=%WORKSPACE_HOME%\com.oracle.cep.bus.tracking
set JAVA_HOME=%CEP_HOME%\jrockit_160_29

cd "%PROJECT_HOME%\loadgen"

"%CEP_HOME%\ocep_11.1\utils\load-generator\runloadgen.cmd" bus_positions.prop

pause