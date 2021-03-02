package com.lunatech.training.quarkus.pricing;

import java.math.BigDecimal;

public class Price {
    public Long productId;
    public BigDecimal price;

    public Price(){}

    public Price(Long productId, BigDecimal price) {
        this.productId = productId;
        this.price = price;
    }

    public String toString() {
        return "Price(" + productId + ", " + price.toString() + ")";
    }
}
