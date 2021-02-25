package com.lunatech.training.quarkus;

import io.vertx.mutiny.sqlclient.Row;

import java.math.BigDecimal;

public class Product {

    public final String name;
    public final String description;
    public final BigDecimal price;

    public Product(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public static Product from(Row row) {
        return new Product(
                row.getString("name"),
                row.getString("description"),
                row.getBigDecimal("price"));
    }

}
