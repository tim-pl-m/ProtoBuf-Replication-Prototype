//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.05 um 11:55:30 PM CEST 
//


package replication.prototype.server.environment;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AwsLocationEnumType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="AwsLocationEnumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EU_WEST_1"/>
 *     &lt;enumeration value="US_EAST_1"/>
 *     &lt;enumeration value="US_WEST_1"/>
 *     &lt;enumeration value="US_WEST_2"/>
 *     &lt;enumeration value="AP_SOUTHEAST_1"/>
 *     &lt;enumeration value="AP_SOUTHEAST_2"/>
 *     &lt;enumeration value="AP_NORTHEAST_1"/>
 *     &lt;enumeration value="CN_NORTH_1"/>
 *     &lt;enumeration value="SA_EAST_1"/>
 *     &lt;enumeration value="EU_CENTRAL_1"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AwsLocationEnumType")
@XmlEnum
public enum AwsLocationEnumType {

    EU_WEST_1,
    US_EAST_1,
    US_WEST_1,
    US_WEST_2,
    AP_SOUTHEAST_1,
    AP_SOUTHEAST_2,
    AP_NORTHEAST_1,
    CN_NORTH_1,
    SA_EAST_1,
    EU_CENTRAL_1;

    public String value() {
        return name();
    }

    public static AwsLocationEnumType fromValue(String v) {
        return valueOf(v);
    }

}
