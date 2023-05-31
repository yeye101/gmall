package com.yeye.mall.product.exception;

import com.yeye.common.exception.BizCodeEnum;
import com.yeye.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Program: mall
 * Author: zdc
 * Date: 2022/11/13
 * Time: 15:10
 * Description:
 */
@RestControllerAdvice(basePackages = "com.yeye.mall.product.controller")
public class MallExceptionControllerAdvice {

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public R handleVaildException(MethodArgumentNotValidException e) {

    BindingResult bindingResult = e.getBindingResult();
    Map<String, String> errorMap = new HashMap<>();
    bindingResult.getFieldErrors().forEach((fieldError) -> {
      errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
    });

    return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data", errorMap);
  }

  @ExceptionHandler(value = Exception.class)
  public R handleException(Exception e) {
    return R.error(BizCodeEnum.UNKNOW_EXEPTION.getCode(), BizCodeEnum.UNKNOW_EXEPTION.getMsg());
  }

}
