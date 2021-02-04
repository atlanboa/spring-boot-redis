package com.example.redis.model;

import lombok.Data;

@Data
public class Address {
    String country;
    String city;

    public Address(String country, String city) {
        this.country = country;
        this.city = city;
    }
}
