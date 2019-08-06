package com.rahnemacollege.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "pictures")
public class Picture {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(name = "filename")
    private String fileName;
    @NotNull(message = "Invalid date.")
    private Date date;

    public Picture(String fileName){
        this.fileName = fileName;
        date = new Date();
    }


}
