# Programmation réactive


<!-- .slide: data-visibility="hidden" -->
## Connaissances obtenues

Après ce module, vous devrez :
* ...


## Modèle d’exécution

Lors de l’utilisation de RESTEasy, *impératif* par défaut, Quarkus créé autant de `executor` threads que nécessaire, jusqu'à atteindre le maximum configuré:

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

La configuration par défaut du maximum est `max(200, 8 * nr_of_cores)`

Note:
What we see here, is that if we execute 50 concurrent requests, they all get executed in parallel.


## Modèle d’exécution

Si nous limitons volontairement le nombre maximal de fils (threads):

```quarkus.thread-pool.max-threads=10```

Alors l’exécution de la même commande `ab` prend beaucoup plus longtemps:

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
Ce que nous voyons ici, c’est que la réduction du nombre maximal de thread impose que de nombreuses requêtes soient mises en attente avant de pouvoir être exécutées.


## Modèle d’exécution - Threads bloquants

Deux choses qui peuvent occuper un thread :

* Effectuer une tâche utile sur le CPU
* Attendre quelqu’un d’autre (Base de données, Appel d’API, IO Disque, etc.). C’est ce que l’on appelle _(bloquage) blocking_.

Note:
Explain the following:
* Doing useful work on the CPU is good. It's what we have it for. If all CPU's are busy doing useful work, we have great utilization of our resources, and we can be happy.
* Waiting for others is fine, it's a fact of life. But it means we need to be *doing something else* with the CPU.

So suppose we have 4 cores, and 10 threads. If 5 threads are actively computing stuff, and 5 threads are blocked, there's no problem. But if 8 threads are blocked, and only 2 doing useful CPU work, it ís a problem.

That's why Quarkus makes sure there's a royal amount of threads: at least 200 in the default config. So we can have at least 200 concurrent requests.

But there is a limitation: Quarkus can't discriminate between a thread blocked on CPU, and a thread blocked on IO. If all 200 threads are used for CPU, it will cause _thread starvation_: the computation doesn't make much progress, because a thread is scheduled only occasionally.

In the next chapter, we will see a different model that solves this.

## CPU vs IO Non-bloquant IO vs IO bloquantes

Rappelez vous la dernière section :

* Un thread occupant le CPU est bon, mais nous ne voulons pas trop de ceux-ci.
* Un thread effectuant des IO est bien, mais il ne devrait pas empêcher les autres threads d’effectuer des opérations sur le CPU.

<div class="fragment">
Solution ?

<ul>
<li> Avoir un nombre limité de threads qui effectue de taches sur le CPU
<li> Essayer d’effectuer les IO sans bloquer un thread
<li> Et si c’est impossible, avoir éventuellement un nombre illimité de threads d’attente des IO.
</ul>
</div>


## RESTEasy Reactive

L’extension `quarkus-resteasy-reactive` ajoute un support du modèle réactif de JAX-RS à Quarkus.

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

Par exemple la définition du point d’entrée REST fonctionne exactement comme la version impérative.


## RESTEasy Reactive

L’extension `quarkus-resteasy-reactive` ajoute un support du modèle réactif de JAX-RS à Quarkus.

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

Mais nous pouvons également retourner un `CompletionStage`


## Le modèle d’exécution réactif

RESTEasy reactive n'est pas indifférent aux bloquages

* Votre méthode sera appelée par le thread de la boucle d’évènement Vert.x (eventloop)
* Vous ne devriez pas le bloquer
* Mais si vous devez, annoter avec `@Blocking`


## Le modèle d’exécution réactif

### Exemple

```java
@GET
@Path("/regular")
public String regular() {
  return Thread.currentThread().getName();
}
```

C’est bien - retourne le nom du thread de la boucle Vert.x, par exemple `vert.x-eventloop-thread-3`


## Le modèle d’exécution réactif

### Mauvais exemple

```java [|4|]
@GET
@Path("/regular-slow")
public String regularSlow() {
  Thread.sleep(1000);
  return Thread.currentThread().getName();
}
```

Ce n’est pas correct.

Note:
Here we block the eventloop thread for IO. Ask the audience what they expect to happen if we measure this with a high number of concurrent requests?

See next page for the results

Ici nous avons bloqué le thread de la boucle d'événements pour de l’IO. Demander à l’audience ce qui pourrait arriver si on mesurait la performance de ceci avec un grand nombre de requêtes concurrentes ?

Voir la page suivante pour les résultats


## Le modèle d’exécution réactif

