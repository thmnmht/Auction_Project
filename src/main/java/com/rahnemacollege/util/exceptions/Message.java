package com.rahnemacollege.util.exceptions;

public enum Message {
    TITLE_NULL,
    TITLE_TOO_LONG,
    DESCRIPTION_TOO_LONG,
    BASE_PRICE_NULL,
    CATEGORY_NULL,
    CATEGORY_INVALID,
    DATE_NULL,
    MAX_NUMBER_TOO_LOW,
    MAX_NUMBER_TOO_HIGH,
    DATE_INVALID,
    MAX_SIZE_EXCEEDED,

    EMAIL_INVALID,
    PASSWORD_TOO_LOW,
    PASSWORD_TOO_HIGH,
    NAME_NULL,
    EMAIL_NULL,
    EMAIL_DUPLICATED,

    PASSWORD_INCORRECT,
    EMAIL_NOT_FOUND,
    INVALID_RESET_LINK,
    TOKEN_NOT_FOUND,
    NOT_RECORDED_REQUEST,

    INVALID_ID,
    AUCTION_NOT_FOUND,
    REALLY_BAD_SITUATION,

    FORBIDDEN_REQUEST,
    PICTURE_NULL,

    PRICE_TOO_LOW,

    FINISHED_AUCTION,
    SCHEDULER_ERROR,
    ALREADY_BID,
    THE_USER_IS_THE_OWNER_OF_THE_AUCTION,
    THE_AUCTION_DIDNT_START_YET,
    THE_USER_IS_NOT_IN_AUCTION,
    AUCTION_IS_FULL
}
