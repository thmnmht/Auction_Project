package com.rahnemacollege.resourceAssembler;

import com.rahnemacollege.model.User;
import com.rahnemacollege.resources.UserResource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {
    public UserResourceAssembler() {
        super(User.class, UserResource.class);
    }

    @Override
    public UserResource toResource(User user) {
        UserResource ur = super.createResourceWithId(user.getId(),user);
        ur.setName(user.getName());
        ur.setEmail(user.getEmail());
        return ur;
    }
}
