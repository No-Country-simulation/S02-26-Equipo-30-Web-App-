package com.nc.horseretail.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tbl_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;
}
