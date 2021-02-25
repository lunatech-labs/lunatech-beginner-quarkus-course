package com.lunatech.training.quarkus.reactive;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

// TODO, this class seems necessary for the dead-letter configuration.
public class GreetSerializer<Greet> extends ObjectMapperSerializer<Greet> {

}
