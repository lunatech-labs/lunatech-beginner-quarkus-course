package com.lunatech.training.quarkus.reactive;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

@Entity
public class Product extends PanacheEntity {
    public String name;
    public String description;
    public BigDecimal price;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    public Set<Identifier> identifiers = Collections.emptySet();
}
