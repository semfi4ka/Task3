package com.filippovich.multithreadingtask.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Warehouse {
    private static final Logger logger = LogManager.getLogger(Warehouse.class);

    private final int capacity;
    private int currentContainers;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public Warehouse(int capacity, int initialContainers) {
        this.capacity = capacity;
        this.currentContainers = initialContainers;
    }

    public int getCurrentContainers() {
        lock.lock();
        try {
            return currentContainers;
        } finally {
            lock.unlock();
        }
    }

    public void addContainers(int amount) throws InterruptedException {
        lock.lock();
        try {
            while (currentContainers + amount > capacity) {
                logger.warn("Warehouse is FULL. Ship waiting to unload {} containers. Current: {}", amount, currentContainers);
                notFull.await();
            }
            currentContainers += amount;
            logger.info("Added {} containers. Warehouse now has {}/{}", amount, currentContainers, capacity);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void removeContainers(int amount) throws InterruptedException {
        lock.lock();
        try {
            while (currentContainers - amount < 0) {
                logger.warn("Warehouse is EMPTY. Ship waiting to load {} containers. Current: {}", amount, currentContainers);
                notEmpty.await();
            }
            currentContainers -= amount;
            logger.info("Removed {} containers. Warehouse now has {}/{}", amount, currentContainers, capacity);
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }
}