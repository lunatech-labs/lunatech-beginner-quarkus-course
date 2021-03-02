# Persistence



## Learning outcomes

After this module, you should:
* Understand the different connectivity options
* Know how to configure a datasource
* Know how to use Hibernate+Panache to retrieve and store data
* Understand where @Transactional annotation can be placed
* Know how to write tests that include the persistence layer


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


<!-- .slide: data-background="#abcdef" -->
## Exercise: Product from the database

In this exercise, we will start reading products from the database, rather than from the hardcoded `Products` class. We will use Hibernate + Panache as the ORM, with a Postgres database that we run on Docker using Docker Compose

* In the root of the student app project, there is a docker-compose.yml, which contains a single service; a postgres database. Start it up using:

```bash
docker-compose up --detach
```


<!-- .slide: data-background="#abcdef" -->
## Exercise: Product from the database (cont'd)

* Next, we need to add some extensions. Add the following to your `pom.xml` in the dependencies section:

```xml
<dependency>
 <groupId>io.quarkus</groupId>
 <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>
<dependency>
 <groupId>io.quarkus</groupId>
 <artifactId>quarkus-jdbc-postgresql</artifactId>
</dependency>
```
* Now we need to tell Quarkus where our database lives. Add the following to your `application.properties`:

```
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:8765/postgrs
quarkus.hibernate-orm.database.generation = drop-and-create
```


<!-- .slide: data-background="#abcdef" -->
## Exercise: Product from the database (cont'd)

* Next, make your existing `Product` class extend from `PanacheEntity’, and add an @Entity annotation.
    * This makes your `Product` class suitable for ‘Active Record’-style persistence, where the class you persist has static methods to interact with the storage.
* Add a default constructor, and make the fields non-final. Also, remove the `id` field from Product, since that field is already defined on `PanacheEntity`.
* Delete your old ‘Products’ class, and update your `ProductsResource` to use the static methods on `Product` instead.
    * Which methods did you pick?


<!-- .slide: data-background="#abcdef" -->
## Exercise: Product from the database (cont'd)

* Copy the file `materials/exercise-5/import.sql` to `src/main/resources/import.sql`.
    * Hibernate will automatically pick up this file, and execute its contents after creating the database.
    * The file will populate your database with the HIQUEA products we love so much.
* Run your app and check if everything still works :)


## CDI & ArC

* ArC - Quarkus approach of build-time (instead of runtime) wiring of dependencies
* Allows to fail fast


<!-- .slide: data-background="#abcdef" -->
## Exercise: CDI & ArC

In this exercise we won’t be doing much for HIQUEA, but we’ll practice a little bit with some ArC features!

* Create a class `SubjectBean`, with a public constructor that prints `"SubjectBean constructed"` and a method `String() subject()` that returns `"everyone"` (You can also copy this class from `/materials/exercise-6/SubjectBean.java`) .
* Add the following to your `GreetingResource` class:

```java
@Inject SubjectBean subjectBean;
```

* Run the app. What happens?


<!-- .slide: data-background="#abcdef" -->
## Exercise: CDI & ArC (cont'd)

* Add an `@Singleton` annotation to your `SubjectBean` class.
    * What happens now, if you refresh http://localhost:8080/greet several times?
* Change the annotation on MyBean from `@Singleton` to `@RequestScoped`.
    * If your refresh several times now, what happens now?
* Now, let’s start actually using the bean. Change the `greet` method on `GreetingResource` to:

```java
@GET
@Path("greet")
public String greet() {
   return "Hello, " + subjectBean.subject();
}
```

* Refresh several times. What happens now? Why is it different from the previous question?


<!-- .slide: data-background="#abcdef" -->
## Exercise: CDI & ArC (cont'd)

* Make the `GreetingResource` print `"GreetingResource Ready"` on application startup!
* Add a configuration property `name` to your configuration file, inject it into your `GreetingResource`, and use it instead of the hardcoded `"Hello"` in the `greet()` method.
* Don’t forget to update the test `GreetingResourceTest` as well!
* Constructor injection is typically preferable over field injection. Change `GreetingResource` to use constructor injection instead.


## Quarkus testing

// TODO, write about Quarkus testing


<!-- .slide: data-background="#abcdef" -->
## Exercise: Testing


## Recap

In this module we have:
* Configured a Quarkus datasource to connect to PostgreSQL
* Added the Hibernate+Panache extension for our persistence layer
* Seen how to use the @Transactional annotation
