package com.filippovich.multithreadingtask.service;

import com.filippovich.multithreadingtask.entity.Port;
import com.filippovich.multithreadingtask.entity.Ship;
import com.filippovich.multithreadingtask.entity.Warehouse;
import com.filippovich.multithreadingtask.exception.PortException;
import com.filippovich.multithreadingtask.entity.ShipTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataInitializer {
    private static final Logger logger = LogManager.getLogger(DataInitializer.class);

    public Port initializePort(String configFilePath) throws PortException {
        Properties properties = new Properties();
        URL resource = getClass().getClassLoader().getResource(configFilePath);
        if (resource == null) {
            throw new PortException("Config file not found: " + configFilePath);
        }

        try (FileReader reader = new FileReader(resource.getFile())) {
            properties.load(reader);
            int berths = Integer.parseInt(properties.getProperty("port.berths"));
            int capacity = Integer.parseInt(properties.getProperty("port.warehouse.capacity"));
            int initial = Integer.parseInt(properties.getProperty("port.warehouse.initial.containers"));

            Warehouse warehouse = new Warehouse(capacity, initial);
            Port port = new Port(berths, warehouse);
            logger.info("Port initialized with {} berths. Warehouse: {}/{}", berths, initial, capacity);
            return port;
        } catch (IOException | NumberFormatException e) {
            logger.error("Failed to initialize port from config", e);
            throw new PortException("Failed to read or parse config file", e);
        }
    }

    public List<Ship> initializeShips(String shipsFilePath, Port port) throws PortException {
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