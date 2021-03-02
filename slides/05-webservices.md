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

In this exercise, we will abandon our Qute templates, and convert our endpoints to returning JSON instead of HTML. Later, we will hook up a React frontend application to these endpoints.

* Remove the `@Injected` templates from the `ProductsResource`
* Make the `products` and `details` method return a JSON representation of a list of products or a single product, respectively, instead. For this you will need to add an `@Produces` annotation either on the class, or on each of the methods.
* Update the tests for the list and details endpoint and make them check for the right content-type.
* Update the test for the details endpoint, and use the Json-path expression `name` to test that the value for the url `/products/1` equals `"Chair"`.


## OpenAPI and Swagger UI

// TODO, demonstrate how to use OpenAPI and Swagger


<!-- .slide: data-background="#abcdef" -->
## Exercise: Add Open API

Now, we will be adding OpenAPI support and Swagger UI to our application, so we have better visibility into our REST endpoint.

* Add the `quarkus-smallrye-openapi` extension to your application:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-openapi</artifactId>
</dependency>
```

* Browse to http://localhost:8080/ and observer under `Additional endpoints`, that two new endpoints emerged: /q/openapi and /q/swagger-ui/
* Browse to http://localhost:8080/q/swagger-ui/. You will see our four endpoints, and you can try them out in the UI. Try sending some requests to them!


<!-- .slide: data-background="#abcdef" -->
## Exercise: Adding REST data Panache

In this exercise, we will see how we can create close to no-code CRUD endpoints with the _hibernate-orm-rest-data-panache_ extension.

* Add the following extension to your dependencies:

```xml
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-hibernate-orm-rest-data-panache</artifactId>
</dependency>
```

* Create a new *interface* `PanacheProductsResource` that extends `PanacheEntityResource<Product, Long>`
* Browse to the Swagger UI endpoint at http://localhost:8080/q/swagger-ui/ and observe the new endpoints that Panache created.


<!-- .slide: data-background="#abcdef" -->
## Exercise: Adding REST data Panache (cont'd)

* Create a new product using Swagger UI, by posting the following JSON to the POST panache-products endpoint:

```json
{
  "name": "Couch",
  "description": "A leather couch",
  "price": 399
}
```

* Check the /panache-products (or your own /products) endpoint to see if you find your newly created couch back.


<!-- .slide: data-background="#abcdef" -->
## Exercise: Test your endpoints

// TODO (include the TestTransaction feature)


<!-- .slide: data-background="#abcdef" -->
## Exercise: Hook up the react app

In this exercise we will add a (premade) React frontend to our application. This frontend application understands some _feature flags_ to enable or disable certain functionality. So first, we will add a backend resource to serve these feature flags to the frontend.

* Create a `/feature-flags` endpoint that serves the following JSON structure.
  * Make it such that the flags can be configured in the application.properties configuration file:

  ```json
  {
			“productDetails”: true,
			“productSearch”: false,
			“reactivePrices”: false
	}
  ```

* Run the react app


## Recap

In this module we have:
* Seen how JSON serialisation works in Quarkus
* Added a POST endpoint with validation of JSON body
* Generated an OpenAPI spec and seen how to access Swagger UI
* Hooked up a React frontend to our HIQUEA application
* Seen how to pass @ConfigProperty values through our code
