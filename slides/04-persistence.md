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


## CDI & ArC

* ArC - Quarkus approach of build-time (instead of runtime) wiring of dependencies
* Allows to fail fast


<!-- .slide: data-background="#abcdef" -->
## Exercise: CDI & ArC


## Quarkus testing

// TODO, write about Quarkus testing


<!-- .slide: data-background="#abcdef" -->
## Exercise: Testing


## Recap

In this module we have:
* Configured a Quarkus datasource to connect to PostgreSQL
* Added the Hibernate+Panache extension for our persistence layer
* Seen how to use the @Transactional annotation
