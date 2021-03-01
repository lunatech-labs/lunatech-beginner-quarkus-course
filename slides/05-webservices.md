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
## Exercise: moving to JSON

In this exercise, we will be switching the Catalogue endpoint to JSON.

* Change the `/catalogue` endpoint to `Produce` an `APPLICATION_JSON` return type.


<!-- .slide: data-background="#abcdef" -->
## Exercise: Creating the edit and create endpoints

The React application has functionality to create and edit products as well!

// TODO, write the exercise to create these endpoints.


<!-- .slide: data-background="#abcdef" -->
## Exercise: Test your endpoints

// TODO (include the TestTransaction feature)


## OpenAPI and Swagger UI


// TODO, demonstrate how to use OpenAPI and Swagger


<!-- .slide: data-background="#abcdef" -->
## Exercise: Add Open API


## JSON feature flags

We have a pre-made React frontend for our Hiquéa app. The first call to the backend this React app does, is to the endpoint `/feature-flags`.

It expects an JSON response like the following:

```json
{
  "catalogue": true,
  "productDetails": false,
  "search": false,
  "searchPagination": false,
  "productUpdaate": false,
  "realtimeInventory": false
}
```

based on the values in this object, the frontend enables or disables certain features.


<!-- .slide: data-background="#abcdef" -->
## Exercise: Hook up the react app

* Create an endpoint `/feature-flags` that returns the following object:
  ```json
  {
    "catalogue": true,
    "productDetails": false,
    "search": false,
    "searchPagination": false,
    "productUpdaate": false,
    "realtimeInventory": false
  }
  ```

// TODO, make them put the feature flags in the config file.


## Recap

In this module we have:
* Seen how JSON serialisation works in Quarkus
* Added a POST endpoint with validation of JSON body
* Generated an OpenAPI spec and seen how to access Swagger UI
* Hooked up a React frontend to our HIQUEA application
* Seen how to pass @ConfigProperty values through our code
