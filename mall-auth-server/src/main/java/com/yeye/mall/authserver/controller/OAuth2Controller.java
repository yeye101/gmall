package com.yeye.mall.authserver.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yeye.common.constant.AuthServerConstant;
import com.yeye.common.utils.R;
import com.yeye.mall.authserver.feign.MemberFeignService;
import com.yeye.mall.authserver.feign.ThirdPartyFeignService;
import com.yeye.mall.authserver.util.RedisUtil;
import com.yeye.common.to.MemberRes;
import com.yeye.mall.authserver.vo.UserSocialLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@Controller
public class OAuth2Controller {

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

  @GetMapping("gitee/success")
  public String gitee(@RequestParam("code") String code, RedirectAttributes attributes, HttpSession session) {

    Map<String, String> errors = new HashMap<>();


    HashMap<String, Object> formMap = new HashMap<>();
    formMap.put("grant_type", "authorization_code");
    formMap.put("code", code);
    formMap.put("client_id", "1b199bf9cd6dc357258e38b931b0dc4e31b26a6d4fa250b171f536db3e77b936");
    formMap.put("client_secret", "bc357877925fffe656ac54f7f5c9670655dc621a437372629be8fbc1cc91ee84");
    formMap.put("redirect_uri", "http://auth.mall-yeye.com/gitee/success");
    // 网络请求，需要防止意外
    HttpResponse response = HttpRequest.post("https://gitee.com/oauth/token")
      .method(Method.POST)
      .form(formMap)
      .execute();
    // 不为200则失败
    if (response.getStatus() != 200) {
      errors.put("msg", "三方登陆出现错误，请重试");
      attributes.addFlashAttribute("errors", errors);
      log.error(response.body());
      return REDIRECT_LOGIN_HTML;
    }

    UserSocialLoginVo vo = JSON.parseObject(response.body(), UserSocialLoginVo.class);

    try {
      R r = memberFeignService.memberSocialLogin(vo);

      if (r.getCode() != 0) {
        errors.put("msg", "三方登陆出现错误，请重试");
        attributes.addFlashAttribute("errors", errors);
        return REDIRECT_LOGIN_HTML;
      }

      MemberRes data = r.getData("data", new TypeReference<MemberRes>() {
      });

      log.info("欢迎 [" + data.getUsername() + "] 使用社交账号登录");

      session.setAttribute(AuthServerConstant.LOGIN_USER, data);

    } catch (Exception e) {

      errors.put("msg", "三方登陆出现错误，请重试");
      attributes.addFlashAttribute("errors", errors);
      log.error("三方登陆出现错误，请重试{}", e.getMessage());
      return REDIRECT_LOGIN_HTML;
    }


    return REDIRECT_MALL_COM;
  }


}

