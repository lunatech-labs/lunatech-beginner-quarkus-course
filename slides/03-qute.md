# Qute


## Qute Template Engine

Quarkus comes with a template engine named *qute*:

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

```html
Manufacturer: {product.manufacturer ?: 'Unknown'}
Manufacturer: {product.manufacturer or 'Unknown'}
Available: {product.isAvailable ? 'Yep' : 'Nope' }

{product.isAvailable && product.isCool} // Logical AND
{product.isAvailable || product.isCool} // Logical OR
```

Note:
* First two lines are _Elvis operator_, resolve to the default value if the previous part cannot be resolved or resolves to null
* Third line is the _ternary operator_.


## Qute Usage

```java
@Inject // 1
Template productDetails;

@GET
@Path("{productId}")
public TemplateInstance product(@PathParam("productId") long productId) { // 2
  Product product = Product.findById(productId);
  return productDetails.data("product", product); // 3
}
```

1. Inject a template. Quarkus derives the template file name from the field name.
2. Resource method returns a `TemplateInstance`. RESTeasy knows how to convert this to a response.
3. Populate the template with data to create a `TemplateInstance`.


## Exercise #2: A Qute Hello World

Enhance your Hello World application with an HTML template that renders the message.

* Create a file `src/main/resources/....html` // TODO
* Populate it with some HTML and use an expression `{message}`
* Inject it into your `Resource` class as a `Template`.
* Make your `Resource` method return a `TemplateInstance`
* Create the `TemplateInstance` using the `data` method on the `Template`
* Observe your wonderful new Hello World :-)


## Qute Template Engine

What does Quarkus do?

* Compile the template into // TODO

How does it work in native mode?

* How does it work? // TODO


## Qute Virtual Methods

// TODO (also explain extension methods)


## Qute Custom Tags

// TODO


## Exercise #3: Qute products, part 1

Now that we know some Quarkus, and some Qute, we will start on the HIQUÉA catalog!

You can find some templates you can use in the directory // TODO, or you can come up with your own templates :)

TODO, tell people how to get the Product class, and how to get some sample products.

* Create an endpoint that lists all products, using Qute
* Use a custom tag for each product in the list


## Exercise #3, Qute products, part 2

Create a 'details' page that shows the details of a product

* Use a path parameter for the product identifier

// TODO, work out the details


# Type safe templates

// TODO, explain the two methods of making template type-safe.
// TODO, demonstrate a typing error in a template and the error Quarkus gives.
// TODO, explain why the 'native' keyword is used in Java


# Summary

// TODO, explain again why Qute is created
