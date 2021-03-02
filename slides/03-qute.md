# Qute


## Learning outcomes

After this module, you should:
* Understand the motivations behind Qute
* Know how to create and use a simple Qute template
* Know how to create custom Qute tags and Extension methods


## Qute Template Engine

Quarkus comes with a template engine named *Qute* (**Qu**arkus **te**mplating):

* Simple syntax
* Minimized reflection usage
* Optionally type-safe
* Output can be streamed

Note:
* Minimizing reflection reduces the size of the native image.
* Optionally type-safe: we'll see this in later slides
* Output can be streamed: Uses HTTP chunking to reduce required memory.


## Qute Expressions

Given a class `Product`:
```java
public class Product {
    public String name;
    public BigDecimal price;
}
```

This is a template rendering the product details:
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


## Qute iterations

You can iterate over collections:

```html
<ul>
{#for product in products}
  <li>{product.name}</li>
{/for}
</ul>
```


## Some Qute operators

Qute has some useful operators:

```html [|1|2|3|5|6|]
Manufacturer: {product.manufacturer ?: 'Unknown'}
Manufacturer: {product.manufacturer or 'Unknown'}
Available: {product.isAvailable ? 'Yep' : 'Nope' }

{product.isAvailable && product.isCool}
{product.isAvailable || product.isCool}
```

Note:
* First two lines are _Elvis operator_, resolve to the default value if the previous part cannot be resolved or resolves to null
* Third line is the _ternary operator_.


## Qute Usage

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

1. Inject a template. Quarkus derives the template file name from the field name.
2. Resource method returns a `TemplateInstance`. RESTeasy knows how to convert this to a response.
3. Populate the template with data to create a `TemplateInstance`.


<!-- .slide: data-background="#abcdef" -->
## Exercise: A Qute Hello World

In this exercise, we will use the Qute template engine to make our Hello World endpoint a tiny bit nicer.

* Create an HTML file that shows ‘Hello World!’.
  * You can create one yourself, or copy the example from `materials/exercise-2/greet.html`.
  * Save it as `src/main/resources/templates/greet.html`.
* Inject a `io.quarkus.qute.Template` field with name `greet` using a `javax.inject.Inject` annotation.
  * Quarkus will look for a template with that name, and automatically generate the `Template` object for you!


<!-- .slide: data-background="#abcdef" -->
## Exercise: A Qute Hello World (cont'd)

* Make your hello endpoint return `greet.instance()`
* Check http://localhost:8080/hello/world to see if it works :)
* Now, change your template to use an expression ‘subject’ instead of the hardcoded ‘World’
* Change your resource to supply the subject parameter to the template.
* Check http://localhost:8080/hello/quarkus to see if it works!


## Qute Template Engine

What does Quarkus do?

* Compile the template into // TODO

How does it work in native mode?

* How does it work? // TODO


## Qute Virtual Methods

Qute allows you to call _virtual methods_ on values. They are called _virtual_ because they don't correspond to real methods on the Java value, but to

```html [|1|2-3]
<p>Name: {name}</p>
<p>Name: {name.toUpperCase()}</p>
<p>Name: {name.toUpperCase}</p>
```

Note:

* `toUpperCase` is a nullary method on Java's String. We can call that, with or without parentheses


## Qute Virtual Methods

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
* `shout` is a virtual method. In fact there are two virtual methods, with and without parameters
* In the last line, we use infix notation


## Qute Virtual Methods

We can't call _real_ methods with parameters out of the box:

    <p>Name: {name.replace('k', 'c'}}</p>

Will print:

    <p>Name: NOT_FOUND</p>


## Qute Virtual Methods - Template Data

But we can instruct Qute to generate a _value resolver_ for us:

    @TemplateData(target = String.class)

Now this works as expected:

    <p>Name: {name.replace('k', 'c'}}</p>

Note:
// TODO, explain value resolvers a bit more.


## Type safe templates

In the previous example we saw that the following line:

     <p>Name: {name.replace('k', 'c'}}</p>

printed `NOT_FOUND`, at run time. We can improve on this, and make Qute generate an error at build time, by indicating in the template that we expect a value of type `String`:

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



## Type-safe templates

Now, Qute will render an error:

![Qute error message rendered in the browser](images/qute/qute-error-1.png)


## Type-safe templates

// TODO, explain the two methods of making template type-safe.
// TODO, demonstrate a typing error in a template and the error Quarkus gives.
// TODO, explain why the 'native' keyword is used in Java


<!-- .slide: data-background="#abcdef" -->
## Exercise: Qute products, part 1

In this exercise, we will start on the HIQUEA catalogue. We will make two pages, a page that lists all products, and a page that shows the details of a product.

* Create a class `Product`, with the following public final fields, and a suitable constructor:
  * `Long id`
  * `String name`
  * `String description`
  * `BigDecimal price`
* Copy the file `materials/exercise-3/Products.java` into your project.
* Create a new `ProductsResource`


<!-- .slide: data-background="#abcdef" -->
## Exercise: Qute products, part 1 (cont'd)

* Create a `products` endpoint, that shows an HTML page with all products (use the products from the `all()` method on the `Products` class).
  * You can use the HTML from the file `materials/exercise-3/catalogue.html`.
  * Make sure to replace the following with Qute expressions:
    * Product names
    * Path parameters in URLs
    * Total number of products
* Create a `products/{productId}` endpoint, that lists the details of a product (use the `getById` method on the `Products` class).
  * You can use the HTML from the file `materials/exercise-3/details.html`.
  * Make sure to replace the following with Qute expressions:
    * Product name (twice)
    * Product ID
    * Description
    * Price


<!-- .slide: data-background="#abcdef" -->
## Exercise: Qute products, part 1 (cont'd)

* **Extra**: How would you deal with products that can’t be found?
* **Extra**: Write a test for both endpoints, testing that they give a `200` response, and contain some strings that should be there.


<!-- .slide: data-background="#abcdef" -->
## Exercise: Qute products, part 2

In this exercise, we will use some more Qute features to make some parts of our templates reusable. You will probably need the [Qute Reference Documentation](https://quarkus.io/guides/qute-reference) to figure out how to do these things.

* Create a file `layout.html`, that contains the <html>, <head> and <body> tags, and which can be used by other templates as a layout, using `{#include}`.
  * Let the templates for product listing and product details make use of this `layout.html`.
  * Make sure that both the body content and the content of the <title> tag can be overridden by a template that includes the layout.


<!-- .slide: data-background="#abcdef" -->
## Exercise: Qute products, part 2 (cont'd)

* Write an extension method `monetary`, such that BigDecimal values can be printed as money, with always to decimal places. So “40” should be printed as “€ 40.00” and “39.95” as “€ 39.95”.
* Use it in the details template where we display the price of a product.
  * Tip: You may need to use the `RawString` feature to avoid escaping.
* Write a _user-defined tag_ that displays a single list item of the products list page. So essentially the <li> tag.
* **Extra**: How would you make this template type-safe?


# Namespaces

// TODO, tell a bit about namespaces
// TODO, give an example where we use a namespace to inject info about the current request into a template?


# Recap

In this module we have:
* Discussed why Qute was created and how it compares to other template engines
* Created a Qute template and used it from a Resource
* Seen how to create custom tags and extension methods
* Seen how to create type-safe templates
