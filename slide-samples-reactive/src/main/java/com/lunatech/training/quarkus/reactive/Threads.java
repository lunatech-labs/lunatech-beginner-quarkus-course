package com.lunatech.training.quarkus.reactive;

import io.smallrye.mutiny.Multi;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Threads {

    public static void main(String... args) {
        Executor executor = Executors.newCachedThreadPool(new ThreadFactory() {
            private final AtomicInteger idx = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "executor-" + idx.getAndIncrement());
            }
        });


        Multi.createFrom().items(1,2,3)
                .emitOn(executor)
                .map(i -> {
                    System.out.println(Thread.currentThread().getName());
                    return i;
                })
                .subscribe().with(__ -> {});

        System.out.println("Done!");

    }
}
