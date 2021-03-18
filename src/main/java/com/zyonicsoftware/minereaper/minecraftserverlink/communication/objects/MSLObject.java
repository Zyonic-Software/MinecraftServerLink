package com.zyonicsoftware.minereaper.minecraftserverlink.communication.objects;

import com.google.gson.Gson;

import java.io.Serializable;

public class MSLObject implements Serializable {

    private final String object;
    private final String objectIdentifier;

    public MSLObject(Object object, String objectIdentifier) {
        Gson gson = new Gson();
        this.object = gson.toJson(object);
        this.objectIdentifier = objectIdentifier;
    }

    public Object getObject(Class targetClass) {
        Gson gson = new Gson();
        return gson.fromJson(object, targetClass);
    }

    public String getObjectIdentifier() {
        return objectIdentifier;
    }

    public String toString() {
        return "MSLObject [object=" + object + ", objectIdentifier = " + objectIdentifier + "]";
    }
}
