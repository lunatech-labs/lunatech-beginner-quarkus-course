# Getting Started


## Hello World Demo

Note:
Demo the following:
* Generate a project on the Quarkus website
* Run it with `./mvnw compile quarkus:dev` downloaded directory (we compile first, to show the speed of quarkus in the second command)
* Show http://localhost:8080/
* Import in Intellij
* Add a new endpoint, that returns 'Hello ' + name of the group you're teaching.
* Make a Java error, show the exception page
* Fix the error, show people that no restart is needed
* Show the Dev UI
* Start the generation of the native executable! (Already, because it takes time!)


## Why Quarkus exists

* *Developer Joy*
* Supersonic Subatomic
* Unifies Imperative & Reactive
* Best of breed libraries and frameworks

Note:
At this slide, we explain _developer joy_ only:
* Zero config (note there is no config file yet in this hello world app!). Show the config of the Dev UI, so people see how easy it is to lookup all possible configuration options.
* Live reload (already shown)
* Standards (Jax-RS in this example)
* Unified config (we'll see this later, everything in one config file)
* Streamlined code (a lot of stuff works out of the box, but you can fall back to full framework capabilitis when needed)
* No hassle native executable generation (show the results, and run the native image. Highlight startup time!)

Explain that in the coming two days, we will dive deeper into each of these to fully grasp what they mean and why they are important.


## Hello World in Quarkus

We can use JAX-RS annotations to create a Hello World endpoint:

```java
package com.lunatech.training.quarkus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("hello")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello world!";
    }
}
```


## JAX-RS & RESTeasy

JAX-RS is a Jakarta EE API spec. It contains annotations such as:

* `@Path`
* `@GET`, `@PUT`, `@POST`, `@DELETE`, `@HEAD`
* `@Produces` and `@Consumes`
* `@PathParam`, `@QueryParam`, `@HeaderParam` and more...

So, a standard way of describing RESTful web services


## JAX-RS & RESTeasy

RESTeasy is the Red Hat _implementation_ of the JAX-RS standard. It's what Quarkus uses to provide web services.


## Hello World in Quarkus with Spring compatiblity

Or use the Quarkus extension for Spring Web API:

```java
package com.lunatech.training.quarkus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloResourceSpring {

    @GetMapping("hello-spring")
    public String hello() {
        return "Hello from Spring!";
    }

}
```


## Quarkus & Spring

Quarkus has _Spring Compatiblity_ extensions; the annotations for several popular Spring components can be used in Quarkus.

Primarily useful for porting existing Spring applications.

Note:
Discuss some of the limitations; like that Quarkus still requires beans to be resolved compile-time, so you can't use
`@Conditional` for example.


## Quarkus and Microprofile 

The JAX-RS API's are part of Eclipse Microprofile (and Jakarta EE!)

Eclipse Microprofile is a set of APIs suitable for Microservices that can be implemented by vendors:

* CDI
* JSON-B
* JAX-RS
* Config  
* Health
* Context Propagation
* OpenAPI
* OpenTracing
* Fault Tolerance
* Metrics
* ... and more

Quarkus implements these APIs (among others!) and is thus a runtime for Microprofile applications.

Note:
* Some of these APIs are just Jakarta EE APIs. Others are Microprofile specific.
* Both Microprofile and Jakarta EE now fall under the Eclipse foundation. Further integration between the 
two seems likely.


## Quarkus and Microprofile

Typically, Quarkus uses the _SmallRye_ implementation of these APIs. SmallRye is a RedHat project, and is also used by 
WildFly, Thorntail and Open Liberty. 


## Quarkus and Vert.x

Quarkus is built on top of Vert.x, so many Vert.x APIs and types are also available in Quarkus. 

For example, the `quarkus-routes` extension provides Vert.x annotations for creating HTTP endpoints.


## Quarkus and ...

Quarkus implements many APIs and supports a lot of frameworks. This is part of the Quarkus Philosophy.

Note:
* Explain to people that they can choose what they like
* Explain that it's typically a good idea to *not fight quarkus*. Some of the libraries are quite opinionated. If you yourself 
are also quite opinionated, that might not match well. You can spend a ton of time reconfiguring a framework, for little benefit. _Going with the flow_ is useful. Luckily, you have a choice of libraries to use :)


## Automatic JSON serialization

Given a class `Greet`:

```java
public class Greet {

    public final String subject;
    public final String greet;

    public Greet(String subject, String greet) {
        this.subject = subject;
        this.greet = greet;
    }

}
```


## Automatic JSON serialization

Quarkus and RESTeasy can automatically serialize it to JSON, if you tell it to produce JSON:

```java
@Path("hello-json")
public class HelloJsonResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Greet hello() {
        return new Greet("world", "Hello");
    }

}
```

This returns:

```json
{
  "subject": "world",
  "greet": "Hello"
}
```


## Automatic JSON serialization with Jackson

We can use Jackson annotations to change how the JSON is generated:

```java [|1,7]|]
import com.fasterxml.jackson.annotation.JsonProperty;

public class Greet {

    public final String subject;

    @JsonProperty("TheGreeting")
    public final String greet;

    public Greet(String subject, String greet) {
        this.subject = subject;
        this.greet = greet;
    }

}
```

Now we get the following output:

```json
{
  "subject": "world",
  "TheGreeting": "Hello"
}
```

Note: Jackson annotation to control JSON output


## Alternatives

But as you'd expect by now, Quarkus isn't tied to Jackson! This just happens to use Jackson, because we have the extension 
`quarkus-resteasy-jackson` installed. But if we prefer JSON-B instead (part of Microprofile!), we can use
the `quarkus-resteasy-jsonb` extension. And then we can use JSON-B annotations:

```java [|1,5|]
import javax.json.bind.annotation.JsonbProperty;

public class Greet {

    @JsonbProperty("sayWho")
    public final String subject;

    public final String greet;

    public Greet(String subject, String greet) {
        this.subject = subject;
        this.greet = greet;
    }

}
```


## Alternatives

We can also have both extensions installed. Quarkus will pick the right serialization framework based on the annotations
that are used by the class.

Note:
* You can leave it as an exercise to the reader to figure out which one prevails if there are annotations of both.


## Exercise #1: Hello World

* Generate a new Quarkus app on the Quarkus website
* Download it, import it in your IDE
* Create an endpoint on the `/hello` path that returns 'Hello World'
* Run it
* Browse to http://localhost:8080/hello and observer your message :)

Extra:
* Add a query parameter that let's you greet a different subject than 'world'


## Recap


// TODO, say something about that Quarkus typically has multiple ways of getting something done, you can pick your API
// Getting started is easy
// It's about four things (see above)
