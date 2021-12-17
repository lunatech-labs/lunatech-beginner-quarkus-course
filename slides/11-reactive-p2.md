# Reactive programming 2

## Learning outcomes

After this module, you should:
* Understand Event Bus

### Using the Event Bus

Quarkus uses the Vert.x EventBus, so you need to enable the vertx extension to use this feature:

```xml
<dependency>
<groupId>io.quarkus</groupId>
<artifactId>quarkus-vertx</artifactId>
</dependency>
```
### Sending messages

```java
@Path("/async")
public class EventResource {

    @Inject
    EventBus bus;                                       

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{name}")
    public Uni<String> greeting(@PathParam String name) {
        return bus.<String>request("greeting", name)        
                .onItem().transform(Message::body);
    }
}
```

### Sending messages
The EventBus object provides methods to:
1. send a message to a specific address - one single consumer receives the message.
2. publish a message to a specific address - all consumers receive the messages.
3. send a message and expect reply

``` [|1|8|9-10|]
// Case 1
bus.sendAndForget("greeting", name)
// Case 2
bus.publish("greeting", name)
// Case 3
Uni<String> response = bus.<String>request("address", "Hello")
.onItem().transform(Message::body);
```

### Configuring the address

```java[|4|]
@ConsumeEvent("greeting")               
public String consume(String name) {
return name.toUpperCase();
}
```
Adresse to receive the messages is `greeting`

### Using codecs
The Vert.x Event Bus uses codecs to serialize and deserialize objects.
Quarkus provides a default codec for local delivery (class `LocalEventBusCodec.java`). So you can exchange objects as follows:

```java[|4|]
@GET
@Produces(MediaType.TEXT_PLAIN)
@Path("{name}")
public Uni<String> greeting(@PathParam String name) {
return bus.<String>request("greeting", new MyName(name))
.onItem().transform(Message::body);
}

@ConsumeEvent(value = "greeting")
Uni<String> greeting(MyName name) {
return Uni.createFrom().item(() -> "Hello " + name.getName());
}
```

You can also define a specific codec:
```java[|4|]
@GET
@Produces(MediaType.TEXT_PLAIN)
@Path("{name}")
public Uni<String> greeting(@PathParam String name) {
return bus.<String>request("greeting", name,
new DeliveryOptions().setCodecName(MyNameCodec.class.getName()))
.onItem().transform(Message::body);
}

@ConsumeEvent(value = "greeting", codec = MyNameCodec.class)            
Uni<String> greeting(MyName name) {
return Uni.createFrom().item(() -> "Hello "+name.getName());
}
```





