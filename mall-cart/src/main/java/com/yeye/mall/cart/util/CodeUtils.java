package com.yeye.mall.cart.util;


import cn.hutool.core.util.RandomUtil;

public class CodeUtils {

  private static final Integer DEFAULT_LENGTH = 6;

  public static String getSmsDigitCode() {
    return getSmsDigitCode(DEFAULT_LENGTH);
  }

  public static String getSmsDigitCode(Integer length) {
    return RandomUtil.randomNumbers(length);
  }

  public static String getCode() {
    return getCode(DEFAULT_LENGTH);
  }

  public static String getCode(Integer length) {
    return RandomUtil.randomString(length);
  }

}
