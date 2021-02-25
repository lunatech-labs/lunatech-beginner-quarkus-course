# Reactive Programming


## CPU vs Non-Blocking IO vs Blocking IO

Remember from last section:

* A thread doing CPU is good, but we don't want a ton of those
* A thread doing IO is fine, but it shouldn't prevent other threads doing CPU work

<div class="fragment">
Solution?

<ul>
<li> Have a limited number of threads doing CPU-work
<li> Try to do IO without blocking a thread
<li> If you can't, have potentially an unlimited number of threads waiting for IO
</ul>
</div>


## RESTEasy Reactive

The `quarkus-resteasy-reactive` extension brings reactive JAX-RS support to Quarkus.

```xml
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-resteasy-reactive</artifactId>
</dependency>
```

```java
@GET
@Path("/hello")
public String hello() {
  return "Hello World";
}
```

Works identically.


## RESTEasy Reactive

The `quarkus-resteasy-reactive` extension brings reactive JAX-RS support to Quarkus.

```xml
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-resteasy-reactive</artifactId>
</dependency>
```

```java [|4|]
@GET
@Path("/hello")
@Produces(MediaType.TEXT_PLAIN)
public CompletionStage<String> hello() {
  return CompletableFuture.completedFuture("Hello!");
}
```

We can also return a `CompletionStage`


## Reactive Execution Model

RESTEasy reactive **does** care about blocking

* Your method will be called by a Vert.x eventloop thread
* You shouldn't block it
* If you do block, annotate with `@Blocking`


## Reactive Execution Model

### Example

```java
@GET
@Path("/regular")
public String regular() {
  return Thread.currentThread().getName();
}
```

This is fine - returns something like `vert.x-eventloop-thread-3`


## Reactive Execution Model

### Bad Example

```java [|4|]
@GET
@Path("/regular-slow")
public String regularSlow() {
  Thread.sleep(1000);
  return Thread.currentThread().getName();
}
```

This is **not** fine.

Note:
Here we block the eventloop thread for IO. Ask the audience what they expect to happen if we measure this with a high number of concurrent requests?

See next page for the results


## Reactive Execution Model 

### Bad Example

``` [|1|8|9-10|]
ab -c50 -n300  http://127.0.0.1:8082/threads/regular-slow

Connection Times (ms)
min  mean[+/-sd] median   max
Connect:        0    1   0.7      0       3
Processing:  1005 2013 402.7   2012    3087
Waiting:     1005 2012 402.7   2012    3087
Total:       1007 2013 402.3   2013    3088
WARNING: The median and mean for the initial connection time are not within a normal deviation
These results are probably not that reliable.
```

Note:
Ask the audience if they can guess how many IO threads quarkus has out of the box?

Answer: 
* We run 50 requests concurrently, and you see that the slowest take 3 seconds. This gives us between 17 and 24 threads to get this behaviour.
* According to the Quarkus docs, it's twice the number of cores.
* This measurement was done on 6 cores with hyperthreading, so effectively 12 cores and 24 IO threads. We guessed correctly!

Also note that AB finds the results suspicious :)


## Reactive Execution Model 

### Good Example

```java [|2|]
@GET
@Path("/regular-slow")
@Blocking
public String blockingSlow() {
  Thread.sleep(1000);
  return Thread.currentThread().getName();
}
```

This returns `executor-thread-221`

Note:
And remember, of these threads there are very many, and you can safely tie them up in blocking IO.


## Reactive Execution Model 

### Good Example

```
ab -c70 -n300  http://127.0.0.1:8082/threads/blocking-slow

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1   0.7      1       3
Processing:  1001 1008   4.5   1007    1023
Waiting:     1001 1008   4.5   1007    1023
Total:       1001 1009   4.9   1008    1024
```

Back to normal :)

Note:
Here we see that if we tell quarkus that our method is blocking, it will run it with an executor thread; of which there are many more available. 


