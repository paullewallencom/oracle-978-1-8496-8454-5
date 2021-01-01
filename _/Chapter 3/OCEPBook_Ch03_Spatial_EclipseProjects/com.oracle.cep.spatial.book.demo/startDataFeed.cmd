set CEP_HOME=C:\CEP
set WORKSPACE_HOME=C:\CEP-WORKSPACES\book

set PROJECT_HOME=%WORKSPACE_HOME%\com.oracle.cep.spatial.book.demo
set JAVA_HOME=%CEP_HOME%\jrockit_160_29

cd "%PROJECT_HOME%\loadgen"

"%CEP_HOME%\ocep_11.1\utils\load-generator\runloadgen.cmd" ship_positions.prop
