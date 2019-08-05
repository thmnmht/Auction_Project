package com.rahnemacollege.util.exceptions;

public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 11L;
    public NotFoundException(Integer id, Class aClass) {
        super(aClass.getName() + " of id("+id+") was not found");
    }
    public NotFoundException(String name, Class aClass) {
        super(aClass.getName() + " of name("+name+") was not found");
    }
}
