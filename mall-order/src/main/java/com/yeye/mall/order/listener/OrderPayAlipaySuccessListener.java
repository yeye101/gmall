package com.yeye.mall.order.listener;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.yeye.mall.order.service.OrderService;
import com.yeye.mall.order.util.AlipayTemplate;
import com.yeye.mall.order.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@Slf4j
public class OrderPayAlipaySuccessListener {


  @Autowired
  private OrderService orderService;

  @Autowired
  private AlipayTemplate alipayTemplate;

  @PostMapping("/order/pay/alipay/success")
  public String handleOrderPayAlipaySuccess(PayAsyncVo asyncVo,
                                            HttpServletRequest request) {

    //获取支付宝POST过来反馈信息，将异步通知中收到的待验证所有参数都存放到map中
    Map<String, String> params = new HashMap<String, String>();
    Map requestParams = request.getParameterMap();
    for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
      String name = (String) iter.next();
      String[] values = (String[]) requestParams.get(name);
      String valueStr = "";
      for (int i = 0; i < values.length; i++) {
        valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
      }
      //乱码解决，这段代码在出现乱码时使用。
      //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
      params.put(name, valueStr);
    }
    //调用SDK验证签名
    //公钥验签示例代码
    boolean signVerified = false;
    try {
      signVerified = AlipaySignature.rsaCheckV1(params,
        alipayTemplate.getAlipay_public_key(),
        alipayTemplate.getCharset(),
        alipayTemplate.getSign_type());
    } catch (AlipayApiException e) {
      e.printStackTrace();
      log.error("支付宝回调失败，参数是{}", JSON.toJSONString(params));
      return "fail";
    }
    //公钥证书验签示例代码
    //   boolean flag = AlipaySignature.rsaCertCheckV1(params,alipayPublicCertPath,"UTF-8","RSA2");
    System.out.println("结果是" + signVerified + ",参数是" + JSON.toJSONString(params));

    if (signVerified) {
      //  验签成功后
      //按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success
      String res = orderService.handleOrderPayAlipaySuccessRes(asyncVo);
      return res;
    } else {
      // TODO 验签失败则记录异常日志，并在response中返回fail.

      return "fail";
    }

  }

}
