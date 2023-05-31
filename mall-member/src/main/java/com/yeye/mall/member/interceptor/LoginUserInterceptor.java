package com.yeye.mall.member.interceptor;

import cn.hutool.core.text.AntPathMatcher;
import com.yeye.common.constant.AuthServerConstant;
import com.yeye.common.to.MemberRes;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

  public static ThreadLocal<MemberRes> loginUser = new ThreadLocal<>();


  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    String source = request.getHeader("source");
    if (StringUtils.isNotBlank(source) && StringUtils.equals(source, "dashboard")) {
      return true;
    }

    String uri = request.getRequestURI();
    AntPathMatcher pathMatcher = new AntPathMatcher();

    Boolean math = Boolean.FALSE;
    if(pathMatcher.match("/member/memberreceiveaddress/**", uri)){
      math=Boolean.TRUE;
    }
    if(pathMatcher.match("/member/member/**", uri)){
      math=Boolean.TRUE;
    }
    if (math) {
      return true;
    }


    HttpSession session = request.getSession();
    MemberRes user = (MemberRes) session.getAttribute(AuthServerConstant.LOGIN_USER);

    if (user == null) {
      request.getSession().setAttribute("msg","请先登录！");
      response.sendRedirect("http://auth.mall-yeye.com/login.html");
      return false;
    }
    loginUser.set(user);
    return true;
  }

}
