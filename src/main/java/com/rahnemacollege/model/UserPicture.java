package com.rahnemacollege.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "Pictures")
public class Picture {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    
    private Date date;

    public Picture(){
        date = new Date();
    }


}
