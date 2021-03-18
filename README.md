# MinecraftServerLink

Library used to send Objects between (Minecraft) Servers

MSL Can be used to Send objects containing anything between Servers.

MSL can communicate with multiple Clients, with one Instance serving as the Server / Relay.

To start MSL you first need a "MinecraftServerLink" Object. 
You can Initialize with just a Port, which will launch MSL in Server mode.
If you want to Start MSL in Client mode, you will have to provide a Hostname.
In both Cases you will need an Object extending InformationParser.

### Information Parser

```Java
public class InformationHandler implements InformationParser {

  @Override
  public void parseObject(MSLObject mslObject) { //MSLObject is the Encoding Object, Documented later.
  
  }
}
```
### Starting MSL in Server mode

```Java
public class Test {
  
  private final MinecraftServerLink minecraftServerLink;
  
  public Test() {
    this.minecraftServerLink = new MinecraftServerLink(1010, new InformationHandler());
  }
  
}
```

### Starting MSL in Client mode

```Java
public class Test {
  
  private final MinecraftServerLink minecraftServerLink;
  
  public Test() {
    this.minecraftServerLink = new MinecraftServerLink("test.test", 1010, new InformationHandler());//This will connect to the instance we started before.
  }
  
}
```


### Sending Object

```Java
public class Test {
  
  private final MinecraftServerLink minecraftServerLink;
  
  public Test() {
    this.minecraftServerLink = new MinecraftServerLink("test.test", 1010, new InformationHandler());
  }
  
  public void send(YourObject yourObject) {
    this.minecraftServerLink.sendObject(new MSLObject(yourObject, "a String telling your System what this object is for");
  }
  
}
```


### Receiving Object

```Java
public class InformationHandler implements InformationParser {

  @Override
  public void parseObject(MSLObject mslObject) { //MSLObject is the Encoding Object, Documented later.
    if(mslObject.getObjectIdentifier().equals("Your Identification String") {
      YourObject object = mslObject.getObject(YourObject.class);
    }
  }
}
```
