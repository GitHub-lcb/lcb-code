package com.lcb.draw.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemBean {
  private Integer num;
  private String name;
  // 抽奖次数
  private Integer count;
  private Integer money;
  private Integer totalCount;

}
