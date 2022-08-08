package com.example;

import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class CountHolder {

    private final AtomicInteger asyncCount = new AtomicInteger();
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAsyncCount() {
        return asyncCount.get();
    }

    @Async
    public void setAsyncCount(int asyncCount) {
        int currentSyncCount = count;
        int millis = new Random().nextInt(2000);
        System.out.println("setting the async count (" + millis + " ms) when sync count is " + currentSyncCount);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("setting the async count interrupted");
            throw new RuntimeException(e);
        }
        this.asyncCount.set(asyncCount);
        System.out.println("async count set when the original sync count is " + currentSyncCount + " and the original wait time was " + millis);
    }

}
