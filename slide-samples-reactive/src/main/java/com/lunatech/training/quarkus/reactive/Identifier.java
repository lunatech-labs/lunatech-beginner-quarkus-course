package com.lunatech.training.quarkus.reactive;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Identifier extends PanacheEntity {
    String identifier;

    @ManyToOne()
    Product product;
}
