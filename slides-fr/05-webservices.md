# Web Services


## Connaissances obtenues

Après ce module, vous devrez :
* Savoir écrire un endpoint GET qui retourne un JSON
* Savoir écrire un endpoint POST qui prend et valide un JSON
* Savoir comment générer une spécification Open API
* Savoir comment utiliser `@QuarkusTest` pour écrire un test d'intégration pour les endpoints RESTful


## Introduction

Dans ce chapitre, nous allons mettre à jour nos endpoints Qute en endpoints JSON, et interagir avec eux en utilisant un frontend React.


## Sérialisation automatique en JSON

Donnant une classe `Greet`:

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


## Sérialisation automatique JSON

Quarkus et RESTeasy peuvent automatiquement sérialiser ceci en JSON, si nous lui indiquons de produire du JSON:

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

Le retour:

```json
{
  "subject": "world",
  "greet": "Hello"
}
```


## Sérialisation automatique JSON avec Jackson

Nous pouvons utiliser les annotations Jackson pour modifier le JSON généré:

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

Maintenant, nous avons ce résultat:

```json
{
  "subject": "world",
  "TheGreeting": "Hello"
}
```

Note:
Jackson annotation to control JSON output


## Alternatives

Mais Quarkus n’est pas forcément lié à Jackson! Ici c’est le cas, car nous avons installé l’extension
`quarkus-resteasy-jackson`. Mais si nous préférons JSON-B à la place (faisant partie de Microprofile!), nous pouvons utiliser l’extension `quarkus-resteasy-jsonb`. Et alors nous pouvons appliquer les annotations JSON-B:

```java
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

Nous pouvons aussi avoir les deux extensions installées. Quarkus choisira l’implémentation du sérialiseur en se basant sur les annotations utilisées dans la classe.


## Jackson JSON object

Afin de retourner du JSON, nous pouvons utiliser la librairie Jackson:

```java
@Inject
ObjectMapper mapper;

@GET
public ObjectNode node() {
    ObjectNode node = mapper.createObjectNode();
    node.put("greeting", "Hello");
    node.put("subject", "Quarkus Students");
    return node;
}
```


<!-- .slide: data-background="#abcdef" -->
## Exercice: Convertir les endpoints en JSON


## OpenAPI and Swagger UI

* Ajoutez simplement l'extension SmallRye OpenAPI
  * Ceci est une implémentation de la spécification MicroProfile Open API
* Nous obtenons automatiquement `/openapi` and `/swagger-ui`
* Nous pouvons enrichir les descriptions OpenAPI avec plus d'annotations :
  * `@Operation`, `@APIResponse`, `@Parameter`, `@RequestBody`, `@OpenAPIDefinition`
* Swagger UI est bien pour tester l'API

Note:
Cf. Le [Quarkus OpenAPI / Swagger UI Guide](https://quarkus.io/guides/openapi-swaggerui)


<!-- .slide: data-background="#abcdef" -->
## Exercice: Ajouter Open API


<!-- .slide: data-background="#abcdef" -->
## Exercice: Ajouter REST data Panache


<!-- .slide: data-background="#abcdef" -->
## Exercice: Tester ses endpoints


<!-- .slide: data-background="#abcdef" -->
## Exercice: Hook up the react app


## Validation

* Bean Validation peut être utilisé pour appliquer certaines contraintes
* Nous pouvons utiliser Hibernate Validator, en particulier pour valider l'entrée d'un endpoint REST que nous voulons ajouter pour créer des produits
  * Le moyen le plus simple est d'utiliser une annotation `@Valid` sur le paramètre du corps de la requête
* Many standard constraints available under `javax.validation.constraints.*`

```java
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

public class Person {

    @NotBlank
    @NotNull
    public String name;

    @Min(value=0)
    @Max(value=150)
    public int age;
}
```


<!-- .slide: data-background="#abcdef" -->
## Exercice: Créer un endpoint PUT, activer le feature-flag et essayer dans l’application react



## Recap

Au cours de ce module nous avons:
* Vu comment fonctionne la sérialisation JSON dans Quarkus
* Ajouté un point d’entrée POST avec la validation du contenu de la requête JSON
* Généré une spécification OpenAPI et vus comment accéder à l’interface Swagger
* Câblé un frontal React à notre application HIQUEA
* Vu comment utiliser la _Bean Validation_ pour valider des requêtes vers nos endpoints REST
