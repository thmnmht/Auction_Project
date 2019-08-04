package com.rahnemacollege.resources;

import lombok.Data;
import org.springframework.hateoas.ResourceSupport;


@Data
public class UserResource extends ResourceSupport {
    String name;
    String email;
}
