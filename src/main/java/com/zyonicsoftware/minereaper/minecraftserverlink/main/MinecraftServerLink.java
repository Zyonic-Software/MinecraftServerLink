package com.zyonicsoftware.minereaper.minecraftserverlink.main;

import com.zyonicsoftware.minereaper.minecraftserverlink.communication.CommunicationManager;
import com.zyonicsoftware.minereaper.minecraftserverlink.communication.InformationParser;
import com.zyonicsoftware.minereaper.minecraftserverlink.communication.objects.MSLObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MinecraftServerLink {

    public enum     Type {
        SERVER,
        CLIENT
    }

    private final int port;
    private String hostname;
    private final Type type;
    private final CommunicationManager communicationManager;

    public MinecraftServerLink(final int port, InformationParser informationParser) {
        this.port = port;
        this.type = Type.SERVER;
        this.communicationManager = new CommunicationManager(this, informationParser);
    }

    public MinecraftServerLink(final String hostname, final int port, InformationParser informationParser) {
        this.hostname = hostname;
        this.port = port;
        this.type = Type.CLIENT;
        this.communicationManager = new CommunicationManager(this, informationParser);
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

    public void sendObject(MSLObject object) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ObjectOutputStream objectOutputStream = null;
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            this.communicationManager.sendObject(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
