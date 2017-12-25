package com.plus3.privilege.common;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by admin on 2017/12/20.
 */
public class Result extends JSONObject {
    private static final String CODE = "code";
    private static final String DATA = "data";

    public Result() {
        put(CODE, Error.Ok.getCode());
    }

    public Result(Object data) {
        put(CODE, Error.Ok.getCode());
        put(DATA, data);
    }

    public Result(Error error) {
        put(CODE, error.getCode());
    }

    public Result(Error error, Object data) {
        put(CODE, error.getCode());
        put(DATA, data);
    }

    public Result addData(Object data) {
        put(DATA, data);
        return this;
    }

    public Result addData(String key, Object data) {
        put(key, data);
        return this;
    }

    //===================================================================================
    public static final Result Ok = new Result();
    public static final Result Failed = new Result(Error.Failed);
    public static final Result IncorrectPassword = new Result(Error.IncorrectPassword);

    //===================================================================================
    public static Result RESULT(Object object) {
        return new Result(Error.Ok, object);
    }

    public static Result RESULT(Error error, Object object) {
        return new Result(error, object);
    }
}
