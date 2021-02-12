package com.lunatech.training.quarkus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloResourceSpring {

    @GetMapping("hello-spring")
    public String hello() {
        return "Hello from Spring!";
    }

}

