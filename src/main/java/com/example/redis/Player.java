package com.example.redis;

import lombok.Data;

import java.io.Serializable;

@Data
public class Player implements Serializable {
    private String name;
    private int level;

    public Player(String name, int level) {
        this.name = name;
        this.level = level;
    }
}
