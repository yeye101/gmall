package com.yeye.common.constant;

public class ProductConstant {
  public enum AttrEnum {
    ATTR_TYPE_BASE(1, "基本属性"),
    ATTR_TYPE_SALE(0, "销售属性");

    private int code;
    private String msg;

    AttrEnum(int code, String msg) {
      this.code = code;
      this.msg = msg;
    }

    public int getCode() {
      return code;
    }

    public String getMsg() {
      return msg;
    }
  }


  public enum PublishStatusEnum {
    SPU_CREAT(0, "新建商品"),
    SPU_UP(1, "上架商品"),
    SPU_DOWN(2, "下架商品");

    private int code;
    private String msg;

    PublishStatusEnum(int code, String msg) {
      this.code = code;
      this.msg = msg;
    }

    public int getCode() {
      return code;
    }

    public String getMsg() {
      return msg;
    }
  }
}