package com.yeye.mall.order.util;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.yeye.mall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

  //在支付宝创建的应用的id
  private String app_id = "2021000122619433";

  // 商户私钥，您的PKCS8格式RSA2私钥
  private String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCzuACSqQUU/7FT6ujxcnq/4NMALHspkdpivmbcbMpERpQALn1+lxV8I5FxHx6AtMVWTJ5uHgDUSz9B/AgyU36AB1+W+W0npUcuoJYMWWqLHkcKP1cPYnazXXCdCoFX9IGNfxRoHiDjYcvHJopw0GzfQQSJ/k2nsT84Hy1mNdUx25M6KLkXBdp9L+nF3jDSUhHBFqPv9a62mSuQzNfLTrFUwO1WRa5ajNl7c1mM6ylwZ7tESNv55Y5E3g3DzYsqPJ1fps2OFB5yRHvaPhLzrOBzaw9iGtnSYmouZqxqRTv1JUcz0MnKBMJg+q3SUjHj/TbvLkuTm89Kcgf4GnT6+Y2TAgMBAAECggEALEEhEvw/3h3ggrOTzPDNvTYVfzN02JWr2yjbl2fKnulqRGkuSd/WUI1JoTtyfWsGNOwFD0SgG6az2wG+2vWobspiBYFCGcMfNiIhEMVr8PnxsFpMi+NJK33U3zbZBtmUlU03rzPMEXHhhha9fG42gPs3rOtMIAELejxjJ4oWB4KJvWrOmkoAlvKaZZRWDBrFHcK0wxDkjk+oAig8Tq1dMZy+jSCijab195rc8E7foYGv0K/+JdYqxRdnPQxjwwJv9PuVQMVV91DyZFBIOM1ZOJ5U2ERz54ndaxeNaChlFDWGKHLY2GpWyopT7S/Med9RWpT7Clb/uIRN6WDWA2etsQKBgQD0xDluCj/EGW3AIPff/Li0PKBA6N7FKgdbmhjCFOHUOk+o6kdk2JMM2BIZchne0fwUd5irBRlvVVnL4n/LQ5JCGnfxvAgrUqypQ37u4YScu/SKwDC/mH5MqQTbeUK2j3M0qhvVltE/rqYjCBKPe6efRPOI8JKT0K7peuHz7cF0VQKBgQC794c6+p3gUBnVP8awxJSuoiEhOxgv9mfFFaH+UNe4WUL51dBJ+qOKm07naTdjS8CgIY8uvPQ2nYBRazleUf4ZSeLGbmqkJWqNwR9TpK0qdoa1AXQkH/lrlZxPybxav1g/z9Z/92DUD45nU+HC7qrCHc2oymhpEvfnq8h87GMiRwKBgQC0/r0w/+czTVJ49mgVe73hmcsu9IR266NRO1v/eI2d8Bij4co1amki4ExSAKvctw5e3ClKnZS3XTfvqmexmR/4PbZgTRkFYDdJWx3r61uywtIMIB5sL4mFip6K5NpOQTmBwiCfCs1k9/LmiLug2mGTRtuHcc4R/Ai7oiCfzjp1mQKBgQCQOPu0wEjUrQE1Q2sDhMIEQHglFD+ZxXmXmeKizIrIcwzVvz3DFaHYHxRqsKuD78LL2xQOq4LCSRUE472v5j+OTcfHsz711ZOtUonj1s3bb/gp+ASSF0p8Rt4ZxlNUWc6vn5wYscnEzG7aT11BAXlZ2h5LrYpxXNGEG6WIXKQvzwKBgBJOYtftZf6zGREgIAU8anwB/KRaJFWnFJ3cic2GSQGbnvL5L6ZoB/kehMAxUdNR0t9/hu9QAjXLAdJ5fI00L14gJdBgLJPyE4CRn51gZkiX+wALBXPxrtg75j5EIod4PhQ/m3IcPw4FeHyw2wsuznzPQDjrYZ9If2+6tyjmyFsc";

  // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
  private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAymrKGsEAsrmSvhippN+xy4alvFvXE9PYguMG9sAtqdapAhvGg8Ok44maenfsNagIbs2sTeQR22zAQDxF7bXvrxXDTNYrwDUvdeuO34O/L4coKueFXxC8eJiEqAUsopkuIJhWj3v35B0MQJLw9ZsU1PDl27+5se9TQ3f6c4iu4K8EtXC3WDF6VnRPt4owb4RcNdGNRXePN5DuQtZSoWZN80YoQD7NV82bVDqgBMo8pXAcpmXI17LFNYfHtwCktmtrObotb3c5cFntrygUrqlMgkq+B80XwJfOFgQbKQIhUwpRA798PrLU57nOSY9tVDfTpHmL+f9jS/WHIMpm8wJHqwIDAQAB";

  // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
  // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
  private String notify_url;

  // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
  //同步通知，支付成功，一般跳转到成功页
  private String return_url;

  // 订单超时自动关单时间
  // 订单绝对超时时间。
  // 格式为yyyy-MM-dd HH:mm:ss。超时时间范围：1m~15d。
  // 注：time_expire和timeout_express两者只需传入一个或者都不传，两者均传入时，优先使用time_expire。
  private String timeout_express;

  // 签名方式
  private String sign_type = "RSA2";

  // 字符编码格式
  private String charset = "utf-8";

  // 支付宝网关； https://openapi.alipaydev.com/gateway.do
  private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

  public String pay(PayVo vo) throws AlipayApiException {

    //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
    //1、根据支付宝的配置生成一个支付客户端
    AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
      app_id, merchant_private_key, "json",
      charset, alipay_public_key, sign_type);

    //2、创建一个支付请求 //设置请求参数
    AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
    alipayRequest.setReturnUrl(return_url);
    alipayRequest.setNotifyUrl(notify_url);

    //商户订单号，商户网站订单系统中唯一订单号，必填
    String out_trade_no = vo.getOut_trade_no();
    //付款金额，必填
    String total_amount = vo.getTotal_amount();
    //订单名称，必填
    String subject = vo.getSubject();
    //商品描述，可空
    String body = vo.getBody();

    alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
      + "\"total_amount\":\"" + total_amount + "\","
      + "\"subject\":\"" + subject + "\","
      + "\"body\":\"" + body + "\","
      + "\"timeout_express\":\"" + timeout_express + "\","
      + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

    String result = alipayClient.pageExecute(alipayRequest).getBody();

    //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
    System.out.println("支付宝的响应：" + result);

    return result;

  }
}
