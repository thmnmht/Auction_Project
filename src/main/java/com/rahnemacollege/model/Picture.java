package com.rahnemacollege.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Picture {


    @Id
    private int id;
    private String fileName;
    private String date;

    public Picture(String fileName){
        this.fileName = fileName;

    }


}
