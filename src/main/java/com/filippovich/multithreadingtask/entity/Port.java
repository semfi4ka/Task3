package com.filippovich.multithreadingtask.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class Port {
    private static final Logger logger = LogManager.getLogger(Port.class);
    private static final int DEFAULT_BERTHS = 2;
    private static final int DEFAULT_WAREHOUSE_CAPACITY = 1000;
    private static final int DEFAULT_WAREHOUSE_INITIAL = 500;
    private Port(int berthCount, Warehouse warehouse) {
        this.warehouse = warehouse;
        this.freeBerths = new ArrayDeque<>(berthCount);
        for (int i = 1; i <= berthCount; i++) {
            freeBerths.add(new Berth(i));
        }
        logger.info("Port (Singleton) initialized with {} berths. Warehouse: {}/{}", berthCount, warehouse.getCurrentContainers(), DEFAULT_WAREHOUSE_CAPACITY);
    }

    private static class PortHolder {
        private static final Warehouse DEFAULT_WAREHOUSE =
                new Warehouse(DEFAULT_WAREHOUSE_CAPACITY, DEFAULT_WAREHOUSE_INITIAL);

        private static final Port INSTANCE =
                new Port(DEFAULT_BERTHS, DEFAULT_WAREHOUSE);
    }

    public static Port getInstance() {
        return PortHolder.INSTANCE;
    }
    private final Warehouse warehouse;
    private final Queue<Berth> freeBerths;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition berthAvailable = lock.newCondition();

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public Berth acquireBerth() throws InterruptedException {
        lock.lock();
        try {
            while (freeBerths.isEmpty()) {
                logger.info("No free berths. Ship {} waiting...", Thread.currentThread().getName());
                berthAvailable.await();
            }
            Berth berth = freeBerths.poll();
            logger.info("Ship {} acquired {}", Thread.currentThread().getName(), berth);
            return berth;
        } finally {
            lock.unlock();
        }
    }

    public void releaseBerth(Berth berth) {
        lock.lock();
        try {
            freeBerths.add(berth);
            logger.info("Ship {} released {}. Free berths: {}", Thread.currentThread().getName(), berth, freeBerths.size());
            berthAvailable.signal();
        } finally {
            lock.unlock();
        }
    }
}