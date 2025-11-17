package com.filippovich.multithreadingtask.state.impl;

import com.filippovich.multithreadingtask.entity.Berth;
import com.filippovich.multithreadingtask.entity.Ship;
import com.filippovich.multithreadingtask.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WaitingState implements ShipState {
    private static final Logger logger = LogManager.getLogger(WaitingState.class);

    @Override
    public void doAction(Ship ship) throws InterruptedException {
        logger.info("Ship {} waiting for a berth...", ship.getId());
        Berth berth = ship.getPort().acquireBerth();
        ship.setBerth(berth);
        ship.setState(new MooringState());
    }
}