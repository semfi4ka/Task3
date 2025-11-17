package com.filippovich.multithreadingtask.state.impl;

import com.filippovich.multithreadingtask.entity.Ship;
import com.filippovich.multithreadingtask.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewState implements ShipState {
    private static final Logger logger = LogManager.getLogger(NewState.class);

    @Override
    public void doAction(Ship ship) {
        logger.info("Ship {} created. Task: {}, Current/Capacity: {}/{}, Target: {}",
                ship.getId(), ship.getTask(), ship.getCurrentContainers(),
                ship.getCapacity(), ship.getContainersToMove());
        ship.setState(new WaitingState());
    }
}