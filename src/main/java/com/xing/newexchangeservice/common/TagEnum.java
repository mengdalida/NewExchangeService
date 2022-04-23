package com.xing.newexchangeservice.common;

import com.xing.newexchangeservice.util.EnumUtils;

import java.util.function.Function;

public enum TagEnum {

    BANK("银行", TagTypeEnum.FIX, null, null),
    TECH("科技", TagTypeEnum.FIX, null, null),
    TO_C("消费", TagTypeEnum.FIX, null, null),
    TO_B("工厂", TagTypeEnum.FIX, null, null),
    RESOURCE("资源", TagTypeEnum.FIX, null, null),
    NEW_TECH("新科技", TagTypeEnum.FIX, null, null),

    YYZ("元宇宙", TagTypeEnum.FLEXIBLE, 0.05, null),
    GYMJ("工业母鸡", TagTypeEnum.FLEXIBLE, 0.1, enterprise -> EnumUtils.hasAny(enterprise.getTagList(), TECH, TO_B)),

    BONUS("送转", TagTypeEnum.TF, 0.3, enterprise -> enterprise.getPE() < 30),
    MORE_EXPECT("增长", TagTypeEnum.CONDITION, 0.3, enterprise -> enterprise.getHistIncrease() > 0.15),

    ;

    private String name;
    private TagTypeEnum tagType;
    private Double probablity;
    private Function<Enterprise, Boolean> condition;

    TagEnum(String name, TagTypeEnum tagType, Double probablity, Function<Enterprise, Boolean> condition) {
        this.name = name;
        this.tagType = tagType;
        this.probablity = probablity;
        this.condition = condition;
    }

}
