package com.rahnemacollege.util.exceptions;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {


    Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = {InvalidInputException.class})
    protected void handler(InvalidInputException ex,
                                  HttpServletResponse response) {
        if(ex.getMessageStatus().equals(Message.TITLE_NULL))
            response.setStatus(430);
        else if(ex.getMessageStatus().equals(Message.BASE_PRICE_NULL))
            response.setStatus(432);
        else if(ex.getMessageStatus().equals(Message.CATEGORY_NULL))
            response.setStatus(433);
        else if(ex.getMessageStatus().equals(Message.MAX_NUMBER_TOO_LOW))
            response.setStatus(434);
        else if(ex.getMessageStatus().equals(Message.MAX_NUMBER_TOO_HIGH))
            response.setStatus(435);
        else if(ex.getMessageStatus().equals(Message.CATEGORY_INVALID))
            response.setStatus(436);
        else if(ex.getMessageStatus().equals(Message.DATE_INVALID))
            response.setStatus(437);
        else if(ex.getMessageStatus().equals(Message.DATE_NULL))
            response.setStatus(438);

        //sign up
        else if(ex.getMessageStatus().equals(Message.EMAIL_NULL))
            response.setStatus(439);
        else if(ex.getMessageStatus().equals(Message.NAME_NULL))
            response.setStatus(440);
        else if(ex.getMessageStatus().equals(Message.PASSWORD_TOO_LOW))
            response.setStatus(441);
        else if(ex.getMessageStatus().equals(Message.PASSWORD_TOO_HIGH))
            response.setStatus(442);
        else if(ex.getMessageStatus().equals(Message.EMAIL_INVALID))
            response.setStatus(443);
        else if(ex.getMessageStatus().equals(Message.EMAIL_DUPLICATED))
            response.setStatus(447);

        //login
        else if(ex.getMessageStatus().equals(Message.PASSWORD_INCORRECT))
            response.setStatus(445);
        else if(ex.getMessageStatus().equals(Message.EMAIL_INCORRECT))
            response.setStatus(446);

        logger.error(ex.getMessageStatus().toString());
    }
}