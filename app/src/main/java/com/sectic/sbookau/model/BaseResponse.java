package com.sectic.sbookau.model;

/**
 * Created by bioz on 6/22/2017.
 */

public class BaseResponse {
    public String id;
    public String error;
    public String info;

    public BaseResponse(){
    }

    public BaseResponse(String id, String error, String info){
        this.id = id;
        this.error = error;
        this.info = info;
    }
}
