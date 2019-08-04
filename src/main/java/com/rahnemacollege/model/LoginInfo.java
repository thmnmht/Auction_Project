package com.rahnemacollege.model;


import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "Login_infos")
public class LoginInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
//    @Column(name = "user_uid")
    private User user;
    private Date date;


}
