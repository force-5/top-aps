/**
 * CareCentral_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.force5solutions.care.web;

public interface CareCentral_PortType extends java.rmi.Remote {
    public java.lang.String getEntitlementRoleId(java.lang.String in0, java.lang.String in1, long in2) throws java.rmi.RemoteException;
    public void validateUser(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException;
    public boolean createEntitlement(java.lang.String in0, java.lang.String in1, com.force5solutions.care.web.EntitlementDTO in2) throws java.rmi.RemoteException;
    public boolean createEntitlementRole(java.lang.String in0, java.lang.String in1, com.force5solutions.care.web.EntitlementRoleDTO in2) throws java.rmi.RemoteException;
    public java.lang.String getRequiredCertificationIdsForEntitlementPolicyOnAGiveDate(java.lang.String in0, java.lang.String in1, long in2, java.lang.String in3, long in4) throws java.rmi.RemoteException;
    public java.lang.Object this$Dist$Invoke$2(java.lang.String in0, java.lang.Object in1) throws java.rmi.RemoteException;
    public boolean markCcEntitlementRoleAsDeleted(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public void markWorkflowAsAborted(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public com.force5solutions.care.web.WorkerEntitlementRoleDTO getWorkerEntitlementRole(java.lang.String in0, java.lang.String in1, long in2) throws java.rmi.RemoteException;
    public boolean processEntitlementManagerResponse(java.lang.String in0, java.lang.String in1, long in2, java.lang.String in3) throws java.rmi.RemoteException;
    public java.lang.Object this$Dist$Get$2(java.lang.String in0) throws java.rmi.RemoteException;
    public void changeWorkflowTaskStatusToPending(java.lang.String in0, java.lang.String in1, long in2) throws java.rmi.RemoteException;
    public void this$Dist$Set$2(java.lang.String in0, java.lang.Object in1) throws java.rmi.RemoteException;
    public boolean triggerProvisionerDeprovisionerTaskOnRoleUpdateWorkflowInCentral(java.lang.String in0, java.lang.String in1, com.force5solutions.care.web.TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO in2) throws java.rmi.RemoteException;
}
