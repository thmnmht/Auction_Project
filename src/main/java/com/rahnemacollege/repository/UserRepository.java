package com.rahnemacollege.repository;

import com.rahnemacollege.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> getByEmail(String email);

    Optional<User> findByEmail(String email);

    void deleteByEmail(String email);


}
