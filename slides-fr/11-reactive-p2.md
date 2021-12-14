##Programming réactive avancée

## Connaissances obtenues
* Comprendre Even Bus
* Comprendre websocket réactive

##Utilisation de Event Bus

Quarkus utilise le Vert.x EventBus, vous devez donc activer l'extension vertx pour utiliser cette fonctionnalité :

```xml
<dependency>
<groupId>io.quarkus</groupId>
<artifactId>quarkus-vertx</artifactId>
</dependency>
```
### Envoi de messages

```java
@Path("/async")
public class GreetingResource {

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

### Envoi de messages
L'objet EventBus fournit des méthodes pour :
1. Envoyer un message à une adresse spécifique - un seul consommateur reçoit le message.
2. Publier un message à une adresse spécifique - tous les consommateurs reçoivent les messages.
3. Envoyer un message et attendre une réponse.

``` [|1|8|9-10|]
// Cas 1
bus.sendAndForget("greeting", name)
// Cas 2
bus.publish("greeting", name)
// Cas 3
Uni<String> response = bus.<String>request("address", "Hello")
.onItem().transform(Message::body);
```

### Configuration de l'adresse reçue

```java[|4|]
@ConsumeEvent("greeting")               
public String consume(String name) {
return name.toUpperCase();
}
```
L'adresse pour recevoir les messages est `greeting`