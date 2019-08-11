package com.rahnemacollege.service;

import com.rahnemacollege.model.ResetRequest;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.ResetRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResetRequestService {
    private final ResetRequestRepository repository;

    @Autowired
    public ResetRequestService(ResetRequestRepository repository) {
        this.repository = repository;
    }

    public Optional<ResetRequest> findByUser(User user){
        return repository.findByUser(user);
    }

    public void addRequest(ResetRequest request){
        this.repository.save(request);
    }

    public Optional<ResetRequest> findByToken(String token) {
        return repository.findByToken(token);
    }

    public void removeRequest(ResetRequest request) {
        repository.delete(request);
    }
}
