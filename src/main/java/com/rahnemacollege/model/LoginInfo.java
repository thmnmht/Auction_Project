package com.rahnemacollege.model;


import lombok.Data;


import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class LoginInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne
    private User user;
    private Date date;


}
