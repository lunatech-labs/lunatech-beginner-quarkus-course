package com.lunatech.training.quarkus;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.vertx.mutiny.sqlclient.Row;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class Product extends PanacheEntity {

    public String name;
    public String description;
    public BigDecimal price;

    public Product() {

    }

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
