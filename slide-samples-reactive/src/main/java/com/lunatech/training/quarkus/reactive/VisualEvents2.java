package com.lunatech.training.quarkus.reactive;

import io.smallrye.mutiny.Multi;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VisualEvents2 {

    // 256 request
    public static void main(String... args) {
        Stream<Integer> out = Multi.createFrom().items(1,2,3)
                .onSubscribe().invoke(() -> System.out.println("⬇️ Subscribed"))
                .onItem().invoke(i -> System.out.println("⬇️ Received item: " + i))
                .onFailure().invoke(f -> System.out.println("⬇️ Failed with " + f))
                .onCompletion().invoke(() -> System.out.println("⬇️ Completed"))
                .onCancellation().invoke(() -> System.out.println("⬆️ Cancelled"))
                .onRequest().invoke(l -> System.out.println("⬆️ Requested: " + l))
                .subscribe().asStream();

        Set<Integer> set = out.collect(Collectors.toSet());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
