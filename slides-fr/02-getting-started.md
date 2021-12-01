# Démarrage


## Connaissances obtenues

À l’issue de ce module, vous devriez :
* Comprendre la philosophie et les principes fondamentaux de Quarkus
* Être capable de générer un nouveau projet Quarkus depuis le site web Quarkus
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
C'est le slogan de la page d'accueil du site Quarkus


## Pourquoi Quarkus existe

* *Java à l’ère de l'infonuagique*
* Unifie les modèles impératif et réactif
* Satisfaction des développeurs

Note:
C'est là que nous pouvons expliquer le slogan Supersonic (démarrage rapide) Subatomic (faible empreinte mémoire) qui résume les objectifs "cloud-native" de Quarkus.

Comparez cela avec le contexte historique des frameworks Enterprise Java qui pouvaient accepter des démarrages lents et une empreinte mémoire importante pour les applications qui sont censées être durables et passer par une phase de "préchauffage" avant d'être optimisées.


## Pourquoi Quarkus existe

* Java à l’ère de l'infonuagique
* *Unifie les modèles impératif et réactif*
* Satisfaction des développeurs

Note:
Indiquez maintenant que ces 2 sujets seront abordés pendant ces 2 jours de formation


## Pourquoi Quarkus existe

* Java à l’ère de l'infonuagique
* Unifie les modèles impératif et réactif
* *Satisfaction des développeurs*

Note:
Sur cette diapositive, nous expliquons seulement la _joie du développeur_ :
* Zero config (notez qu'il n'y a pas encore de fichier de configuration dans cette application Hello World!). Montrez la configuration de l'interface utilisateur de développement afin que les utilisateurs voient à quel point il est facile de rechercher toutes les options de configuration possibles.
* Rechargement en direct (déjà montré)
* Normes (Jax-RS dans cet exemple)
* Configuration unifiée (nous verrons cela plus tard, tout est dans un même fichier de configuration)
* Code simplifié (pleins de choses fonctionnent dès l'installation, mais vous pouvez revenir aux capacités complètes du framework si nécessaire)
* Génération d'exécutable natif sans tracas (affichez les résultats et exécutez l'image native. Mettez en surbrillance l'heure de démarrage !)

Expliquez que pendant ces deux prochains jours, nous approfondirons chacun de ces points pour bien comprendre ce qu'ils signifient et pourquoi ils sont importants.


## Construit sur des standards, inspiré des meilleures pratiques
* Quarkus et de nombreuses extensions sont bâties sur des standards de l’industrie tels que Jakarta EE et MicroProfile
* Quarkus lui même est construit sur un des meilleurs frameworks réactifs, Vert.x
* Quarkus est inspiré par l’expérience développeur d’autres frameworks comme Spring Boot et Play Java
    * Quarkus possède même une extension de compatibilité avec Spring
* Quarkus implémente de nombreuses APIs et supporte beaucoup de frameworks. Cela fait partie de la philosophie de Quarkus.

Note:
* * Expliquez aux gens qu'ils peuvent choisir ce qu'ils aiment
* Expliquez que c'est généralement une bonne idée de *ne pas se battre avec Quarkus*. Certaines bibliothèques sont assez contraignantes. Si vous êtes vous-même assez obstiné, cela pourrait bien ne pas fonctionner. Vous pouvez perdre énormément de temps à reconfigurer le framework pour un faible bénéfice. _Going with the flow_ is useful. Luckily, you have a choice of libraries to use :)


<!-- .slide: data-visibility="hidden" -->
## Quarkus et Spring

Quarkus possède une extension de compatibilité avec Spring, les annotations de plusieurs composants populaires de Spring peuvent être utilisés dans Quarkus.

L’utilisation principale concerne le portage d’applications Spring existantes.

Note:
Parlez de certaines limitations; comme le fait que Quarkus exige toujours que les beans soient résolus au moment de la compilation, ainsi vous ne pouvez pas utiliser
`@Conditional` par exemple.


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
* Certaines de ces API sont juste des API Jakarta EE.
* D'autres sont spécifiques à Microprofile.
* Microprofile et Jakarta EE relèvent désormais de la fondation Eclipse. Une intégration plus poussée entre le deux semble probable.


<!-- .slide: data-visibility="hidden" -->
## Quarkus et Microprofile

* De manière générale, Quarkus utilise les implémentations SmallRye de ces APIs
* SmallRye est un projet RedHat, qui est également utilisé par WildFly, Thorntail et Open Liberty.


<!-- .slide: data-visibility="hidden" -->
## Quarkus et Vert.x

Quarkus est construit au-dessus de Vert.x, donc de nombreuses APIs et types de Vert.x APIs sont également disponibles dans Quarkus.

Par exemple, l’extension quarkus-routes fournit des annotations Vert.x pour déclarer des points d’entrée HTTP.


## Démo Hello World

Note :
Demo the following:
* Générer b-un projet depuis le site web Quarkus
* Lancez la commande `./mvnw compile quarkus:dev` depuis le dossier téléchargé (nous compilons d'abord pour montrer la vitesse de Quarkus dans la seconde commande)
* Affichez http://localhost:8080/
* Affichez l'UI de Dev
* Importez dans IntelliJ
    * Pour IntelliJ, il existe un plugin Quarkus et ils peuvent créer une configuration d'exécution pour l'application

En option :
* Ajoutez un point d'entrée qui retourne 'Hello ' + nom du groupe des participants.
* Provoquez une erreur Java, affichez la page d'exception
* Corrigez l'erreur, montrez que le redémarrage n'est pas nécessaire
* Démarrez la génération de l'exécutable natif ! (Ici, car ça prend du temps)


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
