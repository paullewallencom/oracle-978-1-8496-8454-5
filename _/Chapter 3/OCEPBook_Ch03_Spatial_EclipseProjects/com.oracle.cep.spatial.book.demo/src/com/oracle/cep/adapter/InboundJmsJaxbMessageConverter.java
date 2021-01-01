package com.oracle.cep.adapter;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.bea.wlevs.adapters.jms.api.InboundMessageConverter;
import com.bea.wlevs.adapters.jms.api.MessageConverterException;
//import com.oracle.cep.utils.XmlDateFormatter;
import com.oracle.cep.xml.event.ObjectFactory;
import com.oracle.cep.xml.event.ShipPosType;

import com.oracle.cep.event.ShipPosEvent;

public class InboundJmsJaxbMessageConverter implements InboundMessageConverter, InitializingBean {

	Log log_ = LogFactory.getLog(InboundJmsJaxbMessageConverter.class);
	private JAXBContext jaxbContext_;
	private Unmarshaller unmarshaller_;
	private Marshaller marshaller_;
		
	@SuppressWarnings("rawtypes")
	public List convert(Message message) throws MessageConverterException {
		
		if (log_.isDebugEnabled()){
			log_.info("convert(): RECEIVED MESSAGE!");
		}
				
		List result = null;
		if (message instanceof TextMessage) {
			result = handleMessage((TextMessage) message);
		} else {
			log_.warn("onMessage():skipping unexpected message type:" + message.toString());
			result = Collections.EMPTY_LIST;
		}
		return result;
	}

	/**
	 * For now we assume that this is XML and we need to unmarshall into a JAXB
	 * class
	 * @param message
	 */
	@SuppressWarnings({ "rawtypes" })
	private List handleMessage(TextMessage message) throws MessageConverterException {
		
		List<Object> result = new ArrayList<Object>();
		try {
			if (message != null){					
				Object eventPoJo = unmarshaller_.unmarshal(new StringReader(message.getText()));
				
				if (log_.isDebugEnabled()){
					marshaller_.marshal( eventPoJo, System.out );
				}
											
				if (eventPoJo instanceof JAXBElement) {
					
					//System.out.println("Adapter found a JAXBElement!");
					Object element = ((JAXBElement) eventPoJo).getValue();
					
					if(element instanceof ShipPosType) {
	
						ShipPosType e = (ShipPosType) element;					
						
						if (log_.isDebugEnabled()){
							log_.info("Converting ShipID: " + e.getShipId() + " SEQ: " + e.getSeq() + "\n");
						}
						
						//CREATE THE TRANSACTION EVENT THAT THE CEP APPLICATION USES
						ShipPosEvent sp = new ShipPosEvent();
						sp.setShipId(e.getShipId());
						sp.setSeq(e.getSeq());
						sp.setLatitude(e.getLat());
						sp.setLongitude(e.getLon());
						sp.setInfo(e.getInfo());
						
						//Date d = CalendarUtils.convertXMLGregorianCalendarToDate(e.getTransactionDateTime());
						//Calendar c = CalendarUtils.convertDateToCalendar(d);
						//ct.setTransactionDateTime(c.getTimeInMillis());
						/*
						ct.setTransactionDateTime(e.getTransactionDateTime());
						ct.setTransactionID(e.getTransactionID());
						ct.setTransactionType(e.getTransactionType());						
						XmlDateFormatter xmlf = new XmlDateFormatter();
						ct.setTransactionTimeStr(xmlf.format(e.getTransactionDateTime().longValue())); 
						*/					
						result.add(sp);
					}
				
	        	}
			}
			
			return result;
		} catch(JMSException jmse) {
			throw new MessageConverterException("Error getting JMS message content",jmse);
		}
		catch (JAXBException jaxbException) {
			throw new MessageConverterException("JAXB unmarshalling failure:", jaxbException);
		}
		
	}
	
	/**
	 * Spring callback
	 */
	public void afterPropertiesSet() throws Exception {		
		initJaxb();
	}

	private void initJaxb() {
		try {
			jaxbContext_ = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());			
			unmarshaller_ = jaxbContext_.createUnmarshaller();
			marshaller_ = jaxbContext_.createMarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException("\n initJaxb(): JAXBException: COULD NOT INITIALIZE JAXB!\n" + e.getMessage(), e);
		}
		if(log_.isInfoEnabled()) {
			log_.info("Initialized JAXB");
		}
	}

}
