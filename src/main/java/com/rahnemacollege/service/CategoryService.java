package com.rahnemacollege.service;

import com.rahnemacollege.model.Category;
import com.rahnemacollege.repository.CategoryRepository;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category findById(Integer id){
        return repository.findById(id).orElseThrow(() -> new InvalidInputException(Message.CATEGORY_INVALID));
    }
}
