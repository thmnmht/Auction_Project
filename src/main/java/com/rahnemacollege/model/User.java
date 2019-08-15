package com.rahnemacollege.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.rest.core.annotation.RestResource;
import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@Table(name = "Users")
@Embeddable
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    //    @Email(message = "Invalid email address.")
    @Column(name = "email", unique = true)
    private String email;
    @JsonIgnore
    private String password;
    private String picture;
    @ManyToMany
    @RestResource(exported = false)
    private Set<Auction> bookmarks;

    @OneToOne(mappedBy = "user")
    private ResetRequest resetRequest;


    public User() {
    }


    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.picture = null;
        this.bookmarks = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                name.equals(user.name) &&
                email.equals(user.email) &&
                password.equals(user.password) &&
                Objects.equals(picture, user.picture) &&
                Objects.equals(bookmarks, user.bookmarks) &&
                Objects.equals(resetRequest, user.resetRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, picture, bookmarks, resetRequest);
    }
}
