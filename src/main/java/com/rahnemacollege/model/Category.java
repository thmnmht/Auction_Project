package com.rahnemacollege.model;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public enum Category {

    DIGITAL_GOODS(1 , "کالای دیجیتال" ),
    HOME(3 , "منزل" ),
    SPORT(2 , "ورزشی" );

    @Id @Getter
    private final int id;
    @Getter
    private final String categoryName;

    Category(int id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }
}