### Mauvais exemple

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


## Le modèle d’exécution réactif

### Bon exemple

```java [|2|]
@GET
@Path("/regular-slow")
@Blocking
public String blockingSlow() {
  Thread.sleep(1000);
  return Thread.currentThread().getName();
}
```

Ceci retourne `executor-thread-221`

Note:
And remember, of these threads there are very many, and you can safely tie them up in blocking IO.


## Le modèle d’exécution réactif

### Bon exemple

```
ab -c70 -n300  http://127.0.0.1:8082/threads/blocking-slow

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1   0.7      1       3
Processing:  1001 1008   4.5   1007    1023
Waiting:     1001 1008   4.5   1007    1023
Total:       1001 1009   4.9   1008    1024
```

Retour à des performances normales :)

Note:
Here we see that if we tell quarkus that our method is blocking, it will run it with an executor thread; of which there are many more available.


## Le modèle d’exécution réactif

### Un exemple encore meilleur

Evidemment, nous pouvons faire encore mieux, en ne bloquant pas du tout le thread :

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

La sortie:
    Initial: vert.x-eventloop-thread-18, later: executor-thread-1

Note:
This demonstrates asynchronous 'waiting'. No thread is blocked here. The initial part of the computation is performed on the Vert.x IO thread. The `delayIt` method doesn't block, just returns a `Uni` that completes after the specified time. Continued work with that `Uni` is not performed by an IO Thread.


## Reactive Routes

Une alternative à _RESTEasy Reactive_ est d’utiliser l’extension _Reactive Routes_:

> Reactive routes propose an alternative approach to implement HTTP endpoints where you declare and chain routes. This approach became very popular in the JavaScript world, with frameworks like Express.Js or Hapi. Quarkus also offers the possibility to use reactive routes. You can implement REST API with routes only or combine them with JAX-RS resources and servlets.

Note:
This extension is also known as 'Vert.x web'

> Reactive routes propose une approche différente à l’implémentation des points d’entrée HTTP où vous déclarez et enchaînez des routes. Cette approche a été popularisée par le monde JavaScript, avec des frameworks comme Express.Js et Hapi. Quarkus offre également la possibilité d’utiliser le routage réactif. Vous pouvez implémenter ne API REST avec des routes seules ou les combiner avec des resources ou des servlets JAX-RS.


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

Also:
Instead of injecting `RoutingContext` you can also choose some other Vert.x or Quarkus or even Mutiny HTTP model classes. Just pick one that you like working with or that has the stuff you need easily available.

How to choose between this and RESTEasy Reactive?
- RESTEasy Reactive is _experimental_ as of February 2021
- Vert.x Web is stable, so typically a better choice.


# Accès réactif à la base de données


## A propos de JDBC

JDBC est une API bloquante

Exemple:

    ResultSet rs = stmt.executeQuery(query);

Il n’existe pas de solution pour obtenir le `ResultSet` sans thread bloquante.

Note:
Remark that you don't necessarily have to block the thread you're working on. Of course you can execute the call on some different thread, and obtain a `CompletionStage` here. But then you have to block that other thread!

Of course it's not a huge problem in most applications, for several reasons:
- Databases don't like thousands of concurrent queries, so we'd probably queue requests anyway if we have many of them
- As shown, Quarkus can deal quite will with a relatively large amount of threads that are okay to block on IO
- For big apps, the overhead of several tens of threads for this isn't huge

But for *supersonic* *subatomic* we can do better!


## Hibernate devient Réactif

Depuis Décembre 2020, Hibernate Reactive a été lancé:

```java
Uni<Book> bookUni = session.find(Book.class, book.id);
bookUni.invoke( book -> System.out.println(book.title + " is a great book!") )
```

C’est une API réactive pour Hibernate ORM.


## Hibernate devient Réactif

* Fonctionne avec les clients non-bloquant d'accès aux bases de données. Pour le moment, les clients Vert.x pour Postgres, MySQL et DB2
* L’intégration avec Quarkus est agréable
* Pas de _lazy_ chargement bloquant, mais des opérations asynchrones explicites pour récupérer les associations

Note:
One other thing to mention, the creators _don't expect this to be faster than regular Hibernate ORM_. They don't expect many applications to benefit from it. However, they do expect better degradation under load for some applications, and maybe there will be performancee improvements in the future.


## Hibernate Reactive + Panache

