package com.oracle.cep.demo.simulator;

import javax.jms.*;

import weblogic.jms.extensions.*;

import java.util.Properties ;
import java.util.Random;
import java.io.FileInputStream ;
import java.io.FileNotFoundException;
import java.io.StringWriter;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.xml.bind.JAXBContext;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

//import javax.xml.bind.Unmarshaller;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import java.math.BigDecimal;
import com.oracle.cep.xml.event.*;

public class Simulator implements Runnable {

	private static Log log_ = LogFactory.getLog(Simulator.class);
	private JAXBContext jaxbContext_;
	//private static Unmarshaller unmarshaller_;
	private Marshaller marshaller_ ;
	
	private QueueConnectionFactory qconFactory;
    private QueueConnection qcon;
    private QueueSession qsession;
    private QueueSender qsender;
    private Queue queue;
    private TextMessage msg;
    
    private String WLS_QUEUE_NAME = "jms/demoQueue" ;
    private String WLS_CONN_FACTORY_NAME = "jms/QueueConnectionFactory" ;    
   
    private Properties simProps ;
	private int NUMBER_EVENTS = 10 ;
	private int MAX_WAIT_BETWEEN_EVENTS = 10 ;

    private static String TRANSACTION_XML_FILE = "ShipPosList.xml" ;

    private static DataHandler dh = null ;    
    
    @SuppressWarnings("unused")
	private Random r = new Random();
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
		Simulator sim = new Simulator();
		sim.run();
		
	}
	
	@Override
	public void run() {
		
		Simulator sim = new Simulator();

		// load the properties
		sim.loadProperties();
		//initialize JAXB context
		sim.initJaxb();
		
		// get the vehicles
		try {
			dh = new DataHandler(TRANSACTION_XML_FILE,0);
		} catch (Exception e){
			System.out.println("Could not load inventory from file: " + TRANSACTION_XML_FILE );
			e.printStackTrace();
		}
						
		// send the events
		sim.sendJMSEvents() ;
	}
	
    
	public void loadProperties(){
		
		simProps = new Properties();
		
		try {
			String propFileName = "simulator.xml.properties" ;
			
			FileInputStream in = new FileInputStream(propFileName);
			if (log_.isInfoEnabled()){			
				log_.info("loadProperties():" + propFileName);				
			}
			simProps.load(in);
			in.close();		
		} 
		catch (FileNotFoundException nofile){
			System.out.println("Simulator properties file not found!");
		}
		catch (Exception ex){
			ex.printStackTrace();
			System.out.println("Exception occured retreiving the simulator properties!");
		}
		
		setProperties();
			
	}
	
	public void setProperties(){
	
		NUMBER_EVENTS = Integer.valueOf(simProps.getProperty("number.events")).intValue();
		MAX_WAIT_BETWEEN_EVENTS = Integer.valueOf(simProps.getProperty("wait.between.events")).intValue();
		
		WLS_QUEUE_NAME = simProps.getProperty("queue.name");
		WLS_CONN_FACTORY_NAME = simProps.getProperty("connection.factory.name");
		
		TRANSACTION_XML_FILE = simProps.getProperty("transaction.xml.file");

	}
	
	@SuppressWarnings("rawtypes")
	private String createXMLMessage(JAXBElement element) {
		try {			
			
			StringWriter sw = new StringWriter();
			marshaller_.marshal(element, sw);
			//TESTING: show the xml string on the console
			System.out.println(sw.toString());
			return sw.toString();		
		}
		catch (JAXBException jaxbException) {
			//throw new Exception("JAXB unmarshalling failure:", jaxbException);
			jaxbException.printStackTrace();
			return null ;
		}
	}

	
	
    public String createEventXML(int i){
    	
    	String EventMessage = null ;
		
		//GET A TRANSACTION
    	ShipPosType tt = dh.getTransactionForIndex(i);
				
		ObjectFactory factory = new ObjectFactory();		
		JAXBElement<ShipPosType> xElement = factory.createShipPos(tt);
		
		// CREATE THE XML 
		EventMessage = createXMLMessage(xElement); 	
    	
		// SHOW THE MESSAGE
		if (log_.isDebugEnabled()){
			log_.debug(EventMessage);
		}
    	
    	return EventMessage ;
    	
    }

    public void sendJMSEvents(){

	    //Random randomGenerator = new Random();
		
		try {

			Properties namingProps = new Properties();
			FileInputStream in = new FileInputStream("etc/naming.props");
			namingProps.load(in);
			log_.info("Loaded naming.props file!");
			in.close();

			Context jmsContext = new InitialContext(namingProps);
			log_.info("Connection Factory Name: " + WLS_CONN_FACTORY_NAME); 
			qconFactory = (QueueConnectionFactory) jmsContext.lookup(WLS_CONN_FACTORY_NAME);
			
			log_.info("Creating a queue connection!");
			qcon = (QueueConnection)qconFactory.createQueueConnection();
			
			log_.info("Creating a queue session!");
			qsession = (WLQueueSession)qcon.createQueueSession(false, WLSession.AUTO_ACKNOWLEDGE);

			log_.info("Queue Name: " + WLS_QUEUE_NAME);
			queue = (Queue) jmsContext.lookup(WLS_QUEUE_NAME);

			qsender = qsession.createSender(queue);
			log_.info("Created a queue sender!");

		    String EventMessage = null ;
		    
		    log_.info("Preparing to send " + NUMBER_EVENTS + " events...");
		    for(int i=0; i<NUMBER_EVENTS; i++){
	        			    	
		    	//CREATE THE TEXT MESSAGE
		    	EventMessage = createEventXML(i);
		    
			    msg = qsession.createTextMessage();
			    msg.setText(EventMessage);
	
			    qsender.send(msg);
	
			    qcon.start();
			    log_.info("Sending event #" + i  + "\n" + EventMessage);
			    
			    Thread.sleep(MAX_WAIT_BETWEEN_EVENTS);
		    
		    }


		} catch (JMSException jmse){
			jmse.printStackTrace();
			System.out.println("JMS Exception");
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("non-JMS Exception!");
		} finally {
			close();
		}
    	    	
	}
    

    public void close()
    {
    	try {
	    	qsender.close();
		    qsession.close();
		    qcon.close();
    	} catch (JMSException jmse){
			jmse.printStackTrace();
			System.out.println("JMS Exception during closing!");
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Non-JMS Exception during closing!");
		}
    }

	 @SuppressWarnings("unused")
	private XMLGregorianCalendar getDate() {	
	    	try {	    
	    		return DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());	
	    	} 
	    	catch (DatatypeConfigurationException e) {	    
	    		throw new Error(e); 
	    	}
	    }
	
  private void initJaxb() {
		try {
			jaxbContext_ = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
			//unmarshaller_ = jaxbContext_.createUnmarshaller();
			marshaller_ = jaxbContext_.createMarshaller();
			marshaller_.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		} catch (JAXBException e) {
			throw new RuntimeException("Could not init JAXB :" + e.getMessage(), e);
		}
		if(log_.isInfoEnabled()) {
			log_.info("Initialized JAXB");
		}
	  }



	
}
