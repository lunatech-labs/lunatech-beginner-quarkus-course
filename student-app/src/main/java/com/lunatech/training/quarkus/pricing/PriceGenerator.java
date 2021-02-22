package com.lunatech.training.quarkus.pricing;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class PriceGenerator {

    private final Random random = new Random();

    @Outgoing("raw-prices-out")
    public Multi<Price> generate() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(5))
                .onOverflow().drop()
                .flatMap(tick ->
                    Multi.createFrom().range(1, 8).map(productId ->
                    new Price(productId.toString(), new BigDecimal(random.nextInt(100)))));
    }

}
