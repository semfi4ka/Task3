package com.filippovich.multithreadingtask.state.impl;

import com.filippovich.multithreadingtask.entity.Ship;
import com.filippovich.multithreadingtask.entity.ShipTask;
import com.filippovich.multithreadingtask.entity.Warehouse;
import com.filippovich.multithreadingtask.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

public class UnloadingState implements ShipState {
    private static final Logger logger = LogManager.getLogger(UnloadingState.class);
    private static final long PROCESS_TIME_MS = 1500;

    @Override
    public void doAction(Ship ship) throws InterruptedException {
        int amountToUnload = Math.min(ship.getCurrentContainers(), ship.getContainersToMove());

        if (amountToUnload <= 0) {
            logger.info("Ship {} has nothing to unload.", ship.getId());
        } else {
            logger.info("Ship {} UNLOADING {} containers...", ship.getId(), amountToUnload);
            Warehouse warehouse = ship.getPort().getWarehouse();
            warehouse.addContainers(amountToUnload);
            ship.setCurrentContainers(ship.getCurrentContainers() - amountToUnload);
            simulate(PROCESS_TIME_MS, TimeUnit.MILLISECONDS);
            logger.info("Ship {} UNLOADED {}. Current cargo: {}", ship.getId(), amountToUnload, ship.getCurrentContainers());
        }

        if (ship.getTask() == ShipTask.LOAD_UNLOAD) {
            ship.setState(new LoadingState());
        } else {
            ship.setState(new DoneState());
        }
    }
}