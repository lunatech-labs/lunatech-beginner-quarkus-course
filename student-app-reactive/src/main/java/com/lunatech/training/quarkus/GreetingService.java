package com.lunatech.training.quarkus;

import io.quarkus.vertx.ConsumeEvent;

public class GreetingService {
    @ConsumeEvent("greeting")
    public String consume(String name) {
        return "Hello " + name.toUpperCase();
    }
}
