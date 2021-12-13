/**
 * CareCentral_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.force5solutions.care.web;

public class CareCentral_ServiceLocator extends org.apache.axis.client.Service implements com.force5solutions.care.web.CareCentral_Service {

    public CareCentral_ServiceLocator() {
    }


    public CareCentral_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CareCentral_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for careCentralHttpPort
    private java.lang.String careCentralHttpPort_address = com.force5solutions.care.UtilService.getCareCentralUrl();

    public java.lang.String getcareCentralHttpPortAddress() {
        return careCentralHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String careCentralHttpPortWSDDServiceName = "careCentralHttpPort";

    public java.lang.String getcareCentralHttpPortWSDDServiceName() {
        return careCentralHttpPortWSDDServiceName;
    }

    public void setcareCentralHttpPortWSDDServiceName(java.lang.String name) {
        careCentralHttpPortWSDDServiceName = name;
    }

    public com.force5solutions.care.web.CareCentral_PortType getcareCentralHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(careCentralHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getcareCentralHttpPort(endpoint);
    }

    public com.force5solutions.care.web.CareCentral_PortType getcareCentralHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.force5solutions.care.web.CareCentralHttpBindingStub _stub = new com.force5solutions.care.web.CareCentralHttpBindingStub(portAddress, this);
            _stub.setPortName(getcareCentralHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setcareCentralHttpPortEndpointAddress(java.lang.String address) {
        careCentralHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.force5solutions.care.web.CareCentral_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.force5solutions.care.web.CareCentralHttpBindingStub _stub = new com.force5solutions.care.web.CareCentralHttpBindingStub(new java.net.URL(careCentralHttpPort_address), this);
                _stub.setPortName(getcareCentralHttpPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("careCentralHttpPort".equals(inputPortName)) {
            return getcareCentralHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://web.care.force5solutions.com", "careCentral");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://web.care.force5solutions.com", "careCentralHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("careCentralHttpPort".equals(portName)) {
            setcareCentralHttpPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
