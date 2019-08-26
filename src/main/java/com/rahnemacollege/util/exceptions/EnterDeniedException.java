package com.rahnemacollege.util.exceptions;

public class EnterDeniedException extends RuntimeException{

    private String description;

    public EnterDeniedException(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
