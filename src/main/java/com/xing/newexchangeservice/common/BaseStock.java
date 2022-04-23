package com.xing.newexchangeservice.common;

import lombok.Data;


@Data
public abstract class BaseStock {

    private String stockCode;//股票代码
    private Boolean isOpen=true;//是否可交易

    private Double feePercent = 0.001;

    private Long totalStock; //总股数
    private Long profit; //盈利
    private Long netAsset; //净资产

    private Double PE; //市盈率
    private Double PB; //市净率

    private Double histIncrease;//历史增长率


   private TransactionInf transactionInf = new TransactionInf();


}
