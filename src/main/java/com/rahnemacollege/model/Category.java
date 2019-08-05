package com.rahnemacollege.model;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Data
@Table(name = "Categories")
public class Category {

    @Id @Getter
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Getter
    @Column(name = "name")
    private String categoryName;
}
