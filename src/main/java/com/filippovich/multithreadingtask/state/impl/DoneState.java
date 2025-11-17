package com.filippovich.multithreadingtask.state.impl;

import com.filippovich.multithreadingtask.entity.Ship;
import com.filippovich.multithreadingtask.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

public class DoneState implements ShipState {
    private static final Logger logger = LogManager.getLogger(DoneState.class);
    private static final long LEAVING_TIME_MS = 50;

    @Override
    public void doAction(Ship ship) throws InterruptedException {
        logger.info("Ship {} finished processing and leaving Berth {}...", ship.getId(), ship.getBerth().getId());
        simulate(LEAVING_TIME_MS, TimeUnit.MILLISECONDS);
        ship.getPort().releaseBerth(ship.getBerth());
        ship.setBerth(null);
    }
}