* Les méthodes qui retournent `T` ou `List<T>` retourne maintenant `Uni<T>` et `Uni<List<T>>`
* Nouvelles méthodes `streamXXX` qui retournent `Multi<T>`
* Les classes sont dans un nouveau package `io.quarkus.hibernate.reactive`


## Hibernate Reactive + Panache usage

```java
@GET
public Multi<Product> products() {
    return Product.streamAll();
}

@GET
@Path("{productId}")
public Uni<Product> details(@PathParam("productId") Long productId) {
    return Product.findById(productId);
}
```

Note:
* Here we modified the `products` endpoint to use a `streamXXX` method of Hibernate, to get a `Multi`
Remark the difference between `Uni<List<T>>` and `Multi<T>`: The `Uni<List<T>>` will get the List into memory, while the Multi is fully streaming.


## Mutiny, Uni & Multi

**Mutiny** est la librarie pour la programmation réactive que Quarkus utilise. Il y a deux types principaux:

- `Multi<T>` représente un stream d’éléments de type `T`
- `Uni<T>`, représente un stream de zéro ou un élément de type `T`

Note:
* Mention that Multi is potentially unbounded
* Mention that they also support indicating failure.
* Mention that we *will learn much more about these types in later slides*
* Mention that Hibernate Reactive has two APIs: one using Mutiny types and one using Java Stdlib types: `CompletionStage` and `Publisher`.


## RESTEasy Reactive with Mutiny Uni

Les `Uni`s sont supportés comme un type de résultat:
```java [|3|4]
@GET
@Produces(MediaType.TEXT_PLAIN)
public Uni<String> helloUni() {
    return Uni.createFrom().item("Hello!");
}
```

Note: Again, a `Uni` is like a stream that emits up to one element. But it can also be cancelled or fail.


## RESTEasy Reactive

`Multi` est aussi supporté:

```java
@GET
@Produces(MediaType.TEXT_PLAIN)
public Multi<String> helloMulti() {
  return Multi.createFrom().items("Hello", "world!");
}
```

Cela retourne une réponse HTTP `chunked`.

Note: Meaning that Quarkus doesn't need to create the full response in memory before sending it.

So it can support arbitrarily long HTTP responses in bounded memory.

Cela sous-entend que Quarkus n’a pas besoin de créer la réponse entière en mémoire avant de l’envoyer.
Donc il peut supporter arbitrairement de longue réponse HTTP sans consommer trop de mémoire.


<!-- .slide: data-background="#abcdef" -->
## Exercise: Going Reactive


## Sessions & Transactions

```java
session.find(Product.class, id)
    .call(product -> session.remove(product))
    .call(() -> session.flush())
```

Note:
- The major point to make here is that 'sessions' and 'transactions' aren't bound to threads anymore, but need to be explicitly handled.
- Of the Hibernate Session, there are also multiple variants available; one for Java standardlib types, and one for Mutiny types!
- These are examples without Panache, using Hibernate directly


## Sessions & Transactions - Exemple

Bon exemple:
```java
Uni<Product> product = session.find(Product.class, id)
    .call(session::remove)
    .call(session::flush)
```

Mauvais exemple:
```java
Uni<Product> product = session.find(Product.class, id)
    .call(session::remove)
    .invoke(session::flush)
```

Les méthodes:
```java
Uni<T> call(Supplier<Uni<?>> supplier)
Uni<T> invoke(Runnable callback)
```

Les deux exemples compilent et possèdent les types correctes, but le deuxième _déclenchera jamais l'operation `flush`_.

Note:
* This shows a common mistake. Both of these examples compile, but the second one _will never execute the flush_.

This is because `call` expects a `Uni`, _and subscribes to it_ when the 'outer' `Uni` (containing the product) is subscribed to, even through the _result_ of the Uni created by Flush is ignored.
But `invoke` never subscribes to the `Uni` returned by `invoke`.

People familiar with reactive programming will have experienced this before typically!


## Clients SQL Réactifs Low-level

Une autre possibilité pour se connecter à la base de données est d’utiliser un client SQL low-level.
```
PgConnectOptions connectOptions = new PgConnectOptions()
  .setPort(5432)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool options
PoolOptions poolOptions = new PoolOptions()
  .setMaxSize(5);

// Create the client pool
PgPool client = PgPool.pool(connectOptions, poolOptions);
```

L’objet de base dont nous avons besoin est une instance de `PgPool`.

Note:
^ Remark that in Quarkus, *of course* we can just configure it in the unified config, so this is not needed.


## Clients SQL Réactifs Low-level

```
@Inject
PgPool client;
```