## Reactive Execution Model

### Event better example

Of course, we can do even much better, by not blocking a thread at all:

```java [|3|5|6-7|]
@GET
@Path("/nonblocking-slow")
public Uni<String> nonblockingSlow() {
  return Uni.createFrom().item(Thread.currentThread().getName())
    .onItem().delayIt().by(Duration.ofSeconds(1))
    .onItem().transform(i ->
      "Initial: " + i + ", later: " + Thread.currentThread().getName());
}
```

Outputs:
    Initial: vert.x-eventloop-thread-18, later: executor-thread-1

Note:
This demonstrates asynchronous 'waiting'. No thread is blocked here. The initial part of the computation is performed on the Vert.x IO thread. The `delayIt` method doesn't block, just returns a `Uni` that completes after the specified time. Continued work with that `Uni` is not performed by an IO Thread.


## Exercise

TODO, rework some stuff into reactive


## Reactive Routes

An alternative to _RESTEasy Reactive_ is to use the _Reactive Routes_ extension:

> Reactive routes propose an alternative approach to implement HTTP endpoints where you declare and chain routes. This approach became very popular in the JavaScript world, with frameworks like Express.Js or Hapi. Quarkus also offers the possibility to use reactive routes. You can implement REST API with routes only or combine them with JAX-RS resources and servlets.

Note:
This extension is also known as 'Vert.x web'


## Reactive Routes

```java [|1-5|7-10|12-16|]
@Route(methods = HttpMethod.GET) 
void hello(RoutingContext rc) { 
    rc.response().end("hello");
}

@Route(path = "/hello")
Uni<String> hello(RoutingContext context) {
  return Uni.createFrom().item("Hello world!");
}

@Route(produces = "application/json")
Person createPerson(@Body Person person, @Param("id") Optional<String> primaryKey) {
  person.setId(primaryKey.map(Integer::valueOf).orElse(42));
  return person;
}
```

Note:
1. No path or regex set, path derived from the method name! Shows working with the `RoutingContext`, which is a Vert.x class.
2. Shows returning a Uni instead of putting the response on the `RoutingContext`
3. Shows parameter usage

Also note:
Instead of injecting `RoutingContext` you can also choose some other Vert.x or Quarkus or even Mutiny HTTP model classes. Just pick one that you like working with or that has the stuff you need easily available.

How to choose between this and RESTEasy Reactive?
- RESTEasy Reactive is _experimental_ as of February 2021
- Vert.x Web is stable, so typically a better choice.


# Reactive Database Access


## About JDBC

JDBC is a blocking API

Example:

    ResultSet rs = stmt.executeQuery(query);

There is **no way** to obtain the `ResultSet` without blocking **a** thread.

Note:
Remark that you don't necessarily have to block the thread you're working on. Of course you can execute the call on some different thread, and obtain a `CompletionStage` here. But then you have to block that other thread! 

Of course it's not a huge problem in most applications, for several reasons:
- Databases don't like thousands of concurrent queries, so we'd probably queue requests anyway if we have many of them
- As shown, Quarkus can deal quite will with a relatively large amount of threads that are okay to block on IO
- For big apps, the overhead of several tens of threads for this isn't huge

But for *supersonic* *subatomic* we can do better!


## Hibernate going Reactive

In December 2020, Hibernate Reactive was launched:

```java
Uni<Book> bookUni = session.find(Book.class, book.id);
bookUni.invoke( book -> System.out.println(book.title + " is a great book!") )
```

It's a reactive API for Hibernate ORM. 


## Hibernate going Reactive

* Works with non-blocking database clients. Currently the Vert.x clients for Postgres, MySQL and DB2
* Returns Mutiny `Uni` and `Multi` types
* Well-integrated with Quarkus
* No implicit blocking lazy loading, but explicit asynchronous operations for fetching associations

... but we won't be using it in this course :)

