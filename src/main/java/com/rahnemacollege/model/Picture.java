package com.rahnemacollege.model;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "Pictures")
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
