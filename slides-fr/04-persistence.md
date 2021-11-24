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
* Expliquez comment nous pouvons utiliser l'approche de facto d'Hibernate ORM et JPA si nous voulons adopter une approche plus classique (en utilisant les classes EntityManager et Repository).
* Insistez sur le fait qu'Hibernate + Panache concerne vraiment 90% des cas d'utilisation (tout cela fait partie de la philosophie "Developer Joy")
* Mentionnez la prise en charge de Panache pour les magasins de données NoSQL comme MongoDB


## Quarkus, Hibernate & Panache

* Quarkus et Hibernate sont les meilleurs copains.
* Panache est une couche qui :
    * Facilite les _Active Record_ ou _Repository patterns_
    * ... avec de nombreuses méthodes pré-créées
    * Peut créer des points d'extrémité RESTful pour les entités


Note:
Faites remarquer que Quarkus et Hibernate sont tous les deux principalement maintenus par Red Hat et par beaucoup d'autres personnes.


## Configuring the Data Source

* [Agroal](https://agroal.github.io/) est l'implémentation par défaut du pool de connexion à la datasource pour la configuration avec le driver JDBC
* `quarkus.datasource.*` dans `application.properties`

```
quarkus.datasource.db-kind=...
quarkus.datasource.username=...
quarkus.datasource.password=...
quarkus.datasource.jdbc.url=...
```

Note:
* Cf. https://quarkus.io/guides/datasource
* Nous pouvons mentionner [Agroal](https://agroal.github.io/) comme l'implémentation par défaut du pool de connexion à la datasource
* Nous pouvons mentionner les différentes options pour les clés de quarkus.hibernate-orm.database.generation quand on configure la datasource dans le fichier 'application.properties'.
* Nous pouvons mentionner plusieurs sources de données nommées - https://quarkus.io/guides/datasource#multiple-datasources


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
* L'extension de PanacheEntity donne une tonnes de méthodes statiques pour rechercher, récupérer, lister, supprimer, mettre à jour etc.
* `findByName` utilise la méthode `find` de `PanacheEntity`, qui prend un morceau de HQL (Hibernate Query Language) raccourci. Fondamentalement, cela se développe en `FROM Product WHERE name = ?1`. Notez également la méthode d'assistance pratique 'firstResult'.
* `findExpensive` utilise un fragment HQL légèrement plus long, pour prendre en charge d'autres opérateurs qu'égal
* `deleteChairs` montre une méthode de mutation.

Remarquez qu'il s'agit d'un exemple _Active Record_, mais que vous n'avez pas besoin d'aimer ce style. Vous pouvez également choisir un style de Repository Pattern, dans la diapositive suivante.


## Exemple pour un _Repository_

```java
@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

   public Product findByName(String name){
       return find("name", name).firstResult();
   }

   public List<Product> findExpensive(){
       return list("price > ?1", new BigDecimal("100"));
   }

   public void deleteChairs(){
       delete("name", "Chair");
  }

}
```

Note:
* Évidemment, cela fait la même chose, mais maintenant, vous injectez généralement l'un de ces repositories là où vous en avez besoin.
* Bien sûr, vous pouvez étendre ce repository avec toutes les méthodes dont vous avez besoin.


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
Expliquez chaque ligne


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
* On peut aussi trier. Soyez conscient de l'ordre ici; après la partie HQL, d'abord le tri, puis à la fin les paramètres de la requête HQL.


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
* Cela ne récupérera que le champ 'nom' de la base de données. C'est donc un DTO.
* Le 'RegisterForReflection' est nécessaire pour le déploiement natif.


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
Cela affichera le nom du produit en majuscules, car Quarkus réécrit l'accès au champ public pour utiliser à la place le getter.
C'est assez ressemblant à une fonctionnalité similaire dans Scala ou Kotlin.


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
* Nous utilisons 'drop-and-create' pour plus de simplicité, mais dans une application réelle, nous utiliserions la migration de schéma avec Flyway - https://quarkus.io/guides/flyway


<!-- .slide: data-background="#abcdef" -->
## Exercice : Produit depuis la base de données


## CDI & ArC

CDI est une spécification de Jakarta EE et MicroProfile pour l'injection de contexte et de dépendance

Quarkus possède une implémentation partielle de *ArC*

Note:
* Remarquez que ArC n'est pas conforma, en raison du temps de construction important. Certaines parties du CDI sont essentiellement impossibles à implémenter au moment de la construction.


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
* SessionScoped est seulement disponible si `quarkus-undertow` est utilisé. Pas très courant à utiliser de nos jours, car nous préférons les applications stateless.
* Ainsi, pour les beans à portée normale, un proxy est injecté, qui instancie la classe uniquement lorsqu'une méthode est invoquée.
* Notez également que tous les beans sont créés en mode lazy ! Si vous avez besoin de créer un bean en mode eager, invoquez une méthode dessus ou faites-lui observer le `StartupEvent`.


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
* Nous définissons une annotation de liaison d'intercepteur
* Nous créons un intercepteur et l'annotons avec l'annotation de liaison

Celui-ci imprime la durée de l'invocation de la méthode.


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
- L'exigence d'avoir un constructeur sans argument pour n'importe quel bean vient normalement de CDI. L'ArC assouplit donc cette exigence.
- @DefaultBean le met en priorité derrière les beans normaux.


<!-- .slide: data-background="#abcdef" -->
## Exercise: CDI & ArC


## Recapitulatif

Après ce module, vous avez :
* Configuré une source de données Quarkus pour se connecter à PostgreSQL
* Ajouté l’extension Hibernate+Panache pour notre couche de persistance
* Vu comment utiliser l’annotation @Transactional
* Exploré l'injection de dépendance avec CDI et ArC
