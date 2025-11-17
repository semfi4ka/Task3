package com.filippovich.multithreadingtask.state.impl;

import com.filippovich.multithreadingtask.entity.Ship;
import com.filippovich.multithreadingtask.entity.Warehouse;
import com.filippovich.multithreadingtask.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

public class LoadingState implements ShipState {
    private static final Logger logger = LogManager.getLogger(LoadingState.class);
    private static final long PROCESS_TIME_MS = 1500;

    @Override
    public void doAction(Ship ship) throws InterruptedException {
        int freeSpace = ship.getCapacity() - ship.getCurrentContainers();
        int amountToLoad = Math.min(freeSpace, ship.getContainersToMove());

        if (amountToLoad <= 0) {
            logger.info("Ship {} is full or has no task to load.", ship.getId());
        } else {
            logger.info("Ship {} LOADING {} containers...", ship.getId(), amountToLoad);
            Warehouse warehouse = ship.getPort().getWarehouse();
            warehouse.removeContainers(amountToLoad);
            ship.setCurrentContainers(ship.getCurrentContainers() + amountToLoad);
            simulate(PROCESS_TIME_MS, TimeUnit.MILLISECONDS);
            logger.info("Ship {} LOADED {}. Current cargo: {}", ship.getId(), amountToLoad, ship.getCurrentContainers());
        }

        ship.setState(new DoneState());
    }
}