Récupérer le bon `PgPool`:
* `io.vertx.mutiny.pgclient.PgPool` uses Mutiny types
* `io.vertx.pgclient.PgPoool` uses Vert.x types

Note:
There are created with a code generator. There are also variants for RxJava 2 and RxJava 3. But when using Quarkus, sticking with the Mutiny variants is certainly your best option.


## Requêtage

Les requêtes retournent un `Uni` contenant un `RowSet`:

    Uni<RowSet<Row>> rowSetUni = client.query("SELECT name, age FROM people").execute();

Bien sûre, nous pouvons transformer cela par un `Multi` de `Rows`:

    Multi<Row> people = client.query("SELECT name, age FROM people").execute()
        .onItem().transformToMulti(set -> Multi.createFrom().iterable(set));


## Requêtage

```java
Multi<Person> people = client.query("SELECT name, age FROM people")
  .execute()
  .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
  .onItem().transform(Person::fromRow);
```

```java
static Person fromRow(Row row) {
  return new Person(row.getString("name"), row.getInteger("age"));
}
```

Note:
Explain how `onItem` and `transform` are just regular Mutiny methods
Explain that `RowSet` is an iterator that reads from the DB when requested.

Explain how we can utilize a static method on Person and a method reference to cleanly map from a `Row` to a `Person`



## Paramétres

```java [|1|2|3|]
client.preparedQuery(
    "SELECT id, name FROM fruits WHERE id = $1")
    .execute(Tuple.of(id))
```

Note:
Explain that this `Tuple` comes from Mutiny.


## Insertions et Mise à jour

```java [|1|2|3|4|]
  Uni<Long> personId = client
    .preparedQuery("INSERT INTO people (name, age) VALUES ($1, $2) RETURNING id")
    .execute(Tuple.of(name, age))
    .onItem().transform(pgRowSet -> pgRowSet.iterator().next().getLong("id"));
}
```

Note:
Explain that we retrieve back the generated Id from the database.


<!-- .slide: data-background="#abcdef" -->
## Exercise: Reactive search endpoint


## Listen & Notify

L’une des propriétés sympa de Postgres est d’écouter (`Listen`) des canaux. Dans le cadre des transactions, vous pouvez notifier les canaux, par exemple pour alerter les consommateurs qui attendent un événement.

Note:
This lends itself very well for reactive programming: keep a connection open to Postgres, have a stream of subscriptions/unsubscribes going there, and a stream of notifications coming back.



## Listen & Notify

```java
@Path("/listen/{channel}")
@GET
@Produces(MediaType.SERVER_SENT_EVENTS)
@RestSseElementType(MediaType.APPLICATION_JSON)
public Multi<JsonObject> listen(@PathParam("channel") String channel) {
  return client.getConnection()
    .onItem().transformToMulti(connection -> {
      Multi<PgNotification> notifications = Multi.createFrom().
        emitter(c -> toPgConnection(connection).notificationHandler(c::emit));
      return connection.query("LISTEN " + channel).execute().onItem().transformToMulti(__ -> notifications);
    }).map(PgNotification::toJson);
}
```

```java
@Path("/notify/{channel}")
@POST
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.WILDCARD)
public Uni<String> notif(@PathParam("channel") String channel, String stuff) {
    return client.preparedQuery("NOTIFY " + channel +  ", $$" + stuff + "$$").execute()
            .map(rs -> "Posted to " + channel + " channel");
}
```

Note:
First image shows `LISTEN`, we start listening to a Postgres _channel_. Every notification for that channel ends up in the Multi, so will be observed by someone who connects to the SSE endpoint.

Of course, like this we make a new connection per customer that connects. Ask the audience; what could we do to make this better? Answer: broadcast.

Second image shows how we `NOTIFY`


## Listen & Notify

```shell
➜ http localhost:8082/db/listen/milkshakes --stream
HTTP/1.1 200 OK
Content-Type: text/event-stream
X-SSE-Content-Type: application/json
transfer-encoding: chunked

data:{"channel":"milkshakes","payload":"{\"flavour\": \"banana\"}","processId":57}

data:{"channel":"milkshakes","payload":"{\"flavour\": \"strawberry\"}","processId":58}
```

```shell
➜ http POST localhost:8082/db/notify/milkshakes flavour=banana
HTTP/1.1 200 OK
Content-Type: text/plain
content-length: 28

Posted to milkshakes channel

➜ http POST localhost:8082/db/notify/milkshakes flavour=strawberry
HTTP/1.1 200 OK
Content-Type: text/plain
content-length: 28

Posted to milkshakes channel
```

