package ru.medgrand.service.Model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {

    private Long Id;
    private User user;
    private List<Dish> dishes;
    private Long sum;
    private String address;

    private LocalDateTime creationDate;
    private LocalDateTime deliveryDate;


}
