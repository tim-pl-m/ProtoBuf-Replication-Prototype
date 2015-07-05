//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.05 um 09:37:52 PM CEST 
//


package replication.prototype.server.environment;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the replication.prototype.server.environment package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Configuration_QNAME = new QName("http://www.ise.tu-berlin.de/AEC/Prototyping/Replication", "configuration");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: replication.prototype.server.environment
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link QuorumReplicationLinkType }
     * 
     */
    public QuorumReplicationLinkType createQuorumReplicationLinkType() {
        return new QuorumReplicationLinkType();
    }

    /**
     * Create an instance of {@link ReplicationConfigurationType }
     * 
     */
    public ReplicationConfigurationType createReplicationConfigurationType() {
        return new ReplicationConfigurationType();
    }

    /**
     * Create an instance of {@link ReplicationPathConfigurationType }
     * 
     */
    public ReplicationPathConfigurationType createReplicationPathConfigurationType() {
        return new ReplicationPathConfigurationType();
    }

    /**
     * Create an instance of {@link ReplicationPathType }
     * 
     */
    public ReplicationPathType createReplicationPathType() {
        return new ReplicationPathType();
    }

    /**
     * Create an instance of {@link NodeType }
     * 
     */
    public NodeType createNodeType() {
        return new NodeType();
    }

    /**
     * Create an instance of {@link ReplicationNodeConfigurationType }
     * 
     */
    public ReplicationNodeConfigurationType createReplicationNodeConfigurationType() {
        return new ReplicationNodeConfigurationType();
    }

    /**
     * Create an instance of {@link ReplicationLinkType }
     * 
     */
    public ReplicationLinkType createReplicationLinkType() {
        return new ReplicationLinkType();
    }

    /**
     * Create an instance of {@link QuorumReplicationLinkType.Qparticipant }
     * 
     */
    public QuorumReplicationLinkType.Qparticipant createQuorumReplicationLinkTypeQparticipant() {
        return new QuorumReplicationLinkType.Qparticipant();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReplicationConfigurationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ise.tu-berlin.de/AEC/Prototyping/Replication", name = "configuration")
    public JAXBElement<ReplicationConfigurationType> createConfiguration(ReplicationConfigurationType value) {
        return new JAXBElement<ReplicationConfigurationType>(_Configuration_QNAME, ReplicationConfigurationType.class, null, value);
    }

}
