package com.oracle.cep.demo.simulator;

import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.oracle.cep.xml.event.ObjectFactory;
//import com.oracle.cep.xml.event.ShipPosList;
import com.oracle.cep.xml.event.ShipPositions;
import com.oracle.cep.xml.event.ShipPosType;

public class DataHandler {
  
  private Map<String,ShipPosType> data = new HashMap<String,ShipPosType>();
  private Map<Integer,ShipPosType> iData = new HashMap<Integer,ShipPosType>();
  private int index = 0 ;
  private Random randomGenerator = new Random();
		
  private Log log_ = LogFactory.getLog(DataHandler.class);
  private JAXBContext jaxbContext_;
  private Unmarshaller unmarshaller_;

  @SuppressWarnings("rawtypes")
public DataHandler(String srcfile, long STARTING_ACCT_NUMBER) throws Exception {
	
	initJaxb();
	
	File f = new File(srcfile);
	
	try {
		
		Object refData = unmarshaller_.unmarshal(f);			
		if(refData instanceof JAXBElement) {
			
			Object element = ((JAXBElement) refData).getValue();
			if (element instanceof ShipPositions) {
				ShipPositions sp = (ShipPositions) element;
				for(ShipPosType p : sp.getShipPos() ) {				
					addShipPos(p);
				}
			}
		} else {
			throw new IllegalStateException("Unexpected XML type.  Got:" + refData.getClass() + 
					" Expected:"+ShipPositions.class.getName());
		}
	} catch (Exception e) {
		throw new IllegalStateException("Error processing product data!",e);
	}
	//log_.info("Product data loaded:" + data);       
   
  }
  
  public ShipPosType getTransaction(String id) {
    return (ShipPosType)data.get(id);
  }
  
  public int getSize(){
	  return data.size();
  }
  
  public ShipPosType getRandomTransaction(){
	  ShipPosType e = null ;
	  
	  int randomNumber = randomGenerator.nextInt(getSize());	  
	  e = iData.get(randomNumber);
  
	  return e ;
  }

   
  public ShipPosType getTransactionForIndex(int index){
	  ShipPosType e = null ;
	  	  
	  e = iData.get(index);
  
	  return e ;
  }
  
  private synchronized void addShipPos(ShipPosType event){
	  String key = event.getShipId() + "_" + event.getSeq() ;
	  data.put(key, event);
      iData.put(index, event);
      index += 1 ;
      log_.info("There are " + getSize() + " transactions loaded!" );
  }
  
  public synchronized void updateShipPosTable(ShipPosType event){
	  String key = event.getShipId() + "_" + event.getSeq() ;
	  if (data.containsKey(key)){
		  data.remove(key);
	  }
	  if (iData.containsKey(key)){
		  data.remove(key);
	  }
	  addShipPos(event);
  }

  private void initJaxb() {
	try {
		jaxbContext_ = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
		unmarshaller_ = jaxbContext_.createUnmarshaller();
	} catch (JAXBException e) {
		throw new RuntimeException("Could not init JAXB :" + e.getMessage(), e);
	}
	if(log_.isInfoEnabled()) {
		//log_.info("Initialized JAXB");
	}
  }
  
}