Note:
People should try it, but it's too new to use in the course right now. Instead, we will be using the lower-level libraries directly.

One other thing to mention, the creators _don't expect this to be faster than regular Hibernate ORM_. They don't expect many applications to benefit from it. However, they do expect better degradation under load for some applications, and maybe there will be performancee improvements in the future.


## Vert.x Postgres Client

TODO



## What's looming on the horizon?

![Project Loom](images/reactive/loom.jpeg)

Note:
Explain a bit about Project Loom:
- Upcoming changes to the JDK, to support _virtual threads_, aka fibers, threads that don't consume an OS thread and thus don't suffer from the memory-overhead and poor cache behavior of regular threads. With Loom it is possible to create millions of virtual threads!
- Loom will make most existing JDBC drivers suddenly non-blocking, because all networking API's will be reimplemented in a non-blocking fashion. User code won't have to change
- Game changer for many applications, but also quite far away. Unlikely to make the Java 17 LTS, so maybe the first LTS that has it will be Java 23, due in 2024, so used at a company near you in 2026...


# Reactive Streams


## Reactive Streams

*Streaming data* is frequently found in modern applications:

- Events that flow from a system to consumers
- Records that flow from a database into a chunked HTTP response
- Messages that are consumed from a queue, transformed and put on another queue

<p class="fragment">A fundamental problem of streaming data systems, is to make sure that the <em>consumer</em> of the stream
can handle the messages that are being sent to it.</p> 


## Slow consumer

Suppose you have a system that reads records from a database, transforms them to JSON and stores them in a file.

*Question:* What happens if you read 1000 records per second, but you can only write 500 per second to files?

Note: 
Answer: memory will fill up, until the system breaks.


## Slow consumer

TODO, draw diagram of slow consumer being overloaded by fast producer


## Slow consumer - solutions

Possible solutions:
* <!-- .element: class="fragment" -->Have a really fast consumer instead
* <!-- .element: class="fragment" -->Have a really slow producer  
* <!-- .element: class="fragment" -->Have more memory than your database size
* <!-- .element: class="fragment" -->Adapt the speed of the producer, based on the capacity of the consumer

Note:
That last one is essentially what's called back-pressure. The consumer can indicate how much it wants to read.


## Back pressure

*Back pressure* means that the consumer can indicate *demand* to the producer. The producer will only produce the amount that the consumer requested.

// TODO, draw diagram of a consumer indicating demand.


## Back pressure 

* Works for the entire stream, not just the consumer at the end
* Each element can adapt the demand that's sent upstream
  - Slow components reduce demand
  - Some components, like buffers, can increase demand


## Streaming across TCP

* TCP natively supports back-pressure! 
* *ack* messages contain a *window* field, indicating how much the sender may send.
* When the receiver processed data, a new *ack* gets sent, with a bigger window.

Note:
So we can make reactive back-pressuring systems across TCP. For example using chunked HTTP responses.


## Compared with JMS

* Blocking interface: we don't want a blocking interface
* Asynchronous interface: no back pressure


## The Reactive Streams standard

Around 2013, engineers from Netflix, Pivotal, Lightbend, Twitter and others were all working on streaming systems, essentially solving the same issues. 

To make sure their libraries would be interoperable, they came up with the **Reactive Streams** standard.

It's a minimal interface needed to connect streaming libraries, retaining full non-blocking operation and back-pressure and a shared cancellation and error model.


## The Reactive Streams standard

Ended up into the Java Standard Library as of Java 9, under `java.util.concurrent.Flow`.

Note: 
The standard itself is very small, you can't really program against it directly. For example, ther are no `map` or `filter` methods in the standard library.

So you need to use an *implementation* of the standard, to do meaningful streaming work.


## Reactive Streams implementations

* Akka Streams
* RxJava
* Reactor
* Vert.x
* Mutiny
* ... and others

Note: Mutiny is the one that Quarkus uses. It's part of Smallrye.

