package com.dc.lockphone.model;

/**
 * Created by dcoellar on 11/10/15.
 */
public class NoDeviceException extends RuntimeException
{
    public NoDeviceException(String message)
    {
        super(message);
    }
}