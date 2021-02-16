# Qute


## Qute Template Engine

Quarkus comes with a template engine named *qute*:

* Simple syntax
* Minimized reflection usage
* Optionally type-safe


## Qute Template Engine - Expressions

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


## Qute Template Engine - Usage

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


## Qute Template Engine

What does Quarkus do?

* 