But some parts of the Vert.x implementation are also used, since Quarkus uses Vert-x as well.


## Getting Started

```java [1|2|3|4]
Multi<String> greeter = Multi.createFrom().items("Hello", "world");
Uni<List<String>> out = greeter.collectItems().asList();
List<String> results = out.subscribe().asCompletionStage().join();
System.out.println(results);
```

Note:
Explain this line by line:
1. We create a *Multi*. A Multi is stream, parameterized by the element type. This particular one is for a *bounded* stream. We know how many elements ther are. But the abstraction is the same. A `Multi` can also be unbounded.
2. We accumulate the elements into a List. As this is all non-blocking and async, it can't return this List as is, but it returns it into a `Uni`. A Uni is a special stream with one element. It's quite similar to a `CompletableFuture`. Note that at this point, the stream hasn't started yet!
3. Subscribe starts the stream, and `asCompletionStage` transforms the Uni into a `CompletableFuture`, which we then block on using `join`.
4. Finally we print the results.

Obviously, this is for demo purposes. In real code, you should almost never use *join*. Instead, the frameworks we work with (like Quarkus), support returning Uni's and Multi's.


## RESTEasy Reactive with Mutiny UNI

`Uni`s are supported as a result type:
```java [|3|4]
@GET
@Produces(MediaType.TEXT_PLAIN)
public Uni<String> helloUni() {
    return Uni.createFrom().item("Hello!");
}
```

Note: Again, a `Uni` is like a stream that emits up to one element. But it can also be cancelled or fail.



## Mutiny Uni

A `Uni` only gets executed when connected to a *subscriber*:

```java [1-6|8-9]
Uni<Integer> myUni = Uni.createFrom().item(() -> {
    System.out.println("Creating the item!");
    return 5;
});

// Nothing has been printed at this point

System.out.println("Subscribing:");
myUni.subscribe().with(System.out::println); // Prints 'Creating the item!' and '5'
```

Note:
This is different from a `CompletionStage`, which is already running. 

A `Uni` is more a descriptor of an operation.


## RESTEasy Reactive

Also, `Multi` is supported:

```java
@GET
@Produces(MediaType.TEXT_PLAIN)
public Multi<String> helloMulti() {
  return Multi.createFrom().items("Hello", "world!");
}
```

This returns a `chunked` HTTP response.

Note: Meaning that Quarkus doesn't need to create the full response in memory before sending it.

So it can support arbitrarily long HTTP responses in bounded memory.


## Mutiny Multi

Mutiny's `Multi` interface *extends* `org.reactivestreams.Publisher<T>`, so it's a reactive stream.

Multi has many methods to operate on it:

```java [1|2|3|4|5]
return Multi.createFrom().items("One", "Two", "Three", "Four", "Five", "Six")
        .map(String::toUpperCase)
        .filter(s -> s.length() >= 4)
        .flatMap(s -> Multi.createFrom().items(s.toCharArray()))
        .map(String::valueOf);
```

Note:
1. There many methods to *create* a Multi: From elements, from iterators, by imperatively pushing them in, from timer ticks. However, most typically as an application builder you won't create your own multi's, but obtain a multi from a library, like a Kafka connector, a Database connector, a web service client, a form upload, and transform that Multi. 
2. The `map` method. Familiar from streams.
3. The `filter` method. Familiar from streams.
4. The `flatMap' method. To flatten nested Multi's. 
5. Another `map`

There are many more, see the docs. Be aware; Quarkus uses a somewhat outdated Mutiny version sometimes.


## Mutiny Multi

A subscriber to a Multi, must deal with the following situations:

* An element arrives
* A failure occurred
* The (bounded) stream completed

```java [|3|4|5]
Cancellable cancellable = multi
.subscribe().with(
  item -> System.out.println(item),
  failure -> System.out.println("Failed with " + failure),
  () -> System.out.println("Completed"));
