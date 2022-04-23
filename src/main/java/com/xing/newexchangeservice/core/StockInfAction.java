package com.xing.newexchangeservice.core;

import com.xing.newexchangeservice.common.TranReq;
import com.xing.newexchangeservice.core.i.IAction;
import com.xing.newexchangeservice.exception.OptExcpetion;
import com.xing.newexchangeservice.util.NumberUtils;

public class StockInfAction implements IAction {

    @Override
    public void process(TranReq tranReq) {
        if (tranReq.getEnterprise() == null) {
            throw new OptExcpetion("公司名称为空");
        }
        if (!tranReq.getEnterprise().getIsOpen()) {
            throw new OptExcpetion("公司未上市");
        }
        if (tranReq.getPrice() > tranReq.getEnterprise().getTransactionInf().getStartPrice() * 1.2 ||
                tranReq.getPrice() < tranReq.getEnterprise().getTransactionInf().getStartPrice() * 0.8) {
            throw new OptExcpetion("价格高于开盘价20%或低于开盘价20%");
        }
        if (tranReq.getNumber() < 100) {
            throw new OptExcpetion("买入或卖出数量不能小于100股");
        }
        //股数修正 100整数倍
        tranReq.setNumber(tranReq.getNumber() - tranReq.getNumber() % 100);
        //价格修正  保留2位小数
        tranReq.setPrice(NumberUtils.left2Scale(tranReq.getPrice()));
    }

    @Override
    public void afterProcess(TranReq tranReq) {
        //调整股价
        tranReq.getEnterprise().getTransactionInf().changePrice(tranReq.getPrice());
    }

    @Override
    public boolean cancel(TranReq tranReq) {
        return true;
    }
}
