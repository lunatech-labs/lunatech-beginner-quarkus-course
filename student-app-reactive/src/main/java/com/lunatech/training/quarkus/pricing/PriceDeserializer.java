package com.lunatech.training.quarkus.pricing;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class PriceDeserializer extends ObjectMapperDeserializer<Price> {
    public PriceDeserializer(){
        // pass the class to the parent.
        super(Price.class);
    }
}