```

Note: If you check the Reactive Streams spec, this is also what you see.


## Server-Sent Events

Server-Sent Events is a technology that allows the server to push data to the client when it wants. The client opens a connection and the connection is kept open. The server can send chunks of data.


## Server-Sent Events

Here's an example of an endpoint that sends a chunk every second, containing the current time: 

```java [|2|3|6-7|8]
@GET
@Produces(MediaType.SERVER_SENT_EVENTS)
@RestSseElementType(MediaType.TEXT_PLAIN)
public Multi<String> time() {
  return Multi.createFrom()
    .ticks()
    .every(Duration.ofSeconds(1))
    .map(__ -> LocalDateTime.now().toString());
}
```

``` [|1|2-4|5-10|11]
➜  lunatech-beginner-quarkus-course git:(main) ✗ http localhost:8082/time --stream
HTTP/1.1 200 OK
Content-Type: text/event-stream
X-SSE-Content-Type: text/plain
transfer-encoding: chunked
data:2021-02-23T14:09:59.233302
data:2021-02-23T14:10:00.237587
data:2021-02-23T14:10:01.236240
data:2021-02-23T14:10:02.236214
data:2021-02-23T14:10:03.236526
^C
```

Note:
1. Endpoint produces Server Sent Events
2. Each chunk has type text/plain
3. We create a Multi from ticks. The elemens are just an incrementing counter
4. We throw away the element, and create a new element containing the time
5. We use httpie with `--stream` to show each chunk as it comes in. You can also use `cURL`.
6. Each 'data:' element is a chunk
7. We need to `Ctrl-C` to abort, since it's a never-ending stream :)


## Visualising the events

```java [|1|2|3|4|5|6|7|8]
Multi.createFrom().items(1,2,3)
.onSubscribe().invoke(() -> System.out.println("⬇️ Subscribed"))
.onItem().invoke(i -> System.out.println("⬇️ Received item: " + i))
.onFailure().invoke(f -> System.out.println("⬇️ Failed with " + f))
.onCompletion().invoke(() -> System.out.println("⬇️ Completed"))
.onCancellation().invoke(() -> System.out.println("⬆️ Cancelled"))
.onRequest().invoke(l -> System.out.println("⬆️ Requested: " + l))
.subscribe().with(__ -> {}); // Drain
```

This prints the following:
``` [|1|2|3-5|6]
⬇️ Subscribed
⬆️ Requested: 9223372036854775807
⬇️ Received item: 1
⬇️ Received item: 2
⬇️ Received item: 3
⬇️ Completed
```

Note:
Explain every line

What's going on here?

What we see is that the `with` method requests Long.MaxValue elements. Basically, it's a subscriber that doesn't ever apply back-pressure!


## Visualising the events

```java [|8-10]
Stream<Integer> out = Multi.createFrom().items(1,2,3)
        .onSubscribe().invoke(() -> System.out.println("⬇️ Subscribed"))
        .onItem().invoke(i -> System.out.println("⬇️ Received item: " + i))
        .onFailure().invoke(f -> System.out.println("⬇️ Failed with " + f))
        .onCompletion().invoke(() -> System.out.println("⬇️ Completed"))
        .onCancellation().invoke(() -> System.out.println("⬆️ Cancelled"))
        .onRequest().invoke(l -> System.out.println("⬆️ Requested: " + l))
        .subscribe().asStream();

