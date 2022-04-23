package com.xing.newexchangeservice.core;

import com.xing.newexchangeservice.common.Order;
import com.xing.newexchangeservice.common.TranReq;
import com.xing.newexchangeservice.common.User;
import com.xing.newexchangeservice.core.i.IAction;
import com.xing.newexchangeservice.exception.OptExcpetion;

import java.util.concurrent.atomic.AtomicLong;

public class AccountAction implements IAction {
    @Override
    public void process(TranReq tranReq) {
        User user = tranReq.getUser();

        if (tranReq.getIsBuy()) {
            //冻钱
            Double RMB = tranReq.getNumber() * tranReq.getPrice() * (1 + tranReq.getEnterprise().getFeePercent());
            if (user.getRMB().get() >= RMB) {
                user.getRMB().accumulateAndGet(RMB, (currentRMB, minRMB) -> currentRMB - minRMB);
                user.getFreezeRMB().accumulateAndGet(RMB, (freezeRMB, addRMB) -> freezeRMB + addRMB);
            } else {
                throw new OptExcpetion("No money " + user.getUserName());
            }
        } else {
            //冻账
            Long stockNum = tranReq.getNumber();
            if (user.getStockMap().get(tranReq.getEnterprise()).get() >= stockNum) {
                user.getStockMap().get(tranReq.getEnterprise()).accumulateAndGet(stockNum, (currentStock, tranStock) -> currentStock - tranStock);
                AtomicLong freezeNum = user.getFreezeStockMap().get(tranReq.getEnterprise());
                if (freezeNum == null) {
                    user.getFreezeStockMap().put(tranReq.getEnterprise(), new AtomicLong(0));
                }
                user.getFreezeStockMap().get(tranReq.getEnterprise()).accumulateAndGet(stockNum, (freezeStock, tranStock) -> freezeStock + tranStock);
            } else {
                throw new OptExcpetion("No stock " + user.getUserName());
            }
        }

    }

    @Override
    public void afterProcess(TranReq tranReq) {
        User user = tranReq.getUser();
        if (tranReq.getIsBuy()) {
            for (Order order : tranReq.getOrderList()) {
                changeBuyUser(user, tranReq.getPrice(), order);
                //上买下卖---------------------------------------
                changeSellUser(order.getSellUser(), order);
            }
        } else {
            for (Order order : tranReq.getOrderList()) {
                changeBuyUser(order.getBuyUser(), order.getPrice(), order);
                //上买下卖---------------------------------------
                changeSellUser(user, order);
            }
        }
    }

    private void changeBuyUser(User user, Double requestPrice, Order order) {
        //money -
        //冻结的钱
        double freezeMoney = order.getNumber() * requestPrice * (1 + order.getEnterprise().getFeePercent());
        //返还的钱
        double returnRMB = order.getNumber() * (requestPrice - order.getPrice()) * (1 + order.getEnterprise().getFeePercent());

        //多余的人民币退还
        user.getRMB().accumulateAndGet(returnRMB, (currentRMB, addRMB) -> currentRMB + addRMB);
        //冻结的人民币取消
        user.getFreezeRMB().accumulateAndGet(freezeMoney, (freezeRMB, minRMB) -> freezeRMB - minRMB);
        //stock +
        AtomicLong atomicLong = user.getStockMap().get(order.getEnterprise());
        if (atomicLong == null) {
            user.getStockMap().put(order.getEnterprise(), new AtomicLong(0));
        }
        user.getStockMap().get(order.getEnterprise()).accumulateAndGet(order.getNumber(), (currentStock, tranStock) -> currentStock + tranStock);
    }

    private void changeSellUser(User user, Order order) {
        //money +
        double money = order.getPrice() * order.getNumber() * (1 - order.getEnterprise().getFeePercent());
        user.getRMB().accumulateAndGet(money, (currentRMB, addRMB) -> currentRMB + addRMB);
        //stock -
        user.getFreezeStockMap().get(order.getEnterprise()).accumulateAndGet(order.getNumber(), (freezeStock, tranStock) -> freezeStock - tranStock);

    }

    @Override
    public boolean cancel(TranReq tranReq) {
        if (tranReq.getIsBuy()) {
            double freezeMoney = tranReq.getCancelNumber() * tranReq.getPrice() * (1 + tranReq.getEnterprise().getFeePercent());
            //多余的人民币退还
            tranReq.getUser().getRMB().accumulateAndGet(freezeMoney, (currentRMB, addRMB) -> currentRMB + addRMB);
            //冻结的人民币取消
            tranReq.getUser().getFreezeRMB().accumulateAndGet(freezeMoney, (freezeRMB, minRMB) -> freezeRMB - minRMB);
        }else {
            //多余的Stock退还
            tranReq.getUser().getStockMap().get(tranReq.getEnterprise()).accumulateAndGet(tranReq.getCancelNumber(),(currentStock, tranStock) -> currentStock + tranStock);
            //冻结的Stock取消
            tranReq.getUser().getFreezeStockMap().get(tranReq.getEnterprise()).accumulateAndGet(tranReq.getCancelNumber(),(currentStock, tranStock) -> currentStock - tranStock);
        }
        return true;

    }
}
