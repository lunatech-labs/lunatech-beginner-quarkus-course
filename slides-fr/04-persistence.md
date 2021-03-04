# Persistance


## Connaissances obtenues

A l’issue de ce module, vous devriez :
* Comprendre les différentes options de connectivité
* Savoir configurer une source de données
* Savoir utiliser Hibernate + Panache pour récupérer et stocker des données
* Comprendre où l'annotation `@Transactional` peut être placée


## Multiples options pour la persistance

* Hibernate ORM et JPA
* Hibernate ORM avec Panache
* Reactive SQL
* Plusieurs clients NoSQL (MongoDB, Redis, Neo4j, Cassandra, etc.)

Note:
* Talk about how we can use the de facto approach of Hibernate ORM and JPA if we want to take a more classic approach (using EntityManager and Repository classes).
* Emphasise that Hibernate + Panache is really about the 90% use-case (all part of the "Developer Joy" philosophy)
* Mention Panache support for NoSQL data stores like MongoDB


## Quarkus, Hibernate & Panache

* Quarkus et Hibernate sont les meilleurs copains.
* Panache est une couche qui :
    * Facilite les _Active Record_ ou _Repository patterns_
    * ... avec de nombreuses méthodes pré-créées
    * Peut créer des points d'extrémité RESTful pour les entités


Note:
Remark that Quarkus and Hibernate are both primarily maintained by Red Hat, and a lot of overlapping people.


## Configuring the Data Source

