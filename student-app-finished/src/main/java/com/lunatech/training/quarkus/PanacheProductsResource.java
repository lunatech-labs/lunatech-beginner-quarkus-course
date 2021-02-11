package com.lunatech.training.quarkus;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.rest.data.panache.ResourceProperties;

@ResourceProperties(hal = true, halCollectionName = "products", path = "pproducts")
public interface PanacheProductsResource extends PanacheEntityResource<Product, Long> {
}
