loadProperties('ServerEnv.properties')
connect(_connectUser,_connectPassword,_connectUrl)
edit()
servermb=getMBean("Servers/"+_serverName)
if servermb is None:
	print '@@@ No server MBean found'
else:
	print "@@@ Starting Script..."
	startEdit()
	print "@@@ Creating JMS Server..."
	jmsServer = create('SpatialDemoJMSServer','JMSServer')
	jmsServer.addTarget(servermb)
	jmsMySystemResource = create("SpatialDemoJMSModule","JMSSystemResource")
	jmsMySystemResource.addTarget(servermb)
	subDeployment = jmsMySystemResource.createSubDeployment('SpatialDemoSubdeployment')
	subDeployment.addTarget(jmsServer)    
	theJMSResource = jmsMySystemResource.getJMSResource()

	print "@@@ Creating Connection Factory.."
	connfact1 = theJMSResource.createConnectionFactory('SpatialDemoConnectionFactory')
	connfact1.setJNDIName('jms/SpatialDemoConnectionFactory')
	connfact1.setSubDeploymentName('SpatialDemoSubdeployment')

	print "@@@ Creating Queue..."
	jmsqueue1 = theJMSResource.createQueue('GPSPositionsInputQueue')
	jmsqueue1.setJNDIName('jms/GPSPositionsInputQueue')
	jmsqueue1.setSubDeploymentName('SpatialDemoSubdeployment')

	print "@@@ Creating Queue..."
	jmsqueue2 = theJMSResource.createQueue('SpatialAlertsQueue')
	jmsqueue2.setJNDIName('jms/SpatialAlertsQueue')
	jmsqueue2.setSubDeploymentName('SpatialDemoSubdeployment')	

	print '@@@ Resources created.  Saving changes ...'
	save()
	activate(block="true")
	print '@@@ Changes activated.'
disconnect()
