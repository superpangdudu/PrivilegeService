package com.plus3.privilege.common;

/**
 * Created by admin on 2017/12/20.
 */
public enum Action {
    All(31, "*"), // 0x1F
    Read(16, "r"), // 0x10
    Write(8, "w"), // 0x08
    Update(4, "u"), // 0x04
    Delete(2, "d"), // 0x02
    Run(1, "x"); // 0x01

    //===================================================================================
    private int code;
    private String description;
    Action(int code, String description) {
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
