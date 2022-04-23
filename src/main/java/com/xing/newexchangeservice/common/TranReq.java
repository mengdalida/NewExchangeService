package com.xing.newexchangeservice.common;

import com.xing.newexchangeservice.util.EnumUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TranReq implements Comparable<TranReq> {

    private StatusEnum statusEnum;

    private User user;
    private Double price;
    private Long number;
    private Boolean isBuy;
    private Long timeStamp;
    private List<Order> orderList;
    private Enterprise enterprise;

    private Long originNumber;
    private Long cancelNumber;

    public TranReq(User user, Double price, Long number, Boolean isBuy, Enterprise enterprise) {

        statusEnum = StatusEnum.NOT_FINISH;
        this.user = user;
        this.price = price;
        this.number = number;
        this.originNumber=number;
        this.isBuy = isBuy;
        this.timeStamp = System.currentTimeMillis();
        orderList = new ArrayList<>();
        this.enterprise=enterprise;
    }

    public static boolean isNotNeed(TranReq tranReq) {
        return tranReq == null || tranReq.getNumber() == 0 || EnumUtils.equaksAny(tranReq.getStatusEnum(), StatusEnum.CANCELED);
    }

    @Override
    public int compareTo(TranReq tranReq) {
        int rst = tranReq.getPrice().compareTo(getPrice()) * (isBuy ? 1 : -1);

        if (rst != 0) {
            return rst;
        }
        return tranReq.getTimeStamp().compareTo(getTimeStamp());
    }
}
