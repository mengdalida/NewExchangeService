package com.xing.newexchangeservice.core.i;

import com.xing.newexchangeservice.common.TranReq;

public interface IAction {
    void process(TranReq tranReq);

    void afterProcess(TranReq tranReq);

    boolean cancel(TranReq tranReq);
}
