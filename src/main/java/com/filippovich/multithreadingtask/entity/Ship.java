package com.filippovich.multithreadingtask.entity;

import com.filippovich.multithreadingtask.state.ShipState;
import com.filippovich.multithreadingtask.state.impl.DoneState;
import com.filippovich.multithreadingtask.state.impl.NewState;
import com.filippovich.multithreadingtask.util.IdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ship implements Runnable {
    private static final Logger logger = LogManager.getLogger(Ship.class);


    private final int id;
    private final Port port;    // оставил ссылку на порт для лучшей читабельности кода в стейтах
    private Berth berth;        // корабль хранит занимаемый причал чтобы знать какой причал освобождать в DoneState
    private ShipState state;

    private final int capacity;
    private int currentContainers;
    private final ShipTask task;
    private final int containersToMove;

    public Ship(Port port, int capacity, int currentContainers, ShipTask task, int containersToMove) {
        this.id = IdGenerator.getInstance().getNextShipId();
        this.port = port;
        this.capacity = capacity;
        this.currentContainers = currentContainers;
        this.task = task;
        this.containersToMove = containersToMove;
        this.state = new NewState();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Ship-" + id);
        try {
            while (!(state instanceof DoneState)) {
                state.doAction(this);
            }
            state.doAction(this);
        } catch (InterruptedException e) {
            logger.error("Ship {} was interrupted", id);
            Thread.currentThread().interrupt();
        }
    }

    public int getId() { return id; }
    public Port getPort() { return port; }
    public int getCapacity() { return capacity; }
    public int getCurrentContainers() { return currentContainers; }
    public ShipTask getTask() { return task; }
    public int getContainersToMove() { return containersToMove; }
    public Berth getBerth() { return berth; }

    public void setState(ShipState state) {
        this.state = state;
    }
    public void setBerth(Berth berth) {
        this.berth = berth;
    }
    public void setCurrentContainers(int currentContainers) {
        this.currentContainers = currentContainers;
    }
}