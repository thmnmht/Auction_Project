package com.rahnemacollege.repository;

import com.rahnemacollege.model.ResetRequest;
import com.rahnemacollege.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ResetRequestRepository extends CrudRepository<ResetRequest, Integer> {


    Optional<ResetRequest> findByUser(User user);

    List<ResetRequest> findAll();


    Optional<ResetRequest> findByToken(String token);
}
