package com.yeye.mall.product.vo;

import com.yeye.mall.product.entity.AttrEntity;
import com.yeye.mall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrsVo extends AttrGroupEntity {

  private List<AttrEntity> attrs;
}
