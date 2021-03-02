# Persistence


## Learning outcomes

After this module, you should:
* Understand the different connectivity options
* Know how to configure a data source
* Know how to use Hibernate + Panache to retrieve and store data
* Understand where `@Transactional` annotation can be placed
* Know how to write tests that include the persistence layer


## Options options options




## Quarkus, Hibernate & Panache

* Quarkus and Hibernate are best buddies.
* Panache is a layer on top which:
    * Facilitates Active Record or Repository patterns
    * ... with many pre-created methods
    * Can create RESTful endpoints for entities

Note:
Remark that Quarkus and Hibernate are both primarily maintained by Red Hat, and a lot of overlapping people.


## Configuring the Data Source

TODO


## Active Record Example

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


## Repository Example

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


## Paging

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


## Sorting

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


##  Query Projection

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


## Field access rewrite

* You can write your Panache Entity with public fields
* Quarkus will automatically rewrite all access to (generated) getters and setters
* You can override getters and setters when you want.


## Field access rewrite example

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


## Putting our products in the database

* TODO, show setup SQL script
* TODO, show docker-compose to setup a database


## Transactions

TODO



<!-- .slide: data-background="#abcdef" -->
## Exercise: Product from the database


## CDI & ArC

CDI is a Jakarta EE and MicroProfile spec for Context & Dependency Injection

Quarkus has a partial implementation called *ArC*

Note:
* Remark that ArC is non-compliant, because of the strong build-time focus. Some parts of CDI are essentially impossible to implement at build-time.


## ArC - Build Time DI

* At compile-time ArC analyzes all classes and dependencies
* At runtime, ArC just has to read the generated metadata and instantiate classes.


## Features

* Field, constructor and setter injection
* @Dependent, @ApplicationScoped, @Singleton, @RequestScoped and @SessionScoped scopes
* @AroundInvoke, @PostConstruct, @PreDestroy, @AroundConstruct lifecycle callbacks and interceptors


## Injection

* Basically what you'd expect
* Uses `@Inject` annotation
* All resolved compile time, so no general `@Conditional` like Spring
* But there is `@IfBuildProperty`


## Scopes

* Normal scopes: `@ApplicationScoped`, `@RequestScoped`, `@SessionScoped` - Created when a method is invoked.
* Pseudo scopes: `@Singleton`, `@Dependent` - Created when injected.

Note:
SessionScoped is only available if `quarkus-undertow` is used. Not very common to use these days, since we prefer stateless apps.

So for the normal scoped beans, a proxy is injected, which instantiates the actual class only when a method is invoked.

Also note: all beans are created lazily! If you need to create a bean eagerly, invoke a method on it, or make it observe the `StartupEvent`


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

Create an _Interceptor Binding_
```java
@InterceptorBinding
@Target({METHOD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Timed {}
```

and an Interceptor:

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

Now we can use it on a method:

```java
@Timed
public static List<Product> getAll() {
    return listAll();
}
```

Alternatively, we could put it on the class, to time all method invocations on the class.


## Non-standard Features

* @Inject can be skipped if there's a qualifier annotation like `@ConfigProperty` present
* No-args constructors can be skipped, and `@Inject` is not needed if there's only one constructor
* Mark a bean intended to be overridden as `@DefaultBean`

Note:
- The requirement to have a no-arg constructor for any bean normally comes from CDI. So ArC relaxes this requirement.
- @DefaultBean puts it in priority behind regular beans.


<!-- .slide: data-background="#abcdef" -->
## Exercise: CDI & ArC


## Recap

In this module we have:
* Configured a Quarkus datasource to connect to PostgreSQL
* Added the Hibernate+Panache extension for our persistence layer
* Seen how to use the @Transactional annotation
