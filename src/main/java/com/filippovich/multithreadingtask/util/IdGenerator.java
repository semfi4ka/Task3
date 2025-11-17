package com.filippovich.multithreadingtask.util;

import java.util.concurrent.locks.ReentrantLock;

public class IdGenerator {
    private static IdGenerator instance;
    private static final ReentrantLock instanceLock = new ReentrantLock();

    private int shipIdCounter = 0;
    private final ReentrantLock idLock = new ReentrantLock();

    private IdGenerator() {
    }

    public static IdGenerator getInstance() {
        if (instance == null) {
            instanceLock.lock();
            try {
                if (instance == null) {
                    instance = new IdGenerator();
                }
            } finally {
                instanceLock.unlock();
            }
        }
        return instance;
    }

    public int getNextShipId() {
        idLock.lock();
        try {
            return ++shipIdCounter;
        } finally {
            idLock.unlock();
        }
    }
}