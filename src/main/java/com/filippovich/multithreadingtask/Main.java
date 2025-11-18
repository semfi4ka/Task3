package com.filippovich.multithreadingtask;

import com.filippovich.multithreadingtask.entity.Port;
import com.filippovich.multithreadingtask.entity.Ship;
import com.filippovich.multithreadingtask.exception.PortException;
import com.filippovich.multithreadingtask.service.DataInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final String SHIPS_FILE = "data/ships.txt";

    public static void main(String[] args) {
        logger.info("Application starting...");

        try {
            DataInitializer initializer = new DataInitializer();
            List<Ship> ships = initializer.initializeShips(SHIPS_FILE);
            Port port = Port.getInstance();
            ExecutorService executor = Executors.newFixedThreadPool(ships.size());
            ships.forEach(executor::submit);
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                    logger.warn("Executor did not terminate in the specified time. Forcing shutdown...");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.error("Main thread interrupted while waiting for ships.", e);
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }

            logger.info("All ships have been processed.");
            logger.info("Final warehouse state: {} containers.", port.getWarehouse().getCurrentContainers());

        } catch (PortException e) {
            logger.fatal("Failed to initialize application: {}", e.getMessage(), e);
        }
    }
}