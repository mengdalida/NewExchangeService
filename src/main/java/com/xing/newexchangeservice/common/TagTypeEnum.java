package com.xing.newexchangeservice.common;

public enum TagTypeEnum {
    FIX(0),
    CONDITION(1),
    FLEXIBLE(2),
    TF(3),
    ;

    private int code;

    TagTypeEnum(int code){
        this.code=code;
    }
}
