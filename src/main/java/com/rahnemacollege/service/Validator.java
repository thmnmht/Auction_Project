package com.rahnemacollege.service;


import com.rahnemacollege.repository.UserRepository;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class Validator {

    @Autowired
    private UserRepository repository;


    public void validName(String name) {
        if (name == null || name.length() < 1)
            throw new InvalidInputException(Message.NAME_NULL);
    }

    public boolean isEmpty(String varriable) {
        if (varriable == null || varriable.length() < 1)
            return true;
        return false;
    }

    public void validPassword(String password) {
        if (password == null || password.length() < 6)
            throw new InvalidInputException(Message.PASSWORD_TOO_LOW);
        if (password.length() > 100)
            throw new InvalidInputException(Message.PASSWORD_TOO_HIGH);
    }

    public void validTitle(String title) {
        if (title == null || title.length() < 1)
            throw new InvalidInputException(Message.TITLE_NULL);
        if (title.length() > 50)
            throw new InvalidInputException(Message.TITLE_TOO_LONG);
    }

    public void validDescription(String description) {
        if (description.length() > 1000)
            throw new InvalidInputException(Message.DESCRIPTION_TOO_LONG);
    }

    public void validPrice(int price) {
        if (price < 0)
            throw new InvalidInputException(Message.BASE_PRICE_NULL);
    }

    public void validDate(long date) {
        if (date < 1)
            throw new InvalidInputException(Message.DATE_NULL);
        if (date - new Date().getTime() < 1800000L)
            throw new InvalidInputException(Message.DATE_INVALID);
    }

    public void validMaxNumber(int number) {
        if (number < 2)
            throw new InvalidInputException(Message.MAX_NUMBER_TOO_LOW);
        if (number > 15)
            throw new InvalidInputException(Message.MAX_NUMBER_TOO_HIGH);
    }

    public void validEmail(String email) {
        if (email == null || email.length() < 5)
            throw new InvalidInputException(Message.EMAIL_NULL);
        if (!email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"))
            throw new InvalidInputException(Message.EMAIL_INVALID);
        if (repository.findByEmail(email).isPresent())
            throw new InvalidInputException(Message.EMAIL_DUPLICATED);
    }

}
