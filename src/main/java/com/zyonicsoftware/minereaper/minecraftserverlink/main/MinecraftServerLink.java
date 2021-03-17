package com.zyonicsoftware.minereaper.minecraftserverlink.main;

import com.zyonicsoftware.minereaper.minecraftserverlink.communication.CommunicationManager;

import java.io.IOException;

public class MinecraftServerLink {

    public enum Type {
        SERVER,
        CLIENT
    }

    private final int port;
    private String hostname;
    private final Type type;
    private CommunicationManager communicationManager;

    public MinecraftServerLink(final int port) {
        this.port = port;
        this.type = Type.SERVER;
        this.communicationManager = new CommunicationManager(this);
    }

    public MinecraftServerLink(final String hostname, final int port) {
        this.hostname = hostname;
        this.port = port;
        this.type = Type.CLIENT;
        this.communicationManager = new CommunicationManager(this);
    }

    public void startCommunicator() throws IOException {
        this.communicationManager.start();
    }

    public Type getType() {
        return type;
    }

    public int getPort() {
        return port;
    }

    public String getHostname() {
        return hostname;
    }
}
