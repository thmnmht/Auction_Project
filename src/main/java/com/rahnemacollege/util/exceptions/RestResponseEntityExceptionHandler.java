package com.rahnemacollege.util.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {InvalidInputException.class})
    protected void handler(InvalidInputException ex,
                           HttpServletResponse response) {
        switch (ex.getMessageStatus()) {
            //add auction
            case TITLE_NULL:
                response.setStatus(430);
                break;
            case BASE_PRICE_NULL:
                response.setStatus(432);
                break;
            case CATEGORY_NULL:
                response.setStatus(433);
                break;
            case MAX_NUMBER_TOO_LOW:
                response.setStatus(434);
                break;
            case MAX_NUMBER_TOO_HIGH:
                response.setStatus(435);
                break;
            case CATEGORY_INVALID:
                response.setStatus(436);
                break;
            case DATE_INVALID:
                response.setStatus(437);
                break;
            case DATE_NULL:
                response.setStatus(438);
                break;

            //sign up
            case EMAIL_NULL:
                response.setStatus(439);
                break;
            case NAME_NULL:
                response.setStatus(440);
                break;
            case PASSWORD_TOO_LOW:
                response.setStatus(441);
                break;
            case PASSWORD_TOO_HIGH:
                response.setStatus(442);
                break;
            case EMAIL_INVALID:
                response.setStatus(443);
                break;
            case EMAIL_DUPLICATED:
                response.setStatus(444);
                break;

            //login
            case PASSWORD_INCORRECT:
                response.setStatus(445);
                break;
            case EMAIL_INCORRECT:
                response.setStatus(446);
                break;

            //password recovery
            case EMAIL_NOT_FOUND:
                response.setStatus(447);
                break;
            case INVALID_RESET_LINK:
                response.setStatus(448);
                break;
            case TOKEN_NOT_FOUND:
                response.setStatus(449);
                break;
            case NOT_RECORDED_REQUEST:
                response.setStatus(450);
                break;
        }
    }
}