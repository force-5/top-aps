/**
 * WorkerEntitlementRoleDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.force5solutions.care.web;

public class WorkerEntitlementRoleDTO  implements java.io.Serializable {
    private java.lang.String id;

    private java.lang.String workerName;

    public WorkerEntitlementRoleDTO() {
    }

    public WorkerEntitlementRoleDTO(
           java.lang.String id,
           java.lang.String workerName) {
           this.id = id;
           this.workerName = workerName;
    }


    /**
     * Gets the id value for this WorkerEntitlementRoleDTO.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this WorkerEntitlementRoleDTO.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the workerName value for this WorkerEntitlementRoleDTO.
     * 
     * @return workerName
     */
    public java.lang.String getWorkerName() {
        return workerName;
    }


    /**
     * Sets the workerName value for this WorkerEntitlementRoleDTO.
     * 
     * @param workerName
     */
    public void setWorkerName(java.lang.String workerName) {
        this.workerName = workerName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WorkerEntitlementRoleDTO)) return false;
        WorkerEntitlementRoleDTO other = (WorkerEntitlementRoleDTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.workerName==null && other.getWorkerName()==null) || 
             (this.workerName!=null &&
              this.workerName.equals(other.getWorkerName())));
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getWorkerName() != null) {
            _hashCode += getWorkerName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WorkerEntitlementRoleDTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://web.care.force5solutions.com", "WorkerEntitlementRoleDTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://web.care.force5solutions.com", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("workerName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://web.care.force5solutions.com", "workerName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
