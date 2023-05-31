package com.yeye.common.exception;

/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10: 通用
 *      001：参数格式校验
 *  11: 商品
 *  12: 订单
 *  13: 购物车
 *  14: 物流
 *  15: login/reg
 */
public enum BizCodeEnum {

  UNKNOW_EXEPTION(10000, "系统未知异常"),

  VALID_EXCEPTION(10001, "参数格式校验失败"),

  SMS_CODE_EXCEPTION(10002, "验证码获取频率过于频繁"),


  REG_EXIST_EXCEPTION(15001, "重复信息"),

  LOGIN_ERROR(15002, "账号密码错误"),

  LOGIN_SOCIAL_ERROR(15003, "三方登陆出现错误，请重试"),

  PRODUCT_UP_EXCEPTION(20001, "商品上架错误"),

  WARE_NO_SOCK_EXCEPTION(20002, "库存不足"),

  PRODUCT_DOWN_EXCEPTION(20003, "商品下架错误"),

  PRODUCT_SKU_LIST_EXCEPTION(20004, "商品SKU列表获取错误"),


  ORDER_SUBMIT_PRICE_EXCEPTION(30001, "商品验价错误"),

  ORDER_SUBMIT_COMMIT_EXCEPTION(30002, "商品重复提交错误"),

  ORDER_SUBMIT_ADDRESS_EXCEPTION(30003, "用户地址获取错误"),



  ;





  private int code;
  private String msg;

  BizCodeEnum(int code, String msg) {
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