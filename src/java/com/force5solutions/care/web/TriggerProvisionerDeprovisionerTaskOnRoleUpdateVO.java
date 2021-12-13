/**
 * TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.force5solutions.care.web;

public class TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO  implements java.io.Serializable {
    private java.lang.String guid;

    private java.lang.Long workerEntitlementRoleId;

    public TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO() {
    }

    public TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO(
           java.lang.String guid,
           java.lang.Long workerEntitlementRoleId) {
           this.guid = guid;
           this.workerEntitlementRoleId = workerEntitlementRoleId;
    }


    /**
     * Gets the guid value for this TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO.
     * 
     * @return guid
     */
    public java.lang.String getGuid() {
        return guid;
    }


    /**
     * Sets the guid value for this TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO.
     * 
     * @param guid
     */
    public void setGuid(java.lang.String guid) {
        this.guid = guid;
    }


    /**
     * Gets the workerEntitlementRoleId value for this TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO.
     * 
     * @return workerEntitlementRoleId
     */
    public java.lang.Long getWorkerEntitlementRoleId() {
        return workerEntitlementRoleId;
    }


    /**
     * Sets the workerEntitlementRoleId value for this TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO.
     * 
     * @param workerEntitlementRoleId
     */
    public void setWorkerEntitlementRoleId(java.lang.Long workerEntitlementRoleId) {
        this.workerEntitlementRoleId = workerEntitlementRoleId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO)) return false;
        TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO other = (TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.guid==null && other.getGuid()==null) || 
             (this.guid!=null &&
              this.guid.equals(other.getGuid()))) &&
            ((this.workerEntitlementRoleId==null && other.getWorkerEntitlementRoleId()==null) || 
             (this.workerEntitlementRoleId!=null &&
              this.workerEntitlementRoleId.equals(other.getWorkerEntitlementRoleId())));
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
        if (getGuid() != null) {
            _hashCode += getGuid().hashCode();
        }
        if (getWorkerEntitlementRoleId() != null) {
            _hashCode += getWorkerEntitlementRoleId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://workflow.care.force5solutions.com", "TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("guid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://workflow.care.force5solutions.com", "guid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("workerEntitlementRoleId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://workflow.care.force5solutions.com", "workerEntitlementRoleId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
