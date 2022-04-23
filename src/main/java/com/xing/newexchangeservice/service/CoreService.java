package com.xing.newexchangeservice.service;

import com.xing.newexchangeservice.common.TranReq;
import com.xing.newexchangeservice.core.AccountAction;
import com.xing.newexchangeservice.core.StockInfAction;
import com.xing.newexchangeservice.core.TransactionAction;
import com.xing.newexchangeservice.core.i.IAction;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CoreService {

    List<? extends IAction> iActionList = Arrays.asList(new StockInfAction(), new AccountAction(), new TransactionAction());

    public void process(TranReq tranReq) {
        for (IAction iAction : iActionList) {
            iAction.process(tranReq);
        }

        //倒序执行afterProcess
        for (int i = iActionList.size() - 1; i >= 0; i--) {
            iActionList.get(i).afterProcess(tranReq);
        }
    }

    public boolean cancel(TranReq tranReq) {
        //倒序执行cancel
        for (int i = iActionList.size() - 1; i >= 0; i--) {
            if (!iActionList.get(i).cancel(tranReq)) {
                return false;
            }
        }
        return true;
    }
}
