package com.xing.newexchangeservice.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ListUtils {
    public static <T> void removeIfMatch(List<T> list, Predicate<T> predicate){
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (predicate.test(iterator.next())) {
                iterator.remove();
            }
        }
    }
}
