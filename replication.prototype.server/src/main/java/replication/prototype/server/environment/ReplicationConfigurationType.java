//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.05 um 11:55:30 PM CEST 
//


package replication.prototype.server.environment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ReplicationConfigurationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ReplicationConfigurationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="replicationpaths" type="{http://www.ise.tu-berlin.de/AEC/Prototyping/Replication}ReplicationPathConfigurationType"/>
 *         &lt;element name="replicationnodes" type="{http://www.ise.tu-berlin.de/AEC/Prototyping/Replication}ReplicationNodeConfigurationType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement( name = "configuration" )
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReplicationConfigurationType", propOrder = {
    "replicationpaths",
    "replicationnodes"
})
public class ReplicationConfigurationType {

    @XmlElement(required = true)
    protected ReplicationPathConfigurationType replicationpaths;
    @XmlElement(required = true)
    protected ReplicationNodeConfigurationType replicationnodes;

    /**
     * Ruft den Wert der replicationpaths-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReplicationPathConfigurationType }
     *     
     */
    public ReplicationPathConfigurationType getReplicationpaths() {
        return replicationpaths;
    }

    /**
     * Legt den Wert der replicationpaths-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplicationPathConfigurationType }
     *     
     */
    public void setReplicationpaths(ReplicationPathConfigurationType value) {
        this.replicationpaths = value;
    }

    /**
     * Ruft den Wert der replicationnodes-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReplicationNodeConfigurationType }
     *     
     */
    public ReplicationNodeConfigurationType getReplicationnodes() {
        return replicationnodes;
    }

    /**
     * Legt den Wert der replicationnodes-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplicationNodeConfigurationType }
     *     
     */
    public void setReplicationnodes(ReplicationNodeConfigurationType value) {
        this.replicationnodes = value;
    }

}
