package com.rahnemacollege.util.exceptions;




public class InvalidInputException extends RuntimeException{

    private static final long serialVersionUID = 5489516240608806490L;
    private Message message;

    public InvalidInputException() {
        super("Invalid Input");
    }
    public InvalidInputException(Message message) {
        this.message = message;
//        super(message);
    }

    public Message getMessageStatus() {
        return message;
    }
}