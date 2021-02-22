# Web Services


## Introduction

In this chapter we will be updating our Qute endpoints to JSON endpoints, and interact with them using a React frontend.


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


## Exercise NN, JSON feature flags

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


## Exercise #N, JSON objects

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



## Exercise #4, moving to JSON 

In this exercise, we will be switching the Catalogue endpoint to JSON.

* Change the `/catalogue` endpoint to `Produce` an `APPLICATION_JSON` return type. 


## Database Connectivity

* Generalized config
* Options:
    * JDBC
    * Others // TODO
    

## Putting our products in the database

* TODO, show setup SQL script
* TODO, show docker-compose to setup a database


## Transactions

TODO


## Exercise #5, Creating the edit and create endpoints

The React application has functionality to create and edit products as well!

// TODO, write the exercise to create these endpoints.


## Quarkus testing

// TODO, write about Quarkus testing


## Exercise #6, Test your endpoints

// TODO (include the TestTransaction feature)


## OpenAPI and Swagger UI

// TODO, demonstrate how to use OpenAPI and Swagger


