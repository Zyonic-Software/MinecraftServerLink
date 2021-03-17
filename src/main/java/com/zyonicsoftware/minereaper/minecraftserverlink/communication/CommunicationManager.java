package com.zyonicsoftware.minereaper.minecraftserverlink.communication;

import com.zyonicsoftware.minereaper.minecraftserverlink.communication.objects.MSLConnection;
import com.zyonicsoftware.minereaper.minecraftserverlink.main.MinecraftServerLink;
import com.zyonicsoftware.minereaper.minecraftserverlink.util.ThreadHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class CommunicationManager {

    //Server
    private final MinecraftServerLink minecraftServerLink;
    private final HashMap<Socket, Runnable> communicationProcesses;
    private final Interpreter interpreter;
    private Runnable connectionProcess;
    private boolean connectorRun;
    private ServerSocket serverSocket;
    private ArrayList<MSLConnection> connections;

    //Client
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private boolean readerrun;
    private Runnable readerProcess;

    public CommunicationManager(MinecraftServerLink minecraftServerLink, InformationParser informationParser) {
        this.minecraftServerLink = minecraftServerLink;
        this.communicationProcesses = new HashMap<>();
        this.interpreter = new Interpreter(informationParser);
    }

    public void start() throws IOException {
        if(this.minecraftServerLink.getType().equals(MinecraftServerLink.Type.CLIENT)) {
            this.startClientSide(this.minecraftServerLink.getHostname(), this.minecraftServerLink.getPort());
            this.serverSocket = new ServerSocket(this.minecraftServerLink.getPort());
        } else {
            this.startServerSide();
        }
    }

    private void startServerSide() {
        System.out.println("[MinecraftServerLink] Connector opened");
        ThreadHandler.startExecute(this.connectionProcess = () -> {
            while (this.connectorRun) {
                try {
                    Socket clientSocket = this.serverSocket.accept();
                    System.out.println("[MinecraftServerLink] Connection incoming...");
                    if(clientSocket != null) {
                        System.out.println("[MinecraftServerLink] New Connection on " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                        MSLConnection mslConnection = new MSLConnection(clientSocket, new PrintWriter(clientSocket.getOutputStream(), true), new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
                        this.connections.add(mslConnection);
                        this.startReadWriteHead(mslConnection);
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    //ServerReaderCode
    private void startReadWriteHead(MSLConnection connection) {
        final Runnable headProcess = () -> {
            String currentIncomingLine;
            boolean thisrun = true;
            while (this.connectorRun && thisrun) {
                try {
                    if (connection.getSocket().isBound() && connection.getSocket().isConnected()) {
                        currentIncomingLine = connection.getInput().readLine();
                        if(currentIncomingLine != null) {
                            try {
                                interpreter.interpretServer(currentIncomingLine, connection);
                            } catch (Exception e) {
                                System.err.println("[MinecraftServerLink] Interpreter Failure");
                                e.printStackTrace();
                            }
                        } else {
                            thisrun = false;
                            System.out.println("[MinecraftServerLink] Connection closed on "+ connection.getSocket().getInetAddress() + ":" + connection.getSocket().getPort());
                            connection.getSocket().close();
                            connection.getOutput().close();
                            connection.getInput().close();
                            ThreadHandler.removeExecute(communicationProcesses.get(connection.getSocket()));
                        }
                    } else {
                        thisrun = false;
                        System.out.println("[MinecraftServerLink] Connection closed on "+ connection.getSocket().getInetAddress() + ":" + connection.getSocket().getPort());
                        connection.getSocket().close();
                        connection.getOutput().close();
                        connection.getInput().close();
                        ThreadHandler.removeExecute(communicationProcesses.get(connection.getSocket()));
                    }
                } catch (Exception ignored) {
                    try {
                        thisrun = false;
                        System.out.println("[MinecraftServerLink] Connection closed on " + connection.getSocket().getInetAddress() + ":" + connection.getSocket().getPort());
                        connection.getSocket().close();
                        connection.getOutput().close();
                        connection.getInput().close();
                        ThreadHandler.removeExecute(communicationProcesses.get(connection.getSocket()));
                    } catch (Exception ignored2) { }
                }
            }
            thisrun = false;
            System.out.println("[MinecraftServerLink] Connection closed on "+ connection.getSocket().getInetAddress() + ":" + connection.getSocket().getPort());
            try {
                connection.getSocket().close();
                connection.getOutput().close();
                connection.getInput().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ThreadHandler.removeExecute(communicationProcesses.get(connection.getSocket()));
        };
        this.communicationProcesses.put(connection.getSocket(), headProcess);
        ThreadHandler.startExecute(headProcess);
    }

    private void startClientSide(String hostname, int port) throws IOException {
        this.socket = new Socket(hostname, port);
        System.out.println("[MMS-Connector] Established Connection on " + this.socket.getInetAddress() + ":" + this.socket.getPort());
        this.writer = new PrintWriter(this.socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.readerrun = true;

        System.out.println("[MinecraftServerLink] Initializing Reader");

        ThreadHandler.startExecute(this.readerProcess = () -> {
            System.out.println("[MinecraftServerLink] Reading");
            while (this.readerrun) {
                try {
                    if(this.socket.isBound() && this.socket.isConnected()) {
                        String currentLine = this.reader.readLine();
                        if (currentLine != null) {
                            this.interpreter.read(currentLine);
                        }
                    } else {
                        this.readerrun = false;
                        this.reader.close();
                        this.socket.close();
                        this.readerrun = false;
                        ThreadHandler.removeExecute(this.readerProcess);
                        System.out.println("[MinecraftServerLink] Reader stopped");
                    }
                } catch (IOException | ClassNotFoundException ignored1) {
                    try {
                        this.readerrun = false;
                        this.reader.close();
                        this.socket.close();
                        this.writer.close();
                        this.readerrun = false;
                        ThreadHandler.removeExecute(this.readerProcess);
                        System.out.println("[MinecraftServerLink] Disconnected");
                    } catch (Exception ignored) { }
                }
            }
        });
    }

    public void sendObject(Object object) {
        this.connections.forEach(connection -> {
            connection.getOutput().println(Base64.getEncoder().encodeToString((byte[]) object));
        });
    }

    public void disconnect() throws IOException {
        this.readerrun = false;
        this.connectorRun = false;
        this.writer.close();
        this.reader.close();
        this.socket.close();
        ThreadHandler.removeExecute(this.readerProcess);
    }

}
