package com.xing.newexchangeservice;

import com.xing.newexchangeservice.common.Enterprise;
import com.xing.newexchangeservice.common.Order;
import com.xing.newexchangeservice.common.TranReq;
import com.xing.newexchangeservice.common.User;
import com.xing.newexchangeservice.core.TransactionAction;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class TransactionActionTest {
    @Test
    public void test1(){
        Enterprise enterprise=Enterprise.builder().build();
        
        TransactionAction transactionAction=new TransactionAction();
        Map<Double, List<TranReq>> map= enterprise.getTransactionInf().getAndRefactorTranReqMap();
        List<Order> list=enterprise.getTransactionInf().getOrderList();
        User user1=new User("1",1);
        User user2=new User("2",2);
        TranReq tranReq1=new TranReq(user1,10.0,500L,true, enterprise);
        transactionAction.process(tranReq1);
        TranReq tranReq2=new TranReq(user1,9.0,300L,true, enterprise);
        transactionAction.process(tranReq2);
        TranReq tranReq3=new TranReq(user2,8.0,1200L,false, enterprise);
        transactionAction.process(tranReq3);
        TranReq tranReq4=new TranReq(user2,9.0,200L,false, enterprise);
        transactionAction.process(tranReq4);
        TranReq tranReq5=new TranReq(user1,10.0,500L,true, enterprise);
        transactionAction.process(tranReq5);
        map= enterprise.getTransactionInf().getAndRefactorTranReqMap();
        System.out.println(map);
        System.out.println(list);
    }
}
