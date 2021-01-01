//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.14-hudson-jaxb-ri-2.1-60--SNAPSHOT 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.06 at 11:57:12 AM PST 
//


package com.oracle.cep.xml.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ShipPositions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShipPositions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ShipPos" type="{http://xmlns.oracle.com/ns/spatialdemo}ShipPosType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShipPositions", propOrder = {
    "shipPos"
})
public class ShipPositions
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "ShipPos", required = true)
    protected List<ShipPosType> shipPos;

    /**
     * Gets the value of the shipPos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shipPos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShipPos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShipPosType }
     * 
     * 
     */
    public List<ShipPosType> getShipPos() {
        if (shipPos == null) {
            shipPos = new ArrayList<ShipPosType>();
        }
        return this.shipPos;
    }

}