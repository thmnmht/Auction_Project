package com.rahnemacollege.model;

import javax.persistence.*;
import java.util.Date;


@lombok.Data
@Entity
@Table(name = "ResetRequests")
public class ResetRequest {

    @Id
    private int id;
    @OneToOne
    @MapsId
    private User user;
    private Date date;
    private String token;

    public ResetRequest() {
    }

    public ResetRequest(User user, Date date, String token) {
        this.user = user;
        this.date = date;
        this.token = token;
    }
}