* [Agroal](https://agroal.github.io/) is the default datasource connection pooling implementation for configuring with JDBC driver
* `quarkus.datasource.*` keys in `application.properties`

```
quarkus.datasource.db-kind=...
quarkus.datasource.username=...
quarkus.datasource.password=...
quarkus.datasource.jdbc.url=...
```

Note:
* Cf. https://quarkus.io/guides/datasource
* We can mention [Agroal](https://agroal.github.io/) as the default datasource connection pooling implementation
* We can mention the different options for the key quarkus.hibernate-orm.database.generation when configuring the datasource in the 'application.properties' file.
* Can mention multiple named data sources - https://quarkus.io/guides/datasource#multiple-datasources


## Exemple d’un _Active Record_

```java [|1,2|8-10|12-14|16-18|]
@Entity
public class Product extends PanacheEntity {

    public String name;
    public String description;
    public BigDecimal price;

    public static Product findByName(String name){
        return find("name", name).firstResult();
    }

    public static List<Product> findExpensive(){
        return list("price > ?1", new BigDecimal("100"));
    }

    public static void deleteChairs(){
        delete("name", "Chair");
    }
}
```

Note:
* Extending PanacheEntity gives a ton of static methods for searching, retrieving, listing, deleting, updating etcetera.
* `findByName` uses `PanacheEntity`'s `find` method, which takes a piece of shortened HQL (Hibernate Query Language). Basically this gets expanded into `FROM Product WHERE name = ?1`. Also note the convenient 'firstResult' helper method.
* `findExpensive` uses a slightly longer HQL fragment, to support other operators than equals
* `deleteChairs` shows a mutation methohd.

Remark that this is an _Active Record_ example, but that you don't need to like this style. You can also choose a Repository Pattern style, in the next slide.


## Exemple pour un _Repository_

```java
@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

   public Product findByName(String name){
       return find("name", name).firstResult();
   }

   public List<Person> findExpensive(){
       return list("price > ?1", new BigDecimal("100"));
   }

   public void deleteChairs(){
       delete("name", "Chair");
  }

}
```

Note:
* Obviously this does the same, but now you'd typically inject one of these repositories where you need it.
* Of course you can extend this repository with any methods that you need.


## Pagination

```java [1|1-3|1-5|1-7|1-9|1-11|1-13|1-18|]
PanacheQuery<Product> activeProducts = Product.find("status", Status.Active);

activeProducts.page(Page.ofSize(25));

List<Product> firstPage = activeProducts.list();

List<Product> secondPage = activeProducts.nextPage().list();

List<Product> page7 = activeProducts.page(Page.of(7, 25)).list();

int numberOfPages = activeProducts.pageCount();

long count = activeProducts.count();

return Product.find("status", Status.Alive)
.page(Page.ofSize(25))
.nextPage()
.stream()
```

Note:
Explain every line


## Triage

```java
public static List<Product> findExpensive(){
    return list(Sort.by("price"));
}
```

```java
public static List<Product> findExpensive(){
    return list("price > ?1", Sort.by("price").descending(), new BigDecimal("100"));
}
```

Note:
* We can sort as well. Be aware of the order here; after the HQL part, first the sorting, and then at the end the parameters for the HQL query.


##  Requête de projection

```java
@RegisterForReflection
public class ProductName {
public final String name;

    public ProductName(String name){
        this.name = name;
    }
}
```

```java
PanacheQuery<ProductName> query = Product.find("active", Status.Active).project(ProductName.class);
```

Note:
* This will only retrieve the 'name' field from the database. So it's a DTO.
* The 'RegisterForReflection' is needed for native deployment.


## Réécriture de l'accès aux champs

* Vous pouvez écrire vos Entités Panache avec des champs publics
* Quarkus réécrira automatiquement tous les accès aux getters et setters (générés)
* Vous pouvez réécrire les getters and setters quand vous le souhaitez.


## Exemple de réécriture de l'accès aux champs

```java [|3,7-9|]
public class Product extends PanacheEntity {

  public String name;
  public String description;
  public BigDecimal price;

  public String getName() {
    return name.toUpperCase();
  }

}
```

Elsewhere:
```java
System.out.println(product.name);
```

Note:
This will print the product name in uppercase, because Quarkus rewrites the public field access to use the getter instead.
This is quite like similar functionality in Scala or Kotlin.


## Transactions

* Toutes les extensions liées à la persistance intégrent le _Transaction Manager_
* Une approache declarative avec l'annotation `@Transactional`
* Six types de configuration
    * `REQUIRED` (la valeur par défaut)
    * `REQUIRED_NEW`
    * `MANDATORY`
    * `SUPPORTS`
    * `NOT_SUPPORTED`
    * `NEVER`


## Insérer nos produits dans la base de données

En mode développement:
* `import.sql` dans le repertoire `src/main/resources`
* `quarkus.hibernate-orm.database.generation=drop-and-create`

En production on utiliserait plutôt la _schema migration_ avec [Flyway](https://quarkus.io/guides/flyway)

Note:
* Will want to mention that we use 'drop-and-create' for simplicity, but in a real application we would want to use schema migration with Flyway - https://quarkus.io/guides/flyway


<!-- .slide: data-background="#abcdef" -->
## Exercice : Produit depuis la base de données


## CDI & ArC

CDI est une spécification de Jakarta EE et MicroProfile pour l'injection de contexte et de dépendance

Quarkus possède une implémentation partielle de *ArC*

Note:
* Remark that ArC is non-compliant, because of the strong build-time focus. Some parts of CDI are essentially impossible to implement at build-time.


## ArC - Temps pour build avec DI (injection de dépendance)

* Au moment de la compilation, ArC analyse toutes les classes et dépendances
* Au moment de l'exécution, ArC n'a plus qu'à lire les métadonnées générées et à instancier les classes.


## Fonctionnalités

* Champs, constructeur et setter injection
* @Dependent, @ApplicationScoped, @Singleton, @RequestScoped et @SessionScoped _scopes_
* @AroundInvoke, @PostConstruct, @PreDestroy, @AroundConstruct _lifecycle callbacks_ et _interceptors_


## Injection

* En gros, ce à quoi vous vous attendez
* Utiliser l’annotation `@Inject`
* Tout est résolu durant la compilation, il n’y a donc pas de `@Conditional` comme avec Spring
* Mais il y a `@IfBuildProperty`


## Scopes (Portées)

* Portées normales: `@ApplicationScoped`, `@RequestScoped`, `@SessionScoped` - Créé lorsqu'une méthode est invoquée.
* Pseudo-portées: `@Singleton`, `@Dependent` - Créé lorsqu'il est injecté.

Note:
* SessionScoped is only available if `quarkus-undertow` is used. Not very common to use these days, since we prefer stateless apps.
* So for the normal scoped beans, a proxy is injected, which instantiates the actual class only when a method is invoked.
* Also note, all beans are created lazily! If you need to create a bean eagerly, invoke a method on it, or make it observe the `StartupEvent`.


## Lifecycle callbacks

```java

@ApplicationScoped
public class MyBean {

    @PostConstruct
    void init() {
        System.out.println("MyBean created!");
    }

}
```


## Interceptors

Créer un _Interceptor Binding_
```java
@InterceptorBinding
@Target({METHOD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Timed {}
```

Et un _Interceptor_:

```java [|1|3|6-17|]
@Timed
@Priority(100)
@Interceptor
public class TimedInterceptor {

    @AroundInvoke
    Object timeInvocation(InvocationContext context) {
        long start = System.currentTimeMillis();
        try {
            Object ret = context.proceed();
            long duration = System.currentTimeMillis() - start;
            System.out.println(context.getMethod().getName() + " call took " + duration + " millis");
            return ret;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
```

Note:
* We define an interceptor binding annotation
* We create an interceptor, and annotate it with the binding annotation

This particular one prints the duration of the method invocation.


## Interceptors

Maintenant nous pouvons l’utiliser sur une méthode:

```java
@Timed
public static List<Product> getAll() {
    return listAll();
}
```

On pourrait aussi le mettre sur la classe, pour chronométrer toutes les invocations de méthode sur la classe.


## Caractéristiques non standard

* @Inject peut être ignoré si une annotation comme `@ConfigProperty` est présente
* Les constructeurs sans arguments peuvent être ignorés, et `@Inject` n'est pas nécessaire s'il n'y a qu'un seul constructeur
* Marquer un bean destiné à être réécrit avec `@DefaultBean`

Note:
- The requirement to have a no-arg constructor for any bean normally comes from CDI. So ArC relaxes this requirement.
- @DefaultBean puts it in priority behind regular beans.


<!-- .slide: data-background="#abcdef" -->
## Exercise: CDI & ArC


## Recapitulatif

Après ce module, vous avez :
* Configuré une source de données Quarkus pour se connecter à PostgreSQL
* Ajouté l’extension Hibernate+Panache pour notre couche de persistance
* Vu comment utiliser l’annotation @Transactional
* Exploré l'injection de dépendance avec CDI et ArC
