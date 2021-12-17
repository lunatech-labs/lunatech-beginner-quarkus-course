package com.lunatech.training.quarkus;

import io.quarkus.vertx.ConsumeEvent;

public class GreetingService {

    @ConsumeEvent("greeting")
    public String consume(String name) {
        return "Hello " + name.toUpperCase();
    }

    @ConsumeEvent("name")
    public Uni<String> name(MyName name) {
        return Uni.createFrom().item(() -> "Hello " + name.getName());
    }
}
