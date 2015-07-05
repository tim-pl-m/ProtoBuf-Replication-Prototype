//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.05 um 11:55:30 PM CEST 
//


package replication.prototype.server.environment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für NodeType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="NodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="label" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ipadress" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="port" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="webserverport" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="location" type="{http://www.ise.tu-berlin.de/AEC/Prototyping/Replication}AwsLocationEnumType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NodeType")
public class NodeType {

    @XmlAttribute(name = "label", required = true)
    protected String label;
    @XmlAttribute(name = "ipadress")
    protected String ipadress;
    @XmlAttribute(name = "port")
    protected Integer port;
    @XmlAttribute(name = "webserverport")
    protected Integer webserverport;
    @XmlAttribute(name = "location")
    protected AwsLocationEnumType location;

    /**
     * Ruft den Wert der label-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Legt den Wert der label-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Ruft den Wert der ipadress-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpadress() {
        return ipadress;
    }

    /**
     * Legt den Wert der ipadress-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpadress(String value) {
        this.ipadress = value;
    }

    /**
     * Ruft den Wert der port-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Legt den Wert der port-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPort(Integer value) {
        this.port = value;
    }

    /**
     * Ruft den Wert der webserverport-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWebserverport() {
        return webserverport;
    }

    /**
     * Legt den Wert der webserverport-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWebserverport(Integer value) {
        this.webserverport = value;
    }

    /**
     * Ruft den Wert der location-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AwsLocationEnumType }
     *     
     */
    public AwsLocationEnumType getLocation() {
        return location;
    }

    /**
     * Legt den Wert der location-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AwsLocationEnumType }
     *     
     */
    public void setLocation(AwsLocationEnumType value) {
        this.location = value;
    }

}
