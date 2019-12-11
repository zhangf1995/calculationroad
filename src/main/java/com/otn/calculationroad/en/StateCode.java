package com.otn.calculationroad.en;

public enum StateCode {
    SUCCESS(200,"success"),
    FAIL(500,"Internal Server Error"),
    INVAILD(400,"Invaild Paramter");

    private Integer code;
    private String msg;

    StateCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode(){
        return code;
    }

    public String getMsg(){
        return msg;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }
}