Set<Integer> set = out.collect(Collectors.toSet());
```

This prints:
``` [|2]
⬇️ Subscribed
⬆️ Requested: 256
⬇️ Received item: 1
⬇️ Received item: 2
⬇️ Received item: 3
⬇️ Completed
```

Note: So `asStream` only requests 256 items!


## Visualising the events

We can configure the amount of items to queue:
```java [8-9]
Stream<Integer> out = Multi.createFrom().items(1,2,3)
  .onSubscribe().invoke(() -> System.out.println("⬇️ Subscribed"))
  .onItem().invoke(i -> System.out.println("⬇️ Received item: " + i))
  .onFailure().invoke(f -> System.out.println("⬇️ Failed with " + f))
  .onCompletion().invoke(() -> System.out.println("⬇️ Completed"))
  .onCancellation().invoke(() -> System.out.println("⬆️ Cancelled"))
  .onRequest().invoke(l -> System.out.println("⬆️ Requested: " + l))
  // Use a buffer capacity of 2
  .subscribe().asStream(2, () -> new ArrayBlockingQueue<>(2));

Set<Integer> set = out.collect(Collectors.toSet());
```

``` [2|3-4|5|6-7]
⬇️ Subscribed
⬆️ Requested: 2
⬇️ Received item: 1
⬇️ Received item: 2
⬆️ Requested: 2
⬇️ Received item: 3
⬇️ Completed
```

Note:
Our buffer of 2 creates a demand of 2. When empty, it creates new demand of 2. 


## Visualising the events

The Stream created by `asStream` will *cancel* the `Multi` when `close`d.

```java [|11]
Stream<Integer> out = Multi.createFrom().items(1,2,3,4,5,6)
  .onSubscribe().invoke(() -> System.out.println("⬇️ Subscribed"))
  .onItem().invoke(i -> System.out.println("⬇️ Received item: " + i))
  .onFailure().invoke(f -> System.out.println("⬇️ Failed with " + f))
  .onCompletion().invoke(() -> System.out.println("⬇️ Completed"))
  .onCancellation().invoke(() -> System.out.println("⬆️ Cancelled"))
  .onRequest().invoke(l -> System.out.println("⬆️ Requested: " + l))
  .subscribe().asStream(2, () -> new ArrayBlockingQueue<>(2));

Set<Integer> set = out.limit(2).collect(Collectors.toSet());
out.close();
```

``` [|8]
⬇️ Subscribed
⬆️ Requested: 2
⬇️ Received item: 1
⬇️ Received item: 2
⬆️ Requested: 2
⬇️ Received item: 3
⬇️ Received item: 4
⬆️ Cancelled
```


## Backpressure strategies

Essentially, there are three things we can do when the producer is faster than the consumer:

* Reduce the speed of the producer
* Buffer items
* Drop items

Note:
- We've seen the first, that's backpressure. But it's not always possible. What if you have a realtime stream that you can't pause? For example, if you're connected to Twitter, you can't tell the people to not Tweet for a while.
- The second one is viable in some cases. In memory buffering works well for small spikes. Longer-term buffering can be done in systems like Kafka.
- The third one is sometimes necessary. Items can be sampled, or maybe they can be cheaply merged.


## Backpressure strategies

```java [|1|2-4|6-7|9-11|]
Multi.createFrom().ticks().every(Duration.ofMillis(10))
  .onItem().invoke(i -> System.out.println("A - ⬇️ Received item: " + i))
  .onFailure().invoke(f -> System.out.println("A - ⬇️ Failed with " + f))
  .onRequest().invoke(l -> System.out.println("A - ⬆️ Requested: " + l))

  .onItem().transformToUni(e -> Uni.createFrom().item(e).onItem()
        .delayIt().by(Duration.ofSeconds(1))).concatenate()

  .onItem().invoke(i -> System.out.println("B - ⬇️ Received item: " + i))
  .onFailure().invoke(f -> System.out.println("B - ⬇️ Failed with " + f))
  .onRequest().invoke(l -> System.out.println("B - ⬆️ Requested: " + l))
  .subscribe().with(__ -> {});`
```

Prints the following:
``` [|1|2|3|4-5]
B - ⬆️ Requested: 9223372036854775807
A - ⬆️ Requested: 1
A - ⬇️ Received item: 0
A - ⬇️ Failed with io.smallrye.mutiny.subscription.BackPressureFailure: Could not emit tick 1 due to lack of requests
B - ⬇️ Failed with io.smallrye.mutiny.subscription.BackPressureFailure: Could not emit tick 1 due to lack of requests
```

