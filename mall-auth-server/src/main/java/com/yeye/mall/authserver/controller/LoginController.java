package com.yeye.mall.authserver.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.TypeReference;
import com.yeye.common.constant.AuthServerConstant;
import com.yeye.common.exception.BizCodeEnum;
import com.yeye.common.to.MemberRes;
import com.yeye.common.utils.R;
import com.yeye.mall.authserver.feign.MemberFeignService;
import com.yeye.mall.authserver.feign.ThirdPartyFeignService;
import com.yeye.mall.authserver.util.CodeUtils;
import com.yeye.mall.authserver.util.RedisUtil;
import com.yeye.mall.authserver.vo.UserLoginVo;
import com.yeye.mall.authserver.vo.UserRegisterVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Controller
public class LoginController {

  private static final Log log = LogFactory.get();


  public static final String REDIRECT_REG_HTML = "redirect:http://auth.mall-yeye.com/reg.html";

  public static final String REDIRECT_LOGIN_HTML = "redirect:http://auth.mall-yeye.com/login.html";

  public static final String REDIRECT_MALL_COM = "redirect:http://mall-yeye.com/";


  @Autowired
  ThirdPartyFeignService thirdPartyFeignService;

  @Autowired
  MemberFeignService memberFeignService;

  @Autowired
  RedisUtil redisUtil;


  @ResponseBody
  @GetMapping("/sms/sendCode")
  public R sendCode(@RequestParam("phone") String phone) {


    String redisCode = redisUtil.get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);

    if (StringUtils.isNotEmpty(redisCode)) {
      long l = Long.parseLong(StringUtils.split(redisCode, "_")[1]);
      if (DateUtil.currentSeconds() - l < 60) { // 60s
        return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
      }
    }


    // 发送验证码
    String code = CodeUtils.getSmsDigitCode();
    System.out.println(code);
    try {
      thirdPartyFeignService.sendCode(phone, code);
    } catch (Exception e) {
      e.printStackTrace();
    }
    /*节约成本*/
/*    String code = CodeUtils.getSmsDigitCode();*/
    System.out.println(code);
    code = code + "_" + DateUtil.currentSeconds();

    redisUtil.set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code, 10L, TimeUnit.MINUTES);

    return R.ok();
  }

  //todo 重定向携带数据，携带serssion
  @PostMapping("/register")
  public String register(@Valid UserRegisterVo vo, BindingResult result, RedirectAttributes attributes) {

    Map<String, String> errors = new HashMap<>();

    if (result.hasErrors()) {
      //1.1 如果校验不通过，则封装校验结果
      result.getFieldErrors().forEach(item -> {
        // 获取错误的属性名和错误信息
        errors.put(item.getField(), item.getDefaultMessage());
        //1.2 将错误信息封装到session中

      });
      attributes.addFlashAttribute("errors", errors);
      //1.2 重定向到注册页
      return REDIRECT_REG_HTML;
    }

    String smsCode = redisUtil.get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());

    if (StringUtils.isEmpty(smsCode)) {
      errors.put("code", "验证码错误！");
      attributes.addFlashAttribute("errors", errors);
      return REDIRECT_REG_HTML;
    }

    // redis的验证码
    String redisCode = smsCode.split("_")[0];

    // 如果和redis的验证码不相等，则返回错误
    if (!StringUtils.equals(redisCode, vo.getCode())) {

      errors.put("code", "验证码错误！");
      attributes.addFlashAttribute("errors", errors);
      return REDIRECT_REG_HTML;
    }

    // 删除验证码，保持一致
    redisUtil.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
    // 通过则调用远程逻辑
    try {
      R r = memberFeignService.memberRegister(vo);

      if (r.getCode() != 0) {

        errors.put("msg", r.getMsg());
        attributes.addFlashAttribute("errors", errors);
        //1.2 重定向到注册页
        return REDIRECT_REG_HTML;
      }

    } catch (Exception e) {
      e.printStackTrace();
      return REDIRECT_REG_HTML;
    }

    return REDIRECT_LOGIN_HTML;
  }


  @PostMapping("/userLogin")
  public String userLogin(UserLoginVo vo, BindingResult result, RedirectAttributes attributes,HttpSession session) {

    Map<String, String> errors = new HashMap<>();
    try {
      R r = memberFeignService.memberLogin(vo);

      if (r.getCode() != 0) {
        errors.put("msg", r.getMsg());
        attributes.addFlashAttribute("errors", errors);
        return REDIRECT_LOGIN_HTML;
      }
      MemberRes data = r.getData("data", new TypeReference<MemberRes>() {
      });
      log.info("欢迎 [" + data.getUsername() + "] 使用账号登录");

      session.setAttribute(AuthServerConstant.LOGIN_USER, data);

    } catch (Exception e) {
      log.error("远程调用异常失败{}", e.getMessage());
      return REDIRECT_LOGIN_HTML;
    }
    return REDIRECT_MALL_COM;
  }


}

