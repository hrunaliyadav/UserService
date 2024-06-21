package com.example.UserServiceMay24.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String value;
    @ManyToOne
    private User user;
    private Date expiresAt;
    private boolean isActive;
}
