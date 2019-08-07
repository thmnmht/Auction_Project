package com.rahnemacollege.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "User_Pictures")
public class UserPicture {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    private Date date;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    public UserPicture(){
        date = new Date();
    }


}
