# Qute


## Connaissances obtenues

A l’issue de ce module, vous devriez :
* Comprendre les motivations derrières Qute
* Apprendre à créer et utiliser une template Qute
* Apprendre à créer des tags Qute personnalisés et des extensions de méthodes


## Moteur de template Qute

Quarkus est livré avec un moteur de template appelé *Qute* (**Qu**arkus **te**mplating):

* Une syntaxe simple
* Minimiser l'usage de la réflexion
* Optionnellement, sûreté du typage
* La sortie peut être streamée

Note:
* La diminution de la reflexion diminue la taille de l'image native.
* Type-safe en option : nous le verrons dans les prochains slides.
* La sortie peut être diffusée en continu : utilise la segmentation HTTP pour réduire la mémoire requise.
* Possibilité de parler ici de sous quelle forme les templates sont compilés, et comment cela fonctionne en mode natif.
* IMPORTANT : Besoin de mentionner que c'est Expérimental -- pas de guarantie de stabilité tant que la solution n'est pas mature. Cela peut être une bonne transition pour discuter des extension Quarkus en général.


## Detour: Quarkus Extensions
* On peut les considérer comme des dépendances de projet, mais avec des dimensions supplémentaires
  * Augmentation du temps de construction
* Elles aident les bibliothèques tierces à s'intégrer plus facilement dans les applications Quarkus et à créer
* Les intégrations peuvent cibler plus facilement GraalVM
* Status : `Stable`, `Preview`, `Experimental`

Note:
* On peut indiquer que les développeurs peuvent créer leurs propres extensions, bien que nous n'allons pas aborder cela dans ce cours


## Les expressions de Qute

Avec une classe `Product` donnée:
```java
public class Product {
    public String name;
    public BigDecimal price;
}
```

Le template présentant les détails d’un produit :
```html
<html>
  <head>
    <title>{product.name}</title>
  </head>
  <body>
    <h1>{product.name}</h1>
    <div>Price: {product.price}</div>
  </body>
</html>
```


## Iterations avec Qute

Vous pouvez faire l’itération d’une collection:

```html
<ul>
{#for product in products}
  <li>{product.name}</li>
{/for}
</ul>
```

Note:
À l'intéreur du {for} il y a des valeurs utilisables telles que index, hasNext, odd, even, count


## Quelques opérateurs Qute

Qute possède quelques opérateurs utiles :

```html [|1|2|3|5|6|]
Manufacturer: {product.manufacturer ?: 'Unknown'}
Manufacturer: {product.manufacturer or 'Unknown'}
Available: {product.isAvailable ? 'Yep' : 'Nope' }

{product.isAvailable && product.isCool}
{product.isAvailable || product.isCool}
```

Note:
* Les deux premières lignes possèdent l'_opérateur Elvis_, affectation d'une valeur par défaut si ce qui est à gauche est inaccessible ou null.
* La troisième ligne utilise l'_opérateur ternaire_.


## Utilisation de Qute

```java [|1-2|6|8|]
@Inject
Template productDetails;

@GET
@Path("{productId}")
public TemplateInstance product(@PathParam("productId") long productId) {
  Product product = Product.findById(productId);
  return productDetails.data("product", product);
}
```

1. Injection d’un template. Quarkus dérive le nom du fichier du template file à partir du nom du champ.
2. La méthode dans la ressource retourne un `TemplateInstance`. RESTeasy sait comment le convertir en réponse HTML.
3. Alimente le template avec les données pour créer un `TemplateInstance`.


<!-- .slide: data-background="#abcdef" -->
## Exercice: Un Hello World avec Qute

Note:
* Faire remarquer qu'ils peuvent accéder à l'interface utilisateur de développement et prévisualiser le template en transmettant du JSON


## Méthodes virtuelles Qute

Qute autorise l’appel de méthodes virtuelles sur des valeurs. Elles sont appelées virtuelles car elles ne correspondent pas à de réelles méthodes dans l’objet Java:

```html [|1|2-3]
<p>Name: {name}</p>
<p>Name: {name.toUpperCase()}</p>
<p>Name: {name.toUpperCase}</p>
```

Note:
* `toUpperCase` est une méthode nullaire sur une String Java. On peut l'appeler avec ou sans parenthèses


## Méthodes virtuelles Qute

```java [|1|3-9|]
@TemplateExtension
public class StringExtension {
    public static String shout(String in) {
        return in + "!";
    }

    public static String shout(String in, String append) {
        return in + append;
    }
}
```

```html [|1|2|3|]
<p>Name: {name.shout}</p>
<p>Name: {name.shout('!!!')}</p>
<p>Name: {name shout '!!!'}</p>
```

Note:
* `shout` est une méthode virtuelle. En fait il y a deux méthodes virtuelles, avec et sans paramètre
* À la dernière ligne, nous utilisons la notation `infix`


## Méthodes virtuelles Qute

Nous ne pouvons appeler des méthodes _réelles_ avec des paramètres :

    <p>Name: {name.replace('k', 'c'}}</p>

Affichera:

    <p>Name: NOT_FOUND</p>


## Méthodes virtuelles Qute - Template Data

Mais nous pouvons apprendre à Qute à générer un _value resolver_ pour nous :

    @TemplateData(target = String.class)

Maintenant cela fonctionne comme attendu :

    <p>Name: {name.replace('k', 'c'}}</p>

Note:
// Essayez d'expliquer un peu plus les values resolvers.


## Templates type-safe

Dans l’exemple précédent, nous avons vu la ligne suivante

     <p>Name: {name.replace('k', 'c'}}</p>

affichait `NOT_FOUND`, lors de l'exécution. Nous pouvons améliorer cela, et faire en sorte que Qute génère une erreur à l'exécution, en indiquant dans le template que la valeur attendue est du type `String`:

```html [|1,7|]
{@java.lang.String name}
<html>
  <head>
      <title>Qute Examples</title>
  </head>
  <body>
  <p>Name: {name.replace('k', 'c')}</p>
  </body>
</html>
```



## Templates type-safe

Maintenant, Qute devrait afficher une erreur :

![Qute error message rendered in the browser](images/qute/qute-error-1.png)


<!-- .slide: data-background="#abcdef" -->
## Exercice: Produits avec Qute, partie 1


<!-- .slide: data-background="#abcdef" -->
## Exercice: Produits avec Qute, partie 2


# Récapitulatif

Dans ce module nous avons:
* Discuté pourquoi Qute a été créé et comment il est différent des autres moteurs de templating
* Créé un template Qute et utilisé celui-ci à partir d’une Ressource
* Vu comment créer des tags personnalisés et des extensions de méthodes
* Vu comment créer des templates type-safe