Note:
First screenshot show connecting to the RESTful SSE 'listen' endpoint with path param 'milkshakes'.

Second screenshot shows posting messages to the 'notify' endpoint with path param 'milkshakes' and a request body.

In the first screenshot you see those come in.

TODO, maybe prepare a little demo for this?


<!-- .slide: data-background="#abcdef" -->
## Exercise: Listen & Notify

Note:

Different ways to fill in the missing part

```java
.onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
.map(Product::from)
```

```java
.toMulti().flatMap(rows -> Multi.createFrom().iterable(rows))
.map(Product::from)
```

```java
.onItem().<Row>disjoint()
.map(Product::from)
```


<!-- .slide: data-visibility="hidden" -->
## Qu’est ce qui approche (looming) à l’horizon ?

![Project Loom](images/reactive/loom.jpeg)

Note:
Explain a bit about Project Loom:
- Upcoming changes to the JDK, to support _virtual threads_, aka fibers, threads that don't consume an OS thread and thus don't suffer from the memory-overhead and poor cache behavior of regular threads. With Loom it is possible to create millions of virtual threads!
- Loom will make most existing JDBC drivers suddenly non-blocking, because all networking API's will be reimplemented in a non-blocking fashion. User code won't have to change
- Game changer for many applications, but also quite far away. Unlikely to make the Java 17 LTS, so maybe the first LTS that has it will be Java 23, due in 2024, so used at a company near you in 2026...


# Reactive Streams


## Reactive Streams

Les flux de données (*Streaming data*) sont fréquents dans les applications modernes:

- Flux d’évènements d’un système vers des consommateurs
- Flux d’enregistrements d’une base de données vers une réponse HTTP chunked (découpée en gros morceaux)
- Messages consommés depuis une queue, transformés et déposés dans une autre queue

<p class="fragment">Un problème fondamental des systèmes de traitement de flux de données est d’avoir l’assurance que le <em>consommateur</em> du flux peut traiter les messages qu’on lui envoie.</p>


## Consommateur lent

Suppose you have a system that reads records from a database, transforms them to JSON and stores them in a file.

*Question:* What happens if you read 1000 records per second, but you can only write 500 per second to files?

Note:
Answer: memory will fill up, until the system breaks.


<!-- .slide: data-visibility="hidden" -->
## Consommateur lent

Note:
TODO, draw diagram of slow consumer being overloaded by fast producer


## Consommateur lent - solutions

Solutions possibles:
* <!-- .element: class="fragment" -->Avoir un consommateur réellement rapide
* <!-- .element: class="fragment" -->Avoir un producteur lent
* <!-- .element: class="fragment" -->Avoir plus de mémoire que la taille de votre base de données
* <!-- .element: class="fragment" -->Adapter la vitesse du producteur, basé sur la capacité du consommateur

Note:
That last one is essentially what's called back-pressure. The consumer can indicate how much it wants to read.


## Back pressure

La *Back pressure* signifie que le consommateur peut faire la *demande* au producteur. Le producteur ne produira que la quantité demandée par le consommateur.

Note:
TODO, draw diagram of a consumer indicating demand.


## Back pressure

* Fonctionne avec le flux entier, et pas juste avec le consommateur à la fin
* Chaque élément peut adapter la demande qui a été envoyée en amont
  - Les composants lents réduisent la demande
  - Quelques composants, comme les buffers, peuvent augmenter la demande


## Les flux au dessus TCP

* TCP supporte nativement la back-pressure!
* Les messages *ack* *(accusés)* contiennent un champ *window* *(fenêtre)*, indiquant comment l’expéditeur doit faire l’envoi.
* Quand le destinataire reçoit les données, un nouvel accusé est envoyé, avec une fenêtre plus grande.

Note:
So we can make reactive back-pressuring systems across TCP. For example using chunked HTTP responses.


<!-- .slide: data-visibility="hidden" -->
## Comparaison avec JMS

* Interface bloquante: nous ne voulons pas d’interface bloquante
* Interface asynchrone: ne supporte pas la back pressure


## Les flux réactifs standards

A partir de 2013, les ingénieurs de Netflix, Pivotal, Lightbend, Twitter et d’autres ont tous travaillés autour des systèmes de flux, essentiellement en résolvants les mêmes problèmes.

Pour être certain que leurs librairies seront compatibles, ils ont mis en place un standard de **Reactive Streams**.

