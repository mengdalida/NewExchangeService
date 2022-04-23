package com.xing.newexchangeservice.common;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Enterprise extends BaseStock {

    private String name;

    private List<TagEnum> tagList;

}
