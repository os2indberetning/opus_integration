//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.11.13 at 11:43:37 AM CET 
//


package eu.kmd.opus;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="suppId" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
"value"
})
@XmlRootElement(name = "cpr")
public class Cpr
{

    @XmlValue
    protected String value;
    @XmlAttribute(name = "suppId")
    protected Byte suppId;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Gets the value of the suppId property.
     *
     * @return possible object is
     * {@link Byte }
     */
    public Byte getSuppId()
    {
        return suppId;
    }

    /**
     * Sets the value of the suppId property.
     *
     * @param value allowed object is
     *              {@link Byte }
     */
    public void setSuppId(Byte value)
    {
        this.suppId = value;
    }

}
