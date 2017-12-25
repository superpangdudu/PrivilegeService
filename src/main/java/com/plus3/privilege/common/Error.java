package com.plus3.privilege.common;

/**
 * Created by admin on 2017/12/19.
 */
public enum Error {
    Ok(0, "Ok"),
    Failed(-1, "Failed"),
    NotPermitted(1, "Not Permitted"),
    IncorrectPassword(2, "Incorrect Password");

    private int code;
    private String description;

    Error(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
