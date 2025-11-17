package com.filippovich.multithreadingtask.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private static final Logger logger = LogManager.getLogger(Port.class);

    private final Warehouse warehouse;
    private final Queue<Berth> freeBerths;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition berthAvailable = lock.newCondition();

    public Port(int berthCount, Warehouse warehouse) {
        this.warehouse = warehouse;
        this.freeBerths = new ArrayDeque<>(berthCount);
        for (int i = 1; i <= berthCount; i++) {
            freeBerths.add(new Berth(i));
        }
    }

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