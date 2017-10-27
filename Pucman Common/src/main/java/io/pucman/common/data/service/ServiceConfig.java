package io.pucman.common.data.service;

import lombok.Getter;

/**
 * For storing all the configuration options for a data service.
 */
public abstract class ServiceConfig
{
    /**
     * Address of the service.
     */
    private String address;

    /**
     * Port of the service.
     */
    private int port;

    /**
     * The clients password.
     */
    @Getter
    private String password;

    /**
     * The clients username.
     */
    @Getter
    private String username;

    public ServiceConfig(String address, int port, String password, String username)
    {
        this.address = address;
        this.port = port;
        this.password = password;
        this.username = username;
    }

    /**
     * The full address of the service.
     * @return the full address.
     */
    public String getAddress()
    {
        return this.address + ":" + String.valueOf(this.port);
    }
}
