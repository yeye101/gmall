package com.yeye.mall.cart.interceptor;

import com.yeye.common.constant.AuthServerConstant;
import com.yeye.common.constant.CartConstant;
import com.yeye.common.to.MemberRes;
import com.yeye.mall.cart.util.CodeUtils;
import com.yeye.mall.cart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CartInterceptor implements HandlerInterceptor {

  public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

  /**
   * 执行前执行
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    UserInfoTo userInfoTo = new UserInfoTo();

    HttpSession session = request.getSession();
    MemberRes user = (MemberRes) session.getAttribute(AuthServerConstant.LOGIN_USER);

    if (user != null) {
      userInfoTo.setUserId(user.getId());

    }

    Cookie[] cookies = request.getCookies();

    if (cookies != null && cookies.length > 0) {
      for (Cookie cookie : cookies) {
        if (CartConstant.TEMP_USER_COOKIE_NAME.equals(cookie.getName())) {
          userInfoTo.setUserKey(cookie.getValue());
          userInfoTo.setTempUser(Boolean.TRUE);
        }
      }
    }

    if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
      String code = CodeUtils.getCode(16);
      userInfoTo.setUserKey(code);
    }

    threadLocal.set(userInfoTo);

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    UserInfoTo userInfoTo = threadLocal.get();
    if (!userInfoTo.getTempUser()) {
      Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
      cookie.setDomain("mall-yeye.com");
      cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
      response.addCookie(cookie);
    }
  }
}
