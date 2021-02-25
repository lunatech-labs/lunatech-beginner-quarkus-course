package com.lunatech.training.quarkus.reactive;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class GreetDeserializer extends ObjectMapperDeserializer<Greet> {
    public GreetDeserializer() {
        super(Greet.class);
    }

    @Override
    public Greet deserialize(String topic, byte[] data) {
        Greet g = super.deserialize(topic, data);
        System.out.println("Deserialized a greet: " + g);
        return g;
    }

}
