package com.example.dartmobileapp;

import androidx.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ресурс ожидания для асинхронных сетевых операций
 */
public class NetworkIdlingResource implements IdlingResource {
    private final String mResourceName;
    private final AtomicInteger counter = new AtomicInteger(0);
    private volatile ResourceCallback resourceCallback;

    public NetworkIdlingResource(String resourceName) {
        mResourceName = resourceName;
    }

    @Override
    public String getName() {
        return mResourceName;
    }

    @Override
    public boolean isIdleNow() {
        return counter.get() == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    public void increment() {
        counter.incrementAndGet();
    }

    public void decrement() {
        int counterVal = counter.decrementAndGet();
        if (counterVal == 0 && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
    }
} 