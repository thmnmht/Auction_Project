package com.rahnemacollege.model;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class Picture {


    @Id
    private int id;
    private String fileName;
    private Date date;

    public Picture(String fileName){
        this.fileName = fileName;
        date = new Date();
    }


}
