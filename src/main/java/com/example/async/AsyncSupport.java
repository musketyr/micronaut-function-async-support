package com.example.async;

import jakarta.inject.Singleton;

import java.util.concurrent.TimeUnit;

@Singleton
public interface AsyncSupport {

    void awaitAsyncFinished(long time, TimeUnit unit);

}
