package com.models2you.model.util;

/**
 * Created by yogeshsoni on 26/09/16.
 */

public interface ErrorView {

    // Custom error code and message for token empty case
    int ERROR_CODE_NOT_VALID_TOKEN = 406;
    String ERROR_MESSAGE_NOT_VALID_TOKEN = "Your session is expired. Please Login";
    String ERROR_MESSAGE_NOT_VALID_TOKEN_LOGOUT = "Not a valid token";

    int ERROR_CODE_IN_EVENT = 405;
    //custom error code and message
    int CUSTOM_ERROR_CODE = 34;
    String CUSTOM_ERROR_MESSAGE = "Please try again later";

    // Already logout condition Custom error
    int CUSTOM_ERROR_CODE_ALREADY_LOGOUT = 35;
    String CUSTOM_ERROR_MESSAGE_ALREADY_LOGOUT = "you are already logout";

    int TOKEN_EXPIRED_ERROR_CODE = 405;
}
