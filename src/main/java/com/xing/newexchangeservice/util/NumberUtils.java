package com.xing.newexchangeservice.util;

import java.math.BigDecimal;

public class NumberUtils {
    public static double left2Scale(double number){
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
