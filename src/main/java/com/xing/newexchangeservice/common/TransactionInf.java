package com.xing.newexchangeservice.common;

import com.xing.newexchangeservice.util.ListUtils;
import lombok.Data;

import java.util.*;

@Data
public class TransactionInf {

    //--------------------------交易信息----------------------------
    private PriorityQueue<TranReq> buyQueue = new PriorityQueue<>();
    private PriorityQueue<TranReq> sellQueue = new PriorityQueue<>();

    private TranReq headerBuyReq = null; //最前买
    private TranReq headerSellReq = null;  //最前卖

    //key-value 价格-未成交单
    private Map<Double, List<TranReq>> priceTranReqMap = new HashMap<>();
    // 成交单
    private List<Order> orderList = new ArrayList<>();

    //开盘
    private volatile Double price;//股价
    private Double startPrice;
    private Double endPrice;
    private Double highestPrice;
    private Double lowestPrice;

    public TransactionInf(Double price) {
        this.price = price;
        this.startPrice=price;
        this.highestPrice=price;
        this.lowestPrice=price;
    }

    public void clear() {
        headerBuyReq = null;
        headerSellReq = null;
        buyQueue = new PriorityQueue<>();
        sellQueue = new PriorityQueue<>();

        priceTranReqMap = new HashMap<>();
        orderList = new ArrayList<>();
    }

    /**
     * 获取 成交单
     *
     * @return
     */
    public List<Order> getOrderList() {
        return orderList;
    }

    /**
     * 获取价格表
     *
     * @return
     */
    public Map<Double, List<TranReq>> getAndRefactorTranReqMap() {

        for (List<TranReq> tranReqList : priceTranReqMap.values()) {
            ListUtils.removeIfMatch(tranReqList, tranReq -> TranReq.isNotNeed(tranReq));
        }
        return priceTranReqMap;
    }

    public void changePrice(Double price) {
        if (price != null) {
            this.price = price;
            this.highestPrice = Math.max(highestPrice, price);
            this.lowestPrice = Math.min(highestPrice, price);
        }
    }
}
