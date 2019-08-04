package com.rahnemacollege.model;


import lombok.Data;

import javax.persistence.*;


@Data
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

}
