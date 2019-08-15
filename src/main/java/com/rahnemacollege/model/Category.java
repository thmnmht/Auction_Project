package com.rahnemacollege.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "Categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    private String categoryName;

    @Override
    public String toString() {
        return categoryName;
    }
}
