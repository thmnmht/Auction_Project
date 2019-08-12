package com.rahnemacollege.util.exceptions;

public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 5489516240608806491L;

    public NotFoundException(Integer id, Class aClass) {
        super(aClass.getName() + " of id(" + id + ") was not found");
    }

}
