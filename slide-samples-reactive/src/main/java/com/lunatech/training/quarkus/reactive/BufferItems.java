package com.lunatech.training.quarkus.reactive;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.time.Duration;

public class BufferItems {

    public static void main(String... args) throws InterruptedException {
        Multi.createFrom().ticks().every(Duration.ofMillis(500))
                .onItem().invoke(i -> System.out.println("A - ⬇️ Received item: " + i))
                .onFailure().invoke(f -> System.out.println("A - ⬇️ Failed with " + f))
                .onRequest().invoke(l -> System.out.println("A - ⬆️ Requested: " + l))
                // Buffer on overflow
                .onOverflow().buffer(10)
                .onOverflow().drop()
                .onItem().transformToUni(e -> Uni.createFrom().item(e).onItem().delayIt().by(Duration.ofSeconds(1))).concatenate()
                .onItem().invoke(i -> System.out.println("B - ⬇️ Received item: " + i))
                .onFailure().invoke(f -> System.out.println("B - ⬇️ Failed with " + f))
                .onRequest().invoke(l -> System.out.println("B - ⬆️ Requested: " + l))
                .subscribe().with(__ -> {});

        Thread.sleep(1000);


    }
}
