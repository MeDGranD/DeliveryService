package ru.medgrand.service.Model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long Id;
    private String username;
    private String password;
    private String role;

    private LocalDateTime creationDate;
    private LocalDateTime birthday;

}
