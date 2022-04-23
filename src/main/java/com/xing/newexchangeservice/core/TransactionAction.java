package com.xing.newexchangeservice.core;

import com.xing.newexchangeservice.common.Order;
import com.xing.newexchangeservice.common.StatusEnum;
import com.xing.newexchangeservice.common.TranReq;
import com.xing.newexchangeservice.common.TransactionInf;
import com.xing.newexchangeservice.core.i.IAction;
import com.xing.newexchangeservice.exception.CodeExcpetion;
import com.xing.newexchangeservice.util.EnumUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TransactionAction implements IAction {

    /**
     * 处理交易
     *
     * @param tranReq
     */
    @Override
    public void process(TranReq tranReq) {
        processBeforeReqFinish(tranReq);
        if (tranReq.getIsBuy()) {
            processBuy(tranReq);
        } else {
            processSell(tranReq);
        }
        processAfterReqFinish(tranReq);
    }

    @Override
    public void afterProcess(TranReq tranReq) {
        //do nothing
    }

    @Override
    public synchronized boolean cancel(TranReq tranReq) {

        Long number = tranReq.getNumber();
        if(number==0){
            return false;
        }
        tranReq.setNumber(0L);
        tranReq.setCancelNumber(number);
        tranReq.setStatusEnum(StatusEnum.CANCELED);
        return true;
    }

    /**
     * 在交易执行前生成
     */
    protected void processBeforeReqFinish(TranReq tranReq) {
        //当前用户增加交易请求
        tranReq.getUser().getTranReqList().add(tranReq);
    }

    /**
     * 在交易执行后生成
     */
    protected void processAfterReqFinish(TranReq tranReq) {

        Map<Double, List<TranReq>> priceTranReqMap=tranReq.getEnterprise().getTransactionInf().getPriceTranReqMap();

        //更新 priceTranReqMap
        if (!tranReq.getStatusEnum().equals(StatusEnum.FINISH)) {
            List<TranReq> tranReqList = priceTranReqMap.get(tranReq.getPrice());
            if (tranReqList == null) {
                tranReqList = new ArrayList<>();
                priceTranReqMap.put(tranReq.getPrice(), tranReqList);
            }
            tranReqList.add(tranReq);
        }
    }

    /**
     * 生成交易时调用
     */
    protected void processAfterGenerateOrder(Order order) {

        List<Order> orderList=order.getEnterprise().getTransactionInf().getOrderList();

        //更新 orderList
        order.getBuyUser().getOrderList().add(order);
        order.getSellUser().getOrderList().add(order);
        orderList.add(order);
    }



    private synchronized void processBuy(TranReq buy) {

        TransactionInf transactionInf = buy.getEnterprise().getTransactionInf();
        PriorityQueue<TranReq> buyQueue = transactionInf.getBuyQueue();
        PriorityQueue<TranReq> sellQueue =  transactionInf.getSellQueue();

         TranReq headerSellReq = transactionInf.getHeaderSellReq();  //最前卖

        ResultEnum resultEnum = null;
        if (headerSellReq != null) {
            resultEnum = compare(buy, headerSellReq, headerSellReq.getPrice());
        }
        if (ResultEnum.doBuyFinish(resultEnum)) {
            buy.setStatusEnum(StatusEnum.FINISH);
            return;
        } else {
            headerSellReq = sellQueue.poll();
            if (headerSellReq != null) {
                processBuy(buy);
            } else {
                buyQueue.add(buy);
                buy.setStatusEnum(StatusEnum.NOT_FINISH);
            }
        }
    }

    private synchronized void processSell(TranReq sell) {

        PriorityQueue<TranReq> buyQueue = sell.getEnterprise().getTransactionInf().getBuyQueue();
        PriorityQueue<TranReq> sellQueue =  sell.getEnterprise().getTransactionInf().getSellQueue();

        TranReq headerBuyReq = sell.getEnterprise().getTransactionInf().getHeaderBuyReq(); //最前买

        ResultEnum resultEnum = null;
        if (headerBuyReq != null) {
            resultEnum = compare(headerBuyReq, sell, headerBuyReq.getPrice());
        }
        if (ResultEnum.doSellFinish(resultEnum)) {
            sell.setStatusEnum(StatusEnum.FINISH);
            return;
        } else {
            headerBuyReq = buyQueue.poll();
            if (headerBuyReq != null) {
                processSell(sell);
            } else {
                sellQueue.add(sell);
                sell.setStatusEnum(StatusEnum.NOT_FINISH);
            }
        }
    }

    private ResultEnum compare(TranReq buyReq, TranReq sellReq, Double price) {
        if (TranReq.isNotNeed(buyReq)) {
            return ResultEnum.BUY_FINISH;
        }
        if (TranReq.isNotNeed(sellReq)) {
            return ResultEnum.SELL_FINISH;
        }
        if (buyReq.getPrice() < sellReq.getPrice()) {
            return ResultEnum.NOT_MATCH;
        }
        Long number = Math.min(buyReq.getNumber(), sellReq.getNumber());

        Order order = Order.builder()
                .buyUser(buyReq.getUser())
                .sellUser(sellReq.getUser())
                .number(number)
                .price(price)
                .buyTimeStamp(buyReq.getTimeStamp())
                .sellTimeStamp(sellReq.getTimeStamp())
                .enterprise(buyReq.getEnterprise())
                .build();

        buyReq.setNumber(buyReq.getNumber() - number);
        sellReq.setNumber(sellReq.getNumber() - number);

        buyReq.getOrderList().add(order);
        sellReq.getOrderList().add(order);

        processAfterGenerateOrder(order);

        if (buyReq.getNumber() == sellReq.getNumber()) {
            buyReq.setStatusEnum(StatusEnum.FINISH);
            sellReq.setStatusEnum(StatusEnum.FINISH);
            return ResultEnum.ALL_FINISH;
        }
        if (buyReq.getNumber() == 0) {
            buyReq.setStatusEnum(StatusEnum.FINISH);
            return ResultEnum.BUY_FINISH;
        }
        if (sellReq.getNumber() == 0) {
            sellReq.setStatusEnum(StatusEnum.FINISH);
            return ResultEnum.SELL_FINISH;
        }
        throw new CodeExcpetion();
    }

    public enum ResultEnum {
        BUY_FINISH,
        SELL_FINISH,
        NOT_MATCH,
        ALL_FINISH,
        ;

        static boolean doBuyFinish(ResultEnum resultEnum) {
            return EnumUtils.equaksAny(resultEnum, BUY_FINISH, ALL_FINISH);
        }

        static boolean doSellFinish(ResultEnum resultEnum) {
            return EnumUtils.equaksAny(resultEnum, SELL_FINISH, ALL_FINISH);
        }
    }

}
