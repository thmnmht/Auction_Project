package com.rahnemacollege.model;

import lombok.Data;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "Categories")
public class Category {

    @Id @Getter
    private int id;

    @Getter
    @Column(name = "name")
    private String categoryName;
}
