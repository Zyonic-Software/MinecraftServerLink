package com.zyonicsoftware.minereaper.minecraftserverlink.communication.objects;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MSLConnection {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    public MSLConnection(final Socket socket, final PrintWriter writer, final BufferedReader reader) {
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
    }

    public BufferedReader getInput() {
        return reader;
    }

    public PrintWriter getOutput() {
        return writer;
    }

    public Socket getSocket() {
        return socket;
    }
}
