package com.rahnemacollege.model;


import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "Login_infos")
public class LoginInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private User user;
    @NotNull(message = "Invalid date.")
    private Date date;


}
