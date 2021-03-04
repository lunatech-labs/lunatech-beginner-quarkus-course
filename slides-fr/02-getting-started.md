# Démarrage


## Connaissances obtenues

A l’issue de ce module, vous devriez :
* Comprendre la philosophie et les principes fondamentaux de Quarkus
* Etre capable de générer un nouveau projet Quarkus depuis le site web Quarkus
* Savoir comment démarrer Quarkus depuis la ligne de commande en utilisant le wrapper Maven
* Savoir comment accéder à l'interface de développement


## Pourquoi Quarkus existe

> _Supersonic Subatomic Java ..._
> _A Kubernetes Native Java stack..._
> _crafted from the best of breed Java libraries and standards._

> _Supersonic Subatomic Java ..._
> _Une stack Java nativement Kubernetes..._
> _construite à partir de la sélection des meilleures librairies et standards Java._

https://quarkus.io/

Note:
This is the tagline from the Quarkus homepage


## Pourquoi Quarkus existe

* *Java à l’ère de l'infonuagique*
* Unifie les modèles impératif et réactif
* Satisfaction des développeurs

Note:
This is where we can explain the Supersonic (fast startups) Subatomic (small memory footprint) tagline that encapsulates the "cloud-native" aims of Quarkus.

Contrast this with historical context of Enterprise Java frameworks that could accept slow startups and large memory footprint for applications that are meant to be long-lived and go through a "warm-up" phase before being optimised.


## Pourquoi Quarkus existe

* Java à l’ère de l'infonuagique
* *Unifie les modèles impératif et réactif*
* Satisfaction des développeurs

Note:
Can mention here that these two themes will be covered over the two days of the training course


## Pourquoi Quarkus existe

* Java à l’ère de l'infonuagique
* Unifie les modèles impératif et réactif
* *Satisfaction des développeurs*

Note:
At this slide, we explain _developer joy_ only:
* Zero config (note there is no config file yet in this hello world app!). Show the config of the Dev UI, so people see how easy it is to lookup all possible configuration options.
* Live reload (already shown)
* Standards (Jax-RS in this example)
* Unified config (we'll see this later, everything in one config file)
* Streamlined code (a lot of stuff works out of the box, but you can fall back to full framework capabilitis when needed)
* No hassle native executable generation (show the results, and run the native image. Highlight startup time!)

Explain that in the coming two days, we will dive deeper into each of these to fully grasp what they mean and why they are important.


## Construit sur des standards, inspiré des meilleures pratiques
* Quarkus et de nombreuses extensions sont bâties sur des standards de l’industrie tels que Jakarta EE et MicroProfile
* Quarkus lui même est construit sur un des meilleurs frameworks réactifs, Vert.x
* Quarkus est inspiré par l’expérience développeur d’autres frameworks comme Spring Boot et Play Java
    * Quarkus possède même une extension de compatibilité avec Spring
* Quarkus implémente de nombreuses APIs et supporte beaucoup de frameworks. Cela fait partie de la philosophie de Quarkus.

Note:
* Explain to people that they can choose what they like
* Explain that it's typically a good idea to *not fight quarkus*. Some of the libraries are quite opinionated. If you yourself
are also quite opinionated, that might not match well. You can spend a ton of time reconfiguring a framework, for little benefit. _Going with the flow_ is useful. Luckily, you have a choice of libraries to use :)


<!-- .slide: data-visibility="hidden" -->
## Quarkus et Spring

Quarkus possède une extension de compatibilité avec Spring, les annotations de plusieurs composants populaires de Spring peuvent être utilisés dans Quarkus.

L’utilisation principale concerne le portage d’applications Spring existantes.

Note:
Discuss some of the limitations; like that Quarkus still requires beans to be resolved compile-time, so you can't use
`@Conditional` for example.


<!-- .slide: data-visibility="hidden" -->
## Quarkus and Microprofile

Les APIs JAX-RS font partie de Eclipse Microprofile (et Jakarta EE!)

Eclipse Microprofile est un ensemble d’APIs adaptées aux Microservices et qui peuvent être implémentés par les éditeurs :

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

Quarkus comprend beaucoup d’extensions qui implémentent ces APIs (parmi d’autres!)

Note:
* Some of these APIs are just Jakarta EE APIs. Others are Microprofile specific.
* Both Microprofile and Jakarta EE now fall under the Eclipse foundation. Further integration between the
two seems likely.


<!-- .slide: data-visibility="hidden" -->
## Quarkus et Microprofile

* De manière générale, Quarkus utilise les implémentation SmallRye de ces APIs
* SmallRye est un projet RedHat, qui est également utilisé par WildFly, Thorntail et Open Liberty.


<!-- .slide: data-visibility="hidden" -->
## Quarkus et Vert.x

Quarkus est construit au-dessus de Vert.x, donc de nombreuses APIs et types de Vert.x APIs sont également disponibles dans Quarkus.

Par exemple, l’extension quarkus-routes fournit des annotations Vert.x pour déclarer des points d’entrée HTTP.


## Démo Hello World

Note:
Demo the following:
* Generate a project on the Quarkus website
* Run it with `./mvnw compile quarkus:dev` downloaded directory (we compile first, to show the speed of quarkus in the second command)
* Show http://localhost:8080/
* Show the Dev UI
* Import in Intellij
    * For IntelliJ there is a Quarkus plugin and they can make a run configuration for the application

Optionally:
* Add a new endpoint, that returns 'Hello ' + name of the group you're teaching.
* Make a Java error, show the exception page
* Fix the error, show people that no restart is needed
* Start the generation of the native executable! (Already, because it takes time!)


## Hello World en Quarkus

Nous pouvons utiliser les annotations JAX-RS pour créer un point d’entrée “Hello World”:

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

JAX-RS est une spécification de Jakarta EE API. Il contient des annotations telles que :

* `@Path`
* `@GET`, `@PUT`, `@POST`, `@DELETE`, `@HEAD`
* `@Produces` and `@Consumes`
* `@PathParam`, `@QueryParam`, `@HeaderParam` and more...

En somme, une manière standardisée de définir des web services RESTful

Note:
Might want to keep handy a couple links:
* [Quarkus Cheat Sheet JAX-RS section](https://lordofthejars.github.io/quarkus-cheat-sheet/#_jax_rs)
* [Another JAX-RS Cheat Sheet](http://www.mastertheboss.com/jboss-frameworks/resteasy/jax-rs-cheatsheet)


## JAX-RS & RESTeasy

RESTeasy est l'implémentation RedHat du standard JAX-RS. C’est ce que Quarkus utilise pour fournir des web services.


<!-- .slide: data-visibility="hidden" -->
## Hello World en Quarkus avec la compatibilité Spring

Ou utiliser l’extension Quarkus pour Spring Web API:

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


<!-- .slide: data-background="#abcdef" -->
## Exercice: Hello World


## Récapitulatif

Dans ce module, nous avons :
* Discuté de la philosophie de Quarkus
* Mis en place un projet de base qui sera utilisé pour la suite
* Découvert la joie des développeurs que Quarkus cherche à déclencher
