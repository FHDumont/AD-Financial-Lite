package com.appdynamics.sample.service;

public interface JMSListenerCallback
{
    public void messageReceived(String speed, String traffic);
}