package com.example.async;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RegisteringExecutorService implements ScheduledExecutorService {

    private final ConcurrentLinkedDeque<Future<?>> futures = new ConcurrentLinkedDeque<>();

    private final ScheduledExecutorService delegate;

    public RegisteringExecutorService(ScheduledExecutorService delegate) {
        this.delegate = delegate;

        // clean up the finished futures regularly
        delegate.schedule(this::cleanUp, 1, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Future<T> future = delegate.submit(task);
        futures.add(future);
        return future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        Future<T> future = delegate.submit(task, result);
        futures.add(future);
        return future;
    }

    @Override
    public Future<?> submit(Runnable task) {
        Future<?> future = delegate.submit(task);
        futures.add(future);
        return future;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        List<Future<T>> response = delegate.invokeAll(tasks);
        futures.addAll(response);
        return response;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        List<Future<T>> response = delegate.invokeAll(tasks, timeout, unit);
        futures.addAll(response);
        return response;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return delegate.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        delegate.execute(command);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return delegate.schedule(command, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return delegate.schedule(callable, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return delegate.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return delegate.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    void awaitAllTaskFinished(long finish) {
        while (!futures.isEmpty() && System.currentTimeMillis() <= finish) {
            cleanUp();
        }

        if (!futures.isEmpty()) {
            throw new IllegalStateException("There are still " + futures.size() + " pending tasks " + futures);
        }
    }

    private void cleanUp() {
        if (!futures.isEmpty()) {
            futures.removeAll(futures.stream().filter(f -> f.isDone() || f.isCancelled()).collect(Collectors.toList()));
        }
    }

}
