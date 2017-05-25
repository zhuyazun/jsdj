package com.sum.alchemist.utils;

/**
 * Created by TUS on 2016/6/1.
 */
public class EventParams {

    public static final int USER_INFO_CHANGE = 0x0000001;
    public static final int NEWS_LIST_CHANGE = 0x0000002;
    public static final int PROVISION_LIST_CHANGE = 0x0000004;
    public static final int REQUIREMENT_CHANGE = 0x0000008;
    public static final int USER_LOGIN_CHANGE = 0x0000010;
    public static final int PURCHASE_RESPONSE = 0x0000020;


    private int code;

    private Object data;

    public EventParams(){
        this(0, null);
    }

    public EventParams(int code){
        this(code, null);
    }

    public EventParams(int code, Object data){
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
