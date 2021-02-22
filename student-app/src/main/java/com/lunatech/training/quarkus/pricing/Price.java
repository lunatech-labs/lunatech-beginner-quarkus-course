package com.lunatech.training.quarkus.pricing;

import java.math.BigDecimal;

public class Price {
    public final Long productId;
    public final BigDecimal price;

    public Price(Long productId, BigDecimal price) {
        this.productId = productId;
        this.price = price;
    }

    public String toString() {
        return "Price(" + productId + ", " + price.toString() + ")";
    }
}
