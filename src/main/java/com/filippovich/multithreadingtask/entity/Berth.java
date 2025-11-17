package com.filippovich.multithreadingtask.entity;

public class Berth {
    private final int id;

    public Berth(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Berth{" + "id=" + id + '}';
    }
}