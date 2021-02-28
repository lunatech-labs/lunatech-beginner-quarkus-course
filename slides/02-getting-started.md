# Getting Started


## Learning outcomes

After this module, you should:
* Understand the Quarkus philosophy and its motivations
* Witness how to generate a new Quarkus project from the Quarkus website
* Know how to run Quarkus from the terminal with the Maven wrapper script
* Know how to access the Dev UI


## Why Quarkus exists

> _Supersonic Subatomic Java ..._
> _A Kubernetes Native Java stack..._
> _crafted from the best of breed Java libraries and standards._

https://quarkus.io/

Note:
This is the tagline from the Quarkus homepage


## Why Quarkus exists

* *Java for the cloud-native age*
* Unifies Imperative & Reactive paradigms
* Developer Joy

Note:
This is where we can explain the Supersonic (fast startups) Subatomic (small memory footprint) tagline that encapsulates the "cloud-native" aims of Quarkus.

Contrast this with historical context of Enterprise Java frameworks that could accept slow startups and large memory footprint for applications that are meant to be long-lived and go through a "warm-up" phase before being optimised.


## Why Quarkus exists

* Java for the cloud-native age
* *Unifies Imperative & Reactive paradigms*
* Developer Joy

Note:
Can mention here that these two themes will be covered over the two days of the training course


## Why Quarkus exists

* Java for the cloud-native age
* Unifies Imperative & Reactive paradigms
* *Developer Joy*

Note:
At this slide, we explain _developer joy_ only:
* Zero config (note there is no config file yet in this hello world app!). Show the config of the Dev UI, so people see how easy it is to lookup all possible configuration options.
* Live reload (already shown)
* Standards (Jax-RS in this example)
* Unified config (we'll see this later, everything in one config file)
* Streamlined code (a lot of stuff works out of the box, but you can fall back to full framework capabilitis when needed)
* No hassle native executable generation (show the results, and run the native image. Highlight startup time!)

Explain that in the coming two days, we will dive deeper into each of these to fully grasp what they mean and why they are important.


## Based on standards, inspired by best practice
* Quarkus and many extensions are based on industry standards like Jakarta EE and MicroProfile
* Quarkus itself is built on a best-of-breed reactive framework Vert.x
* Quarkus inspired by developer experience of other frameworks like Spring Boot and Play Java
    * Quarkus even has a _Spring Compatibility_ extension


<!-- .slide: data-visibility="hidden" -->
## Quarkus & Spring

Quarkus has _Spring Compatiblity_ extensions; the annotations for several popular Spring components can be used in Quarkus.

Primarily useful for porting existing Spring applications.

Note:
Discuss some of the limitations; like that Quarkus still requires beans to be resolved compile-time, so you can't use
`@Conditional` for example.


<!-- .slide: data-visibility="hidden" -->
## Quarkus and Microprofile

Eclipse Microprofile is a set of APIs that can be implemented by vendors:

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

Quarkus has many extensions that implement these APIs (among others!)

Note:
* Some of these APIs are just Jakarta EE APIs. Others are Microprofile specific.
* Both Microprofile and Jakarta EE now fall under the Eclipse foundation. Further integration between the
two seems likely.


<!-- .slide: data-visibility="hidden" -->
## Quarkus and Microprofile

* Typically, Quarkus uses the _[SmallRye](https://smallrye.io/)_ implementation of these APIs
* SmallRye is a RedHat project, and is also used by WildFly, Thorntail and Open Liberty.


<!-- .slide: data-visibility="hidden" -->
## Quarkus and Vert.x

Quarkus is built on top of Vert.x, so many Vert.x APIs and types are also available in Quarkus.

For example, the `quarkus-routes` extension provides Vert.x annotations for creating HTTP endpoints.


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

Note:
Might want to keep handy a couple links:
* [Quarkus Cheat Sheet JAX-RS section](https://lordofthejars.github.io/quarkus-cheat-sheet/#_jax_rs)
* [Another JAX-RS Cheat Sheet](http://www.mastertheboss.com/jboss-frameworks/resteasy/jax-rs-cheatsheet)


## JAX-RS & RESTeasy

RESTeasy is the Red Hat _implementation_ of the JAX-RS standard. It's what Quarkus uses to provide web services.


<!-- .slide: data-visibility="hidden" -->
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


## Exercise #1: Hello World

* Generate a new Quarkus app on the Quarkus website
* Download it, import it in your IDE
* Create an endpoint on the `/hello` path that returns 'Hello World'
* Run it
* Browse to http://localhost:8080/hello and observer your message :)
* Add a query parameter that let's you greet a different subject than 'world'


## Conclusion

In this module we have:
* Discussed the philosophy of Quarkus
* Set up the base project for the rest of the training
* Experienced some of the Developer Joy that Quarkus aims to spark
