//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.12.17 at 11:05:18 AM CET 
//


package com.stratio.crossdata.common.manifest;

import java.io.Serializable;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
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
public class DataStoreFactory implements Serializable {

    private final static QName _DataStore_QNAME = new QName("", "DataStore");
    private static final long serialVersionUID = 1484235131542012575L;

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public DataStoreFactory() {
    }

    public static QName get_DataStore_QNAME() {
        return _DataStore_QNAME;
    }

    /**
     * Create an instance of {@link DataStoreType }
     * 
     */
    public DataStoreType createDataStoreType() {
        return new DataStoreType();
    }

    /**
     * Create an instance of {@link PropertiesType }
     * 
     */
    public PropertiesType createPropertiesType() {
        return new PropertiesType();
    }

    /**
     * Create an instance of {@link PropertyType }
     * 
     */
    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    /**
     * Create an instance of {@link BehaviorsType }
     * 
     */
    public BehaviorsType createBehaviorsType() {
        return new BehaviorsType();
    }

    /**
     * Create an instance of {@link FunctionType }
     * 
     */
    public FunctionType createFunctionType() {
        return new FunctionType();
    }

    /**
     * Create an instance of {@link FunctionsType }
     * 
     */
    public FunctionsType createFunctionsType() {
        return new FunctionsType();
    }

    /**
     * Create an instance of {@link ExcludeType }
     * 
     */
    public ExcludeType createExcludeType() {
        return new ExcludeType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link DataStoreType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DataStore")
    public JAXBElement<DataStoreType> createDataStore(DataStoreType value) {
        return new JAXBElement<DataStoreType>(_DataStore_QNAME, DataStoreType.class, null, value);
    }

}
