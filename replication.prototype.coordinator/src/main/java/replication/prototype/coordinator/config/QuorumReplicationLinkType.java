//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.06.14 at 10:33:55 PM CEST 
//


package replication.prototype.coordinator.config;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for QuorumReplicationLinkType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QuorumReplicationLinkType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ise.tu-berlin.de/AEC/Prototyping/Replication}ReplicationLinkType">
 *       &lt;sequence>
 *         &lt;element name="qparticipant" maxOccurs="unbounded" minOccurs="2">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="qsize" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuorumReplicationLinkType", propOrder = {
    "qparticipant"
})
public class QuorumReplicationLinkType
    extends ReplicationLinkType
{

    @XmlElement(required = true)
    protected List<QuorumReplicationLinkType.Qparticipant> qparticipant;
    @XmlAttribute(name = "qsize", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger qsize;

    /**
     * Gets the value of the qparticipant property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qparticipant property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQparticipant().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QuorumReplicationLinkType.Qparticipant }
     * 
     * 
     */
    public List<QuorumReplicationLinkType.Qparticipant> getQparticipant() {
        if (qparticipant == null) {
            qparticipant = new ArrayList<QuorumReplicationLinkType.Qparticipant>();
        }
        return this.qparticipant;
    }

    /**
     * Gets the value of the qsize property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getQsize() {
        return qsize;
    }

    /**
     * Sets the value of the qsize property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setQsize(BigInteger value) {
        this.qsize = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Qparticipant {

        @XmlAttribute(name = "name", required = true)
        protected String name;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

    }

}
