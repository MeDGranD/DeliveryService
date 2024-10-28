package ru.medgrand.service.Model;

import lombok.Data;

@Data
public class Dish {

    private Long Id;
    private String name;
    private String description;
    private Long cost;

}
