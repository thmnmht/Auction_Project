package com.rahnemacollege.model;

import lombok.Data;
import org.springframework.data.rest.core.annotation.RestResource;
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

    @RestResource(exported = false)
    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    public Picture(String fileName){
        this.fileName = fileName;
        date = new Date();
    }


}
