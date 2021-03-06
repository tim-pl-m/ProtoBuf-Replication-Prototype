//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.05 um 11:55:30 PM CEST 
//


package replication.prototype.coordinator.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r CommunicationEnumType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="CommunicationEnumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="quorum"/>
 *     &lt;enumeration value="async"/>
 *     &lt;enumeration value="sync"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CommunicationEnumType")
@XmlEnum
public enum CommunicationEnumType {

    @XmlEnumValue("quorum")
    QUORUM("quorum"),
    @XmlEnumValue("async")
    ASYNC("async"),
    @XmlEnumValue("sync")
    SYNC("sync");
    private final String value;

    CommunicationEnumType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CommunicationEnumType fromValue(String v) {
        for (CommunicationEnumType c: CommunicationEnumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
