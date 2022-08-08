package com.example.async;

import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class DefaultAsyncSupport implements AsyncSupport, BeanCreatedEventListener<ExecutorService> {

    private final List<RegisteringExecutorService> executors = new ArrayList<>();

    @Override
    public ExecutorService onCreated(BeanCreatedEvent<ExecutorService> event) {
        if (event.getBean() instanceof RegisteringExecutorService) {
            return event.getBean();
        }

        if (event.getBean() instanceof ScheduledExecutorService) {
            RegisteringExecutorService registeringExecutorService = new RegisteringExecutorService((ScheduledExecutorService) event.getBean());
            executors.add(registeringExecutorService);
            return registeringExecutorService;
        }

        return event.getBean();
    }

    @Override
    public void awaitAsyncFinished(long time, TimeUnit unit) {
        long finish = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unit);
        for (RegisteringExecutorService executor : executors) {
            executor.awaitAllTaskFinished(finish);
        }
    }


}
