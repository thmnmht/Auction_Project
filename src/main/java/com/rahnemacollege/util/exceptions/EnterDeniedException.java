package com.rahnemacollege.util.exceptions;

public class EnterDeniedException extends RuntimeException{

    String description;

    public EnterDeniedException(String description){
        this.description = description;
    }
}
