package com.lunatech.training.quarkus;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Page;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.List;

@Entity
public class Product extends PanacheEntity {
    public String name;
    public String description;
    public BigDecimal price;

    public static List<Product> search(String query, Integer page, Integer pageSize) {
        return Product.<Product>find("name LIKE ?1 OR description LIKE ?1", "%" + query + "%")
                .page(Page.of(page, pageSize))
                .list();
    }

}
