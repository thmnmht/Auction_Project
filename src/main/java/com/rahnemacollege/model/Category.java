package com.rahnemacollege.model;

import lombok.Data;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Category {

    @Id @Getter
    private final int id;
    @Getter
    private final String categoryName;

    Category(int id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }
}
