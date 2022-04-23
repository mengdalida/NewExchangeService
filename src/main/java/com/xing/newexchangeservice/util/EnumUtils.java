package com.xing.newexchangeservice.util;

import java.util.Arrays;
import java.util.List;

public class EnumUtils {

     public static <T>  boolean hasAny(List<T> pList, T... list) {
        if (pList == null) {
            return false;
        }
        for(T p:pList){
            if(equaksAny(p,list)){
                return true;
            }
        }
        return false;
    }

    public static <T>  boolean equaksAny(T p, T... list) {
        if (p == null) {
            return false;
        }
        return Arrays.stream(list).filter(q -> p.equals(q)).findAny().isPresent();
    }
}
