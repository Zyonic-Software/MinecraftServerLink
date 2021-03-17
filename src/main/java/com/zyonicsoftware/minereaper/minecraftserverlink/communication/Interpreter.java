package com.zyonicsoftware.minereaper.minecraftserverlink.communication;

import com.zyonicsoftware.minereaper.minecraftserverlink.communication.objects.MSLConnection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;

public class Interpreter {

    InformationParser informationParser;

    public Interpreter(InformationParser informationParser) {
        this.informationParser = informationParser;
    }

    public void interpretServer(String input, MSLConnection connection) throws IOException, ClassNotFoundException {
        informationParser.parseObject(this.decode(Base64.getDecoder().decode(input)));
    }

    public void read(String input) throws IOException, ClassNotFoundException {
        informationParser.parseObject(this.decode(Base64.getDecoder().decode(input)));
    }


    private Object decode(byte[] input) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(input);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

}
