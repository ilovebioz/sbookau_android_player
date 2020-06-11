package com.sectic.sbookau.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bioz on 9/20/2014.
 */
public class Filter {
    @SerializedName("key")
    public String key;
    @SerializedName("operator")
    public String operator;
    @SerializedName("value")
    public String value;

    public Filter(String iSKey, String iSOperator, String iSValue)
    {
        this.key = iSKey;
        this.operator = iSOperator;
        this.value = iSValue;
    }
}
