package com.xing.newexchangeservice.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
public class User {
    private String userName;
    private Integer userId;
    private AtomicReference<Double> RMB = new AtomicReference<>(0.0);
    private AtomicReference<Double> freezeRMB = new AtomicReference<>(0.0);
    private ConcurrentHashMap<Enterprise, AtomicLong> stockMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Enterprise, AtomicLong> freezeStockMap = new ConcurrentHashMap<>();


    List<Order> orderList = new ArrayList<>();
    List<TranReq> tranReqList = new ArrayList<>();

    public User(String userName, Integer userId) {
        this.userName = userName;
        this.userId = userId;
    }

}
