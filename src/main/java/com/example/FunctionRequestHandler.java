package com.example;

import com.example.async.AsyncSupport;
import com.example.async.RegisteringExecutorService;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class FunctionRequestHandler extends MicronautRequestHandler<String, Map<String, Object>> {

    private static int staticCount;

    @Inject
    CountHolder countHolder;

    @Inject
    AsyncSupport asyncSupport;

    private int instanceCount;

    @Override
    public Map<String, Object> execute(String input) {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("static", staticCount++);
        result.put("instance", instanceCount++);
        result.put("context", countHolder.getCount());
        result.put("context async", countHolder.getAsyncCount());

        countHolder.setCount(countHolder.getCount() + 1);
        countHolder.setAsyncCount(countHolder.getAsyncCount() + 1);

        asyncSupport.awaitAsyncFinished(5, TimeUnit.SECONDS) ;

        return result;
    }



}
