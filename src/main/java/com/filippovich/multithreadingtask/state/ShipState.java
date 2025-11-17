package com.filippovich.multithreadingtask.state;

import com.filippovich.multithreadingtask.entity.Ship;
import java.util.concurrent.TimeUnit;

public interface ShipState {
    void doAction(Ship ship) throws InterruptedException;

    default void simulate(long time, TimeUnit unit) throws InterruptedException {
        unit.sleep(time);
    }
}