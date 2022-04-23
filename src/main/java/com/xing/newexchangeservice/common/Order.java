package com.xing.newexchangeservice.common;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Order {

   private Long id;
    /**
     * 用户信息
     */
    private User buyUser;
    private User sellUser;

 private Enterprise enterprise;
    /**
     * 成交信息
     */
    private Long number;
    private Double price;

    private Long buyTimeStamp;

    private Long sellTimeStamp;
}
