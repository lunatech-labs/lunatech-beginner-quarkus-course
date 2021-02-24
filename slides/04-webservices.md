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


## Execution Model

When using the standard *imperative* RESTEasy, Quarkus creates as many `executor` threads as needed, up to the configured maximum:

```java [|6|]
@GET
@Path("/slow")
public String slow() throws InterruptedException {
    String thread = Thread.currentThread().getName();
    System.out.println("Thread: " + thread);
    Thread.sleep(1000);
    return thread;
}
```

``` [|1|8|]
ab -c 50 -n300  http://127.0.0.1:8081/threads/slow
...
Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.7      0       3
Processing:  1002 1009   6.8   1007    1037
Waiting:     1002 1009   6.8   1007    1037
Total:       1002 1010   7.4   1007    1039
```

The default maximum is `max(200, 8 * nr_of_cores)`

Note:
What we see here, is that if we execute 50 concurrent requests, they all get executed in parallel. 


## Execution Model

If we choose a smaller amount of maximum threads:

```quarkus.thread-pool.max-threads=10```

Then running the same `ab` command takes much longer:

``` [|8|]
ab -c 50 -n300  http://127.0.0.1:8081/threads/slow
...
Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.5      0       2
Processing:  1020 4679 959.9   5021    5068
Waiting:     1020 4679 960.0   5020    5068
Total:       1022 4680 959.5   5021    5070
```

Note:
What we see here, is that if we reduce the maximum number of threads, we see that many requests have to wait before being processed.


## Execution Model - Blocking Threads

Two types of a thread being held up:

* Doing useful work on the CPU
* Waiting for somebody else (Database, API call, Disk IO, etc.). This is what we call _blocking_.

Note:
Explain the following:
* Doing useful work on the CPU is good. It's what we have it for. If all CPU's are busy doing useful work, we have great utilization of our resources, and we can be happy.
* Waiting for others is fine, it's a fact of life. But it means we need to be *doing something else* with the CPU.

So suppose we have 4 cores, and 10 threads. If 5 threads are actively computing stuff, and 5 threads are blocked, there's no problem. But if 8 threads are blocked, and only 2 doing useful CPU work, it ís a problem. 

That's why Quarkus makes sure there's a royal amount of threads: at least 200 in the default config. So we can have at least 200 concurrent requests. 

But there is a limitation: Quarkus can't discriminate between a thread blocked on CPU, and a thread blocked on IO. If all 200 threads are used for CPU, it will cause _thread starvation_: the computation doesn't make much progress, because a thread is scheduled only occasionally.

In the next chapter, we will see a different model that solves this.


## OpenAPI and Swagger UI


// TODO, demonstrate how to use OpenAPI and Swagger


## CDI & ArC

// TODO
