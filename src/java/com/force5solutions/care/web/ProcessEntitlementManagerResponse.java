/**
 * ProcessEntitlementManagerResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.force5solutions.care.web;

public class ProcessEntitlementManagerResponse  implements java.io.Serializable {
    private java.lang.String in0;

    private java.lang.String in1;

    private long in2;

    private java.lang.String in3;

    public ProcessEntitlementManagerResponse() {
    }

    public ProcessEntitlementManagerResponse(
           java.lang.String in0,
           java.lang.String in1,
           long in2,
           java.lang.String in3) {
           this.in0 = in0;
           this.in1 = in1;
           this.in2 = in2;
           this.in3 = in3;
    }


    /**
     * Gets the in0 value for this ProcessEntitlementManagerResponse.
     * 
     * @return in0
     */
    public java.lang.String getIn0() {
        return in0;
    }


    /**
     * Sets the in0 value for this ProcessEntitlementManagerResponse.
     * 
     * @param in0
     */
    public void setIn0(java.lang.String in0) {
        this.in0 = in0;
    }


    /**
     * Gets the in1 value for this ProcessEntitlementManagerResponse.
     * 
     * @return in1
     */
    public java.lang.String getIn1() {
        return in1;
    }


    /**
     * Sets the in1 value for this ProcessEntitlementManagerResponse.
     * 
     * @param in1
     */
    public void setIn1(java.lang.String in1) {
        this.in1 = in1;
    }


    /**
     * Gets the in2 value for this ProcessEntitlementManagerResponse.
     * 
     * @return in2
     */
    public long getIn2() {
        return in2;
    }


    /**
     * Sets the in2 value for this ProcessEntitlementManagerResponse.
     * 
     * @param in2
     */
    public void setIn2(long in2) {
        this.in2 = in2;
    }


    /**
     * Gets the in3 value for this ProcessEntitlementManagerResponse.
     * 
     * @return in3
     */
    public java.lang.String getIn3() {
        return in3;
    }


    /**
     * Sets the in3 value for this ProcessEntitlementManagerResponse.
     * 
     * @param in3
     */
    public void setIn3(java.lang.String in3) {
        this.in3 = in3;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProcessEntitlementManagerResponse)) return false;
        ProcessEntitlementManagerResponse other = (ProcessEntitlementManagerResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.in0==null && other.getIn0()==null) || 
             (this.in0!=null &&
              this.in0.equals(other.getIn0()))) &&
            ((this.in1==null && other.getIn1()==null) || 
             (this.in1!=null &&
              this.in1.equals(other.getIn1()))) &&
            this.in2 == other.getIn2() &&
            ((this.in3==null && other.getIn3()==null) || 
             (this.in3!=null &&
              this.in3.equals(other.getIn3())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getIn0() != null) {
            _hashCode += getIn0().hashCode();
        }
        if (getIn1() != null) {
            _hashCode += getIn1().hashCode();
        }
        _hashCode += new Long(getIn2()).hashCode();
        if (getIn3() != null) {
            _hashCode += getIn3().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProcessEntitlementManagerResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://web.care.force5solutions.com", ">processEntitlementManagerResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("in0");
        elemField.setXmlName(new javax.xml.namespace.QName("http://web.care.force5solutions.com", "in0"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("in1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://web.care.force5solutions.com", "in1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("in2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://web.care.force5solutions.com", "in2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("in3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://web.care.force5solutions.com", "in3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