Note:
1. We create a stream with ticks every 10 milliseconds
2. We call the first part of the stream the **A** part, and print messages as such
3. Next, there is a **slow** transformer, that basically transforms a single element per second.
4. We call the second part of the stream the **B** part
5. We see a ton of demand coming from the `subscribe.with`
6. But the `transform` only sends a demand of 1 upstream when it's not processing one already
7. So we see one element fly by
8. And then the stream crashes, because it's 'full': the stream wants to emit an element but there is no demand.


## Dropping excessive elements

```java [|5-6|]
Multi.createFrom().ticks().every(Duration.ofMillis(900))
  .onItem().invoke(i -> System.out.println("A - ⬇️ Received item: " + i))
  .onFailure().invoke(f -> System.out.println("A - ⬇️ Failed with " + f))
  .onRequest().invoke(l -> System.out.println("A - ⬆️ Requested: " + l))
  // Drop on overflow
  .onOverflow().drop()
  .onItem().transformToUni(e -> Uni.createFrom().item(e).onItem().delayIt().by(Duration.ofSeconds(1))).concatenate()
  .onItem().invoke(i -> System.out.println("B - ⬇️ Received item: " + i))
  .onFailure().invoke(f -> System.out.println("B - ⬇️ Failed with " + f))
  .onRequest().invoke(l -> System.out.println("B - ⬆️ Requested: " + l))
  .subscribe().with(__ -> {});
```

``` [|1-2|3-5|5,8|]
B - ⬆️ Requested: 9223372036854775807
A - ⬆️ Requested: 9223372036854775807
A - ⬇️ Received item: 0
A - ⬇️ Received item: 1
B - ⬇️ Received item: 0
A - ⬇️ Received item: 2
A - ⬇️ Received item: 3
B - ⬇️ Received item: 2
```

Note: 
1. We add an `onOverflow.drop()` to drop elements in case there's no demand.
2. We now see infinite demand in both locations in the stream
3. Position A sees element 0 and 1, and then position B sees element 0.
4. But position B never sees element 1! Because it's dropped.


## Buffering

```java [|5-6|]
Multi.createFrom().ticks().every(Duration.ofMillis(900))
  .onItem().invoke(i -> System.out.println("A - ⬇️ Received item: " + i))
  .onFailure().invoke(f -> System.out.println("A - ⬇️ Failed with " + f))
  .onRequest().invoke(l -> System.out.println("A - ⬆️ Requested: " + l))
  // Buffer on overflow
  .onOverflow().buffer(10)
  .onItem().transformToUni(e -> Uni.createFrom().item(e).onItem().delayIt().by(Duration.ofSeconds(1))).concatenate()
  .onItem().invoke(i -> System.out.println("B - ⬇️ Received item: " + i))
  .onFailure().invoke(f -> System.out.println("B - ⬇️ Failed with " + f))
  .onRequest().invoke(l -> System.out.println("B - ⬆️ Requested: " + l))
  .subscribe().with(__ -> {});
```

```
B - ⬆️ Requested: 9223372036854775807
A - ⬆️ Requested: 9223372036854775807
A - ⬇️ Received item: 0
A - ⬇️ Received item: 1
B - ⬇️ Received item: 0
A - ⬇️ Received item: 2
B - ⬇️ Received item: 1
A - ⬇️ Received item: 3
B - ⬇️ Received item: 2
```

Note: 
What we see here is that B receives all elements. Obviously, this is up to a point; after a while the buffer is full and the stream will still crash.

Other strategies, that mutiny currently doesn't have built in yet:
- Sampling
- Batching  
- Conflating items (combining them)

If you want more advanced features, take a look at more advanced reactive streams libraries, like RxJava or Akka Streams.

