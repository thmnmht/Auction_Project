package com.rahnemacollege.service;

import com.rahnemacollege.model.Category;
import com.rahnemacollege.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }
    public Optional<Category> findByCategoryName(String categoryName){
        return repository.findByCategoryName(categoryName);
    }

    public Optional<Category> findById(Integer id){
        return repository.findById(id);
    }
}