C’est une interface minimale nécessaire pour connecter les librairies de flux, retenant toutes les opérations non bloquantes, la back-pressure, le partage de l’annulation et le modèle d’erreur.


## Les flux réactifs standards

Terminé dans la bibliothèque standard Java à partir de Java 9, sous `java.util.concurrent.Flow`.

Note:
The standard itself is very small, you can't really program against it directly. For example, ther are no `map` or `filter` methods in the standard library.

So you need to use an *implementation* of the standard, to do meaningful streaming work.


## Les implémentations de flux réactif

* Akka Streams
* RxJava
* Reactor
* Vert.x
* Mutiny
* ... et bien d’autres

Note: Mutiny is the one that Quarkus uses. It's part of Smallrye. We've already used it in the DB section.

But some parts of the Vert.x implementation are also used, since Quarkus uses Vert-x as well.


## Démarrage

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


## Mutiny Uni

Un `Uni` n’est exécuté que lorsqu’il est connecté à un abonné (*subscriber*):

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


## Mutiny Multi

L’interface `Multi` de Mutiny étend `org.reactivestreams.Publisher<T>`, donc c’est un flux réactif.

Multi possède de nombreuse méthode pour interagir avec:

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

Il y a plusieurs méthodes pour créer un Multi: à partir d’éléments, à partir d'itérateurs, en les poussant impérativement, à partir d’une horloge (timer ticks). Cependant, le plus souvent en tant que développeur d'application, vous ne créez pas vos propres multi, mais vous en obtiendrez un à partir d'une bibliothèque, comme un connecteur Kafka, un connecteur de base de données, un client de service Web, un téléchargement de formulaire et ensuite vous transformerez ce multi.
La méthode  map. Proche de celle de stream.
La méthode filter. Proche de celle de stream.
La méthode `flatMap'. Pour aplatir des multis imbriqués.
Autre map
Il y en a encore bien plus, lisez la documentation. Attention; Quarkus utilise
There are many more, see the docs. Be aware; Quarkus utilise parfois une version de Mutiny quelque peu obsolète.


## Mutiny Multi

Un abonné (subscriber) à un Multi, doit faire face aux situations suivantes:

* Un élément arrive
* Une erreur intervient
* Le flux (borné) et complété

```java [|3|4|5]
Cancellable cancellable = multi
.subscribe().with(
  item -> System.out.println(item),
  failure -> System.out.println("Failed with " + failure),
  () -> System.out.println("Completed"));
```

Note: If you check the Reactive Streams spec, this is also what you see.

Si vous vérifiez les spécifications des flux réactifs, c’est ce que vous pourrez lire.


## Visualisation des événements

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

L’affichage est le suivant:
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


## Visualisation des événements

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

Cela affiche:
``` [|2]
⬇️ Subscribed
⬆️ Requested: 256
⬇️ Received item: 1
⬇️ Received item: 2
⬇️ Received item: 3
⬇️ Completed
```

Note: So `asStream` only requests 256 items!


## Visualisation des événements

Nous pouvons configurer le nombre d’élément dans la queue:
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


## Visualisation des événements

Le flux créé par `asStream` devrait annuler (*cancel*) le `Multi` quand il est fermé (`close`).

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


## Stratégies de Backpressure

Principalement, il y a trois choses à faire quand le producteur va plus vite que le consommateur:

* Réduire la vitesse du producteur
* Bufferiser les éléments
* Jeter des éléments

Note:
- We've seen the first, that's backpressure. But it's not always possible. What if you have a realtime stream that you can't pause? For example, if you're connected to Twitter, you can't tell the people to not Tweet for a while.
- The second one is viable in some cases. In memory buffering works well for small spikes. Longer-term buffering can be done in systems like Kafka.
- The third one is sometimes necessary. Items can be sampled, or maybe they can be cheaply merged.


## Stratégies de Backpressure

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

Affiche:
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


## Rejeter les éléments en trop

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


## Bufferisé

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


## L’envoi d’événement depuis le serveur (Server-Sent Events)

L’envoi d’événement depuis le serveur (Server-Sent Events) est une technologie qui autorise le serveur à pousser des données à un client quand il le veut. Le client ouvre une connexion et la garde ouverte. Le serveur peut envoyer des données tronquées (chunks).


## Server-Sent Events

Ci-dessous un exemple d’un endpoint qui envoi des données tronquées toute les secondes, contenant la date et heure courante:

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


<!-- .slide: data-visibility="hidden" -->
## Recap

In this module we have:
*
