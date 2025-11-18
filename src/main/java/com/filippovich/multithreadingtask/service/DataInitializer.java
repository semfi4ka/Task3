package com.filippovich.multithreadingtask.service;

import com.filippovich.multithreadingtask.entity.Port;
import com.filippovich.multithreadingtask.entity.Ship;
import com.filippovich.multithreadingtask.entity.ShipTask;
import com.filippovich.multithreadingtask.exception.PortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataInitializer {
    private static final Logger logger = LogManager.getLogger(DataInitializer.class);


    public List<Ship> initializeShips(String shipsFilePath) throws PortException {
        Port port = Port.getInstance();

        URL resource = getClass().getClassLoader().getResource(shipsFilePath);
        if (resource == null) {
            throw new PortException("Ships file not found: " + shipsFilePath);
        }

        List<Ship> ships = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(resource.toURI()))) {
            ships = lines.map(line -> line.split(","))
                    .map(parts -> {
                        int capacity = Integer.parseInt(parts[0].trim());
                        int current = Integer.parseInt(parts[1].trim());
                        ShipTask task = ShipTask.valueOf(parts[2].trim());
                        int amount = Integer.parseInt(parts[3].trim());
                        return new Ship(port, capacity, current, task, amount);
                    })
                    .collect(Collectors.toList());
            logger.info("Initialized {} ships from file.", ships.size());
            return ships;
        } catch (Exception e) {
            logger.error("Failed to initialize ships from file", e);
            throw new PortException("Failed to read or parse ships file", e);
        }
    }
}