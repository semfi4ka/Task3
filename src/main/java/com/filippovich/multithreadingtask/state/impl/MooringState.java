package com.filippovich.multithreadingtask.state.impl;

import com.filippovich.multithreadingtask.entity.Ship;
import com.filippovich.multithreadingtask.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

public class MooringState implements ShipState {
    private static final Logger logger = LogManager.getLogger(MooringState.class);
    private static final long MOORING_TIME_MS = 500;

    @Override
    public void doAction(Ship ship) throws InterruptedException {
        logger.info("Ship {} mooring to Berth {}...", ship.getId(), ship.getBerth().getId());
        simulate(MOORING_TIME_MS, TimeUnit.MILLISECONDS);
        logger.info("Ship {} moored.", ship.getId());

        switch (ship.getTask()) {
            case UNLOAD:
            case LOAD_UNLOAD:
                ship.setState(new UnloadingState());
                break;
            case LOAD:
                ship.setState(new LoadingState());
                break;
            default:
                ship.setState(new DoneState());
        }
    }
}