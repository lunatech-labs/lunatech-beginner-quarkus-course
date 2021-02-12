# Qute


## Qute Template Engine

Quarkus comes with a template engine named *qute*:

* Simple syntax
* Minimized reflection usage
* Optionally type-safe


## Qute Template Engine - Expressions

```java
public class Product {
    public String name;
    public BigDecimal price;
}
```

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

