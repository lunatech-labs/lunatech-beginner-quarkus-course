##Programming réactive avancée

## Connaissances obtenues
* Comprendre Even Bus
* Comprendre websocket réactive

##Utilisation de Event Bus

Quarkus uses the Vert.x EventBus, so you need to enable the vertx extension to use this feature:

```xml
<dependency>
<groupId>io.quarkus</groupId>
<artifactId>quarkus-vertx</artifactId>
</dependency>
```