# Web Services


## Learning outcomes

After this module, you should:
* Know how to write a GET endpoint that returns JSON
* Know how to write a POST endpoint that takes and validates JSON
* Know how to generate an Open API spec
* Know how to use @QuarkusTest to write an integration test for RESTful endpoint


## Introduction

In this chapter we will be updating our Qute endpoints to JSON endpoints, and interact with them using a React frontend.


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

But Quarkus isn't tied to Jackson! This just happens to use Jackson, because we have the extension
`quarkus-resteasy-jackson` installed. But if we prefer JSON-B instead (part of Microprofile!), we can use
the `quarkus-resteasy-jsonb` extension. And then we can use JSON-B annotations:

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

We can also have both extensions installed. Quarkus will pick the right serialization framework based on the annotations
that are used by the class.


## Jackson JSON object

To return JSON, we can use the Jackson library:

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
## Exercise: Convert endpoints to JSON


## OpenAPI and Swagger UI

* Simply add SmallRye OpenAPI extension
  * This is an implementation of the MicroProfile Open API spec
* We automatically get `/openapi` and `/swagger-ui`
* Can enrich the OpenAPI descriptions with more annotations:
  * `@Operation`, `@APIResponse`, `@Parameter`, `@RequestBody`, `@OpenAPIDefinition`
* Swagger UI is good for testing API

Note:
Cf. The [Quarkus OpenAPI / Swagger UI Guide](https://quarkus.io/guides/openapi-swaggerui)


<!-- .slide: data-background="#abcdef" -->
## Exercise: Add Open API


<!-- .slide: data-background="#abcdef" -->
## Exercise: Adding REST data Panache


<!-- .slide: data-background="#abcdef" -->
## Exercise: Test your endpoints


<!-- .slide: data-background="#abcdef" -->
## Exercise: Hook up the react app


## Validation

* Bean Validation can be used to enforce certain constraints
* We can use Hibernate Validator, especially to validate input to REST endpoint we want to add for creating products
  * Simplest way is with an `@Valid` annotation on the request body parameter
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
## Exercise: Create PUT endpoint, enable feature flag and try in the react app


## Recap

In this module we have:
* Seen how JSON serialisation works in Quarkus
* Added a POST endpoint with validation of JSON body
* Generated an OpenAPI spec and seen how to access Swagger UI
* Hooked up a React frontend to our HIQUEA application
* Seen how to use Bean Validation for REST endpoint validation
