/*

NOTE: This project has been provided as-is sample code and is for reference only
 
To implement this version of the bus tracking demonstration for Oracle Event Processing 11.1.1.7.0 please execute the following steps:

1) In the config folder, read domain changes.txt file, ensure HTTP pub/sub channels are placed in the server configuration
2) From the loadgen folder, copy the files to the folder in your OEP installation, under ocep_11.1/utils/load-generator
3) In the startDataFeed.cmd file, ensure the paths are correct for your installation 


EXECUTION:

1) Add the com.oracle.cep.bus.tracking application to the Oracle Event Processing Server
2) Execute (normally dbl-click) the Bus Tracking Dashboard.URL (This visualizes the Bus Map)
3) Execute the startDataFeed.cmd (This runs the bus movement runloadgen simulator)


*/