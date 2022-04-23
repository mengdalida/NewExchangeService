package com.xing.newexchangeservice;

import com.xing.newexchangeservice.common.Enterprise;
import com.xing.newexchangeservice.common.TranReq;
import com.xing.newexchangeservice.common.TransactionInf;
import com.xing.newexchangeservice.common.User;
import com.xing.newexchangeservice.service.CoreService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class CoreServiceTest {

    @Test
    public void testProcess(){
        Enterprise enterprise=Enterprise.builder().build();
        enterprise.setTransactionInf(new TransactionInf(10.0));
        CoreService coreService=new CoreService();

        User user1=new User("1",1);
        user1.setRMB(new AtomicReference<>(100000.0));
        User user2=new User("2",2);
        ConcurrentHashMap<Enterprise, AtomicLong> map=new ConcurrentHashMap<>();
        map.put(enterprise,new AtomicLong(1400));
        user2.setStockMap(map);
        TranReq tranReq1=new TranReq(user1,10.0,500L,true, enterprise);
        coreService.process(tranReq1);
        TranReq tranReq2=new TranReq(user1,9.0,300L,true, enterprise);
        coreService.process(tranReq2);
        TranReq tranReq3=new TranReq(user2,8.0,1200L,false, enterprise);
        coreService.process(tranReq3);

        System.out.println(coreService.cancel(tranReq1));
//        System.out.println(coreService.cancel(tranReq3));

        TranReq tranReq4=new TranReq(user2,9.0,200L,false, enterprise);
        coreService.process(tranReq4);

        TranReq tranReq5=new TranReq(user1,10.0,500L,true, enterprise);
        coreService.process(tranReq5);

        System.out.println(coreService.cancel(tranReq5));

    }

}
